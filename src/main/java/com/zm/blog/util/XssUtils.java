package com.zm.blog.util;

import org.owasp.encoder.Encode;

public class XssUtils {

    public static String clean(String input) {
        if (input == null) {
            return null;
        }

        // First remove potentially dangerous tags and attributes
        String cleaned = input.replaceAll("(?i)<script.*?>.*?</script>", "");
        cleaned = cleaned.replaceAll("(?i)<iframe.*?>.*?</iframe>", "");
        cleaned = cleaned.replaceAll("(?i)<object.*?>.*?</object>", "");
        cleaned = cleaned.replaceAll("(?i)<embed.*?>.*?</embed>", "");
        cleaned = cleaned.replaceAll("(?i)on\\w+\\s*=\\s*['\"].*?['\"]", "");

        // Then HTML encode the cleaned content
        return Encode.forHtml(cleaned);
    }

    public static String cleanForText(String input) {
        if (input == null) {
            return null;
        }

        // Remove HTML tags completely for plain text fields
        return input.replaceAll("<[^>]*>", "");
    }
}