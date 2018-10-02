FROM java:8-jre

ENV ZAFIRA_MULTITENANT=false
ENV ZAFIRA_JDBC_URL=jdbc:postgresql://localhost:5432/postgres
ENV ZAFIRA_JDBC_USER=postgres
ENV ZAFIRA_JDBC_PASS=postgres

ENV ZAFIRA_JWT_TOKEN=AUwMLdWFBtUHVgvjFfMmAEadXqZ6HA4dKCiCmjgCXxaZ4ZO8od
ENV ZAFIRA_CRYPTO_SALT=TDkxalR4T3EySGI0T0YyMitScmkxWDlsUXlPV2R4OEZ1b2kyL1VJeFVHST0=

ENV ZAFIRA_REDIS_HOST=redis
ENV ZAFIRA_REDIS_PORT=6379

ENV ZAFIRA_RABBITMQ_HOST=localhost
ENV ZAFIRA_RABBITMQ_PORT=5672
ENV ZAFIRA_RABBITMQ_USER=guest
ENV ZAFIRA_RABBITMQ_PASS=guest
ENV ZAFIRA_RABBITMQ_STOMP_HOST=rabbitmq
ENV ZAFIRA_RABBITMQ_STOMP_PORT=61613

WORKDIR /opt/zafira-batch-services/generated-resources/appassembler/jsw/JobService
COPY /target/ /opt/zafira-batch-services/

RUN mkdir -p logs
RUN chmod +x bin/JobService bin/wrapper-linux-x86-64

ENTRYPOINT bin/JobService console
