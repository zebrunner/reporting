@echo off

set cur_dir=%~dp0
set zafira_home=%cur_dir%..


cd %zafira_home%/sources/zafira-ws
rem set MAVEN_OPTS=-Xmx1024m -Xmx512m -DargLine=-Xmx512m -XX:MaxPermSize=256m -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8002,server=y,suspend=n 
set MAVEN_OPTS=-Xmx1024m -Xmx512m -DargLine=-Xmx512m -XX:MaxPermSize=256m
mvn tomcat7:run>%zafira_home%\logs\startup.log 2>&1

