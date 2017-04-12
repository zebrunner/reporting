<%@ page 
    language="java"
    contentType="text/html; charset=UTF-8"
    trimDirectiveWhitespaces="true"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/fragments/taglibs.jsp" %>

<div data-ng-controller="TestCasesListCtrl">
	<div class="row">
         <div class="col-lg-12">
         	<h2><i class="fa fa-check-square fa-fw"></i> Test cases</h2>
         </div>
    </div>
	<div class="row">
		<div class="col-lg-12">
			<div class="row results_header">
				<div class="col-lg-12">
					<div class="row">
						<form data-ng-submit="loadTestCases(1)">
			            	<div class="col-lg-3"><input type="text" class="form-control" placeholder="Test class" data-ng-model="testCasesSearchCriteria.testClass" data-ng-change="showReset = true"></div>
			            	<div class="col-lg-3"><input type="text" class="form-control" placeholder="Test method" data-ng-model="testCasesSearchCriteria.testMethod" data-ng-change="showReset = true"></div>
			            	<div class="col-lg-2"><input type="text" class="form-control" placeholder="Test suite" data-ng-model="testCasesSearchCriteria.testSuiteFile" data-ng-change="showReset = true"></div>
			            	<div class="col-lg-2"><input type="text" class="form-control" placeholder="Owner" data-ng-model="testCasesSearchCriteria.username" data-ng-change="showReset = true"></div>
			            	<div class="col-lg-2"><input type="date" class="form-control" placeholder="Date" data-ng-model="testCasesSearchCriteria.date" style="min-width:95%" data-ng-change="showReset = true"></div>
		            		<input type="submit" data-ng-hide="true" />
		            	</form>
					</div>
					<div class="row search_controls" align="right">
						<div class="col-lg-12">
		            		<span>Found: {{totalResults}}&nbsp;</span>
							<a data-ng-if="showReset" href="" data-ng-click="resetSearchCriteria(); loadTestCases(1);" class="clear-form danger">Reset&nbsp;<i class="fa fa-lg fa-times-circle"></i>&nbsp;</a>
							<a href="" data-ng-click="loadTestCases(1)">Search&nbsp;<i class="fa fa-lg fa-arrow-circle-right"></i></a>
						</div>
					</div>
				</div>
            </div>
            <div class="run_result row" align="center" data-ng-show="totalResults == 0">
            	<div class="col-lg-12">No results</div>
            </div>
			<div class="run_result row " data-ng-class="'result_' + testCase.status" data-ng-repeat="testCase in testCases" context-menu="menuOptions">
				<div class="col-lg-3">
				  	<b>{{getClassName(testCase.testClass)}}</b>
				</div>
				<div class="col-lg-3">
					<span>{{UtilService.truncate(testCase.testMethod, 40)}}</span>
				</div>
				<div class="col-lg-2">
					<span class="badge">{{testCase.testSuite.fileName}}</span>
				</div>
				<div  class="col-lg-2">
					<span>{{testCase.user.username}}</span>
				</div>
				<div  class="col-lg-2" style="padding-right: 3px;">
					<span>{{testCase.createdAt | date:'MM/dd/yyyy'}}</span>
					&nbsp;
					<i data-ng-class="{'fa fa-lg fa-chevron-circle-down': testCase.showDetails == false, 'fa fa-lg fa-chevron-circle-up': testCase.showDetails == true}" aria-hidden="true" data-ng-click="testCase.showDetails = !testCase.showDetails; loadTests(testCase);" class="float_right action_button separator10"></i>
					<i data-ng-class="{'fa fa-lg fa-area-chart': true}" aria-hidden="true" data-ng-click="openPerformancePage(testCase)" class="float_right action_button"></i>
				</div>
				<div class="col-lg-12" data-ng-if="testCase.showDetails == true" style="margin-top: 10px;">
                    <div class="row test_result" data-ng-class="test.status" data-ng-repeat="test in tests[testCase.id] | orderBy:'id':true">
                    	<div class="col-md-10">
                    		<div><img data-ng-if="test.status == 'IN_PROGRESS'" src="<c:url value="/resources/img/pending.gif" />" class="pending"/> {{test.name}}</div>
                            <div class="result_error {{test.status}}" data-ng-if="test.message && (test.status == 'FAILED' || test.status == 'SKIPPED')">
                            	<show-more text="test.message" limit="100"></show-more>
                            </div>
                    	</div>
                    	<div class="col-md-1 center" style="padding: 0;">
                    		<span class="time">
								<time am-time-ago="test.finishTime" title="{{ main.time | amDateFormat: 'dddd, MMMM Do YYYY, h:mm a' }}"></time>
							</span>
                    	</div>
                    	<div class="col-md-1 center" style="padding: 0;">
                    		<div data-ng-if="test.status != STARTED">
                            	<a data-ng-if="test.logURL && testRun.status != 'IN_PROGRESS' && test.status != 'IN_PROGRESS'" href="{{test.logURL}}" target="blank">Log</a> <span data-ng-if="test.demoURL && testRun.status != 'IN_PROGRESS' && test.status != 'IN_PROGRESS'">| <a href="{{test.demoURL}}" target="blank">Demo</a></span>
                       		</div>
                    	</div>
	                 </div>
				</div>
			</div>
			<paging class="float_right"
				  page="testCasesSearchCriteria.page" 
				  page-size="testCasesSearchCriteria.pageSize" 
				  total="totalResults"
				  show-prev-next="true"
				  show-first-last="true"
				  paging-action="loadTestCases(page)" >
			</paging> 
		</div>
	</div>
</div>

              