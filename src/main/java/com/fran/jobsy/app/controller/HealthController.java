package com.fran.jobsy.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    public Map<String, String> checkHealth() {
        return Map.of("status", "OK");
    }
}
