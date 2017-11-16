FROM tomcat:7-jre8

ARG SERVICE_VER
ARG CLIENT_VER

ENV ZAFIRA_SERVICE_VERSION=${SERVICE_VER}
ENV ZAFIRA_CLIENT_VERSION=${CLIENT_VER}
ENV ZAFIRA_URL=http://localhost:8080
ENV ZAFIRA_USER=admin
ENV ZAFIRA_PASS=qaprosoft
ENV ZAFIRA_JDBC_URL=jdbc:postgresql://localhost:5432/postgres
ENV ZAFIRA_JDBC_USER=postgres
ENV ZAFIRA_JDBC_PASS=postgres

ENV ZAFIRA_LDAP_ENABLED=false
ENV ZAFIRA_LDAP_PROTOCOL=ldap
ENV ZAFIRA_LDAP_SERVER=localhost
ENV ZAFIRA_LDAP_PORT=389
ENV ZAFIRA_LDAP_DN=ou=People,dc=qaprosoft,dc=com
ENV ZAFIRA_LDAP_SEARCH_FILTER=uid
ENV ZAFIRA_LDAP_USER=
ENV ZAFIRA_LDAP_PASSWORD=

RUN apt-get update && apt-get install zip

COPY sources/zafira-ws/target/zafira-ws.war ${CATALINA_HOME}/temp/zafira-ws.war
COPY sources/zafira-web/target/zafira.war ${CATALINA_HOME}/temp/zafira.war
COPY entrypoint.sh /

EXPOSE 8080

ENTRYPOINT /entrypoint.sh
