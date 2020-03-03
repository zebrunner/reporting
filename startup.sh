#!/bin/sh

echo "Startup script execution is strated"

DEBUG_SUSPEND_OPTION="n"
if [ -n "${DEBUG_SUSPEND_STARTUP}" ] && [ "${DEBUG_SUSPEND_STARTUP}" = true ]; then
  DEBUG_SUSPEND_OPTION="y"
  echo "Suspended debug option is enabled"
fi

DEBUG_PORT_VALUE="5005"
if [ -n "${DEBUG_PORT}" ]; then
  DEBUG_PORT_VALUE="${DEBUG_PORT}"
  echo "${DEBUG_PORT} is used for debug"
fi

JAVA_DEBUG_OPTS=""
if [ -n "${DEBUG_ENABLED}" ] && [ "${DEBUG_ENABLED}" = true ]; then
  JAVA_DEBUG_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=$DEBUG_SUSPEND_OPTION,address=*:$DEBUG_PORT_VALUE"
  echo "Debug is enabled"
fi

JAVA_EXEC_COMMAND="$JAVA_DEBUG_OPTS -jar /app/zafira-service.jar"

echo $JAVA_EXEC_COMMAND

java $JAVA_EXEC_COMMAND
