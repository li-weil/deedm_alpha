package com.deedm.validation;

import com.deedm.config.InputValidationProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestSizeLimitFilter extends OncePerRequestFilter {

    private final InputValidationProperties properties;

    public RequestSizeLimitFilter(InputValidationProperties properties) {
        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        if (!properties.isEnabled() || !requiresValidation(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            long contentLength = request.getContentLengthLong();
            if (contentLength > properties.getMaxRequestBytes()) {
                throw new RequestPayloadValidationException(
                    "Request body exceeds limit " + properties.getMaxRequestBytes() + " bytes"
                );
            }

            HttpServletRequest wrappedRequest = new SizeLimitedRequestWrapper(request, properties.getMaxRequestBytes());
            filterChain.doFilter(wrappedRequest, response);
        } catch (RequestPayloadValidationException ex) {
            writePayloadTooLarge(response, ex.getMessage());
        }
    }

    private boolean requiresValidation(HttpServletRequest request) {
        String method = request.getMethod();
        if (!"POST".equalsIgnoreCase(method) && !"PUT".equalsIgnoreCase(method) && !"PATCH".equalsIgnoreCase(method)) {
            return false;
        }
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().contains(MediaType.APPLICATION_JSON_VALUE);
    }

    private void writePayloadTooLarge(HttpServletResponse response, String message) throws IOException {
        if (response.isCommitted()) {
            return;
        }
        response.resetBuffer();
        response.setStatus(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        String json = "{\"success\":false,\"error\":\"" + escapeJson(message) + "\"}";
        response.getWriter().write(json);
        response.flushBuffer();
    }

    private String escapeJson(String value) {
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"");
    }

    private static final class SizeLimitedRequestWrapper extends HttpServletRequestWrapper {

        private final int maxRequestBytes;

        private SizeLimitedRequestWrapper(HttpServletRequest request, int maxRequestBytes) {
            super(request);
            this.maxRequestBytes = maxRequestBytes;
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            return new SizeLimitedServletInputStream(super.getInputStream(), maxRequestBytes);
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
        }
    }

    private static final class SizeLimitedServletInputStream extends ServletInputStream {

        private final ServletInputStream delegate;
        private final int maxRequestBytes;
        private int bytesRead;

        private SizeLimitedServletInputStream(ServletInputStream delegate, int maxRequestBytes) {
            this.delegate = delegate;
            this.maxRequestBytes = maxRequestBytes;
        }

        @Override
        public int read() throws IOException {
            int value = delegate.read();
            if (value != -1) {
                bytesRead++;
                ensureLimit();
            }
            return value;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int count = delegate.read(b, off, len);
            if (count > 0) {
                bytesRead += count;
                ensureLimit();
            }
            return count;
        }

        @Override
        public boolean isFinished() {
            return delegate.isFinished();
        }

        @Override
        public boolean isReady() {
            return delegate.isReady();
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            delegate.setReadListener(readListener);
        }

        private void ensureLimit() {
            if (bytesRead > maxRequestBytes) {
                throw new RequestPayloadValidationException("Request body exceeds limit " + maxRequestBytes + " bytes");
            }
        }
    }
}
