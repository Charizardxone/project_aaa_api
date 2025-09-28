-- Database Schema for Blog System
-- This file contains the table definitions for the blog system

-- Create user table
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `email` VARCHAR(100) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `nickname` VARCHAR(50),
    `avatar` VARCHAR(255),
    `bio` TEXT,
    `status` VARCHAR(20) DEFAULT 'ACTIVE',
    `role` VARCHAR(20) DEFAULT 'USER',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `last_login_at` DATETIME,
    `deleted` INT DEFAULT 0,
    INDEX `idx_username` (`username`),
    INDEX `idx_email` (`email`),
    INDEX `idx_status` (`status`)
);

-- Create article table
CREATE TABLE IF NOT EXISTS `article` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(200) NOT NULL,
    `content` TEXT NOT NULL,
    `summary` VARCHAR(500),
    `tags` VARCHAR(200),
    `status` VARCHAR(20) DEFAULT 'DRAFT',
    `author_id` BIGINT NOT NULL,
    `view_count` BIGINT DEFAULT 0,
    `like_count` BIGINT DEFAULT 0,
    `comment_count` BIGINT DEFAULT 0,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `published_at` DATETIME,
    `deleted` INT DEFAULT 0,
    `version` INT DEFAULT 1,
    INDEX `idx_author_id` (`author_id`),
    INDEX `idx_status` (`status`),
    INDEX `idx_created_at` (`created_at`),
    INDEX `idx_published_at` (`published_at`),
    FOREIGN KEY (`author_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
);

-- Insert test data
INSERT INTO `user` (username, email, password, nickname, status, role) VALUES
('testuser', 'test@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Test User', 'ACTIVE', 'USER'),
('admin', 'admin@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Admin', 'ACTIVE', 'ADMIN');