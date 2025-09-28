package com.zm.blog.common;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.HtmlUtils;

/**
 * 安全工具类
 */
public class SecurityUtils {

    /**
     * XSS过滤
     */
    public static String xssFilter(String input) {
        if (StringUtils.isBlank(input)) {
            return input;
        }
        return HtmlUtils.htmlEscape(input);
    }

    /**
     * SQL注入过滤
     */
    public static String sqlFilter(String input) {
        if (StringUtils.isBlank(input)) {
            return input;
        }
        // 移除危险的SQL关键字和字符
        return input.replaceAll("(?i)\\b(select|insert|update|delete|drop|alter|exec|execute|script|javascript|onload|onerror|onclick|onmouseover|onfocus|onblur|onchange|onsubmit|onreset|onselect|onunload)\\b", "")
                .replaceAll("['\";\\\\-\\\\-]", "");
    }

    /**
     * 综合内容过滤
     */
    public static String contentFilter(String input) {
        if (StringUtils.isBlank(input)) {
            return input;
        }
        return sqlFilter(xssFilter(input));
    }

    /**
     * 生成请求指纹（用于幂等性）
     */
    public static String generateRequestFingerprint(String title, String content, Long authorId) {
        String combined = title + content + authorId + System.currentTimeMillis();
        return org.apache.commons.codec.digest.DigestUtils.md5Hex(combined);
    }
}