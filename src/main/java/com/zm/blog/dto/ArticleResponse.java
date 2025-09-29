package com.zm.blog.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Response DTO for article data
 */
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
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}