#!/bin/bash

echo set.zafira.multitenant=$ZAFIRA_MULTITENANT >> etc/wrapper.conf
echo set.zafira.db.jdbc.url=$ZAFIRA_JDBC_URL >> etc/wrapper.conf
echo set.zafira.db.jdbc.user=$ZAFIRA_JDBC_USER >> etc/wrapper.conf
echo set.zafira.db.jdbc.password=$ZAFIRA_JDBC_PASS >> etc/wrapper.conf
echo set.zafira.redis.host=$ZAFIRA_REDIS_HOST >> etc/wrapper.conf
echo set.zafira.redis.port=$ZAFIRA_REDIS_PORT >> etc/wrapper.conf
echo set.zafira.rabbitmq.host=$ZAFIRA_RABBITMQ_HOST >> etc/wrapper.conf
echo set.zafira.rabbitmq.port=$ZAFIRA_RABBITMQ_PORT >> etc/wrapper.conf
echo set.zafira.rabbitmq.user=$ZAFIRA_RABBITMQ_USER >> etc/wrapper.conf
echo set.zafira.rabbitmq.pass=$ZAFIRA_RABBITMQ_PASS >> etc/wrapper.conf
echo set.zafira.rabbitmq.stomp.host=$ZAFIRA_RABBITMQ_STOMP_HOST >> etc/wrapper.conf
echo set.zafira.rabbitmq.stomp.port=$ZAFIRA_RABBITMQ_STOMP_PORT >> etc/wrapper.conf
echo set.zafira.jwt.secret=$ZAFIRA_JWT_TOKEN >> etc/wrapper.conf
echo set.zafira.crypto_salt=$ZAFIRA_CRYPTO_SALT >> etc/wrapper.conf

bin/JobService console
