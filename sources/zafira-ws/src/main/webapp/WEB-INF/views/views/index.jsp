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
					<i class="float_right fa fa-lg fa-gear pointer"
						style="line-height: 20px;"
						data-ng-click="openJobsViewModal(jtr)"></i>
					</sec:authorize>
				</div>
				<div class="panel-body">
					<div class="run_result row result_{{jtr.testRuns[jobView.job.id].status}}" data-ng-class="{'result_UNKNOWN' : jtr.testRuns[jobView.job.id] == null}" data-ng-repeat="jobView in jtr.jobViews | orderBy:'job.name'" context-menu="userMenuOptions">
						<div class="col-lg-6">
							<input type="checkbox" 
								data-ng-if="jtr.testRuns[jobView.job.id] && jenkinsEnabled"
	                            data-ng-model="isChecked"
	                            data-ng-true-value="true"
	                            data-ng-false-value="false"
	                            data-ng-click="selectJob(jtr.testRuns[jobView.job.id].id, isChecked)"
								id="{{jtr.testRuns[jobView.job.id].id}}"	                           
	                            value="{{jtr.testRuns[jobView.job.id].id}}"
	                            name="{{jtr.testRuns[jobView.job.id].id}}">
							<a href="{{jobView.job.jobURL + '/' + jtr.testRuns[jobView.job.id].buildNumber}}" target="_blank">{{jobView.job.name}}</a>
						</div>
						<div class="col-lg-3" align="center">
							<time data-ng-if="jtr.testRuns[jobView.job.id].startedAt" am-time-ago="jtr.testRuns[jobView.job.id].startedAt" title="{{ main.time | amDateFormat: 'dddd, MMMM Do YYYY, h:mm a' }}"></time>
							<span data-ng-if="!jtr.testRuns[jobView.job.id].startedAt">Long ago</span>
						</div>
						<div class="col-lg-3" align="right">
							<span class="label arrowed arrowed-in-right label-success-border" data-ng-class="{'label-success-empty': jtr.testRuns[jobView.job.id].passed == 0, 'label-success': jtr.testRuns[jobView.job.id].passed > 0}">{{jtr.testRuns[jobView.job.id].passed}}</span>
							<span class="label arrowed arrowed-in-right label-danger-border" data-ng-class="{'label-danger-empty': jtr.testRuns[jobView.job.id].failed == 0, 'label-danger': jtr.testRuns[jobView.job.id].failed > 0}">{{jtr.testRuns[jobView.job.id].failed}}</span>
							<span class="label arrowed arrowed-in-right label-warning-border" data-ng-class="{'label-warning-empty': jtr.testRuns[jobView.job.id].skipped == 0, 'label-warning': jtr.testRuns[jobView.job.id].skipped > 0}">{{jtr.testRuns[jobView.job.id].skipped}}</span>
						</div>
					</div>
					<div align="right">
						<button type="button" class="btn btn-outline btn-success btn-xs" data-ng-click="rebuildJobs(jobsSelected)" data-ng-if="jobsSelected.length > 0 && jenkinsEnabled">Rebuild {{jobsSelected.length}} job(s)</button>
					</div>
				</div>
			</div>
		</div>
	</div>
	<sec:authorize access="hasAnyRole('ROLE_ADMIN')">
	<div class="row">
		<div class="col-lg-12">
			<button type="button" class="btn btn-outline btn-success btn-lg btn-block" data-ng-click="openJobsViewModal()">Create job view</button>
		</div>
	</div>
	</sec:authorize>
	<br/>
</div>

              