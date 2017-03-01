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
		
			<div class="row results_header">
				<div class="col-lg-12">
					<div class="row">
						<form data-ng-submit="loadTestRuns(1);">
		            		<div class="col-lg-1">
			            		<select class="form-control icon-menu" data-ng-model="testRunSearchCriteria.status" style="padding: 0;" data-ng-change="showReset = true">
			            			<option value="" disabled>Status</option>
			            			<option value="PASSED">PASSED</option>
			            			<option value="FAILED">FAILED</option>
			            			<option value="SKIPPED">SKIPPED</option>
			            			<option value="ABORTED">ABORTED</option>
			            			<option value="IN_PROGRESS">IN_PROGRESS</option>
			            			<option value="UNKNOWN">UNKNOWN</option>
			            		</select>
			            	</div>
			            	<div class="col-lg-2"><input type="text" class="form-control" placeholder="Test suite" data-ng-model="testRunSearchCriteria.testSuite" data-ng-change="showReset = true"></div>
			            	<div class="col-lg-2"><input type="text" class="form-control" placeholder="App version" data-ng-model="testRunSearchCriteria.appVersion" data-ng-change="showReset = true"></div>
			            	<div class="col-lg-3"><input type="text" class="form-control" placeholder="Job URL" data-ng-model="testRunSearchCriteria.executionURL" data-ng-change="showReset = true"></div>
			            	<div class="col-lg-1"><input type="text" class="form-control" placeholder="Env" data-ng-model="testRunSearchCriteria.environment" data-ng-change="showReset = true"></div>
			            	<div class="col-lg-1">
			            		<select class="form-control icon-menu" data-ng-model="testRunSearchCriteria.platform" style="padding: 0;" data-ng-change="showReset = true">
			            			<option value="" disabled>Platform</option>
			            			<option value="Android">Android</option>
			            			<option value="iOS">iOS</option>
			            			<option value="chrome">chrome</option>
			            			<option value="firefox">firefox</option>
			            			<option value="safari">safari</option>
			            			<option value="ie">ie</option>
			            		</select>
			            	</div>
			            	<div class="col-lg-2"><input type="datetime-local" class="form-control" placeholder="Date" data-ng-model="startedAt" style="min-width:95%" data-ng-change="showReset = true"></div>
			            	<input type="submit" data-ng-hide="true" />
		            	</form>
					</div>
					<div class="row search_controls">
						<div class="col-lg-3">
		            		Enable real-time events <input type="checkbox" data-ng-model="showRealTimeEvents" />
		            	</div>
		            	<div class="col-lg-9" align="right">
		            		<span>Found: {{totalResults}}&nbsp;</span>
							<a data-ng-if="showReset" href="" data-ng-click="resetSearchCriteria(); loadTestRuns(1);" class="clear-form danger">Reset&nbsp;<i class="fa fa-lg fa-times-circle"></i>&nbsp;</a>
							<a href="" data-ng-click="loadTestRuns(1);">Search&nbsp;<i class="fa fa-lg fa-arrow-circle-right"></i></a>
						</div>
					</div>
				</div>
            </div>
            <div class="run_result row" align="center" data-ng-show="totalResults == 0">
            	<div class="col-lg-12">No results</div>
            </div>
			<div class="run_result row progressbar_container" data-ng-class="'result_' + testRun.status" data-ng-repeat="(id, testRun) in testRuns | orderObjectBy:'startedAt':true" <sec:authorize access="hasAnyRole('ROLE_ADMIN')">context-menu="adminMenuOptions"</sec:authorize> <sec:authorize access="hasAnyRole('ROLE_USER')">context-menu="userMenuOptions"</sec:authorize>>
				<timer countdown="testRun.countdown" eta="testRun.eta" interval="1000" data-ng-if="testRun.status == 'IN_PROGRESS' && testRun.countdown">
					<div class="progressbar_bg" style="width:{{progressBar}}%"></div>
				</timer>
				<div class="col-lg-4">
					<!-- input type="checkbox"
							data-ng-model="isChecked"
							data-ng-true-value="true"
							data-ng-false-value="false"
							data-ng-change="selectTestRun(testRun.id, isChecked)"
							value="{{testRun.id}}" 
							name="{{testRun.id}}"
							data-ng-show="testRun.status != 'IN_PROGRESS' && testRunId == null" onclick="event.cancelBubble=true;"/ -->
					<img src="<c:url value="/resources/img/pending.gif" />" class="pending" data-ng-if="testRun.status == 'IN_PROGRESS'"/>
					<timer countdown="testRun.countdown" eta="testRun.eta" interval="1000" data-ng-if="testRun.status == 'IN_PROGRESS' && testRun.countdown">
						<small>{{progressBar}}%</small>
					</timer>
				  	<b>{{testRun.testSuite.name}} <i data-ng-if="testRun.comments" data-ng-click="openCommentsModal(testRun)" class="fa fa-commenting-o" aria-hidden="true"></i></b>
					<br/>
					<small>{{testRun.appVersion}}</small>
				</div>
				<div class="col-lg-4">
					<a href="{{testRun.jenkinsURL}}" target="_blank">{{testRun.job.name}}</a>
				</div>
				<div class="col-lg-1">
					<span class="badge">{{testRun.env}}</span>
				</div>
				<div  class="col-lg-1" align="center">
					<span class="platform-icon {{testRun.platform}}"></span>
				</div>
				<div  class="col-lg-2" style="padding-right: 3px;">
					<div>
						<span class="time">
							<time am-time-ago="testRun.startedAt" title="{{ main.time | amDateFormat: 'dddd, MMMM Do YYYY, h:mm a' }}"></time>
						</span>
						<br/>
						<span class="label arrowed arrowed-in-right label-success-border" data-ng-class="{'label-success-empty': testRun.passed == 0, 'label-success': testRun.passed > 0}">{{testRun.passed}}</span>
						<span class="label arrowed arrowed-in-right label-danger-border" data-ng-class="{'label-danger-empty': testRun.failed == 0, 'label-danger': testRun.failed > 0}">{{testRun.failed}}</span>
						<span class="label arrowed arrowed-in-right label-warning-border" data-ng-class="{'label-warning-empty': testRun.skipped == 0, 'label-warning': testRun.skipped > 0}">{{testRun.skipped}}</span>
						<i data-ng-class="{'fa fa-lg fa-chevron-circle-down': testRun.showDetails == false, 'fa fa-lg fa-chevron-circle-up': testRun.showDetails == true}" aria-hidden="true" data-ng-click="showDetails(testRun.id)"  class="float_right action_button"></i>
					</div>
				</div>
				<div class="col-lg-12" data-ng-if="testRun.showDetails == true" style="margin-top: 10px;">
                    <div class="row">
						<div class="col-lg-1">
							<div class="pointer" data-ng-click="predicate = 'status'; reverse=!reverse">Status&nbsp;<i class="fa fa-sort"></i></div>
						</div>
						<div class="col-lg-10">
							<div class="pointer" data-ng-click="predicate = 'name'; reverse=!reverse">Title&nbsp;<i class="fa fa-sort"></i></div>
						</div>
						<div class="col-lg-1">
							<div class="pointer" data-ng-click="predicate = 'startTime'; reverse=!reverse">Started at&nbsp;<i class="fa fa-sort"></i></div>
						</div>
					</div>
                    <div class="row test_result" data-ng-class="test.status" data-ng-repeat="(id, test) in testRun.tests | orderObjectBy:predicate:reverse">
                    	<div class="col-lg-10">
                    		<div class="clearfix">
                    			<img data-ng-if="test.status == 'IN_PROGRESS'" src="<c:url value="/resources/img/pending.gif" />" class="pending"/> {{test.name}} 
                    			<a href="" class="float_right clearfix label-success-empty" data-ng-if="test.status == 'FAILED' || test.status == 'SKIPPED'" data-ng-click="markTestAsPassed(test.id)">Mark as passed</a>
                    			<span class="float_right" data-ng-if="test.status == 'FAILED' && test.knownIssue == false" style="margin: 0 5px;">|</span>
                    			<a href="" class="float_right clearfix label-warning-empty" data-ng-if="test.status == 'FAILED' && test.knownIssue == false" data-ng-click="openKnownIssueModal(test)">Mark as known issue</a>
                    			<span data-ng-if="test.testConfig.device" class="disabled"><i class="fa fa-mobile" aria-hidden="true"></i> {{test.testConfig.device}} </span>
                    		</div>
                            <div class="result_error {{test.status}}" data-ng-if="test.message && (test.status == 'FAILED' || test.status == 'SKIPPED')">
                            	<show-more text="test.message" limit="100"></show-more>
                            </div>
                    	</div>
                    	<div class="col-lg-1 center" style="padding: 0;">
                    		<span data-ng-repeat="issue in test.workItems">
                    			<a href="{{jiraURL + '/' + issue.jiraId}}" target="_blank" data-ng-if="issue.type == 'TASK'" class="badge ng-binding" style="background-color: #337ab7;">{{issue.jiraId}}</a>
                    			<a href="{{jiraURL + '/' + issue.jiraId}}" target="_blank" data-ng-if="issue.type == 'BUG' && test.status == 'FAILED'" class="badge ng-binding" style="background-color: #d9534f;" alt="{{issue.description}}" title="{{issue.description}}">{{issue.jiraId}}</a>
                    		</span>
                    	</div>
                    	<div class="col-lg-1 center" style="padding: 0;">
                    		<div data-ng-if="test.status != STARTED">
                            	<a data-ng-if="test.logURL  && testRun.status != 'IN_PROGRESS'" href="{{test.logURL}}" target="blank">Log</a> <span data-ng-if="test.demoURL && testRun.status != 'IN_PROGRESS'">| <a href="{{test.demoURL}}" target="blank">Demo</a></span>
                       		</div>
                    	</div>
	                 </div>
				</div>
			</div>
			<div style="padding: 5px 15px;">
				<a href="#!/tests/runs/{{compareQueryString}}/compare" class="float_left" data-ng-show="testRunsToCompare.length > 1" target="blank">Compare</a>
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

              