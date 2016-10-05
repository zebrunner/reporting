<%@ page language="java" contentType="text/html; charset=UTF-8"
	trimDirectiveWhitespaces="true" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/fragments/taglibs.jsp"%>

<div data-ng-controller="TestMetricsListCtrl">
	<div class="row">
		<div class="col-lg-11">
			<h2>
				<i class="fa fa-check-square fa-fw"></i> Test cases performance dashboards 
			</h2>
		</div>
		<div class="col-lg-1">
			<h2>
				<select class="form-control" data-ng-change="loadAllDashboards()" data-ng-Model="timeStep" data-ng-options="opt for opt in timeSteps"></select>
			</h2>
		</div>
	</div>
	<div class="row">
		<div class="col-lg-12" data-ng-repeat="operation in operations">
			<div class="panel panel-default">
				<div class="panel-heading">{{operation}}</div>
				<div align="center" class="panel-body">
					<div class="col-lg-4" data-ng-repeat="env in widgetEnvs">
						<div class="panel panel-default">
							<div class="panel-heading">{{env}}</div>
							<div align="center" class="panel-body" style="padding-bottom: 30px;">
								<linechart data="chartFilter(operation, env)" options="widgets.model"></linechart>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

