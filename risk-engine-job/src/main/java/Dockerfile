FROM openjdk:11-jdk-slim

# 设置工作目录
WORKDIR /app

# 复制你的 Java 服务 JAR 包到容器中
COPY your-service.jar /app/service.jar

# 安装必要工具（如果需要）
RUN apt-get update && apt-get install -y curl netcat

# 运行时的环境变量
ENV MYSQL_HOST=mysql-8.0
ENV REDIS_HOST=redis-6.0
ENV ROCKETMQ_NAMESRV_ADDR=rocketmq-namesrv:9876
ENV ELASTICSEARCH_HOST=elasticsearch-7.17.4

# 暴露应用端口（根据你的服务实际端口修改）
EXPOSE 8080

# 启动前等待依赖服务就绪（可选）
CMD bash -c 'echo "Waiting for dependencies..." && \
    while ! nc -z $MYSQL_HOST 3306; do sleep 1; done && \
    while ! nc -z $REDIS_HOST 6379; do sleep 1; done && \
    while ! nc -z $ROCKETMQ_NAMESRV_ADDR 9876; do sleep 1; done && \
    while ! nc -z $ELASTICSEARCH_HOST 9200; do sleep 1; done && \
    echo "All dependencies are ready! Starting service..." && \
    exec java -jar /app/service.jar'
