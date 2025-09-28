package com.zm.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zm.blog.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 文章Mapper接口
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 根据ID查询文章（包含作者信息）
     */
    @Select("SELECT a.*, u.username as author_username FROM article a " +
            "LEFT JOIN user u ON a.author_id = u.id " +
            "WHERE a.id = #{id} AND a.deleted = 0")
    Article selectWithAuthor(Long id);
}