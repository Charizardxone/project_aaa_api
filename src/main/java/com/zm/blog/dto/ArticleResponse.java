package com.zm.blog.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleResponse {

    private Long id;
    private String title;
    private String content;
    private String summary;
    private String tags;
    private String status;
    private Long authorId;
    private String authorName;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
}