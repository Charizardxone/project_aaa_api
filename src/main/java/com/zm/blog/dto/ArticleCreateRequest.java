package com.zm.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建文章请求DTO
 */
@Data
public class ArticleCreateRequest {

    /**
     * 文章标题
     */
    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100个字符")
    private String title;

    /**
     * 文章内容
     */
    @NotBlank(message = "内容不能为空")
    @Size(max = 10000, message = "内容长度不能超过10000个字符")
    private String content;

    /**
     * 文章摘要
     */
    @Size(max = 300, message = "摘要长度不能超过300个字符")
    private String summary;

    /**
     * 文章标签
     */
    @Size(max = 200, message = "标签长度不能超过200个字符")
    private String tags;
}