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
                <input type="text" class="form-control" data-ng-model="jenkinsURLFilter" placeholder="Job filter">
                <span class="input-group-btn">
	                <button class="btn btn-default" type="button" disabled>
	                    <i class="fa fa-search"></i>
	                </button>
	            </span>
            </div>
            <div class="run_result row" align="center" data-ng-show="showLoading && totalTestRuns == 0">
            	<div class="col-lg-12"><img src="<c:url value="/resources/img/pending.gif" />" class="pending"/> Loading data...</div>
            </div>
            <div class="run_result row" align="center" data-ng-show="!showLoading && totalTestRuns == 0">
            	<div class="col-lg-12">No results yet</div>
            </div>
			<div class="run_result row" data-ng-repeat="(id, testRun) in testRuns | orderObjectBy:'createdAt':true | filter:{jenkinsURL:jenkinsURLFilter}:false track by $index" ng-show="$index < (page * pageSize)">
				<div style="position: absolute; z-index: 100000;">
					<input type="checkbox"
							data-ng-model="isChecked"
							data-ng-true-value="true"
							data-ng-false-value="false"
							data-ng-change="selectTestRun(testRun.id, isChecked)"
							value="{{testRun.id}}" 
							name="{{testRun.id}}"
							data-ng-show="testRun.status != 'IN_PROGRESS'" onclick="event.cancelBubble=true;"/>
				</div>
				<div class="col-lg-1">
					<div ng-switch on="testRun.status">
				      <div ng-switch-when="PASSED" class="btn btn-success btn-xs w70">PASSED</div>
				      <div ng-switch-when="FAILED" class="btn btn-danger btn-xs w70">FAILED</div>
				      <div ng-switch-when="ABORTED" class="btn btn-warning btn-xs w70">ABORTED</div>
				      <img ng-switch-default src="<c:url value="/resources/img/pending.gif" />" class="pending"/>
				  	</div>
				</div>
				<div class="col-lg-8">
				  	<b>{{truncate(testRun.testSuite.name, 50)}}</b>
					<a href="{{testRun.jenkinsURL}}">{{truncate(testRun.jenkinsURL, 40)}}</a>
					<b>{{getArgValue(testRun.configXML, 'env')}}</b>
				</div>
				<div  class="col-lg-3">
					<div class="float_right">
						<span class="time">{{testRun.createdAt | date:'MM/dd/yy hh:mm'}}</span>
						&nbsp;
						<span class="label label-success arrowed arrowed-in-right">{{testRunResults[testRun.id].passed}}</span>
						<span class="label label-danger arrowed arrowed-in-right">{{testRunResults[testRun.id].failed}}</span>
						<span class="label label-warning arrowed arrowed-in-right">{{testRunResults[testRun.id].skipped}}</span>
						&nbsp;
						<i data-ng-class="{'fa fa-lg fa-sort-desc': testRun.showDetails == false, 'fa fa-lg fa-sort-asc': testRun.showDetails == true}" aria-hidden="true" data-ng-click="testRun.showDetails = !testRun.showDetails"></i>
					</div>
				</div>
				<div class="col-lg-12" data-ng-show="testRun.showDetails == true" style="margin-top: 20px;">
                    <div class="row test_result" data-ng-class="test.status" data-ng-repeat="test in tests[testRun.id] | orderObjectBy:'id':false">
                    	<div class="col-lg-10">
                    		<div><img data-ng-if="test.status == 'IN_PROGRESS'" src="<c:url value="/resources/img/pending.gif" />" class="pending"/> {{test.name}}</div>
                            <div class="result_error wrap" data-ng-if="test.message">{{test.message}}</div>
                    	</div>
                    	<div class="col-lg-2">
                    		<div class="float_right" data-ng-if="test.status != STARTED">
                            	<span class="time">{{test.finishTime | date:'hh:mm'}}</span>
                            	&nbsp;
                            	<a data-ng-if="test.logURL" href="{{test.logURL}}" target="blank">Log</a> <span data-ng-if="test.demoURL">| <a href="{{test.demoURL}}" target="blank">Demo</a></span>
                       		</div>
                    	</div>
	                 </div>
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

              