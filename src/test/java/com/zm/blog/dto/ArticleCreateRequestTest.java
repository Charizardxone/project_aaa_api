package com.zm.blog.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ArticleCreateRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validRequest() {
        ArticleCreateRequest request = new ArticleCreateRequest(
            "Valid Title",
            "Valid Content",
            "Valid Summary",
            "valid, tags"
        );

        Set<jakarta.validation.ConstraintViolation<ArticleCreateRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void emptyTitle() {
        ArticleCreateRequest request = new ArticleCreateRequest(
            "",
            "Valid Content",
            "Valid Summary",
            "valid, tags"
        );

        Set<jakarta.validation.ConstraintViolation<ArticleCreateRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("标题不能为空")));
    }

    @Test
    void nullTitle() {
        ArticleCreateRequest request = new ArticleCreateRequest(
            null,
            "Valid Content",
            "Valid Summary",
            "valid, tags"
        );

        Set<jakarta.validation.ConstraintViolation<ArticleCreateRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("标题不能为空")));
    }

    @Test
    void emptyContent() {
        ArticleCreateRequest request = new ArticleCreateRequest(
            "Valid Title",
            "",
            "Valid Summary",
            "valid, tags"
        );

        Set<jakarta.validation.ConstraintViolation<ArticleCreateRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("内容不能为空")));
    }

    @Test
    void nullContent() {
        ArticleCreateRequest request = new ArticleCreateRequest(
            "Valid Title",
            null,
            "Valid Summary",
            "valid, tags"
        );

        Set<jakarta.validation.ConstraintViolation<ArticleCreateRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("内容不能为空")));
    }

    @Test
    void titleTooLong() {
        String longTitle = "A".repeat(101);
        ArticleCreateRequest request = new ArticleCreateRequest(
            longTitle,
            "Valid Content",
            "Valid Summary",
            "valid, tags"
        );

        Set<jakarta.validation.ConstraintViolation<ArticleCreateRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("标题不能超过100个字符")));
    }

    @Test
    void contentTooLong() {
        String longContent = "A".repeat(10001);
        ArticleCreateRequest request = new ArticleCreateRequest(
            "Valid Title",
            longContent,
            "Valid Summary",
            "valid, tags"
        );

        Set<jakarta.validation.ConstraintViolation<ArticleCreateRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("内容不能超过10000个字符")));
    }

    @Test
    void summaryTooLong() {
        String longSummary = "A".repeat(301);
        ArticleCreateRequest request = new ArticleCreateRequest(
            "Valid Title",
            "Valid Content",
            longSummary,
            "valid, tags"
        );

        Set<jakarta.validation.ConstraintViolation<ArticleCreateRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("摘要不能超过300个字符")));
    }

    @Test
    void tagsTooLong() {
        String longTags = "A".repeat(201);
        ArticleCreateRequest request = new ArticleCreateRequest(
            "Valid Title",
            "Valid Content",
            "Valid Summary",
            longTags
        );

        Set<jakarta.validation.ConstraintViolation<ArticleCreateRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("标签不能超过200个字符")));
    }

    @Test
    void optionalFieldsNull() {
        ArticleCreateRequest request = new ArticleCreateRequest(
            "Valid Title",
            "Valid Content",
            null,
            null
        );

        Set<jakarta.validation.ConstraintViolation<ArticleCreateRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
}