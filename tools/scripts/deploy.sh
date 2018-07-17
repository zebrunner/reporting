#!/bin/bash

cd ../sources
mvn clean:clean install -Dwtpversion=2.0 -DskipTests -P zafira
mvn -f zafira-ws tomcat7:redeploy-only -P zafira
mvn -f zafira-web war:war tomcat7:redeploy-only -P zafira
