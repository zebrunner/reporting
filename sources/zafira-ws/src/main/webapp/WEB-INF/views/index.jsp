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
	                <a class="navbar-brand" href="#/dashboard">Zafira <small>server 1.6 | client 1.6.2</small></a>
	            </div>
	            <ul class="nav navbar-top-links navbar-right">
	                <li>
	                    <a href="#/dashboard"><i class="fa fa-dashboard fa-fw"></i> Dashboard</a>
	               	</li>
	               	<li><a href="login.html"><i class="fa fa-sign-out fa-fw"></i> Logout</a>
	            </ul>
	            <!-- div class="navbar-default sidebar" role="navigation">
	                <div class="sidebar-nav navbar-collapse">
	                    <ul class="nav" id="side-menu">
	                        <li>
	                            <a href="#/dashboard"><i class="fa fa-dashboard fa-fw"></i> Dashboard</a>
	                        </li>
	                    </ul>
	                </div>
	            </div -->
	        </nav>
	        <div id="page-wrapper" data-ng-view>
	 		</div>
	    </div>
	</body>
</html>
