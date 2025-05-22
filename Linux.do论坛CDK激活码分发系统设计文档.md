# Linux.do论坛CDK激活码分发系统设计文档

## 1. 项目概述

### 1.1 项目背景

Linux.do论坛上经常有用户分享各种软件的CDK或激活码，但这些福利经常被商人快速抢走并转卖，导致普通用户无法获得。本系统旨在通过用户信任等级控制和一人一码的限制，确保福利能够公平地分发给真正的社区用户。

### 1.2 项目目标

- 提供一个CDK/激活码分发平台，防止商人抢占资源
- 支持批量导入和发布CDK/激活码
- 基于Linux.do论坛的用户信任等级控制领取权限
- 确保每个用户只能领取一个CDK
- 提供良好的用户体验和管理界面

### 1.3 系统架构

- **前端**：HTML/CSS/JavaScript
- **后端**：Java (Spring Boot)
- **数据库**：MySQL
- **认证**：Linux.do OAuth2

## 2. 功能需求

### 2.1 用户角色

- **管理员**：系统管理员，拥有所有权限
- **发布者**：可以发布CDK/激活码的用户
- **普通用户**：根据信任等级领取CDK/激活码的用户

### 2.2 核心功能

#### 2.2.1 用户认证

- 使用Linux.do OAuth2进行用户认证
- 获取用户的信任等级（1级、2级、3级）
- 保存用户基本信息和信任等级

#### 2.2.2 CDK发布

- 支持单个或批量导入CDK/激活码
- 自动识别和分割多个CDK
- 设置CDK的有效期
- 设置可领取的用户信任等级（1级、2级、3级）
- 设置CDK的描述信息和使用说明
- 可选择是否公开显示

#### 2.2.3 CDK领取

- 根据用户信任等级显示可领取的CDK列表
- 每个用户对每个CDK活动只能领取一次
- 领取后显示CDK内容和使用说明
- 支持复制CDK到剪贴板

#### 2.2.4 管理功能

- CDK发布管理（编辑、删除、查看领取情况）
- 用户领取记录查询
- 系统数据统计（发布数量、领取数量等）

## 3. 数据库设计

### 3.1 用户表（user）

| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | BIGINT | 主键，自增 |
| linux_do_id | VARCHAR(50) | Linux.do用户ID |
| username | VARCHAR(50) | 用户名 |
| avatar | VARCHAR(255) | 头像URL |
| trust_level | INT | 信任等级（1-3） |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### 3.2 CDK活动表（cdk_activity）

| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | BIGINT | 主键，自增 |
| title | VARCHAR(100) | 活动标题 |
| description | TEXT | 活动描述 |
| usage_guide | TEXT | 使用说明 |
| min_trust_level | INT | 最低信任等级要求（1-3） |
| total_count | INT | CDK总数量 |
| remaining_count | INT | 剩余数量 |
| start_time | DATETIME | 开始时间 |
| end_time | DATETIME | 结束时间 |
| is_active | BOOLEAN | 是否激活 |
| is_public | BOOLEAN | 是否公开 |
| creator_id | BIGINT | 创建者ID（关联user表） |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### 3.3 CDK表（cdk）

| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | BIGINT | 主键，自增 |
| activity_id | BIGINT | 关联的活动ID |
| code | VARCHAR(255) | CDK码 |
| is_claimed | BOOLEAN | 是否已被领取 |
| claimed_by | BIGINT | 领取者ID（关联user表） |
| claimed_at | DATETIME | 领取时间 |
| created_at | DATETIME | 创建时间 |

### 3.4 领取记录表（claim_record）

| 字段名 | 类型 | 描述 |
|--------|------|------|
| id | BIGINT | 主键，自增 |
| user_id | BIGINT | 用户ID |
| activity_id | BIGINT | 活动ID |
| cdk_id | BIGINT | CDK ID |
| claimed_at | DATETIME | 领取时间 |
| ip_address | VARCHAR(50) | 领取IP地址 |

## 4. API设计

### 4.1 认证API

#### 4.1.1 OAuth2回调接口

```
GET /api/auth/callback
```

参数：
- code: OAuth2授权码

响应：
- 成功：重定向到首页，并设置JWT token
- 失败：重定向到错误页面

### 4.2 CDK管理API

#### 4.2.1 创建CDK活动

```
POST /api/activities
```

