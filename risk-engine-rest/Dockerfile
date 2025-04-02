FROM openjdk:11-jdk-slim

WORKDIR /app

# 复制 JAR 文件，确保只有一个 JAR 文件
COPY target/risk-engine-rest-*.jar /app/risk-engine-rest.jar

# 安装 curl 和 netcat
RUN apt-get update && apt-get install -y curl netcat-traditional

# 设置环境变量
ENV SPRING_PROFILES_ACTIVE=pro
ENV MYSQL_HOST=mysql-8.0
ENV REDIS_HOST=redis-6.0
ENV ROCKETMQ_NAMESRV_ADDR=rocketmq-namesrv:9876
ENV ELASTICSEARCH_HOST=elasticsearch-7.17.4

EXPOSE 8088

# 启动命令，修正 nc 用法
CMD bash -c 'echo "Waiting for dependencies..." && \
    while ! nc -z $MYSQL_HOST 3306; do echo "Waiting for $MYSQL_HOST:3306..."; sleep 1; done && \
    while ! nc -z $REDIS_HOST 6379; do echo "Waiting for $REDIS_HOST:6379..."; sleep 1; done && \
    while ! nc -z $(echo $ROCKETMQ_NAMESRV_ADDR | cut -d: -f1) $(echo $ROCKETMQ_NAMESRV_ADDR | cut -d: -f2); do echo "Waiting for $ROCKETMQ_NAMESRV_ADDR..."; sleep 1; done && \
    while ! nc -z $ELASTICSEARCH_HOST 9200; do echo "Waiting for $ELASTICSEARCH_HOST:9200..."; sleep 1; done && \
    echo "All dependencies are ready! Starting service..." && \
    exec java -jar /app/service.jar'