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
    <md-fab-speed-dial id="main-fab" md-direction="up" class="md-scale md-fab-bottom-right">
		 <md-fab-trigger>
			 <md-button aria-label="menu" class="md-fab" md-visible="tooltipVisible">
				 <i class="fa fa-bars" aria-hidden="true"></i>
			 </md-button>
		 </md-fab-trigger>
		 <md-fab-actions>
		 	 <md-button data-ng-if="jenkinsEnabled" aria-label="menu" class="md-fab md-raised md-mini" data-ng-click="rebuildJobs()">
				 <i class="fa fa-refresh" aria-hidden="true"></i>
			 </md-button>
		 	 <sec:authorize access="hasAnyRole('ROLE_ADMIN')">
			 <md-button aria-label="menu" class="md-fab md-raised md-mini" data-ng-click="openJobsViewModal()">
				 <i class="fa fa-plus" aria-hidden="true"></i>
			 </md-button>
			 </sec:authorize>
		 </md-fab-actions>
	</md-fab-speed-dial>
	 <div class="run_result row" align="center" data-ng-if="UtilService.isEmpty(jobViews)">
    	<div class="col-lg-12">No job views available</div>
    </div>
	<div class="row">
		<div class="col-lg-{{envJobViews[0].size}}" data-ng-repeat="(env, envJobViews) in jobViews">
			<div class="panel panel-default">
				<div class="panel-heading">
					{{env}} <span data-ng-if="jenkinsEnabled"><a href="" data-ng-click="selectForRerun(env, 'All')">All</a> | <a href="" data-ng-click="selectForRerun(env, 'None')">None</a> | <a href="" data-ng-click="selectForRerun(env, 'Failed')">Failed</a></span>
					<sec:authorize access="hasAnyRole('ROLE_ADMIN')">
					<i class="float_right fa fa-lg fa-gear pointer"
						style="line-height: 20px;"
						data-ng-click="openJobsViewModal(envJobViews)"></i>
					</sec:authorize>
				</div>
				<div class="panel-body">
					<div class="run_result row result_{{jobView.testRun.status}}" data-ng-class="{'result_UNKNOWN' : jobView.testRun == null}" data-ng-repeat="jobView in envJobViews | orderBy:'job.name'" context-menu="userMenuOptions">
						<div class="col-lg-6">
							<input type="checkbox" 
	                            data-ng-model="jobView.testRun.rebuild"
	                            data-ng-if="jobView.testRun != null && jobView.testRun.status != 'IN_PROGRESS' && jenkinsEnabled"
	                            data-ng-true-value="true"
	                            data-ng-false-value="false"
								id="{{jobView.testRun.id}}"	                           
	                            value="{{jobView.testRun.id}}"
	                            name="{{jobView.testRun.id}}">
	                        <img src="<c:url value="/resources/img/pending.gif" />" class="pending" data-ng-if="jobView.testRun.status == 'IN_PROGRESS'"/>
							<a href="{{jobView.job.jobURL + '/' + jobView.testRun.buildNumber}}" target="_blank">{{UtilService.truncate(jobView.job.name, 35)}}</a>
						</div>
						<div class="col-lg-3" align="center">
							<time data-ng-if="jobView.testRun.startedAt" am-time-ago="jobView.testRun.startedAt" title="{{ main.time | amDateFormat: 'dddd, MMMM Do YYYY, h:mm a' }}"></time>
							<span data-ng-if="!jobView.testRun.startedAt">Long ago</span>
						</div>
						<div class="col-lg-3" align="right">
							<span class="label arrowed arrowed-in-right label-success-border" data-ng-class="{'label-success-empty': jobView.testRun.passed == 0, 'label-success': jobView.testRun.passed > 0}">{{jobView.testRun.passed}}</span>
							<span class="label arrowed arrowed-in-right label-danger-border" data-ng-class="{'label-danger-empty': jobView.testRun.failed == 0, 'label-danger': jobView.testRun.failed > 0}">{{jobView.testRun.failed}}</span>
							<span class="label arrowed arrowed-in-right label-warning-border" data-ng-class="{'label-warning-empty': jobView.testRun.skipped == 0, 'label-warning': jobView.testRun.skipped > 0}">{{jobView.testRun.skipped}}</span>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

              