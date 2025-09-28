package com.zm.blog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章响应DTO
 */
@Data
public class ArticleResponse {

    /**
     * 文章ID
     */
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 文章标签
     */
    private String tags;

    /**
     * 文章状态
     */
    private String status;

    /**
     * 作者ID
     */
    private Long authorId;

    /**
     * 作者用户名
     */
    private String authorUsername;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * 版本号
     */
    private Integer version;
}