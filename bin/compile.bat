@echo off

rem specify 1.7 JDK
rem set JAVA_HOME="C:\Tools\Java\jdk1.7.0_80"
rem set PATH=C:\Tools\Java\jdk1.7.0_80\bin:%PATH%

set cur_dir=%~dp0
set zafira_home=%cur_dir%..


cd %zafira_home%\sources
mvn clean install eclipse:eclipse -Dwtpversion=2.0 -DskipTests -P zafira > %zafira_home%\logs\compile.log 2>&1
cd %cur_dir%