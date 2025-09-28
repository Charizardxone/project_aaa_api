package com.zm.blog.article.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ArticleCreateRequestTest {

    private Validator validator;
    private ArticleCreateRequest request;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        request = new ArticleCreateRequest();
        request.setTitle("Valid Title");
        request.setContent("Valid Content");
        request.setSummary("Valid Summary");
        request.setTags("valid,tags");
    }

    @Test
    void validRequest_NoValidationErrors() {
        Set<jakarta.validation.ConstraintViolation<ArticleCreateRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void emptyTitle_ValidationError() {
        request.setTitle("");
        Set<jakarta.validation.ConstraintViolation<ArticleCreateRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("标题不能为空", violations.iterator().next().getMessage());
    }

    @Test
    void nullTitle_ValidationError() {
        request.setTitle(null);
        Set<jakarta.validation.ConstraintViolation<ArticleCreateRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("标题不能为空", violations.iterator().next().getMessage());
    }

    @Test
    void titleTooLong_ValidationError() {
        request.setTitle("a".repeat(101));
        Set<jakarta.validation.ConstraintViolation<ArticleCreateRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("标题长度不能超过100个字符", violations.iterator().next().getMessage());
    }

    @Test
    void emptyContent_ValidationError() {
        request.setContent("");
        Set<jakarta.validation.ConstraintViolation<ArticleCreateRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("正文不能为空", violations.iterator().next().getMessage());
    }

    @Test
    void contentTooLong_ValidationError() {
        request.setContent("a".repeat(10001));
        Set<jakarta.validation.ConstraintViolation<ArticleCreateRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("正文长度不能超过10000个字符", violations.iterator().next().getMessage());
    }

    @Test
    void summaryTooLong_ValidationError() {
        request.setSummary("a".repeat(301));
        Set<jakarta.validation.ConstraintViolation<ArticleCreateRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("摘要长度不能超过300个字符", violations.iterator().next().getMessage());
    }

    @Test
    void tagsTooLong_ValidationError() {
        request.setTags("a".repeat(201));
        Set<jakarta.validation.ConstraintViolation<ArticleCreateRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("标签长度不能超过200个字符", violations.iterator().next().getMessage());
    }
}