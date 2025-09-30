package com.zm.blog.controller;

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

    public static CommonResp<Void> success() {
        return new CommonResp<>(0, "OK", null);
    }
}