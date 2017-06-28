cd ../sources
call mvn clean:clean install -Dwtpversion=2.0 -DskipTests -P zafira
call mvn -f zafira-ws tomcat7:redeploy-only -P zafira
call mvn -f zafira-web war:war tomcat7:redeploy-only -P zafira
