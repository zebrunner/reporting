<%@ page language="java" contentType="text/html; charset=UTF-8"
	trimDirectiveWhitespaces="true" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/fragments/taglibs.jsp"%>

<div data-ng-controller="TestMetricsListCtrl">
	<div class="row">
		<div class="col-lg-10">
			<h2>
				<i class="fa fa-check-square fa-fw"></i> Test performance 
			</h2>
		</div>
		<div class="col-lg-2">
			<h2>
				<select class="form-control" data-ng-change="loadAllDashboards()" data-ng-Model="timeStep" data-ng-options="opt for opt in timeSteps"></select>
			</h2>
		</div>
	</div>
	<div class="row">
		<div class="col-lg-12" data-ng-repeat="operation in operations">
			<h4>{{operation}}</h4>
			<hr/>
			<div class="graph-box" data-ng-repeat="env in widgetEnvs">
				<h5 align="center">{{env}}</h5>
				<linechart data="chartFilter(operation, env)" options="widgets.model"></linechart>
			</div>
		</div>
	</div>
	<br/>
</div>