请求体：
```json
{
  "title": "活动标题",
  "description": "活动描述",
  "usageGuide": "使用说明",
  "minTrustLevel": 1,
  "startTime": "2023-06-01T00:00:00Z",
  "endTime": "2023-06-30T23:59:59Z",
  "isPublic": true,
  "cdkList": ["CDK1", "CDK2", "CDK3"]
}
```

#### 4.2.2 获取CDK活动列表

```
GET /api/activities
```

参数：
- page: 页码
- size: 每页数量
- creatorId: 创建者ID（可选）
- isActive: 是否激活（可选）

#### 4.2.3 获取CDK活动详情

```
GET /api/activities/{id}
```

#### 4.2.4 更新CDK活动

```
PUT /api/activities/{id}
```

#### 4.2.5 删除CDK活动

```
DELETE /api/activities/{id}
```

### 4.3 CDK领取API

#### 4.3.1 获取可领取的CDK活动列表

```
GET /api/activities/available
```

#### 4.3.2 领取CDK

```
POST /api/activities/{id}/claim
```

#### 4.3.3 获取用户领取记录

```
GET /api/users/me/claims
```

## 5. OAuth2认证流程

### 5.1 认证流程

1. 用户点击"使用Linux.do登录"按钮
2. 前端跳转到Linux.do OAuth2授权页面：
   ```
   https://connect.linux.do/oauth2/authorize?client_id=9OI9dizi1Hbzx665WnkkUEJpgxl7aJGL&scope=openid&response_type=code&redirect_uri={回调地址}
   ```
3. 用户在Linux.do完成登录授权
4. Linux.do重定向回系统的回调地址，并附带授权码（code）
5. 后端接收授权码，向Linux.do请求访问令牌
6. 使用访问令牌获取用户信息和信任等级
7. 创建或更新用户记录，生成JWT令牌
8. 返回JWT令牌给前端，完成登录

### 5.2 回调处理代码示例

```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/callback")
    public ResponseEntity<Void> callback(@RequestParam("code") String code, HttpServletResponse response) {
        try {
            String token = authService.handleOAuthCallback(code);
            // 设置Cookie
            Cookie cookie = new Cookie("token", token);
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(86400); // 1天
            response.addCookie(cookie);

            // 重定向到首页
            response.sendRedirect("/");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            // 处理错误
            response.sendRedirect("/error?message=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
```

## 6. 用户界面设计

### 6.1 页面结构

- **首页**：展示可领取的CDK活动列表
- **活动详情页**：显示活动详情和领取按钮
- **我的CDK页**：显示用户已领取的CDK
- **发布CDK页**：用于创建新的CDK活动
- **管理页面**：管理已发布的CDK活动

### 6.2 UI设计原型

#### 6.2.1 首页

