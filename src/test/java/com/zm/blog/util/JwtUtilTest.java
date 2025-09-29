package com.zm.blog.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JwtUtil
 */
@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=mySecretKeyForTestingPurposesAtLeast256BitsLong",
    "jwt.expiration=3600"
})
class JwtUtilTest {

    @Test
    void testTokenGenerationAndValidation() {
        JwtUtil jwtUtil = new JwtUtil();

        // Generate token
        String token = jwtUtil.generateToken("testuser", 1L);

        // Validate token
        assertTrue(jwtUtil.validateToken(token));
        assertEquals("testuser", jwtUtil.getUsernameFromToken(token));
        assertEquals(1L, jwtUtil.getUserIdFromToken(token));
    }

    @Test
    void testInvalidToken() {
        JwtUtil jwtUtil = new JwtUtil();

        assertFalse(jwtUtil.validateToken("invalid-token"));
        assertNull(jwtUtil.getUsernameFromToken("invalid-token"));
        assertNull(jwtUtil.getUserIdFromToken("invalid-token"));
    }
}