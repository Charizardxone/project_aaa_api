package com.zm.blog.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SqlInjectionUtilsTest {

    @Test
    void containsSqlInjection_NullInput() {
        assertFalse(SqlInjectionUtils.containsSqlInjection(null));
    }

    @Test
    void containsSqlInjection_EmptyInput() {
        assertFalse(SqlInjectionUtils.containsSqlInjection(""));
    }

    @Test
    void containsSqlInjection_PlainText() {
        assertFalse(SqlInjectionUtils.containsSqlInjection("Hello there"));
    }

    @Test
    void containsSqlInjection_SelectKeyword() {
        assertTrue(SqlInjectionUtils.containsSqlInjection("SELECT * FROM users"));
    }

    @Test
    void containsSqlInjection_UnionKeyword() {
        assertTrue(SqlInjectionUtils.containsSqlInjection("UNION SELECT * FROM users"));
    }

    @Test
    void containsSqlInjection_OrCondition() {
        assertTrue(SqlInjectionUtils.containsSqlInjection("' OR '1'='1"));
    }

    @Test
    void containsSqlInjection_DropTable() {
        assertTrue(SqlInjectionUtils.containsSqlInjection("DROP TABLE users"));
    }

    @Test
    void containsSqlInjection_CaseInsensitive() {
        assertTrue(SqlInjectionUtils.containsSqlInjection("select * from users"));
    }

    @Test
    void containsSqlInjection_CommentBased() {
        assertTrue(SqlInjectionUtils.containsSqlInjection("' OR 1=1--"));
    }

    @Test
    void sanitize_ValidInput() {
        String input = "Hello there";
        assertEquals(input, SqlInjectionUtils.sanitize(input));
    }

    @Test
    void sanitize_NullInput() {
        assertNull(SqlInjectionUtils.sanitize(null));
    }

    @Test
    void sanitize_WithSqlInjection() {
        String input = "Hello ' OR '1'='1";
        assertThrows(IllegalArgumentException.class, () -> {
            SqlInjectionUtils.sanitize(input);
        });
    }

    @Test
    void sanitize_WithSelectKeyword() {
        String input = "Hello SELECT * FROM users";
        assertThrows(IllegalArgumentException.class, () -> {
            SqlInjectionUtils.sanitize(input);
        });
    }
}