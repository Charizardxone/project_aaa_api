package com.zm.blog.repository;

import com.zm.blog.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Article repository interface
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    /**
     * Find article with author info
     */
    @Query("SELECT a FROM Article a JOIN FETCH a.author WHERE a.id = :id")
    Article findArticleWithAuthor(@Param("id") Long id);
}