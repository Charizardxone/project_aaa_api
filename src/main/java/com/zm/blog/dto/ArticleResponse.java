package com.zm.blog.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleResponse {

    private Long id;
    private String title;
    private String content;
    private String summary;
    private String tags;
    private String status;
    private Long authorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}