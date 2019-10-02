FROM openjdk:11-jdk-slim

ARG version=1.0-SNAPSHOT
ARG SERVICE_VER=1.0-SNAPSHOT
ARG CLIENT_VER=1.0-SNAPSHOT

ENV ZAFIRA_VERSION=${SERVICE_VER} \
    ZAFIRA_CLIENT_VERSION=${CLIENT_VER}

RUN mkdir /opt/assets

COPY ./sources/zafira-ws/build/libs/zafira-ws-${version}.jar /app/zafira-service.jar

CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8002", "-jar", "/app/zafira-service.jar"]

EXPOSE 8080
