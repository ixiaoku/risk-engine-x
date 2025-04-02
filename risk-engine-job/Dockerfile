# 使用更轻量化的基础镜像
FROM openjdk:11-jre-slim

# 设置工作目录
WORKDIR /app

# 复制 JAR 文件
COPY target/risk-engine-job-*.jar /app/risk-engine-job.jar

# 设置环境变量，默认使用 `pro` 配置
ENV SPRING_PROFILES_ACTIVE=pro

# 暴露端口
EXPOSE 8089

# 启动命令
CMD ["java", "-jar", "/app/risk-engine-job.jar"]