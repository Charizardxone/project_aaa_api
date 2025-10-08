package com.zm.blog.dto;

import lombok.Data;

@Data
public class CommonResp<T> {
    private int code;
    private String message;
    private T data;

    public CommonResp(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> CommonResp<T> success(T data) {
        return new CommonResp<>(0, "OK", data);
    }

    public static <T> CommonResp<T> success() {
        return new CommonResp<>(0, "OK", null);
    }

    public static <T> CommonResp<T> error(int code, String message) {
        return new CommonResp<>(code, message, null);
    }

    public static <T> CommonResp<T> error(String message) {
        return new CommonResp<>(-1, message, null);
    }
}