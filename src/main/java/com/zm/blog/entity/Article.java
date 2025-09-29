package com.zm.blog.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

/**
 * Article entity for blog system
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "articles")
@SQLDelete(sql = "UPDATE articles SET deleted = 1 WHERE id = ?")
@Where(clause = "deleted = 0")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "tags", length = 500)
    private String tags;

    @Column(name = "status", length = 20)
    private String status = "draft"; // draft, published, archived

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Column(name = "comment_count")
    private Integer commentCount = 0;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted")
    private Integer deleted = 0;

    // Add relationship mapping
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", insertable = false, updatable = false)
    private User author;
}