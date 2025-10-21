package com.fran.jobsy.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    @RequestMapping("/check")
    public String checkHealth() {
        return "OK";
    }
}
