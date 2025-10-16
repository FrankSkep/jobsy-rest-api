package com.fran.jobsy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@EnableAsync
@EnableWebSocket
public class JobsyApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobsyApplication.class, args);
    }
}