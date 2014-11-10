set MAVEN_OPTS=-Xmx1024m -Xmx512m -DargLine=-Xmx512m -XX:MaxPermSize=256m -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8002,server=y,suspend=n 
mvn tomcat7:run
