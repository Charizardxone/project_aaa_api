package com.zm.blog.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ArticleEditRequestTest {

    private Validator validator;
    private LocalDateTime validUpdatedAt;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        validUpdatedAt = LocalDateTime.now();
    }

    @Test
    void validArticleEditRequest_NoViolations() {
        ArticleEditRequest request = new ArticleEditRequest(
            "Valid Title",
            "Valid Content",
            "Valid Summary",
            "valid, tags",
            validUpdatedAt
        );

        Set<jakarta.validation.ConstraintViolation<ArticleEditRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void emptyTitle_ValidationError() {
        ArticleEditRequest request = new ArticleEditRequest(
            "", // Empty title
            "Valid Content",
            "Valid Summary",
            "valid, tags",
            validUpdatedAt
        );

        Set<jakarta.validation.ConstraintViolation<ArticleEditRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("标题不能为空", violations.iterator().next().getMessage());
    }

    @Test
    void nullTitle_ValidationError() {
        ArticleEditRequest request = new ArticleEditRequest(
            null, // Null title
            "Valid Content",
            "Valid Summary",
            "valid, tags",
            validUpdatedAt
        );

        Set<jakarta.validation.ConstraintViolation<ArticleEditRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("标题不能为空", violations.iterator().next().getMessage());
    }

    @Test
    void titleTooLong_ValidationError() {
        String longTitle = "A".repeat(101); // 101 characters
        ArticleEditRequest request = new ArticleEditRequest(
            longTitle,
            "Valid Content",
            "Valid Summary",
            "valid, tags",
            validUpdatedAt
        );

        Set<jakarta.validation.ConstraintViolation<ArticleEditRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("标题不能超过100个字符", violations.iterator().next().getMessage());
    }

    @Test
    void emptyContent_ValidationError() {
        ArticleEditRequest request = new ArticleEditRequest(
            "Valid Title",
            "", // Empty content
            "Valid Summary",
            "valid, tags",
            validUpdatedAt
        );

        Set<jakarta.validation.ConstraintViolation<ArticleEditRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("内容不能为空", violations.iterator().next().getMessage());
    }

    @Test
    void nullContent_ValidationError() {
        ArticleEditRequest request = new ArticleEditRequest(
            "Valid Title",
            null, // Null content
            "Valid Summary",
            "valid, tags",
            validUpdatedAt
        );

        Set<jakarta.validation.ConstraintViolation<ArticleEditRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("内容不能为空", violations.iterator().next().getMessage());
    }

    @Test
    void contentTooLong_ValidationError() {
        String longContent = "A".repeat(10001); // 10001 characters
        ArticleEditRequest request = new ArticleEditRequest(
            "Valid Title",
            longContent,
            "Valid Summary",
            "valid, tags",
            validUpdatedAt
        );

        Set<jakarta.validation.ConstraintViolation<ArticleEditRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("内容不能超过10000个字符", violations.iterator().next().getMessage());
    }

    @Test
    void summaryTooLong_ValidationError() {
        String longSummary = "A".repeat(301); // 301 characters
        ArticleEditRequest request = new ArticleEditRequest(
            "Valid Title",
            "Valid Content",
            longSummary,
            "valid, tags",
            validUpdatedAt
        );

        Set<jakarta.validation.ConstraintViolation<ArticleEditRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("摘要不能超过300个字符", violations.iterator().next().getMessage());
    }

    @Test
    void tagsTooLong_ValidationError() {
        String longTags = "A".repeat(201); // 201 characters
        ArticleEditRequest request = new ArticleEditRequest(
            "Valid Title",
            "Valid Content",
            "Valid Summary",
            longTags,
            validUpdatedAt
        );

        Set<jakarta.validation.ConstraintViolation<ArticleEditRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("标签不能超过200个字符", violations.iterator().next().getMessage());
    }

    @Test
    void nullUpdatedAt_ValidationError() {
        ArticleEditRequest request = new ArticleEditRequest(
            "Valid Title",
            "Valid Content",
            "Valid Summary",
            "valid, tags",
            null // Null updatedAt
        );

        Set<jakarta.validation.ConstraintViolation<ArticleEditRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertEquals("更新时间不能为空", violations.iterator().next().getMessage());
    }

    @Test
    void nullSummaryAndTags_NoViolations() {
        ArticleEditRequest request = new ArticleEditRequest(
            "Valid Title",
            "Valid Content",
            null, // Null summary (optional)
            null, // Null tags (optional)
            validUpdatedAt
        );

        Set<jakarta.validation.ConstraintViolation<ArticleEditRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void gettersAndSetters_WorkCorrectly() {
        ArticleEditRequest request = new ArticleEditRequest();

        // Test setters
        request.setTitle("Test Title");
        request.setContent("Test Content");
        request.setSummary("Test Summary");
        request.setTags("test, tags");
        request.setUpdatedAt(validUpdatedAt);

        // Test getters
        assertEquals("Test Title", request.getTitle());
        assertEquals("Test Content", request.getContent());
        assertEquals("Test Summary", request.getSummary());
        assertEquals("test, tags", request.getTags());
        assertEquals(validUpdatedAt, request.getUpdatedAt());
    }

    @Test
    void allArgsConstructor_CreatesObjectCorrectly() {
        ArticleEditRequest request = new ArticleEditRequest(
            "Constructor Title",
            "Constructor Content",
            "Constructor Summary",
            "constructor, tags",
            validUpdatedAt
        );

        assertEquals("Constructor Title", request.getTitle());
        assertEquals("Constructor Content", request.getContent());
        assertEquals("Constructor Summary", request.getSummary());
        assertEquals("constructor, tags", request.getTags());
        assertEquals(validUpdatedAt, request.getUpdatedAt());
    }

    @Test
    void noArgsConstructor_CreatesEmptyObject() {
        ArticleEditRequest request = new ArticleEditRequest();

        assertNull(request.getTitle());
        assertNull(request.getContent());
        assertNull(request.getSummary());
        assertNull(request.getTags());
        assertNull(request.getUpdatedAt());
    }
}