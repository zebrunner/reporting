#!/bin/sh

mvn -f sources/pom.xml clean:clean install -Dwtpversion=2.0 -DskipTests -P zafira
mvn -f sources/zafira-web/pom.xml war:war -P zafira
docker build -t qaprosoft/zafira:latest .
docker-compose up
