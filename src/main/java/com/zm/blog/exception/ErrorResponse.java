package com.zm.blog.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private int code;
    private String message;
    private Object data;

    public static ErrorResponse of(int code, String message) {
        return new ErrorResponse(code, message, null);
    }
}