package com.deedm.validation;

import com.deedm.config.InputValidationProperties;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ValidationSmokeRunner {

    public static void main(String[] args) throws Exception {
        InputValidationProperties properties = new InputValidationProperties();
        properties.setEnabled(true);
        properties.setMaxRequestBytes(128);
        properties.setDefaultMaxStringLength(32);
        properties.setExtendedMaxStringLength(64);
        properties.setFilenameMaxLength(32);
        properties.setMaxCollectionSize(10);
        properties.setMaxMapEntries(10);
        properties.setMaxObjectDepth(4);

        RequestPayloadValidator validator = new RequestPayloadValidator(properties);

        validator.validate(Map.of("formula", "(p\\rightarrow q)\\wedge p"));
        System.out.println("PASS validator accepted legal formula");

        expectValidationFailure(() -> validator.validate(Map.of("formula", "abc\u0001def")),
            "contains illegal control characters");
        expectValidationFailure(() -> validator.validate(Map.of("formula", "a".repeat(65))),
            "length exceeds limit 64");

        RequestSizeLimitFilter filter = new RequestSizeLimitFilter(properties);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/truth-value/calculate");
        request.setContentType("application/json");
        request.setContent(("{\"formula\":\"" + "x".repeat(140) + "\"}").getBytes(StandardCharsets.UTF_8));

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        try {
            filter.doFilter(request, response, chain);
            throw new IllegalStateException("Expected payload size rejection");
        } catch (RequestPayloadValidationException ex) {
            if (!ex.getMessage().contains("exceeds limit 128 bytes")) {
                throw ex;
            }
            System.out.println("PASS filter rejected oversized JSON body");
        }
    }

    private static void expectValidationFailure(Runnable runnable, String expectedMessagePart) {
        try {
            runnable.run();
            throw new IllegalStateException("Expected validation failure");
        } catch (RequestPayloadValidationException ex) {
            if (!ex.getMessage().contains(expectedMessagePart)) {
                throw ex;
            }
            System.out.println("PASS " + ex.getMessage());
        }
    }
}
