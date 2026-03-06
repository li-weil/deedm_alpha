package com.deedm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DeedmApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeedmApplication.class, args);
    }
}
