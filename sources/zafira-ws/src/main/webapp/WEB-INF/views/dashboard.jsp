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
			<div class="result_row" data-ng-repeat="(id, testRun) in testRuns | orderObjectBy:'createdAt':true | filter:{jenkinsURL:jenkinsURLFilter}:false track by $index" ng-show="$index < (page * pageSize)">
				<div class="float_left" data>
					<input type="checkbox"
						data-ng-model="isChecked"
						data-ng-true-value="true"
						data-ng-false-value="false"
						data-ng-change="selectTestRun(testRun.id, isChecked)"
						value="{{testRun.id}}" 
						name="{{testRun.id}}"
						data-ng-show="testRun.status != 'IN_PROGRESS'"/>
					&nbsp;
				</div>
				<span ng-switch on="testRun.status">
			      <div ng-switch-when="PASSED" class="btn btn-success btn-xs w65">PASSED</div>
			      <div ng-switch-when="FAILED" class="btn btn-danger btn-xs w65">FAILED</div>
			      <img ng-switch-default src="<c:url value="/resources/img/pending.gif" />" class="pending"/>
			  	</span>
				<b>{{testRun.testSuite.name}}</b>
				<a href="{{testRun.jenkinsURL}}">{{testRun.jenkinsURL}}</a>
				<b>{{getArgValue(testRun.configXML, 'env')}}</b>
				<div class="float_right">
					<span class="time">{{testRun.modifiedAt | date:'hh:mm MM/dd/yyyy'}}</span>
					&nbsp;
					<span class="label label-success arrowed arrowed-in-right">{{testRunResults[testRun.id].passed}}</span>
					<span class="label label-danger arrowed arrowed-in-right">{{testRunResults[testRun.id].failed}}</span>
					<span class="label label-warning arrowed arrowed-in-right">{{testRunResults[testRun.id].skipped}}</span>
					&nbsp;
					<a href="" data-ng-click="testRun.showDetails = true" class="float_right" data-ng-show="testRun.showDetails != true">Open</a>
					<a href="" data-ng-click="testRun.showDetails = false" class="float_right" data-ng-show="testRun.showDetails == true">Close</a>
				</div>
				<div class="results_details" data-ng-show="testRun.showDetails == true">
					<table class="table">
                        <tbody>
                            <tr data-ng-repeat="test in tests[testRun.id] | orderObjectBy:'id':false">
                            	<td ng-switch on="test.status" class="w65" style="padding-left: 40px;">
							      <div ng-switch-when="PASSED" class="btn btn-success btn-xs w65">PASSED</div>
							      <div ng-switch-when="FAILED" class="btn btn-danger btn-xs w65">FAILED</div>
							      <div ng-switch-when="SKIPPED" class="btn btn-warning btn-xs w65">SKIPPED</div>
							      <img ng-switch-default src="<c:url value="/resources/img/pending.gif" />" class="pending"/>
							  	</td>
                                <td>	
                                	<div>{{test.name}}</div>
                                	<div class="result_error" data-ng-if="test.message">{{test.message}}</div>
                                </td>
                                <td class="w250">
                                	<div class="float_right" data-ng-if="test.status != STARTED">
	                                	<span class="time">{{test.finishTime | date:'hh:mm MM/dd/yyyy'}}</span>
	                                	&nbsp;
	                                	<a data-ng-if="test.logURL" href="{{test.logURL}}" target="blank">Log</a> <span data-ng-if="test.demoURL">| <a href="{{test.demoURL}}" target="blank">Demo</a></span>
                            		</div>
                            	</td>
                            </tr>
                        </tbody>
                    </table>
				</div>
			</div>
			<div style="padding: 5px 15px;">
				<a href="#/tests/runs/{{queryString}}/compare" class="float_left" data-ng-show="testRunsToCompare.length > 1" target="blank">Compare</a>
				<a href="" data-ng-click="compareTestRunResults()" class="float_right" data-ng-show="(page * pageSize) < totalTestRuns">Show more</a>
			</div>
			<br/>
		</div>
	</div>
</div>

              