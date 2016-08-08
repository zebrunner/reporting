<%@ page 
    language="java"
    contentType="text/html; charset=UTF-8"
    trimDirectiveWhitespaces="true"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/fragments/taglibs.jsp" %>

<div data-ng-controller="TestRunsListCtrl">
	<div class="row">
         <div class="col-lg-12">
         	<h2><i class="fa fa-play-circle fa-fw"></i> Test runs</h2>
         </div>
    </div>
	<div class="row">
		<div class="col-lg-12">
            <div class="row" align="right">
            	<div class="col-lg-12">
            		<span data-ng-show="totalResults">Found: {{totalResults}}&nbsp;</span>
					<a href="" data-ng-click="resetSearchCriteria(); loadTestRuns(1);" class="clear-form danger">Reset&nbsp;<i class="fa fa-times-circle"></i>&nbsp;</a>
					<a href="" data-ng-click="loadTestRuns(1)">Search&nbsp;<i class="fa fa-arrow-circle-right"></i></a>
				</div>
            </div>
            <div class="row results_header">
            	<div class="col-lg-3"><input type="text" class="form-control" placeholder="Test suite" data-ng-model="testRunSearchCriteria.testSuite"></div>
            	<div class="col-lg-4"><input type="text" class="form-control" placeholder="Execution URL" data-ng-model="testRunSearchCriteria.executionURL"></div>
            	<div class="col-lg-2"><input type="text" class="form-control" placeholder="Environment" data-ng-model="testRunSearchCriteria.environment"></div>
            	<div class="col-lg-1">
            		<!-- input type="text" class="form-control" placeholder="Platform" data-ng-model="testRunSearchCriteria.platform" -->
            		<select class="form-control icon-menu" data-ng-model="testRunSearchCriteria.platform" style="padding: 0;">
            			<option value="" disabled selected>Platform</option>
            			<option value="Android">Android</option>
            			<option value="iOS">iOS</option>
            			<option value="chrome">chrome</option>
            			<option value="firefox">firefox</option>
            			<option value="safari">safari</option>
            			<option value="ie">ie</option>
            		</select>
            	</div>
            	<div class="col-lg-2"><input type="date" class="form-control" placeholder="Date" data-ng-model="testRunSearchCriteria.date"></div>
            </div>
            <div class="run_result row" align="center" data-ng-show="totalResults == 0">
            	<div class="col-lg-12">No results</div>
            </div>
			<div class="run_result row" data-ng-class="'result_' + testRun.status" data-ng-repeat="(id, testRun) in testRuns | orderObjectBy:'createdAt':true" context-menu="menuOptions">
				<div class="col-lg-3">
					<input type="checkbox"
							data-ng-model="isChecked"
							data-ng-true-value="true"
							data-ng-false-value="false"
							data-ng-change="selectTestRun(testRun.id, isChecked)"
							value="{{testRun.id}}" 
							name="{{testRun.id}}"
							data-ng-show="testRun.status != 'IN_PROGRESS' && testRunId == null" onclick="event.cancelBubble=true;"/>
					<img data-ng-if="testRun.status == 'IN_PROGRESS'" src="<c:url value="/resources/img/pending.gif" />" class="pending"/>
				  	<b>{{testRun.testSuite.name}}</b>
				</div>
				<div class="col-lg-4">
					<a href="{{testRun.jenkinsURL}}">{{UtilService.truncate(testRun.jenkinsURL, 50)}}</a>
				</div>
				<div class="col-lg-2">
					<span class="badge">{{getArgValue(testRun.configXML, 'env')}}</span>
				</div>
				<div  class="col-lg-1">
					<span class="platform-icon {{getArgValue(testRun.configXML, 'browser')}} {{getArgValue(testRun.configXML, 'mobile_platform_name')}} chrome"></span>
				</div>
				<div  class="col-lg-2" style="padding-right: 3px;">
					<div>
						<span class="time">{{testRun.createdAt | date:'MM/dd HH:mm'}}</span>
						&nbsp;
						<span class="label arrowed arrowed-in-right label-success-border" data-ng-class="{'label-success-empty': testRunResults[testRun.id].passed == 0, 'label-success': testRunResults[testRun.id].passed > 0}">{{testRunResults[testRun.id].passed}}</span>
						<span class="label arrowed arrowed-in-right label-danger-border" data-ng-class="{'label-danger-empty': testRunResults[testRun.id].failed == 0, 'label-danger': testRunResults[testRun.id].failed > 0}">{{testRunResults[testRun.id].failed}}</span>
						<span class="label arrowed arrowed-in-right label-warning-border" data-ng-class="{'label-warning-empty': testRunResults[testRun.id].skipped == 0, 'label-warning': testRunResults[testRun.id].skipped > 0}">{{testRunResults[testRun.id].skipped}}</span>
						&nbsp;
						<i data-ng-class="{'fa fa-lg fa-sort-desc': testRun.showDetails == false, 'fa fa-lg fa-sort-asc': testRun.showDetails == true}" aria-hidden="true" data-ng-click="testRun.showDetails = !testRun.showDetails"  class="float_right"></i>
					</div>
				</div>
				<div class="col-lg-12" data-ng-if="testRun.showDetails == true" style="margin-top: 10px;">
                    <div class="row test_result" data-ng-class="tests[testId].status" data-ng-repeat="testId in testRunsTestIds[testRun.id] | orderBy:'testId':false">
                    	<div class="col-lg-10">
                    		<div><img data-ng-if="tests[testId].status == 'IN_PROGRESS'" src="<c:url value="/resources/img/pending.gif" />" class="pending"/> {{tests[testId].name}}</div>
                            <div class="result_error" data-ng-if="tests[testId].message && tests[testId].status == 'FAILED'">
                            	<show-more text="tests[testId].message" limit="100"></show-more>
                            </div>
                    	</div>
                    	<div class="col-lg-2">
                    		<div class="float_right" data-ng-if="tests[testId].status != STARTED">
                            	<span class="time">{{tests[testId].finishTime | date:'HH:mm'}}</span>
                            	&nbsp;
                            	<a data-ng-if="tests[testId].logURL  && testRun.status != 'IN_PROGRESS'" href="{{tests[testId].logURL}}" target="blank">Log</a> <span data-ng-if="tests[testId].demoURL && testRun.status != 'IN_PROGRESS'">| <a href="{{tests[testId].demoURL}}" target="blank">Demo</a></span>
                       		</div>
                    	</div>
	                 </div>
				</div>
			</div>
			<div style="padding: 5px 15px;">
				<a href="#/tests/runs/{{compareQueryString}}/compare" class="float_left" data-ng-show="testRunsToCompare.length > 1" target="blank">Compare</a>
				<paging class="float_right"
					  page="testRunSearchCriteria.page" 
					  page-size="testRunSearchCriteria.pageSize" 
					  total="totalResults"
					  show-prev-next="true"
					  show-first-last="true"
					  paging-action="loadTestRuns(page)" >
				</paging>
			</div>
			<br/>
		</div>
	</div>
</div>

              