<%@ page
    language="java"
    contentType="text/html; charset=UTF-8"
    trimDirectiveWhitespaces="true"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/fragments/taglibs.jsp" %>

<!DOCTYPE html>
<html data-ng-app="ZafiraApp">

	<head>
	    <%@ include file="/WEB-INF/fragments/meta.jsp" %>
	    <%@ include file="/WEB-INF/fragments/links.jsp" %>
	    <title>Zafira</title>
	</head>

	<body>
	    <div id="wrapper">
	        <!-- Navigation -->
	        <nav class="navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0">
	            <div class="navbar-header">
	                <a class="navbar-brand" href="#/dashboard">Zafira <small>server 1.8 | client 1.8.2</small></a>
	            </div>
	            <ul class="nav navbar-top-links navbar-left" data-ng-controller="NavigationCtrl">
		            <li class="dropdown">
		                    <a class="dropdown-toggle" data-toggle="dropdown" aria-expanded="false" data-ng-click="loadProjects()">Project <span data-ng-show="project"> ({{project.name}})</span> <b class="caret"></b></a>
		                    <ul class="dropdown-menu">
		                    	<li class="pointer">
		                            <a data-ng-click="setProject(null)" style="color: red;">Clear x</a>
		                        </li>
		                        <li data-ng-repeat="project in projects">
		                            <a data-ng-click="setProject(project)">{{project.name}}</a>
	                        	</li>
	                        	<sec:authorize access="hasAnyRole('ROLE_ADMIN')">
	                        	<li class="pointer">
		                            <a data-ng-click="openProjectDetailsModal()" style="color: green;">New +</a>
		                        </li>
		                        </sec:authorize>
	                    </ul>
	                </li>
	            </ul>
	            <ul class="nav navbar-top-links navbar-right">

	                <li>
	                    <a href="#/dashboards"><i class="fa fa-pie-chart fa-fw"></i> Dashboards</a>
	               	</li>
	               	<li>
	                    <a href="#/tests/runs"><i class="fa fa-play-circle fa-fw"></i> Test runs</a>
	               	</li>
	               	<li>
	                    <a href="#/tests/cases"><i class="fa fa-check-square fa-fw"></i> Test cases</a>
	               	</li>
	               	<sec:authorize access="hasAnyRole('ROLE_ADMIN')">
	               	<li>
	                    <a href="#/users"><i class="fa fa-user fa-fw"></i> Users</a>
	               	</li>
	               	<li>
	                    <a href="#/devices"><i class="fa fa-plug fa-fw"></i> Devices</a>
	               	</li>
	               	<li>
	                    <a href="#/settings"><i class="fa fa-gear fa-fw"></i> Settings</a>
	               	</li>
	               	</sec:authorize>
	               	<li>
	               		<a href="<c:url value="/logout" />"><i class="fa fa-sign-out fa-fw"></i> Logout</a>
	               	</li>
	            </ul>
	        </nav>
	       
	        <div id="page-wrapper" data-ng-view>
	 		</div>
	    </div>
	</body>
</html>
