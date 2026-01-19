# 智能冰箱菜谱系统

基于 AI 的智能冰箱食材识别与菜谱推荐系统。

## 项目结构

```
javaex2/
├── smart-fridge-backend/      # Spring Boot 后端
│   ├── fridge-common/         # 公共模块
│   ├── fridge-domain/         # 领域实体
│   ├── fridge-vision-sdk/     # 视觉服务适配器
│   ├── fridge-llm-sdk/        # DeepSeek 适配器
│   └── fridge-web-api/        # API 控制器
├── fridge-vision-service/     # Python 视觉服务
├── sql/                       # 数据库脚本
└── PROJECT_CONFIG.md          # 项目配置
```

## 快速开始

### 1. 初始化数据库

```bash
mysql -u root -phsp < sql/init.sql
```

### 2. 启动后端服务

```bash
cd smart-fridge-backend
mvn spring-boot:run -pl fridge-web-api
```

### 3. 启动视觉服务

```bash
cd fridge-vision-service
python app.py
```

### 4. 访问

- API 文档: http://localhost:8080/doc.html
- 健康检查: http://localhost:8080/api/health
- 视觉服务: http://localhost:5000/health

## 技术栈

- **后端**: Spring Boot 3.2, MyBatis-Plus, Redis
- **AI**: YOLOv8, DeepSeek API
- **数据库**: MySQL 8.0
