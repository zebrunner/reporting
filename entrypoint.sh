#!/bin/bash

# Change Zafira API URL
cd $CATALINA_HOME/temp

unzip -qq zafira.war -d zafira
rm -rf zafira.war
sed -i -e 's#http://localhost:8080#'"$ZAFIRA_URL"'#g' zafira/scripts/app.js
cd zafira && zip -r ../zafira.war * && cd ..

if [ "$ZAFIRA_LDAP_ENABLED" == "true" ];
then
  unzip -qq zafira-ws.war -d zafira-ws
  rm -rf zafira-ws.war
  sed -i -e 's#<!-- security:authentication-provider ref="ldapAuthProvider"  / -->#<security:authentication-provider ref="ldapAuthProvider"/>#g' zafira-ws/WEB-INF/spring-security.xml
  cd zafira-ws && zip -r ../zafira-ws.war * && cd ..
fi

# Place WAR to webapps
cp zafira-ws.war $CATALINA_HOME/webapps/zafira-ws.war
cp zafira.war $CATALINA_HOME/webapps/zafira.war

# Clear temp
rm -rf zafira*

# Run Tomcat
echo zafira.service.version=$ZAFIRA_SERVICE_VERSION >> $CATALINA_HOME/conf/catalina.properties
echo zafira.client.version=$ZAFIRA_CLIENT_VERSION >> $CATALINA_HOME/conf/catalina.properties
echo zafira.url=$ZAFIRA_URL >> $CATALINA_HOME/conf/catalina.properties
echo zafira.webservice.url=$ZAFIRA_URL/zafira-ws >> $CATALINA_HOME/conf/catalina.properties
echo zafira.admin.username=$ZAFIRA_USER >> $CATALINA_HOME/conf/catalina.properties
echo zafira.admin.password=$ZAFIRA_PASS >> $CATALINA_HOME/conf/catalina.properties
echo zafira.db.jdbc.url=$ZAFIRA_JDBC_URL >> $CATALINA_HOME/conf/catalina.properties
echo zafira.db.jdbc.user=$ZAFIRA_JDBC_USER >> $CATALINA_HOME/conf/catalina.properties
echo zafira.db.jdbc.password=$ZAFIRA_JDBC_PASS >> $CATALINA_HOME/conf/catalina.properties
echo zafira.ldap.protocol=$ZAFIRA_LDAP_PROTOCOL >> $CATALINA_HOME/conf/catalina.properties
echo zafira.ldap.server=$ZAFIRA_LDAP_SERVER >> $CATALINA_HOME/conf/catalina.properties
echo zafira.ldap.port=$ZAFIRA_LDAP_PORT >> $CATALINA_HOME/conf/catalina.properties
echo zafira.ldap.dn=$ZAFIRA_LDAP_DN >> $CATALINA_HOME/conf/catalina.properties
echo zafira.ldap.search_filter=$ZAFIRA_LDAP_SEARCH_FILTER >> $CATALINA_HOME/conf/catalina.properties
echo zafira.ldap.manager.user=$ZAFIRA_LDAP_USER >> $CATALINA_HOME/conf/catalina.properties
echo zafira.ldap.manager.password=$ZAFIRA_LDAP_PASSWORD >> $CATALINA_HOME/conf/catalina.properties

catalina.sh run
