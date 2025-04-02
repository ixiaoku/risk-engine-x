FROM openjdk:11-jdk-slim

WORKDIR /app

COPY target/risk-engine-job.jar /app/service.jar

RUN apt-get update && apt-get install -y curl netcat

ENV MYSQL_HOST=mysql-8.0
ENV REDIS_HOST=redis-6.0
ENV ROCKETMQ_NAMESRV_ADDR=rocketmq-namesrv:9876
ENV ELASTICSEARCH_HOST=elasticsearch-7.17.4

EXPOSE 8088

CMD bash -c 'echo "Waiting for dependencies..." && \
    while ! nc -z $MYSQL_HOST 3306; do sleep 1; done && \
    while ! nc -z $REDIS_HOST 6379; do sleep 1; done && \
    while ! nc -z $ROCKETMQ_NAMESRV_ADDR 9876; do sleep 1; done && \
    while ! nc -z $ELASTICSEARCH_HOST 9200; do sleep 1; done && \
    echo "All dependencies are ready! Starting service..." && \
    exec java -jar /app/service.jar'