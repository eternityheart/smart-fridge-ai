# 🧊 冰箱食材智能菜谱系统 - 项目总览

## 项目信息

| 属性 | 值 |
|-----|-----|
| **项目名称** | Smart Fridge Recipe System (智能冰箱菜谱系统) |
| **创建日期** | 2026-01-19 |
| **项目路径** | `e:\antiPro\javaex2` |
| **开发模式** | 敏捷开发 + 领域驱动设计 (DDD) |

---

## 🔧 环境配置

### Python 环境 (视觉服务)

| 配置项 | 值 |
|-------|-----|
| Python 解释器 | `D:\miniconda3\envs\pytorch-2.6.0-gpu\python.exe` |
| Python 版本 | 3.12 |
| PyTorch 版本 | 2.6.0 (GPU) |
| CUDA 版本 | 11.8 |
| GPU | RTX 2060 |

### Java 环境 (后端服务)

| 配置项 | 值 |
|-------|-----|
| JDK | 17 (LTS) |
| Spring Boot | 3.2.1 |
| 构建工具 | Maven |

### 数据库配置

| 服务 | 配置 |
|-----|------|
| **MySQL** | `localhost:3306` |
| 数据库名 | `smart_fridge` |
| 用户名 | `root` |
| 密码 | `hsp` |
| **Redis** | `localhost:6379` |
| 数据库 | `0` |

### API 密钥

| 服务 | Key |
|-----|-----|
| **DeepSeek API** | `sk-a14e811472d4472baa0e014f35042091` |
| Base URL | `https://api.deepseek.com/v1` |

### 对象存储 (MinIO)

| 配置项 | 值 |
|-------|-----|
| Endpoint | `http://localhost:9000` |
| Access Key | `minioadmin` |
| Secret Key | `minioadmin` |
| Bucket | `fridge-images` |

---

## 📁 项目结构

```
e:\antiPro\javaex2\
├── smart-fridge-backend/          # Spring Boot 后端
│   ├── fridge-common/             # 公共模块
│   ├── fridge-domain/             # 领域实体
│   ├── fridge-security/           # JWT 安全模块
│   ├── fridge-cache/              # 多级缓存
│   ├── fridge-vision-sdk/         # 视觉服务适配器
│   ├── fridge-llm-sdk/            # DeepSeek 适配器
│   └── fridge-web-api/            # API 控制器
│
├── fridge-vision-service/         # Python 视觉服务
│   ├── app.py                     # Flask 主服务
│   ├── models/                    # YOLO 模型
│   └── training/                  # 训练脚本
│
├── sql/                           # 数据库脚本
│   └── init.sql
│
├── docker-compose.yml             # 容器编排
│
└── .agent/workflows/              # Agent Skills
    └── smart-fridge-dev.md
```

---

## 🔀 Git 分支策略

```
main (生产发布)
  └── develop (开发主线)
        ├── feature/vision-service
        ├── feature/deepseek-llm
        ├── feature/security
        └── feature/cache
```

---

## 🎯 核心目标

1. **视觉识别准确率** ≥ 70% (mAP@0.5)
2. **API 响应时间** < 3s (包含 AI 推理)
3. **系统可用性** > 99%

---

## 📋 相关文档

| 文档 | 路径 |
|-----|------|
| 任务清单 | [task.md](file:///C:/Users/明鑫/.gemini/antigravity/brain/3cea6bf4-a129-464f-90f9-f657a89aa0e0/task.md) |
| 实施计划 | [implementation_plan.md](file:///C:/Users/明鑫/.gemini/antigravity/brain/3cea6bf4-a129-464f-90f9-f657a89aa0e0/implementation_plan.md) |
| AI 准确率策略 | [ai_accuracy_strategy.md](file:///C:/Users/明鑫/.gemini/antigravity/brain/3cea6bf4-a129-464f-90f9-f657a89aa0e0/ai_accuracy_strategy.md) |
| 前后端防护 | [integration_safeguards.md](file:///C:/Users/明鑫/.gemini/antigravity/brain/3cea6bf4-a129-464f-90f9-f657a89aa0e0/integration_safeguards.md) |
