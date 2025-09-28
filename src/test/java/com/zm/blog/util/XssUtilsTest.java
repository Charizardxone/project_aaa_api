package com.zm.blog.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class XssUtilsTest {

    @Test
    void clean_NullInput() {
        assertNull(XssUtils.clean(null));
    }

    @Test
    void clean_ScriptTag() {
        String input = "Hello <script>alert('xss')</script> World";
        String result = XssUtils.clean(input);
        // Script tags should be completely removed and content HTML encoded
        assertTrue(result.contains("Hello") && result.contains("World"));
        assertFalse(result.contains("script"));
        assertFalse(result.contains("alert"));
    }

    @Test
    void clean_IframeTag() {
        String input = "Hello <iframe src='malicious.com'></iframe> World";
        String result = XssUtils.clean(input);
        // iframe tags should be completely removed and content HTML encoded
        assertTrue(result.contains("Hello") && result.contains("World"));
        assertFalse(result.toLowerCase().contains("iframe"));
        assertFalse(result.contains("malicious.com"));
    }

    @Test
    void clean_OnClickAttribute() {
        String input = "Hello <div onclick='alert(\"xss\")'>Click me</div> World";
        String result = XssUtils.clean(input);
        // onclick attributes should be removed and content HTML encoded
        assertTrue(result.contains("Hello") && result.contains("World"));
        assertFalse(result.toLowerCase().contains("onclick"));
        assertFalse(result.contains("alert"));
    }

    @Test
    void cleanForText_HTMLTags() {
        String input = "Hello <b>bold</b> and <i>italic</i> text";
        String expected = "Hello bold and italic text";
        assertEquals(expected, XssUtils.cleanForText(input));
    }

    @Test
    void cleanForText_NullInput() {
        assertNull(XssUtils.cleanForText(null));
    }

    @Test
    void cleanForText_PlainText() {
        String input = "Just plain text";
        assertEquals(input, XssUtils.cleanForText(input));
    }
}