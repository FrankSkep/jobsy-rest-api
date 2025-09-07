package com.fran.jobsy.app.exception.handler;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private String code;
    private List<String> details;

    public ErrorResponse(String message, String code) {
        this.message = message;
        this.code = code;
    }
}
