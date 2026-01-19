-- =============================================
-- 智能冰箱菜谱系统 - 数据库初始化脚本
-- 创建日期: 2026-01-19
-- 数据库: MySQL 8.0
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS smart_fridge 
    DEFAULT CHARACTER SET utf8mb4 
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE smart_fridge;

-- =============================================
-- 用户表
-- =============================================
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password_hash` VARCHAR(255) NOT NULL COMMENT '密码哈希',
    `nickname` VARCHAR(50) COMMENT '昵称',
    `avatar_url` VARCHAR(500) COMMENT '头像URL',
    `preferences` JSON COMMENT '用户偏好(忌口/口味等)',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- =============================================
-- 库存表 (冰箱食材)
-- =============================================
CREATE TABLE IF NOT EXISTS `inventory` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '库存ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `ingredient_name` VARCHAR(100) NOT NULL COMMENT '食材名称',
    `quantity` DECIMAL(10,2) DEFAULT 1 COMMENT '数量',
    `unit` VARCHAR(20) DEFAULT '个' COMMENT '单位',
    `expire_date` DATE COMMENT '过期日期',
    `entry_source` ENUM('PHOTO', 'MANUAL', 'BARCODE') DEFAULT 'PHOTO' COMMENT '录入来源',
    `confidence` DECIMAL(3,2) COMMENT '识别置信度',
    `image_url` VARCHAR(500) COMMENT '原始图片URL',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_expire_date` (`expire_date`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='冰箱库存表';

-- =============================================
-- 购物清单表
-- =============================================
CREATE TABLE IF NOT EXISTS `shopping_list` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '清单ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `ingredient_name` VARCHAR(100) NOT NULL COMMENT '食材名称',
    `quantity` DECIMAL(10,2) DEFAULT 1 COMMENT '数量',
    `unit` VARCHAR(20) DEFAULT '个' COMMENT '单位',
    `purchased` BOOLEAN DEFAULT FALSE COMMENT '是否已购买',
    `source_recipe` VARCHAR(100) COMMENT '来源菜谱',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_user_id` (`user_id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物清单表';

-- =============================================
-- 菜谱历史表
-- =============================================
CREATE TABLE IF NOT EXISTS `recipe_history` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `recipe_name` VARCHAR(100) NOT NULL COMMENT '菜谱名称',
    `recipe_content` JSON NOT NULL COMMENT '菜谱内容(步骤等)',
    `used_ingredients` JSON COMMENT '使用的食材',
    `missing_ingredients` JSON COMMENT '缺少的食材',
    `rating` TINYINT COMMENT '用户评分(1-5)',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_user_id` (`user_id`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜谱历史表';

-- =============================================
-- 用户反馈表 (用于模型优化)
-- =============================================
CREATE TABLE IF NOT EXISTS `recognition_feedback` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '反馈ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `image_url` VARCHAR(500) NOT NULL COMMENT '图片URL',
    `original_label` VARCHAR(100) COMMENT '原始识别结果',
    `corrected_label` VARCHAR(100) COMMENT '用户纠正结果',
    `is_processed` BOOLEAN DEFAULT FALSE COMMENT '是否已处理',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_processed` (`is_processed`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='识别反馈表(用于模型优化)';

-- =============================================
-- 插入测试数据
-- =============================================
INSERT INTO `user` (`username`, `password_hash`, `nickname`, `preferences`) VALUES
('demo', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKQ/k2oqd8JfJveaLiXzN9YHbgPu', '演示用户', 
 '{"dislikes": ["香菜", "芹菜"], "preference": "快手菜", "spicy_level": "微辣"}');
