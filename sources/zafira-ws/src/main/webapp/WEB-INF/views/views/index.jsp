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
		<div class="col-lg-{{jobTestRuns.jobViews[0].size}}" data-ng-repeat="(env, jobTestRuns) in jobViews">
			<div class="panel panel-default">
				<div class="panel-heading">
					{{env}}
					<sec:authorize access="hasAnyRole('ROLE_ADMIN')">
					<i class="float_right fa fa-lg fa-gear pointer"
						style="line-height: 20px;"
						data-ng-click="openJobsViewModal(jobTestRuns)"></i>
					</sec:authorize>
				</div>
				<div class="panel-body">
					<div class="run_result row result_{{jobTestRuns.testRuns[jobView.job.id].status}}" data-ng-class="{'result_UNKNOWN' : jobTestRuns.testRuns[jobView.job.id] == null}" data-ng-repeat="jobView in jobTestRuns.jobViews | orderBy:'job.name'" context-menu="userMenuOptions">
						<div class="col-lg-6">
							<input type="checkbox" 
								data-ng-if="jobTestRuns.testRuns[jobView.job.id] && jenkinsEnabled"
	                            data-ng-model="isChecked"
	                            data-ng-true-value="true"
	                            data-ng-false-value="false"
	                            data-ng-click="selectJob(jobTestRuns.testRuns[jobView.job.id].id, isChecked)"
								id="{{jobTestRuns.testRuns[jobView.job.id].id}}"	                           
	                            value="{{jobTestRuns.testRuns[jobView.job.id].id}}"
	                            name="{{jobTestRuns.testRuns[jobView.job.id].id}}">
							<a href="{{jobView.job.jobURL + '/' + jobTestRuns.testRuns[jobView.job.id].buildNumber}}" target="_blank">{{jobView.job.name}}</a>
						</div>
						<div class="col-lg-3" align="center">
							<time data-ng-if="jobTestRuns.testRuns[jobView.job.id].startedAt" am-time-ago="jobTestRuns.testRuns[jobView.job.id].startedAt" title="{{ main.time | amDateFormat: 'dddd, MMMM Do YYYY, h:mm a' }}"></time>
							<span data-ng-if="!jobTestRuns.testRuns[jobView.job.id].startedAt">Long ago</span>
						</div>
						<div class="col-lg-3" align="right">
							<span class="label arrowed arrowed-in-right label-success-border" data-ng-class="{'label-success-empty': jobTestRuns.testRuns[jobView.job.id].passed == 0, 'label-success': jobTestRuns.testRuns[jobView.job.id].passed > 0}">{{jobTestRuns.testRuns[jobView.job.id].passed}}</span>
							<span class="label arrowed arrowed-in-right label-danger-border" data-ng-class="{'label-danger-empty': jobTestRuns.testRuns[jobView.job.id].failed == 0, 'label-danger': jobTestRuns.testRuns[jobView.job.id].failed > 0}">{{jobTestRuns.testRuns[jobView.job.id].failed}}</span>
							<span class="label arrowed arrowed-in-right label-warning-border" data-ng-class="{'label-warning-empty': jobTestRuns.testRuns[jobView.job.id].skipped == 0, 'label-warning': jobTestRuns.testRuns[jobView.job.id].skipped > 0}">{{jobTestRuns.testRuns[jobView.job.id].skipped}}</span>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="row" data-ng-if="jobsSelected.length">
		<div class="col-lg-12">
			<button type="button" class="btn btn-outline btn-success btn-lg btn-block" data-ng-click="rebuildJobs(jobsSelected)">Rebuild {{jobsSelected.length}} job(s)</button>
		</div>
	</div>
	<sec:authorize access="hasAnyRole('ROLE_ADMIN')">
	<br/>
	<div class="row">
		<div class="col-lg-12">
			<button type="button" class="btn btn-outline btn-success btn-lg btn-block" data-ng-click="openJobsViewModal()">Create job view</button>
		</div>
	</div>
	</sec:authorize>
	<br/>
</div>

              