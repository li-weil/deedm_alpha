package com.deedm.validation;

import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.reflect.Type;

@ControllerAdvice(basePackages = "com.deedm.controller")
public class RequestPayloadValidationAdvice extends RequestBodyAdviceAdapter {

    private final RequestPayloadValidator requestPayloadValidator;

    public RequestPayloadValidationAdvice(RequestPayloadValidator requestPayloadValidator) {
        this.requestPayloadValidator = requestPayloadValidator;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object afterBodyRead(Object body, org.springframework.http.HttpInputMessage inputMessage,
                                MethodParameter parameter, Type targetType,
                                Class<? extends HttpMessageConverter<?>> converterType) {
        requestPayloadValidator.validate(body);
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, org.springframework.http.HttpInputMessage inputMessage,
                                  MethodParameter parameter, Type targetType,
                                  Class<? extends HttpMessageConverter<?>> converterType) {
        return body;
    }
}
