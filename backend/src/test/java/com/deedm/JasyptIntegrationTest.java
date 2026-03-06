package com.deedm;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.NoIvGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(
    classes = JasyptIntegrationTest.TestApplication.class,
    properties = {
        "spring.config.location=optional:classpath:/jasypt-test.yml",
        "jasypt.encryptor.password=test-master-key",
        "jasypt.encryptor.algorithm=PBEWithMD5AndDES",
        "jasypt.encryptor.iv-generator-classname=org.jasypt.iv.NoIvGenerator",
        "jasypt.encryptor.string-output-type=base64",
        "spring.main.web-application-type=none",
        "spring.datasource.url=jdbc:h2:mem:jasypt-test;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password="
    }
)
class JasyptIntegrationTest {

    private static final String MASTER_PASSWORD = "test-master-key";
    private static final String ALGORITHM = "PBEWithMD5AndDES";
    private static final String PLAIN_SECRET = "plain-secret-value";

    @Autowired
    private Environment environment;

    @DynamicPropertySource
    static void registerEncryptedProperty(DynamicPropertyRegistry registry) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(MASTER_PASSWORD);
        // Keep test settings aligned with the external CipherGeneratorApp
        // and the deployed application.yml jasypt block.
        encryptor.setAlgorithm(ALGORITHM);
        encryptor.setIvGenerator(new NoIvGenerator());
        encryptor.setStringOutputType("base64");
        String encrypted = encryptor.encrypt(PLAIN_SECRET);
        registry.add("demo.secret", () -> "ENC(" + encrypted + ")");
    }

    @Test
    void decryptsEncryptedPropertiesFromSpringEnvironment() {
        assertEquals(PLAIN_SECRET, environment.getProperty("demo.secret"));
    }

    @Configuration
    @EnableAutoConfiguration(exclude = DataSourceAutoConfiguration.class)
    static class TestApplication {
    }
}
