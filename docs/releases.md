# Release notes

3.3.37 (2018-11-15)
==================

## Enhancements
* Migrated to new Echarts library
* Improved widget management on the dashboard page
* Migrated to html2canvas for dashboards screenshoting
* Removed SeleniumSerice and dependency on selenium container

## Fixes
* Fix all security vulnerabilities provided by Github
* Minor UI fixed on test runs page: #1175, #1187, #1189, #1173 


## [DEPENDENCIES UPDATES]
* spring 4.3.20.RELEASE
* spring-security.version 4.2.9.RELEASE
* jackson. 2.9.7
* tomcat 7.0.91
* amqp-client.version 4.9.0


3.3.36 (2018-11-01)
==================

## Enhancements

* Implemented test grouping functionality based on test package or class
* Implemented test filtering based on the actual test result
* Improved the way of navigation to test details, now are staying in the same window having the ability to go back saving test results revision context
* Implemented AWS Cloud Front integration for more secured way of publishing artifacts and providing access for them

## Fixes
* Now test artifacts do not contain unused links like Video and VNC
* Cleared obsolete links from Zafira emailable reports 
* Fixed problem with short-term links generation for S3 artifacts

## [DEPENDENCIES UPDATES]
N/A
