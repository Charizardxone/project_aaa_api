package com.zm.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 文章实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("article")
public class Article {

    /**
     * 文章ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文章标题
     */
    @TableField("title")
    private String title;

    /**
     * 文章内容
     */
    @TableField("content")
    private String content;

    /**
     * 文章摘要
     */
    @TableField("summary")
    private String summary;

    /**
     * 文章标签
     */
    @TableField("tags")
    private String tags;

    /**
     * 文章状态：draft-草稿，published-已发布
     */
    @TableField("status")
    private String status;

    /**
     * 作者ID
     */
    @TableField("author_id")
    private Long authorId;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 删除标记：0-未删除，1-已删除
     */
    @TableField("deleted")
    @TableLogic
    private Integer deleted;

    /**
     * 乐观锁版本号
     */
    @Version
    @TableField("version")
    private Integer version;

    public Article() {
        this.deleted = 0;
        this.version = 1;
        this.status = "draft";
    }
}