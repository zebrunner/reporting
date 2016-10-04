<%@ page 
    language="java"
    contentType="text/html; charset=UTF-8"
    trimDirectiveWhitespaces="true"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/fragments/taglibs.jsp" %>

<div data-ng-controller="DashboardsCtrl">
	<div class="row">
         <div class="col-lg-12">
         	<h2>
         		<i class="fa fa-pie-chart fa-fw"></i> Dashboards
         		<sec:authorize access="hasAnyRole('ROLE_ADMIN')">
         			<button class="btn btn-xs btn-success" data-ng-click="openDashboardDetailsModal({}, true)"> <i class="fa fa-plus-circle"></i> new</button>
         		</sec:authorize></h2><br/>
         </div>
    </div>
    <div class="row">
         <div class="col-lg-12">
         	<ul class="nav nav-pills">
         		<li data-ng-class="{'active': dashboard.active == true}" data-ng-repeat="dashboard in dashboards | orderBy:'title'">
         			<a href="" data-ng-click="switchDashboard(dashboard.id)">
         				<span>{{dashboard.title}}</span>
	         			<sec:authorize access="hasAnyRole('ROLE_ADMIN')">
	         				&nbsp;
	                    	<span><i class="float_right fa fa-gear pointer" style="line-height: 20px;" data-ng-click="openDashboardDetailsModal(dashboard, false)"></i></span>
	                    </sec:authorize>
         			</a>
         		</li>
         	</ul>
         </div>
    </div>
    <br/>
	<div class="row">
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
                     		<tr>
                     			<th data-ng-repeat="column in widget.model.columns">
                     				{{column}}
                     			</th>
                     		</tr>
                     		<tr data-ng-repeat="row in widget.data.dataset">
                     			<td data-ng-repeat="column in widget.model.columns">{{row[column]}}</td>
                     		</tr>
                     	</table>
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
				<div class="col-lg-2">
					<i class="float_right fa fa-gear pointer" style="line-height: 20px;" data-ng-click="openWidgetDetailsModal(widget, false)"></i>
					<i class="float_right fa fa-copy pointer" style="line-height: 20px; margin-right: 5px;" data-ng-click="openWidgetDetailsModal(widget, true)"></i>
				</div>
			</div>
		</div>
	</div>
	<br/>
	</sec:authorize>
</div>

              