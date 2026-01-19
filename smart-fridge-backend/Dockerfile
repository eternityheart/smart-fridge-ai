# ====================================
# Spring Boot 后端 Dockerfile
# ====================================

# 第一阶段：Maven 构建
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY fridge-common/pom.xml ./fridge-common/
COPY fridge-domain/pom.xml ./fridge-domain/
COPY fridge-vision-sdk/pom.xml ./fridge-vision-sdk/
COPY fridge-llm-sdk/pom.xml ./fridge-llm-sdk/
COPY fridge-web-api/pom.xml ./fridge-web-api/

# 先下载依赖 (利用 Docker 缓存)
RUN mvn dependency:go-offline -B

# 复制源代码并构建
COPY . .
RUN mvn clean package -DskipTests

# 第二阶段：运行时镜像
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 安装常用工具
RUN apk add --no-cache curl tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

# 复制构建产物
COPY --from=builder /app/fridge-web-api/target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
    CMD curl -f http://localhost:8080/api/health || exit 1

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]
