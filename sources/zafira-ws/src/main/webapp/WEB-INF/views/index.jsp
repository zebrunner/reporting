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
	        <nav class="navbar navbar-default navbar-static-top" role="navigation" style="margin-bottom: 0" data-ng-controller="NavigationCtrl">
	            <div class="navbar-header">
	                <a class="navbar-brand" href="#!/dashboards">Zafira <small>server {{version.service}} | client {{version.client}}</small></a>
	            </div>
	            <ul class="nav navbar-top-links navbar-left">
		            <li class="dropdown">
		                    <a class="dropdown-toggle" data-toggle="dropdown" aria-expanded="false" data-ng-click="loadProjects()">Project <span data-ng-show="project"> ({{project.name}})</span> <b class="caret"></b></a>
		                    <ul class="dropdown-menu">
		                    	<li class="pointer">
		                            <a data-ng-click="setProject(null)" style="color: red;">Clear x</a>
		                        </li>
		                        <li data-ng-repeat="project in projects | orderBy:'name'">
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
	                    <a href="#!/dashboards"><i class="fa fa-lg fa-pie-chart fa-fw"></i> Dashboards</a>
	               	</li>
	               	<li>
	                    <a class="dropdown-toggle" data-toggle="dropdown" href="" aria-expanded="false" data-ng-click="loadViews()">
	                        <i class="fa fa-lg fa-play-circle"></i> Test runs <i class="fa fa-caret-down"></i>
	                    </a>
	                    <ul class="dropdown-menu dropdown-user">
	                        <li>
	                        	<a href="#!/tests/runs">Show latest runs</a>
	                        </li>
	                        <li class="divider" data-ng-if="views.length > 0"></li>
	                        <li data-ng-repeat="view in views | orderBy:'name'" style="position: relative;">
	                            <a href="#!/views/{{view.id}}">{{view.name}} 
	                            </a>
	                            <sec:authorize access="hasAnyRole('ROLE_ADMIN')">
                            		<i style="position: absolute; right: 10px; top: 5px;" class="fa fa-gear fa-fw" data-ng-click="openViewDetailsModal(view)"></i>
                            	</sec:authorize>
                        	</li>
	                        <sec:authorize access="hasAnyRole('ROLE_ADMIN')">
	                        	<li class="pointer">
		                            <a data-ng-click="openViewDetailsModal()" style="color: green;">New +</a>
		                        </li>
	                        </sec:authorize>
	                    </ul>
	                    <!-- /.dropdown-user -->
	                </li>
	               	<li>
	                    <a href="#!/tests/cases"><i class="fa fa-lg fa-check-square fa-fw"></i> Test cases</a>
	               	</li>
	               	<sec:authorize access="hasAnyRole('ROLE_ADMIN')">
	               	<li>
	                    <a href="#!/users"><i class="fa fa-lg fa-user fa-fw"></i> Users</a>
	               	</li>
	               	<li>
	                    <a href="#!/devices"><i class="fa fa-lg fa-plug fa-fw"></i> Devices</a>
	               	</li>
	               	</sec:authorize>
	               	<li>
	                    <a class="dropdown-toggle" data-toggle="dropdown" href="" aria-expanded="false">
	                        <i class="fa fa-lg fa-user-circle-o"></i> {{UtilService.truncate(currentUser.username, 15)}} <i class="fa fa-caret-down"></i>
	                    </a>
	                    <ul class="dropdown-menu dropdown-user">
	                    	<sec:authorize access="hasAnyRole('ROLE_USER')">
	                        <li>
	                        	<a href="#!/users/profile"><i class="fa fa-user fa-fw"></i> Profile</a>
	                        </li>
	                        <li data-ng-if="pefrDashboardId">
	                        	<a href="#!/dashboards?id={{pefrDashboardId}}&userId={{currentUser.id}}"><i class="fa fa-sm fa-area-chart fa-fw"></i> Performance</a>
	                        </li>
	                        </sec:authorize>
	                        <sec:authorize access="hasAnyRole('ROLE_ADMIN')">
	                        <li>
	                        	<a href="#!/settings"><i class="fa fa-gear fa-fw"></i> Settings</a>
	                        </li>
	                        </sec:authorize>
	                        <li class="divider"></li>
	                        <li>
								<a href="<c:url value="/logout" />"><i class="fa fa-sign-out fa-fw"></i> Logout</a>
	                        </li>
	                    </ul>
	                    <!-- /.dropdown-user -->
	                </li>
	            </ul>
	        </nav>
	       
	        <div id="page-wrapper" data-ng-view>
	 		</div>
	    </div>
	</body>
</html>
