package com.zm.blog.util;

public class SqlInjectionUtils {

    private static final String[] SQL_KEYWORDS = {
        "SELECT", "INSERT", "UPDATE", "DELETE", "DROP", "CREATE", "ALTER", "UNION",
        "EXEC", "EXECUTE", "TRUNCATE", "LOAD_FILE", "INTO OUTFILE", "INTO DUMPFILE",
        "CONCAT", "CHAR", "HEX", "UNHEX", "ASCII", "ORD", "MID", "SUBSTRING",
        "LENGTH", "WAITFOR", "DELAY", "SLEEP", "BENCHMARK", "IF", "THEN", "ELSE",
        "END", "CASE", "WHEN", "HAVING", "GROUP BY", "ORDER BY", "WHERE", "AND",
        "OR", "NOT", "LIKE", "IN", "EXISTS", "BETWEEN", "IS", "NULL", "TRUE", "FALSE"
    };

    public static boolean containsSqlInjection(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }

        String upperInput = input.toUpperCase();

        for (String keyword : SQL_KEYWORDS) {
            if (upperInput.contains(keyword)) {
                return true;
            }
        }

        // Check for common SQL injection patterns
        String[] patterns = {
            "'\\s*OR\\s*'1'\\s*=\\s*'1",
            "'\\s*OR\\s*1\\s*=\\s*1",
            "'\\s*AND\\s*'1'\\s*=\\s*'1",
            "'\\s*AND\\s*1\\s*=\\s*1",
            "'\\s*;\\s*DROP\\s+",
            "'\\s*;\\s*SELECT\\s+",
            "'\\s*;\\s*INSERT\\s+",
            "'\\s*;\\s*UPDATE\\s+",
            "'\\s*;\\s*DELETE\\s+",
            "'\\s*UNION\\s+SELECT\\s+",
            "'\\s*EXEC\\s*\\(",
            "'\\s*EXECUTE\\s*\\("
        };

        for (String pattern : patterns) {
            if (upperInput.matches("(?i).*" + pattern + ".*")) {
                return true;
            }
        }

        return false;
    }

    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }

        if (containsSqlInjection(input)) {
            throw new IllegalArgumentException("Input contains potential SQL injection");
        }

        return input;
    }
}