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
			<div class="row" align="right">
            	<div class="col-lg-12">
            		<span>Found: {{totalResults}}&nbsp;</span>
					<a href="" data-ng-click="resetSearchCriteria(); loadTestCases(1);" class="clear-form danger">Reset&nbsp;<i class="fa fa-times-circle"></i>&nbsp;</a>
					<a href="" data-ng-click="loadTestCases(1)">Search&nbsp;<i class="fa fa-arrow-circle-right"></i></a>
				</div>
            </div>
			<div class="row results_header">
            	<div class="col-lg-3"><input type="text" class="form-control" placeholder="Test class" data-ng-model="testCasesSearchCriteria.testClass"></div>
            	<div class="col-lg-3"><input type="text" class="form-control" placeholder="Test method" data-ng-model="testCasesSearchCriteria.testMethod"></div>
            	<div class="col-lg-2"><input type="text" class="form-control" placeholder="Test suite" data-ng-model="testCasesSearchCriteria.testSuiteFile"></div>
            	<div class="col-lg-2"><input type="text" class="form-control" placeholder="Owner" data-ng-model="testCasesSearchCriteria.userName"></div>
            	<div class="col-lg-2"><input type="date" class="form-control" placeholder="Date" data-ng-model="testCasesSearchCriteria.date"></div>
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
					<span>{{testCase.user.userName}}</span>
				</div>
				<div  class="col-lg-2" style="padding-right: 3px;">
					<span>{{testCase.createdAt | date:'MM/dd/yyyy'}}</span>
					&nbsp;
					<i data-ng-class="{'fa fa-lg fa-sort-desc': testCase.showDetails == false, 'fa fa-lg fa-sort-asc': testCase.showDetails == true}" aria-hidden="true" data-ng-click="testCase.showDetails = !testCase.showDetails; loadTests(testCase);" class="float_right pointer"></i>
					<i data-ng-class="{'fa fa-sm fa-area-chart': true}" aria-hidden="true" data-ng-click="openPerformancePage(testCase)" class="float_right pointer" style="line-height: 20px; margin-right: 7px;"></i>
				</div>
				<div class="col-lg-12" data-ng-if="testCase.showDetails == true" style="margin-top: 10px;">
                    <div class="row test_result" data-ng-class="test.status" data-ng-repeat="test in tests[testCase.id] | orderBy:'id':true">
                    	<div class="col-lg-10">
                    		<div><img data-ng-if="test.status == 'IN_PROGRESS'" src="<c:url value="/resources/img/pending.gif" />" class="pending"/> {{test.name}}</div>
                            <div class="result_error" data-ng-if="test.message && test.status == 'FAILED'">
                            	<show-more text="test.message" limit="100"></show-more>
                            </div>
                    	</div>
                    	<div class="col-lg-2">
                    		<div class="float_right" data-ng-if="test.status != STARTED">
                            	<span class="time">{{test.finishTime | date:'MM/dd HH:mm'}}</span>
                            	&nbsp;
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

              