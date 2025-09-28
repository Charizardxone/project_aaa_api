package com.zm.blog.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zm.blog.article.entity.Article;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
}