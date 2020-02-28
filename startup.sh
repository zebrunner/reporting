#!/bin/bash


if [ "${DEBUG_ENABLED}" = true ] ; then
  java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:${DEBUG_PORT} -jar /app/zafira-service.jar
else
  java -jar /app/zafira-service.jar
fi