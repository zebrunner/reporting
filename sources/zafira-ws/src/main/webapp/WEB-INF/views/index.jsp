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
	                <a class="navbar-brand" href="#/dashboard">Zafira <small>server 1.5 | client 1.5.1</small></a>
	            </div>
	            <div class="navbar-default sidebar" role="navigation">
	                <div class="sidebar-nav navbar-collapse">
	                    <ul class="nav" id="side-menu">
	                        <li>
	                            <a href="#/dashboard"><i class="fa fa-dashboard fa-fw"></i> Dashboard</a>
	                        </li>
	                    </ul>
	                </div>
	                <!-- /.sidebar-collapse -->
	            </div>
	            <!-- /.navbar-static-side -->
	        </nav>
	
	        <div id="page-wrapper" data-ng-view>
	 		</div>
	        <!-- /#page-wrapper -->
	
	    </div>
	    <!-- /#wrapper -->
	 
	</body>
	
</html>
