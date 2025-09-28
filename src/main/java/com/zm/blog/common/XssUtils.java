package com.zm.blog.common;

import org.owasp.encoder.Encode;

public class XssUtils {

    /**
     * Clean input to prevent XSS attacks
     */
    public static String clean(String input) {
        if (input == null) {
            return null;
        }

        // HTML encode the input
        String encoded = Encode.forHtml(input);

        // Additional filtering for common XSS patterns
        encoded = encoded.replaceAll("(?i)<script.*?>.*?</script>", "");
        encoded = encoded.replaceAll("(?i)javascript:", "");
        encoded = encoded.replaceAll("(?i)on\\w+\\s*=", "");

        return encoded;
    }

    /**
     * Clean input but preserve certain HTML tags (for rich content)
     */
    public static String cleanWithAllowedTags(String input, String... allowedTags) {
        if (input == null) {
            return null;
        }

        // For now, use the same basic cleaning
        // In a real implementation, you would use a proper HTML sanitizer like OWASP Java HTML Sanitizer
        return clean(input);
    }
}