-- 初始用户数据
INSERT INTO users (linux_do_id, username, avatar, trust_level, created_at, updated_at)
VALUES
    ('admin123', '管理员', 'https://linux.do/user_avatar/linux.do/admin/240/1.png', 3, NOW(), NOW()),
    ('user456', '普通用户', 'https://linux.do/user_avatar/linux.do/user/240/2.png', 1, NOW(), NOW()),
    ('trusted789', '信任用户', 'https://linux.do/user_avatar/linux.do/trusted/240/3.png', 2, NOW(), NOW());

-- 初始CDK活动数据
INSERT INTO cdk_activity (title, description, usage_guide, min_trust_level, total_count, remaining_count, 
                         start_time, end_time, is_active, is_public, creator_id, created_at, updated_at)
VALUES
    ('JetBrains全家桶激活码', 'JetBrains全系列IDE一年激活码，包括IntelliJ IDEA、PyCharm、WebStorm等。', 
     '1. 打开任意JetBrains IDE\n2. 点击菜单 Help -> Register\n3. 选择 "Activation code"\n4. 粘贴激活码并点击激活', 
     2, 10, 10, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), TRUE, TRUE, 1, NOW(), NOW()),
    
    ('Windows 11专业版激活码', 'Windows 11专业版正版激活码，支持在线激活。', 
     '1. 打开设置 -> 系统 -> 激活\n2. 点击"更改产品密钥"\n3. 输入激活码\n4. 点击"下一步"完成激活', 
     1, 5, 5, NOW(), DATE_ADD(NOW(), INTERVAL 15 DAY), TRUE, TRUE, 1, NOW(), NOW()),
    
    ('Adobe Creative Cloud会员', 'Adobe Creative Cloud一年会员资格，包含Photoshop、Illustrator等全套软件。', 
     '1. 访问adobe.com并登录您的Adobe账户\n2. 进入"账户"页面\n3. 点击"兑换代码"\n4. 输入激活码并点击"兑换"', 
     3, 3, 3, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), TRUE, FALSE, 3, NOW(), NOW());

-- 初始CDK数据
INSERT INTO cdk (activity_id, code, is_claimed, created_at)
VALUES
    -- JetBrains全家桶激活码
    (1, 'JETB-RAIN-S123-4567', FALSE, NOW()),
    (1, 'JETB-RAIN-S234-5678', FALSE, NOW()),
    (1, 'JETB-RAIN-S345-6789', FALSE, NOW()),
    (1, 'JETB-RAIN-S456-7890', FALSE, NOW()),
    (1, 'JETB-RAIN-S567-8901', FALSE, NOW()),
    (1, 'JETB-RAIN-S678-9012', FALSE, NOW()),
    (1, 'JETB-RAIN-S789-0123', FALSE, NOW()),
    (1, 'JETB-RAIN-S890-1234', FALSE, NOW()),
    (1, 'JETB-RAIN-S901-2345', FALSE, NOW()),
    (1, 'JETB-RAIN-S012-3456', FALSE, NOW()),
    
    -- Windows 11专业版激活码
    (2, 'WIN11-PRO-1234-5678-9012', FALSE, NOW()),
    (2, 'WIN11-PRO-2345-6789-0123', FALSE, NOW()),
    (2, 'WIN11-PRO-3456-7890-1234', FALSE, NOW()),
    (2, 'WIN11-PRO-4567-8901-2345', FALSE, NOW()),
    (2, 'WIN11-PRO-5678-9012-3456', FALSE, NOW()),
    
    -- Adobe Creative Cloud会员
    (3, 'ADOBE-CC-1234-5678-9012', FALSE, NOW()),
    (3, 'ADOBE-CC-2345-6789-0123', FALSE, NOW()),
    (3, 'ADOBE-CC-3456-7890-1234', FALSE, NOW());
