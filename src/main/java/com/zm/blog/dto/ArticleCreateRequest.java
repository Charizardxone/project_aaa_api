package com.zm.blog.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ArticleCreateRequest {

    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100个字符")
    private String title;

    @NotBlank(message = "正文不能为空")
    @Size(max = 10000, message = "正文长度不能超过10000个字符")
    private String content;

    @Size(max = 300, message = "摘要长度不能超过300个字符")
    private String summary;

    @Size(max = 200, message = "标签长度不能超过200个字符")
    private String tags;
}