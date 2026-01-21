package com.win777.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Win777Application {

    public static void main(String[] args) {
        SpringApplication.run(Win777Application.class, args);
    }
}
