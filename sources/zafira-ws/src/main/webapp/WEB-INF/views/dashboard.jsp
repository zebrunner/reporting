<%@ page 
    language="java"
    contentType="text/html; charset=UTF-8"
    trimDirectiveWhitespaces="true"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/fragments/taglibs.jsp" %>

<div data-ng-controller="DashboardCtrl">
	<div class="row">
         <div class="col-lg-12">
         	<h2>Dashboard</h2>
			<hr/>
         </div>
    </div>
	<div class="row">
		<div class="col-lg-12">
			<div class="input-group custom-search-form search">
                <input type="text" class="form-control" data-ng-model="jenkinsURLFilter" placeholder="Job search...">
                <span class="input-group-btn">
	                <button class="btn btn-default" type="button" disabled>
	                    <i class="fa fa-search"></i>
	                </button>
	            </span>
            </div>
			<div class="result_row" data-ng-repeat="(id, testRun) in testRuns | orderObjectBy:'id':true | filter:{jenkinsURL:jenkinsURLFilter}:false">
				<span ng-switch on="testRun.status">
			      <div ng-switch-when="PASSED" class="btn btn-success btn-xs w60">PASSED</div>
			      <div ng-switch-when="FAILED" class="btn btn-danger btn-xs w60">FAILED</div>
			      <img ng-switch-default src="<c:url value="/resources/img/pending.gif" />" class="pending"/>
			  	</span>
				<b>{{testRun.testSuite.name}}</b>
				<a href="{{testRun.jenkinsURL}}">{{testRun.jenkinsURL}}</a>
				<b>{{getArgValue(testRun.configXML, 'env')}}</b>
				<div class="float_right">
					<span class="time">{{testRun.modifiedAt | date:'hh:mm MM/dd/yyyy'}}</span>
					&nbsp;
					<span class="label label-success arrowed arrowed-in-right">{{testRun.passed}}</span>
					<span class="label label-danger arrowed arrowed-in-right">{{testRun.failed}}</span>
					<span class="label label-warning arrowed arrowed-in-right">{{testRun.skipped}}</span>
					&nbsp;
					<a href="" data-ng-click="testRun.showDetails = true" class="float_right" data-ng-show="testRun.showDetails != true">Open</a>
					<a href="" data-ng-click="testRun.showDetails = false" class="float_right" data-ng-show="testRun.showDetails == true">Close</a>
				</div>
				<div class="results_details" data-ng-show="testRun.showDetails == true">
					<table class="table">
                        <tbody>
                            <tr data-ng-repeat="test in tests[testRun.id] | orderObjectBy:'id':false">
                            	<td ng-switch on="test.status" class="w60" style="padding-left: 25px;">
							      <div ng-switch-when="PASSED" class="btn btn-success btn-xs w60">PASSED</div>
							      <div ng-switch-when="FAILED" class="btn btn-danger btn-xs w60">FAILED</div>
							      <div ng-switch-when="SKIPPED" class="btn btn-warning btn-xs w60">SKIPPED</div>
							  	</td>
                                <td>	
                                	<div>{{test.name}}</div>
                                	<div class="result_error" data-ng-if="test.message">{{test.message}}</div>
                                </td>
                                <td class="w210">
                                	<div class="float_right">
	                                	<span class="time">{{test.finishTime | date:'hh:mm'}}</span>
	                                	&nbsp;
	                                	<a data-ng-if="test.logURL" href="{{test.logURL}}" target="blank">Log</a> <span data-ng-if="test.demoURL">| <a href="{{test.demoURL}}" target="blank">Demo</a></span>
                            		</div>
                            	</td>
                            </tr>
                        </tbody>
                    </table>
				</div>
			</div>
		</div>
	</div>
</div>

              