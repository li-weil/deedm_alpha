package com.deedm.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(InputValidationProperties.class)
public class InputValidationConfiguration {
}