![首页设计](https://placeholder-image.com/home-page)

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Linux.do CDK分发平台 - 首页</title>
    <link rel="stylesheet" href="/css/main.css">
</head>
<body>
    <header>
        <div class="container">
            <div class="logo">
                <img src="/images/logo.png" alt="Linux.do CDK分发平台">
                <h1>Linux.do CDK分发平台</h1>
            </div>
            <nav>
                <ul>
                    <li class="active"><a href="/">首页</a></li>
                    <li><a href="/my-cdks">我的CDK</a></li>
                    <li><a href="/publish">发布CDK</a></li>
                </ul>
            </nav>
            <div class="user-area">
                <button class="login-btn">使用Linux.do登录</button>
                <!-- 登录后显示 -->
                <!-- <div class="user-info">
                    <img src="/images/avatar.png" alt="用户头像">
                    <span>用户名</span>
                    <span class="trust-level">信任等级: 2</span>
                </div> -->
            </div>
        </div>
    </header>

    <main>
        <div class="container">
            <section class="banner">
                <h2>Linux.do社区CDK分发平台</h2>
                <p>公平、透明的CDK分发系统，基于用户信任等级控制领取权限</p>
            </section>

            <section class="cdk-list">
                <h3>可领取的CDK</h3>
                <div class="filter">
                    <select>
                        <option value="all">全部</option>
                        <option value="available">可领取</option>
                        <option value="claimed">已领取</option>
                    </select>
                </div>

                <div class="cdk-cards">
                    <!-- CDK卡片示例 -->
                    <div class="cdk-card">
                        <div class="card-header">
                            <h4>JetBrains全家桶激活码</h4>
                            <span class="trust-badge">信任等级: 2+</span>
                        </div>
                        <div class="card-body">
                            <p>JetBrains全系列IDE一年激活码，包括IntelliJ IDEA、PyCharm、WebStorm等</p>
                            <div class="card-meta">
                                <span>剩余: 42/100</span>
                                <span>有效期至: 2023-12-31</span>
                            </div>
                        </div>
                        <div class="card-footer">
                            <a href="/activity/1" class="btn">查看详情</a>
                        </div>
                    </div>
                    <!-- 更多CDK卡片 -->
                </div>

                <div class="pagination">
                    <a href="#" class="prev">上一页</a>
                    <span class="page-num">1/5</span>
                    <a href="#" class="next">下一页</a>
                </div>
            </section>
        </div>
    </main>

    <footer>
        <div class="container">
            <p>&copy; 2023 Linux.do CDK分发平台 | <a href="https://linux.do" target="_blank">Linux.do社区</a></p>
        </div>
    </footer>

    <script src="/js/main.js"></script>
</body>
</html>
```

#### 6.2.2 活动详情页

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>JetBrains全家桶激活码 - Linux.do CDK分发平台</title>
    <link rel="stylesheet" href="/css/main.css">
</head>
<body>
    <header>
        <!-- 同首页 -->
    </header>

    <main>
        <div class="container">
            <div class="breadcrumb">
                <a href="/">首页</a> &gt; <span>JetBrains全家桶激活码</span>
            </div>

            <section class="activity-detail">
                <div class="activity-header">
                    <h2>JetBrains全家桶激活码</h2>
                    <span class="trust-badge">信任等级: 2+</span>
                </div>

                <div class="activity-info">
                    <div class="info-item">
                        <span class="label">发布者:</span>
                        <span class="value">Linux爱好者</span>
                    </div>
                    <div class="info-item">
                        <span class="label">发布时间:</span>
                        <span class="value">2023-06-01 10:00</span>
                    </div>
                    <div class="info-item">
                        <span class="label">有效期至:</span>
                        <span class="value">2023-12-31</span>
                    </div>
                    <div class="info-item">
                        <span class="label">剩余数量:</span>
                        <span class="value">42/100</span>
                    </div>
                </div>

                <div class="activity-description">
                    <h3>活动描述</h3>
                    <div class="content">
                        <p>JetBrains全系列IDE一年激活码，包括IntelliJ IDEA、PyCharm、WebStorm等。</p>
                        <p>这些激活码由JetBrains官方提供，可用于激活所有JetBrains产品一年时间。</p>
                    </div>
                </div>

                <div class="usage-guide">
                    <h3>使用说明</h3>
                    <div class="content">
                        <ol>
                            <li>打开任意JetBrains IDE</li>
                            <li>点击菜单 Help -> Register</li>
                            <li>选择 "Activation code"</li>
                            <li>粘贴激活码并点击激活</li>
                        </ol>
                    </div>
                </div>

                <div class="claim-area">
                    <!-- 未登录状态 -->
                    <div class="not-logged-in">
                        <p>请先使用Linux.do账号登录后领取</p>
                        <button class="login-btn">使用Linux.do登录</button>
                    </div>

                    <!-- 已登录但信任等级不足 -->
                    <!-- <div class="insufficient-trust">
                        <p>您当前的信任等级为 1，需要信任等级 2+ 才能领取</p>
                        <a href="https://linux.do/t/topic/12345" target="_blank" class="btn secondary">如何提升信任等级?</a>
                    </div> -->

                    <!-- 已登录且可领取 -->
                    <!-- <div class="can-claim">
                        <p>您当前的信任等级为 2，符合领取条件</p>
                        <button class="claim-btn">领取CDK</button>
                    </div> -->

                    <!-- 已领取 -->
                    <!-- <div class="claimed">
                        <p>您已成功领取此CDK</p>
                        <div class="cdk-display">
                            <input type="text" value="ABCD-EFGH-IJKL-MNOP" readonly>
                            <button class="copy-btn">复制</button>
                        </div>
                    </div> -->
                </div>
            </section>
        </div>
    </main>

    <footer>
        <!-- 同首页 -->
    </footer>

    <script src="/js/main.js"></script>
</body>
</html>
```

#### 6.2.3 发布CDK页面

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>发布CDK - Linux.do CDK分发平台</title>
    <link rel="stylesheet" href="/css/main.css">
</head>
<body>
    <header>
        <!-- 同首页 -->
    </header>

    <main>
        <div class="container">
            <div class="breadcrumb">
                <a href="/">首页</a> &gt; <span>发布CDK</span>
            </div>

            <section class="publish-form">
                <h2>发布新的CDK活动</h2>

                <form id="cdkPublishForm">
                    <div class="form-group">
                        <label for="title">活动标题</label>
                        <input type="text" id="title" name="title" required placeholder="例如：JetBrains全家桶激活码">
                    </div>

                    <div class="form-group">
                        <label for="description">活动描述</label>
                        <textarea id="description" name="description" rows="4" required placeholder="详细描述此CDK的用途和来源"></textarea>
                    </div>

                    <div class="form-group">
                        <label for="usageGuide">使用说明</label>
                        <textarea id="usageGuide" name="usageGuide" rows="4" required placeholder="详细说明如何使用此CDK"></textarea>
                    </div>

                    <div class="form-row">
                        <div class="form-group half">
                            <label for="startTime">开始时间</label>
                            <input type="datetime-local" id="startTime" name="startTime" required>
                        </div>

                        <div class="form-group half">
                            <label for="endTime">结束时间</label>
                            <input type="datetime-local" id="endTime" name="endTime" required>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="minTrustLevel">最低信任等级要求</label>
                        <select id="minTrustLevel" name="minTrustLevel" required>
                            <option value="1">1级 (所有用户)</option>
                            <option value="2">2级 (仅2级及以上用户)</option>
                            <option value="3">3级 (仅3级用户)</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="isPublic">是否公开</label>
                        <div class="radio-group">
                            <label>
                                <input type="radio" name="isPublic" value="true" checked> 是
                            </label>
                            <label>
                                <input type="radio" name="isPublic" value="false"> 否
                            </label>
                        </div>
                        <p class="form-hint">公开的活动会显示在首页，非公开活动需要通过链接访问</p>
                    </div>

                    <div class="form-group">
                        <label for="cdkList">CDK列表</label>
                        <textarea id="cdkList" name="cdkList" rows="10" required placeholder="每行输入一个CDK，系统会自动识别和分割"></textarea>
                        <p class="form-hint">也可以粘贴包含多个CDK的文本，系统会自动识别和分割</p>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn primary">发布CDK</button>
                        <button type="button" class="btn secondary">保存为草稿</button>
                    </div>
                </form>
            </section>
        </div>
    </main>

    <footer>
        <!-- 同首页 -->
    </footer>

    <script src="/js/main.js"></script>
</body>
</html>
```

## 7. 系统实现关键点

### 7.1 CDK批量识别与分割

系统需要能够智能识别和分割用户输入的CDK，支持多种格式：

```java
/**
 * CDK批量识别与分割工具类
 */
public class CdkParser {

    /**
     * 解析CDK文本，识别并分割成单个CDK列表
     *
     * @param cdkText CDK文本内容
     * @return 分割后的CDK列表
     */
    public static List<String> parseCdks(String cdkText) {
        if (StringUtils.isBlank(cdkText)) {
            return Collections.emptyList();
        }

        // 移除所有不可见字符
        cdkText = cdkText.replaceAll("\\p{C}", " ").trim();

        // 尝试按不同分隔符分割
        List<String> cdks = new ArrayList<>();

        // 1. 首先按行分割
        String[] lines = cdkText.split("\\r?\\n");

        for (String line : lines) {
            line = line.trim();
            if (StringUtils.isBlank(line)) {
                continue;
            }

            // 2. 检查行内是否包含常见分隔符
            if (line.contains(",") || line.contains(";") || line.contains("，") || line.contains("；")) {
                // 按分隔符再次分割
                String[] parts = line.split("[,;，；]");
                for (String part : parts) {
                    part = part.trim();
                    if (StringUtils.isNotBlank(part)) {
                        cdks.add(part);
                    }
                }
            } else {
                // 没有分隔符，整行作为一个CDK
                cdks.add(line);
            }
        }

        // 3. 过滤和清理
        return cdks.stream()
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .distinct() // 去重
                .collect(Collectors.toList());
    }
}
```

### 7.2 信任等级控制

根据Linux.do的OAuth2返回的用户信任等级，控制用户对CDK的访问权限：

```java
/**
 * 信任等级检查服务
 */
@Service
public class TrustLevelService {

    /**
     * 检查用户是否有权限领取指定活动的CDK
     *
     * @param user 用户信息
     * @param activity CDK活动
     * @return 是否有权限领取
     */
    public boolean canUserClaimCdk(User user, CdkActivity activity) {
        // 检查用户信任等级是否满足要求
        return user.getTrustLevel() >= activity.getMinTrustLevel();
    }

    /**
     * 获取用户可见的活动列表
     *
     * @param user 用户信息
     * @param allActivities 所有活动
     * @return 用户可见的活动列表
     */
    public List<CdkActivity> getVisibleActivities(User user, List<CdkActivity> allActivities) {
        return allActivities.stream()
                .filter(activity -> activity.isActive() && activity.isPublic())
                .filter(activity -> user.getTrustLevel() >= activity.getMinTrustLevel())
                .collect(Collectors.toList());
    }
}
```

### 7.3 OAuth2认证实现

实现Linux.do的OAuth2认证流程，获取用户信息和信任等级：

```java
/**
 * OAuth2认证服务
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Value("${oauth2.linux-do.client-id}")
    private String clientId;

    @Value("${oauth2.linux-do.client-secret}")
    private String clientSecret;

    @Value("${oauth2.linux-do.redirect-uri}")
    private String redirectUri;

    @Value("${oauth2.linux-do.token-url}")
    private String tokenUrl;

    @Value("${oauth2.linux-do.user-info-url}")
    private String userInfoUrl;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public String handleOAuthCallback(String code) throws Exception {
        // 1. 获取访问令牌
        String accessToken = getAccessToken(code);

        // 2. 获取用户信息
        UserInfo userInfo = getUserInfo(accessToken);

        // 3. 创建或更新用户
        User user = createOrUpdateUser(userInfo);

        // 4. 生成JWT令牌
        return jwtTokenProvider.generateToken(user);
    }

    private String getAccessToken(String code) throws Exception {
        // 实现获取访问令牌的逻辑
        // ...
    }

    private UserInfo getUserInfo(String accessToken) throws Exception {
        // 实现获取用户信息的逻辑
        // ...
    }

    private User createOrUpdateUser(UserInfo userInfo) {
        // 实现创建或更新用户的逻辑
        // ...
    }
}
```

## 8. 安全性考虑

### 8.1 防止CDK批量抢占

为防止恶意用户通过脚本批量抢占CDK，系统实现以下安全措施：

1. **IP限制**：同一IP短时间内的请求次数限制
2. **验证码**：领取CDK时需要完成验证码
3. **账号冷却期**：同一用户领取CDK后需要等待一定时间才能领取下一个
4. **信任等级要求**：只有达到一定信任等级的用户才能领取

### 8.2 数据安全

1. **CDK加密存储**：CDK在数据库中加密存储
2. **HTTPS传输**：所有API请求使用HTTPS加密传输
3. **JWT令牌安全**：使用安全的JWT签名算法，设置合理的过期时间

## 9. 部署与运维

### 9.1 系统部署架构

```
                    ┌─────────────┐
                    │   Nginx     │
                    │  (反向代理)  │
                    └──────┬──────┘
                           │
                           ▼
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  CDN        │◄────┤  Spring Boot │◄────┤  MySQL      │
│ (静态资源)   │     │  应用服务器   │     │  数据库     │
└─────────────┘     └─────────────┘     └─────────────┘
                           │
                           ▼
                    ┌─────────────┐
                    │  Redis      │
                    │ (缓存/限流)  │
                    └─────────────┘
```

### 9.2 性能优化

1. **CDK活动列表缓存**：使用Redis缓存热门活动列表
2. **数据库索引优化**：为常用查询字段创建索引
3. **静态资源CDN**：使用CDN加速静态资源加载
4. **分页查询**：大数据量查询使用分页技术

## 10. 总结

Linux.do论坛CDK激活码分发系统通过用户信任等级控制和一人一码的限制，有效解决了福利CDK被商人抢占的问题。系统具有以下特点：

1. **公平分发**：基于用户信任等级的权限控制
2. **易用性**：简洁直观的用户界面，支持批量导入CDK
3. **安全性**：多重安全措施防止CDK被批量抢占
4. **可扩展性**：良好的系统架构设计，支持未来功能扩展

通过本系统，Linux.do论坛的用户可以更公平地获取社区福利，提升社区活跃度和用户满意度。