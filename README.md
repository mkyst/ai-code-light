# AI Code Light

基于 AI 的下一代代码生成平台，用自然语言构建你的应用。

## 技术栈

**后端**
- Spring Boot 3.3.5
- MyBatis-Plus
- MySQL 8
- Redis
- LangChain4j + 通义千问

**前端**
- Vue 3 (Composition API)
- Vite
- Arco Design
- Pinia

## 功能特性

- 🤖 智能代码生成 - AI 分析需求，自动生成 HTML/CSS/JS
- ⚡ 实时流式输出 - SSE 流式传输，实时看到生成过程
- 🎨 可视化编辑 - 点击元素即时修改
- 🚀 一键云端部署 - 生成完成后一键部署
- 📦 源码下载 - 支持下载完整项目源码
- 👤 用户系统 - 注册登录、个人资料、头像上传
- 🏢 企业级管理 - 完整后台管理系统

## 快速开始

### 环境要求

- JDK 21+
- Node.js 18+
- MySQL 8.0+
- Redis 6.0+

### 后端配置

1. 创建数据库：
```sql
CREATE DATABASE ai_code CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 配置环境变量（或复制 `application.yml.example` 为 `application.yml` 并修改）：
```bash
export DB_USERNAME=root
export DB_PASSWORD=your_password
export JWT_SECRET=your_jwt_secret_at_least_32_chars
export QWEN_API_KEY=sk-your-qwen-api-key
```

3. 启动后端：
```bash
cd backend
mvn spring-boot:run
```

### 前端配置

```bash
cd frontend
npm install
npm run dev
```

访问 http://localhost:5173

## 项目结构

```
ai-code/
├── backend/                 # Spring Boot 后端
│   ├── src/main/java/
│   │   └── com/aicode/
│   │       ├── controller/  # REST API
│   │       ├── service/     # 业务逻辑
│   │       ├── entity/      # 实体类
│   │       ├── mapper/      # MyBatis Mapper
│   │       ├── ai/          # AI 代码生成
│   │       └── config/      # 配置类
│   └── src/main/resources/
│       └── application.yml  # 配置文件
├── frontend/                # Vue 3 前端
│   ├── src/
│   │   ├── views/          # 页面组件
│   │   ├── api/            # API 调用
│   │   ├── stores/         # Pinia 状态管理
│   │   └── router/         # 路由配置
│   └── package.json
└── docs/                    # 文档
```

## API 文档

启动后端后访问：http://localhost:8080/doc.html

## License

MIT
