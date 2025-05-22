-- 创建数据库
CREATE DATABASE IF NOT EXISTS linuxdo_cdk DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE linuxdo_cdk;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    linux_do_id VARCHAR(50) NOT NULL UNIQUE COMMENT 'Linux.do用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    avatar VARCHAR(255) COMMENT '头像URL',
    trust_level INT NOT NULL COMMENT '信任等级（1-3）',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_linux_do_id (linux_do_id),
    INDEX idx_trust_level (trust_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- CDK活动表
CREATE TABLE IF NOT EXISTS cdk_activity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL COMMENT '活动标题',
    description TEXT COMMENT '活动描述',
    usage_guide TEXT COMMENT '使用说明',
    min_trust_level INT NOT NULL COMMENT '最低信任等级要求（1-3）',
    total_count INT NOT NULL COMMENT 'CDK总数量',
    remaining_count INT NOT NULL COMMENT '剩余数量',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    is_active BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否激活',
    is_public BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否公开',
    creator_id BIGINT NOT NULL COMMENT '创建者ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_creator_id (creator_id),
    INDEX idx_is_active_public (is_active, is_public),
    INDEX idx_start_end_time (start_time, end_time),
    FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CDK活动表';

-- CDK表
CREATE TABLE IF NOT EXISTS cdk (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    activity_id BIGINT NOT NULL COMMENT '关联的活动ID',
    code VARCHAR(255) NOT NULL COMMENT 'CDK码',
    is_claimed BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否已被领取',
    claimed_by BIGINT COMMENT '领取者ID',
    claimed_at DATETIME COMMENT '领取时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_activity_id (activity_id),
    INDEX idx_claimed_by (claimed_by),
    INDEX idx_activity_claimed (activity_id, is_claimed),
    FOREIGN KEY (activity_id) REFERENCES cdk_activity(id) ON DELETE CASCADE,
    FOREIGN KEY (claimed_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CDK表';

-- 领取记录表
CREATE TABLE IF NOT EXISTS claim_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    activity_id BIGINT NOT NULL COMMENT '活动ID',
    cdk_id BIGINT NOT NULL COMMENT 'CDK ID',
    claimed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
    ip_address VARCHAR(50) COMMENT '领取IP地址',
    INDEX idx_user_id (user_id),
    INDEX idx_activity_id (activity_id),
    INDEX idx_cdk_id (cdk_id),
    INDEX idx_claimed_at (claimed_at),
    UNIQUE INDEX idx_user_activity (user_id, activity_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (activity_id) REFERENCES cdk_activity(id) ON DELETE CASCADE,
    FOREIGN KEY (cdk_id) REFERENCES cdk(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='领取记录表';
