package com.zm.blog.controller;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class CommonRespTest {

    @Test
    void testSuccessWithData() {
        Map<String, Long> data = Map.of("ts", 1234567890L);
        CommonResp<Map<String, Long>> response = CommonResp.success(data);

        assertEquals(0, response.getCode());
        assertEquals("OK", response.getMessage());
        assertEquals(data, response.getData());
    }

    @Test
    void testSuccessWithoutData() {
        CommonResp<Void> response = CommonResp.success();

        assertEquals(0, response.getCode());
        assertEquals("OK", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testConstructor() {
        String message = "test message";
        String data = "test data";
        CommonResp<String> response = new CommonResp<>(1, message, data);

        assertEquals(1, response.getCode());
        assertEquals(message, response.getMessage());
        assertEquals(data, response.getData());
    }
}