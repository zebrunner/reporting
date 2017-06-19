#!/bin/bash

docker rm -f tomcat
docker run -d -p 80:80 -p 8888:8080 -e TOMCAT_PASS="admin" --name tomcat cloudesire/tomcat:7-jre8
docker run -it --rm -v ~/.m2:/root/.m2 -v ${PWD}:/app -w /app goyalzz/ubuntu-git-java8-maven mvn clean install -Dwtpversion=2.0 -DskipTests -P zafira
docker run -it --rm -v ~/.m2:/root/.m2 -v ${PWD}:/app -w /app goyalzz/ubuntu-git-java8-maven mvn -f zafira-ws tomcat7:redeploy-only -P zafira
docker run -it --rm -v ~/.m2:/root/.m2 -v $(PWD):/app -w /app goyalzz/ubuntu-git-java8-maven mvn -f zafira-web war:war tomcat7:redeploy-only -P zafira
