<%@ page 
    language="java"
    contentType="text/html; charset=UTF-8"
    trimDirectiveWhitespaces="true"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/fragments/taglibs.jsp" %>

<div data-ng-controller="DashboardCtrl">
	<div class="row">
         <div class="col-lg-12">
         	<h2><i class="fa fa-pie-chart fa-fw"></i> Dashboard <button class="btn btn-xs btn-success" data-ng-click="openDashboardDetailsModal()"> <i class="fa fa-plus-circle"></i> new</button></h2><br/>
         </div>
    </div>
	<div class="row">
		<div class="col-lg-4">
            <div class="panel panel-default">
                <div class="panel-heading">
                    Test results statistics (last 30 days)
                </div>
                <div class="panel-body" style="min-height: 500px">
                    <div id="test-results-statistics"></div>
                </div>
            </div>
        </div>
        <div class="col-lg-4">
            <div class="panel panel-default">
                <div class="panel-heading">
                    Test owners statistics
                </div>
                <div class="panel-body" style="min-height: 500px">
                     <div id="test-owners-statistics"></div>
                </div>
            </div>
        </div>
        <div class="col-lg-4">
            <div class="panel panel-default">
                <div class="panel-heading">
                    Test implementation progress (last 30 days)
                </div>
                <div class="panel-body" style="min-height: 500px">
                     <div id="test-implementation-statistics"></div>
                </div>
            </div>
        </div>
        <div class="col-lg-4" data-ng-repeat="dashboard in dashboards">
            <div class="panel panel-default">
                <div class="panel-heading">
                    {{dashboard.title}}
                    <i class="float_right fa fa-gear pointer" style="line-height: 20px;" data-ng-click="openDashboardDetailsModal(dashboard.id)"></i>
                </div>
                <div class="panel-body" style="height: 350px; padding-bottom: 35px;">
                     <linechart data-ng-if="dashboard.type = 'linechart'" data="dashboard.data" options="dashboard.model"></linechart>
                     <pie-chart data-ng-if="dashboard.type = 'piechart'" data="dashboard.data" options="dashboard.model"></pie-chart>
                </div>
            </div>
        </div>
	</div>
</div>

              