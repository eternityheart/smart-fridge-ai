# 智能冰箱菜谱系统 - 部署指南

## 项目信息

| 配置项 | 值 |
|-------|-----|
| **仓库名称** | smart-fridge-ai |
| **GitHub 账号** | eternityheart |
| **仓库地址** | https://github.com/eternityheart/smart-fridge-ai |

---

## 🚀 本地开发部署

### 1. 环境准备

```bash
# 检查 Java 环境
java -version  # 需要 JDK 17+

# 检查 Maven
mvn -version

# 检查 MySQL
mysql -u root -phsp -e "SELECT 1"

# 检查 Python GPU 环境
D:\miniconda3\envs\pytorch-2.6.0-gpu\python.exe -c "import torch; print(torch.cuda.is_available())"
```

### 2. 初始化数据库

```bash
mysql -u root -phsp < e:\antiPro\javaex2\sql\init.sql
```

### 3. 启动后端服务

```bash
cd e:\antiPro\javaex2\smart-fridge-backend
mvn clean install -DskipTests
mvn spring-boot:run -pl fridge-web-api
```

服务启动后访问：
- API 文档: http://localhost:8080/doc.html
- 健康检查: http://localhost:8080/api/health

### 4. 启动视觉服务

```bash
cd e:\antiPro\javaex2\fridge-vision-service

# 安装依赖
D:\miniconda3\envs\pytorch-2.6.0-gpu\python.exe -m pip install -r requirements.txt

# 启动服务
D:\miniconda3\envs\pytorch-2.6.0-gpu\python.exe app.py
```

视觉服务地址: http://localhost:5000/health

---

## 📦 Docker 部署 (生产环境)

### docker-compose.yml

```yaml
version: '3.8'

services:
  # MySQL 数据库
  mysql:
    image: mysql:8.0
    container_name: fridge-mysql
    environment:
      MYSQL_ROOT_PASSWORD: hsp
      MYSQL_DATABASE: smart_fridge
    volumes:
      - ./sql/init.sql:/docker-entrypoint-initdb.d/init.sql
      - mysql-data:/var/lib/mysql
    ports:
      - "3306:3306"
    networks:
      - fridge-network

  # Redis 缓存
  redis:
    image: redis:7-alpine
    container_name: fridge-redis
    ports:
      - "6379:6379"
    networks:
      - fridge-network

  # MinIO 对象存储
  minio:
    image: minio/minio
    container_name: fridge-minio
    command: server /data --console-address ":9001"
    environment:
      MINIO_ROOT_USER: minioadmin
      MINIO_ROOT_PASSWORD: minioadmin
    volumes:
      - minio-data:/data
    ports:
      - "9000:9000"
      - "9001:9001"
    networks:
      - fridge-network

  # Python 视觉服务
  vision-service:
    build:
      context: ./fridge-vision-service
      dockerfile: Dockerfile
    container_name: fridge-vision
    ports:
      - "5000:5000"
    deploy:
      resources:
        reservations:
          devices:
            - driver: nvidia
              count: 1
              capabilities: [gpu]
    networks:
      - fridge-network

  # Spring Boot 后端
  fridge-api:
    build:
      context: ./smart-fridge-backend
      dockerfile: Dockerfile
    container_name: fridge-api
    depends_on:
      - mysql
      - redis
      - minio
      - vision-service
    environment:
      SPRING_PROFILES_ACTIVE: docker
    ports:
      - "8080:8080"
    networks:
      - fridge-network

volumes:
  mysql-data:
  minio-data:

networks:
  fridge-network:
    driver: bridge
```

### Spring Boot Dockerfile

```dockerfile
# smart-fridge-backend/Dockerfile
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/fridge-web-api/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Python 视觉服务 Dockerfile

```dockerfile
# fridge-vision-service/Dockerfile
FROM pytorch/pytorch:2.1.0-cuda11.8-cudnn8-runtime
WORKDIR /app
COPY requirements.txt .
RUN pip install -r requirements.txt
COPY . .
EXPOSE 5000
CMD ["python", "app.py"]
```

### 启动命令

```bash
# 构建并启动所有服务
docker-compose up -d --build

# 查看日志
docker-compose logs -f fridge-api

# 停止服务
docker-compose down
```

---

## 🌐 GitHub 上传

### 1. 创建远程仓库

在 GitHub 上创建新仓库：
- 仓库名：`smart-fridge-ai`
- 描述：智能冰箱食材识别与菜谱推荐系统
- 可见性：Public/Private

### 2. 关联远程仓库

```bash
cd e:\antiPro\javaex2

# 添加远程仓库
git remote add origin https://github.com/eternityheart/smart-fridge-ai.git

# 推送代码
git push -u origin master
git push -u origin develop
```

### 3. 分支策略

```bash
# 创建功能分支
git checkout -b feature/deepseek-integration

# 开发完成后合并
git checkout develop
git merge feature/deepseek-integration

# 发布版本
git checkout master
git merge develop
git tag -a v1.0.0 -m "第一个正式版本"
git push origin v1.0.0
```

---

## 📋 部署检查清单

### 本地开发
- [ ] JDK 17 已安装
- [ ] MySQL 8.0 已启动
- [ ] Redis 已启动 (可选)
- [ ] Python GPU 环境正常
- [ ] 数据库已初始化

### 生产部署
- [ ] Docker 已安装
- [ ] Docker Compose 已安装
- [ ] NVIDIA Docker 已配置 (GPU)
- [ ] 域名/SSL 已配置 (可选)
