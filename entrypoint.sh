#!/bin/bash

# Change Zafira API URL
cd $CATALINA_HOME/temp
unzip -qq zafira.war -d zafira
rm -rf zafira.war
sed -i -e 's#http://localhost:8080#'"$ZAFIRA_URL"'#g' zafira/scripts/app.js
cd zafira && zip -r ../zafira.war * && cd ..

# Place WAR to webapps
cp zafira-ws.war $CATALINA_HOME/webapps/zafira-ws.war
cp zafira.war $CATALINA_HOME/webapps/zafira.war

# Clear temp
rm -rf zafira*

# Run Tomcat
echo zafira.service.version=$ZAFIRA_SERVICE_VERSION >> $CATALINA_HOME/conf/catalina.properties
echo zafira.client.version=$ZAFIRA_CLIENT_VERSION >> $CATALINA_HOME/conf/catalina.properties
echo zafira.url=$ZAFIRA_URL >> $CATALINA_HOME/conf/catalina.properties
echo zafira.admin.username=$ZAFIRA_USER >> $CATALINA_HOME/conf/catalina.properties
echo zafira.admin.password=$ZAFIRA_PASS >> $CATALINA_HOME/conf/catalina.properties
echo zafira.db.jdbc.url=$ZAFIRA_JDBC_URL >> $CATALINA_HOME/conf/catalina.properties
echo zafira.db.jdbc.user=$ZAFIRA_JDBC_USER >> $CATALINA_HOME/conf/catalina.properties
echo zafira.db.jdbc.password=$ZAFIRA_JDBC_PASS >> $CATALINA_HOME/conf/catalina.properties

catalina.sh run
