package com.zm.blog.common.util;

import org.owasp.encoder.Encode;

public class XssUtils {

    public static String stripXss(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        // HTML encode dangerous characters
        String encoded = Encode.forHtml(value);

        // Remove potential script tags and event handlers
        encoded = encoded.replaceAll("(?i)<script.*?>.*?</script>", "");
        encoded = encoded.replaceAll("(?i)on\\w+\\s*=\\s*['\"](.*?)['\"]", "");

        // Remove potentially dangerous HTML tags
        encoded = encoded.replaceAll("(?i)<iframe.*?>.*?</iframe>", "");
        encoded = encoded.replaceAll("(?i)<object.*?>.*?</object>", "");
        encoded = encoded.replaceAll("(?i)<embed.*?>.*?</embed>", "");

        return encoded;
    }

    public static String cleanForDatabase(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        // Remove potential SQL injection patterns
        String cleaned = value.replaceAll("(?i)(;|--|/\\*|\\*/|xp_|sp_)", "");

        // Escape single quotes
        cleaned = cleaned.replaceAll("'", "''");

        return cleaned;
    }
}