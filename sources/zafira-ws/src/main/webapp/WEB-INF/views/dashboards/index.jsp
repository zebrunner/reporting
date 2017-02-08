<%@ page 
    language="java"
    contentType="text/html; charset=UTF-8"
    trimDirectiveWhitespaces="true"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/fragments/taglibs.jsp" %>

<div data-ng-controller="DashboardsCtrl">
	<div class="row">
         <div class="col-lg-10">
         	<h2>
         		<i class="fa fa-pie-chart fa-fw"></i> Dashboards
         		</h2><br/>
		 </div>
	</div>
	 <md-fab-speed-dial id="main-fab" md-direction="up" class="md-scale md-fab-bottom-right">
		 <md-fab-trigger>
			 <md-button aria-label="menu" class="md-fab" md-visible="tooltipVisible">
				 <i class="fa fa-bars" aria-hidden="true"></i>
			 </md-button>
		 </md-fab-trigger>
		 <md-fab-actions>
			 <md-button aria-label="menu" class="md-fab md-raised md-mini" data-ng-click="openEmailModal()">
				 <i class="fa fa-envelope-o" aria-hidden="true"></i>
			 </md-button>
			 <sec:authorize access="hasAnyRole('ROLE_ADMIN')">
				 <md-button aria-label="menu" class="md-fab md-raised md-mini" data-ng-click="openDashboardDetailsModal(dashboard, false)">
					 <i class="fa fa-gear pointer"></i>
				 </md-button>
				 <md-button aria-label="menu" class="md-fab md-raised md-mini" data-ng-click="openDashboardDetailsModal({}, true)">
					 <i class="fa fa-plus" aria-hidden="true"></i>
				 </md-button>
			 </sec:authorize>
		 </md-fab-actions>
	</md-fab-speed-dial>
    <div>
	    <div class="row">
	         <div class="col-lg-12">
	         	<ul class="nav nav-pills">
					<md-button data-ng-class="{'md-raised md-primary': dashboard.active == true}" data-ng-repeat="dashboard in dashboards | orderBy:'position'"
							   data-ng-click="switchDashboard(dashboard.id)">
						{{dashboard.title}}
					</md-button>
	         	</ul>
	         </div>
	    </div>
	    <br/>
		<div class="row" id="dashboard_content">
			<input type="hidden" id="dashboard_title" value="{{dashboard.title}}" />
	        <div class="col-lg-{{widget.size}}" data-ng-repeat="widget in dashboard.widgets | orderBy:'position'">
	            <div class="panel panel-default">
	                <div class="panel-heading">
	                    {{widget.title}}
	                    <sec:authorize access="hasAnyRole('ROLE_ADMIN')">
	                    <i class="float_right fa fa-arrows pointer" style="line-height: 20px;" data-ng-click="openDashboardWidgetModal(widget, false)"></i>
	                	</sec:authorize>
	                </div>
	                <div align="center" class="panel-body" data-ng-class="{'graph-box' : widget.type != 'table'}">
	                     <linechart data-ng-if="widget.type == 'linechart'" data="widget.data" options="widget.model"></linechart>
	                     <div  data-ng-if="widget.type == 'piechart'" class="pie-chart">
	                     	<pie-chart data="widget.data.dataset" options="widget.model"></pie-chart>
	                     </div>
	                     <div class="table-responsive" data-ng-if="widget.type == 'table'">
	                     	<table class="table table-striped table-bordered table-hover" style="width: 100%;">
	                     		<thead>
		                     		<tr>
		                     			<th class="pointer" data-ng-repeat="column in widget.model.columns" data-ng-click="changeSorting(column)">
		                     				{{column}}&nbsp;<i class="fa fa-sort"></i>
		                     			</th>
		                     		</tr>
	                     		</thead>
	                     		<tbody>
		                     		<tr data-ng-repeat="row in widget.data.dataset | orderBy:sort.column:sort.descending">
		                     			<td data-ng-repeat="column in widget.model.columns" data-ng-bind-html="asString(row[column])"></td>
		                     		</tr>
		                     	</tbody>
	                     	</table>
	                     </div>
	                </div>
	            </div>
	        </div>
		</div>
	</div>
	<sec:authorize access="hasAnyRole('ROLE_ADMIN')">
	<div class="row">
		<div class="col-lg-12">
			<button type="button" class="btn btn-outline btn-success btn-lg btn-block" data-ng-click="openWidgetDetailsModal({}, true)">Create new widget</button>
		</div>
	</div>
	<div class="row">
		<br/>
		<div class="col-lg-12">
			<div class="run_result row " data-ng-repeat="widget in widgets | orderBy:'title'">
				<div class="col-lg-4">
				  	{{widget.title}}
				</div>
				<div class="col-lg-4">
					{{widget.type}}
				</div>
				<div class="col-lg-2">
					<button type="button" class="btn btn-outline btn-primary btn-xs" data-ng-click="openDashboardWidgetModal(widget, true)">Add widget to dashboard</button>
				</div>
				<div class="col-lg-2" style="padding-right: 3px;">
					<i class="float_right fa fa-gear pointer" style="line-height: 20px;" data-ng-click="openWidgetDetailsModal(widget, false)"></i>
					<i class="float_right fa fa-copy pointer" style="line-height: 20px; margin-right: 5px;" data-ng-click="openWidgetDetailsModal(widget, true)"></i>
				</div>
			</div>
		</div>
	</div>
	<br/>
	</sec:authorize>
</div>

              