package com.zm.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("article")
public class Article {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @TableField("summary")
    private String summary;

    @TableField("tags")
    private String tags;

    @TableField("status")
    private String status;

    @TableField("author_id")
    private Long authorId;

    @TableField("view_count")
    private Long viewCount;

    @TableField("like_count")
    private Long likeCount;

    @TableField("comment_count")
    private Long commentCount;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(value = "published_at")
    private LocalDateTime publishedAt;

    @TableField("deleted")
    private Integer deleted;

    @TableField("version")
    private Integer version;
}