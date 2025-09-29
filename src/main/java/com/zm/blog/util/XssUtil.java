package com.zm.blog.util;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

/**
 * XSS filtering utility
 */
public class XssUtil {

    /**
     * Policy that allows safe HTML tags for content
     */
    private static final PolicyFactory CONTENT_POLICY = new HtmlPolicyBuilder()
            .allowElements("p", "br", "strong", "em", "u", "h1", "h2", "h3", "h4", "h5", "h6",
                          "ul", "ol", "li", "blockquote", "code", "pre")
            .allowAttributes("class").onElements("code", "pre")
            .toFactory();

    /**
     * Policy that strips all HTML tags
     */
    private static final PolicyFactory STRIP_ALL_POLICY = new HtmlPolicyBuilder().toFactory();

    /**
     * Sanitize content allowing safe HTML tags
     */
    public static String sanitizeContent(String input) {
        if (input == null) {
            return null;
        }
        return CONTENT_POLICY.sanitize(input);
    }

    /**
     * Sanitize text by stripping all HTML tags
     */
    public static String sanitizeText(String input) {
        if (input == null) {
            return null;
        }
        return STRIP_ALL_POLICY.sanitize(input);
    }
}