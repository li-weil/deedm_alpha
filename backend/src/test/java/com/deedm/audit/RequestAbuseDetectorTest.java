package com.deedm.audit;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestAbuseDetectorTest {

    @Test
    void shouldTriggerOnlyWhenThresholdIsCrossed() {
        RequestAbuseDetector detector = new RequestAbuseDetector();
        ReflectionTestUtils.setField(detector, "thresholdPerMinute", 2);

        RequestAbuseDetector.AbuseResult first = detector.recordAndEvaluate("1.2.3.4|/api/graph-image");
        RequestAbuseDetector.AbuseResult second = detector.recordAndEvaluate("1.2.3.4|/api/graph-image");
        RequestAbuseDetector.AbuseResult third = detector.recordAndEvaluate("1.2.3.4|/api/graph-image");
        RequestAbuseDetector.AbuseResult fourth = detector.recordAndEvaluate("1.2.3.4|/api/graph-image");

        assertFalse(first.triggered());
        assertFalse(second.triggered());
        assertTrue(third.triggered());
        assertFalse(fourth.triggered());
    }
}
