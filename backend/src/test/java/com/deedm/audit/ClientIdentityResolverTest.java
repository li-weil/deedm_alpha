package com.deedm.audit;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientIdentityResolverTest {

    @Test
    void shouldResolveVisitorIdFromHeader() {
        ClientIdentityResolver resolver = new ClientIdentityResolver();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Visitor-Id", "visitor-abc_123");

        String visitorId = resolver.resolveVisitorId(request);

        assertEquals("visitor-abc_123", visitorId);
    }

    @Test
    void shouldFallbackToXForwardedForFirstIp() {
        ClientIdentityResolver resolver = new ClientIdentityResolver();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "1.2.3.4, 10.0.0.1");

        String ip = resolver.resolveClientIp(request);

        assertEquals("1.2.3.4", ip);
    }

    @Test
    void shouldGenerateAnonymousVisitorWhenHeaderMissing() {
        ClientIdentityResolver resolver = new ClientIdentityResolver();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("9.8.7.6");
        request.addHeader("User-Agent", "JUnit");

        String visitorId = resolver.resolveVisitorId(request);

        assertTrue(visitorId.startsWith("anon-"));
    }
}
