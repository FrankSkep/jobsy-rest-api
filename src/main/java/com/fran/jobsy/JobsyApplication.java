package com.fran.jobsy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class JobsyApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobsyApplication.class, args);
    }
}