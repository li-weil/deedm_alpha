package com.deedm.audit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RequestAbuseDetector {

    private static final long WINDOW_MILLIS = 60_000L;

    private final Map<String, SlidingWindowCounter> counters = new ConcurrentHashMap<>();

    @Value("${audit.abuse.threshold-per-minute:120}")
    private int thresholdPerMinute;

    public AbuseResult recordAndEvaluate(String resourceKey) {
        if (resourceKey == null || resourceKey.isBlank()) {
            return AbuseResult.notTriggered(thresholdPerMinute, 0);
        }

        SlidingWindowCounter counter = counters.computeIfAbsent(resourceKey, key -> new SlidingWindowCounter());
        return counter.record(Instant.now().toEpochMilli(), thresholdPerMinute);
    }

    public record AbuseResult(boolean triggered, int threshold, int count) {
        static AbuseResult notTriggered(int threshold, int count) {
            return new AbuseResult(false, threshold, count);
        }
    }

    private static class SlidingWindowCounter {
        private final Deque<Long> timestamps = new ArrayDeque<>();
        private boolean aboveThreshold;

        synchronized AbuseResult record(long nowMillis, int threshold) {
            long cutoff = nowMillis - WINDOW_MILLIS;
            while (!timestamps.isEmpty() && timestamps.peekFirst() < cutoff) {
                timestamps.pollFirst();
            }
            timestamps.addLast(nowMillis);
            int currentCount = timestamps.size();
            boolean nowAboveThreshold = currentCount > threshold;
            boolean triggeredNow = nowAboveThreshold && !aboveThreshold;
            aboveThreshold = nowAboveThreshold;
            return new AbuseResult(triggeredNow, threshold, currentCount);
        }
    }
}
