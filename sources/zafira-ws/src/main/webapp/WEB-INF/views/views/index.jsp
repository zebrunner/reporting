<%@ page 
    language="java"
    contentType="text/html; charset=UTF-8"
    trimDirectiveWhitespaces="true"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/fragments/taglibs.jsp" %>

<div data-ng-controller="JobViewsCtrl">
	<div class="row">
         <div class="col-lg-12">
         	<h2><i class="fa fa-play-circle fa-fw"></i> {{view.name}}</h2>
    	</div>
    </div>
	<div class="row">
		<div class="col-lg-{{jtr.jobViews[0].size}}" data-ng-repeat="(env, jtr) in jobViews">
			<div class="panel panel-default">
				<div class="panel-heading">
					{{env}} 
					<sec:authorize access="hasAnyRole('ROLE_ADMIN')">
					<i class="float_right fa fa-lg fa-remove pointer"
						style="line-height: 20px;"
						data-ng-really-message="Do you really want to delete job view?" data-ng-really-click="deleteJobViews(env)"></i>
					</sec:authorize>
				</div>
				<div class="panel-body">
					<div class="run_result row result_{{jtr.testRuns[jobView.job.id].status}}" data-ng-repeat="jobView in jtr.jobViews | orderBy:'job.name'" context-menu="userMenuOptions">
						<div class="col-lg-6">
							<!--input type="checkbox"
								data-ng-model="isChecked"
								data-ng-true-value="true"
								data-ng-false-value="false"
								data-ng-change="selectTestRun(testRun.id, isChecked)"
								value="{{testRun.id}}" 
								name="{{testRun.id}}"
								data-ng-show="testRun.status != 'IN_PROGRESS' && testRunId == null" onclick="event.cancelBubble=true;"/ -->
							<a href="{{jobView.job.jobURL + '/' + jtr.testRuns[jobView.job.id].buildNumber}}" target="_blank">{{jobView.job.name}}</a>
						</div>
						<div class="col-lg-3" align="center">
							<time data-ng-if="jtr.testRuns[jobView.job.id].startedAt" am-time-ago="jtr.testRuns[jobView.job.id].startedAt" title="{{ main.time | amDateFormat: 'dddd, MMMM Do YYYY, h:mm a' }}"></time>
							<span data-ng-if="!jtr.testRuns[jobView.job.id].startedAt">Never</span>
						</div>
						<div class="col-lg-3" align="right">
							<span class="label arrowed arrowed-in-right label-success-border" data-ng-class="{'label-success-empty': jtr.testRuns[jobView.job.id].passed == 0, 'label-success': jtr.testRuns[jobView.job.id].passed > 0}">{{jtr.testRuns[jobView.job.id].passed}}</span>
							<span class="label arrowed arrowed-in-right label-danger-border" data-ng-class="{'label-danger-empty': jtr.testRuns[jobView.job.id].failed == 0, 'label-danger': jtr.testRuns[jobView.job.id].failed > 0}">{{jtr.testRuns[jobView.job.id].failed}}</span>
							<span class="label arrowed arrowed-in-right label-warning-border" data-ng-class="{'label-warning-empty': jtr.testRuns[jobView.job.id].skipped == 0, 'label-warning': jtr.testRuns[jobView.job.id].skipped > 0}">{{jtr.testRuns[jobView.job.id].skipped}}</span>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<sec:authorize access="hasAnyRole('ROLE_ADMIN')">
	<div class="row">
		<div class="col-lg-12">
			<button type="button" class="btn btn-outline btn-success btn-lg btn-block" data-ng-click="openJobsViewModal()">Create jobs view</button>
		</div>
	</div>
	</sec:authorize>
	<br/>
</div>

              