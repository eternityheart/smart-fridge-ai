---
description: 智能冰箱菜谱系统开发流程 - Spring Boot + Python AI + DeepSeek LLM
---

# Smart Fridge Recipe System Development Skill

本 Skill 指导如何从零开始开发一个**智能冰箱食材识别与菜谱推荐系统**。

## 项目背景

用户打开冰箱门拍照 → 系统识别食材 → 调用 DeepSeek 生成菜谱 → 推荐购物清单。

---

## 第一步：确认环境配置

// turbo
1. 检查 Python GPU 环境
```bash
D:\miniconda3\envs\pytorch-2.6.0-gpu\python.exe -c "import torch; print(torch.cuda.is_available())"
```

// turbo
2. 检查 Java 环境
```bash
java -version
mvn -version
```

// turbo
3. 检查数据库服务
```bash
# MySQL
mysql -u root -phsp -e "SELECT 1"
# Redis
redis-cli ping
```

---

## 第二步：初始化项目结构

// turbo
4. 创建 Maven 多模块项目
```bash
cd e:\antiPro\javaex2
mkdir smart-fridge-backend
cd smart-fridge-backend
```

5. 创建父 POM，包含以下模块：
   - `fridge-common` - 公共工具 (Result, Exception, JWT)
   - `fridge-domain` - 领域实体 (User, Inventory, ShoppingList)
   - `fridge-security` - Spring Security + JWT 认证
   - `fridge-cache` - Caffeine + Redis 多级缓存
   - `fridge-vision-sdk` - 视觉服务 HTTP 客户端
   - `fridge-llm-sdk` - DeepSeek API 封装
   - `fridge-web-api` - Controller + Service 层

---

## 第三步：数据库初始化

6. 创建数据库和表
```sql
CREATE DATABASE IF NOT EXISTS smart_fridge;
USE smart_fridge;

CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    preferences JSON,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE inventory (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    ingredient_name VARCHAR(100) NOT NULL,
    quantity DECIMAL(10,2),
    unit VARCHAR(20),
    expire_date DATE,
    entry_source ENUM('PHOTO', 'MANUAL') DEFAULT 'PHOTO',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE shopping_list (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    ingredient_name VARCHAR(100) NOT NULL,
    quantity DECIMAL(10,2),
    unit VARCHAR(20),
    purchased BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id)
);
```

---

## 第四步：配置文件模板

7. 创建 `application-dev.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/smart_fridge?useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: hsp
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  data:
    redis:
      host: localhost
      port: 6379
      database: 0

deepseek:
  api:
    key: sk-a14e811472d4472baa0e014f35042091
    base-url: https://api.deepseek.com/v1

vision:
  service:
    url: http://localhost:5000
    timeout: 30000
```

---

## 第五步：创建 Python 视觉服务

// turbo
8. 初始化 Python 项目
```bash
cd e:\antiPro\javaex2
mkdir fridge-vision-service
cd fridge-vision-service
```

9. 创建 `requirements.txt`:
```
flask==3.0.0
ultralytics==8.1.0
paddleocr==2.7.0
pyzbar==0.1.9
opencv-python==4.9.0.80
pillow==10.2.0
```

// turbo
10. 安装依赖
```bash
D:\miniconda3\envs\pytorch-2.6.0-gpu\python.exe -m pip install -r requirements.txt
```

11. 创建 `app.py` Flask 服务

---

## 第六步：前后端防护清单

在编写代码时，必须遵守以下规则：

### 后端必做
- [ ] 配置全局 CORS
- [ ] 统一响应格式 `Result<T>`
- [ ] 全局异常处理 `@RestControllerAdvice`
- [ ] 提供 `/api/health` 健康检查端点
- [ ] API 路径统一 `/api` 前缀

### 前端必做
- [ ] 加载状态 (loading / error / empty 三态)
- [ ] 统一 axios 拦截器
- [ ] 路由兜底 404 页面
- [ ] 环境变量配置 API 地址

---

## 第七步：AI 准确率保障

### 数据集来源
1. Kaggle: Refrigerator Contents (7类, 1050张)
2. Roboflow: aicook dataset (20+类, 3050张)
3. UEC FOOD 100 (100类, 14000张)

### 训练策略
1. 使用 YOLOv8m 预训练权重
2. 冻结前 10 层，训练 150 epochs
3. 数据增强：RandomBrightnessContrast, GaussianBlur
4. 多信号融合：YOLO + OCR + 条码

### 目标指标
- mAP@0.5 ≥ 70%
- Precision ≥ 75%
- Recall ≥ 65%

---

## 第八步：验证与部署

// turbo
12. 启动后端
```bash
cd e:\antiPro\javaex2\smart-fridge-backend
mvn spring-boot:run -pl fridge-web-api
```

// turbo
13. 启动视觉服务
```bash
cd e:\antiPro\javaex2\fridge-vision-service
D:\miniconda3\envs\pytorch-2.6.0-gpu\python.exe app.py
```

14. 验证健康检查
```bash
curl http://localhost:8080/api/health
curl http://localhost:5000/health
```

---

## Git 工作流

// turbo-all
```bash
# 初始化仓库
git init
git add .
git commit -m "feat: initial project structure"

# 创建开发分支
git checkout -b develop

# 功能分支开发
git checkout -b feature/vision-service
# ... 开发完成后
git checkout develop
git merge feature/vision-service
```

---

## 常见问题排查

| 问题 | 解决方案 |
|-----|---------|
| CORS 报错 | 检查后端 CorsConfig |
| 404 API 不存在 | 检查 @RequestMapping 路径 |
| 连接拒绝 | 检查 MySQL/Redis 是否启动 |
| CUDA 不可用 | 检查 PyTorch 版本和驱动 |
| 模型加载慢 | 首次加载会下载权重，耐心等待 |
