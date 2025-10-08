package com.zm.blog.controller;

import com.zm.blog.exception.BizException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestExceptionController {

    @GetMapping("/api/test/biz")
    public String testBizException(@RequestParam(defaultValue = "1001") int code,
                                 @RequestParam(defaultValue = "Test business error") String message) {
        throw new BizException(code, message);
    }

    @GetMapping("/api/test/illegal")
    public String testIllegalArgument(@RequestParam String param) {
        if (param == null || param.trim().isEmpty()) {
            throw new IllegalArgumentException("Parameter cannot be empty");
        }
        return "OK";
    }

    @GetMapping("/api/test/runtime")
    public String testRuntimeException() {
        throw new RuntimeException("Test runtime exception");
    }
}