#!/bin/bash

DEBUG_SUSPEND_OPTION="n"
if [ "${DEBUG_SUSPEND_STARTUP}" = true ]; then
  DEBUG_SUSPEND_OPTION="y"
fi

DEBUG_PORT_VALUE="5005"
if [ -n "${DEBUG_PORT}" ]; then
  DEBUG_PORT_VALUE="${DEBUG_PORT}"
fi

JAVA_DEBUG_OPTS=""
if [ "${DEBUG_ENABLED}" = true ]; then
  JAVA_DEBUG_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=$DEBUG_SUSPEND_OPTION,address=*:$DEBUG_PORT_VALUE"
fi

JAVA_EXEC_COMMAND="$JAVA_DEBUG_OPTS -jar /app/zafira-service.jar"

java $JAVA_EXEC_COMMAND
