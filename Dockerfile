FROM tomcat:7-jre8

ARG ZAFIRA_SERVICE_VERSION=1.0
ARG ZAFIRA_CLIENT_VERSION=1.0

ENV ZAFIRA_URL=http://localhost:8080
ENV ZAFIRA_USER=admin
ENV ZAFIRA_PASS=qaprosoft
ENV ZAFIRA_JDBC_URL=jdbc:postgresql://localhost:5432/postgres
ENV ZAFIRA_JDBC_USER=postgres
ENV ZAFIRA_JDBC_PASS=postgres

RUN apt-get update && apt-get install zip

COPY sources/zafira-ws/target/zafira-ws.war ${CATALINA_HOME}/temp/zafira-ws.war
COPY sources/zafira-web/target/zafira.war ${CATALINA_HOME}/temp/zafira.war
COPY entrypoint.sh /

EXPOSE 8080

ENTRYPOINT /entrypoint.sh
