<%@ page 
    language="java"
    contentType="text/html; charset=UTF-8"
    trimDirectiveWhitespaces="true"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/fragments/taglibs.jsp" %>

<div data-ng-controller="UsersListCtrl">
	<div class="row">
         <div class="col-lg-12">
         	<h2><i class="fa fa-user fa-fw"></i> Users</h2>
    	</div>
    </div>
	<md-fab-speed-dial id="main-fab" md-direction="up" class="md-scale md-fab-bottom-right">
		<md-fab-trigger>
			<md-button aria-label="menu" class="md-fab" md-visible="tooltipVisible">
				<i class="fa fa-bars" aria-hidden="true"></i>
			</md-button>
		</md-fab-trigger>
		<md-fab-actions>
			<md-button aria-label="menu" class="md-fab md-raised md-mini" data-ng-click="openGroupDetailsModal()">
				<i class="fa fa-users" aria-hidden="true"></i>
			</md-button>
			<md-button aria-label="menu" class="md-fab md-raised md-mini" data-ng-click="openUserDetailsModal()">
				<i class="fa fa-plus" aria-hidden="true"></i>
			</md-button>
		</md-fab-actions>
	</md-fab-speed-dial>
	<div class="row">
		<div class="col-lg-12">
			<div class="row results_header">
				<div class="col-lg-12">
					<div class="row">
						<form data-ng-submit="loadUsers(1)">
			            	<div class="col-lg-1"><input type="text" class="form-control" placeholder="ID" data-ng-model="usersSearchCriteria.id" data-ng-change="showReset = true"></div>
			            	<div class="col-lg-3"><input type="text" class="form-control" placeholder="Username" data-ng-model="usersSearchCriteria.userName" data-ng-change="showReset = true"></div>
			            	<div class="col-lg-3"><input type="text" class="form-control" placeholder="Email" data-ng-model="usersSearchCriteria.email" data-ng-change="showReset = true"></div>
			            	<div class="col-lg-3"><input type="text" class="form-control" placeholder="First/Last name" data-ng-model="usersSearchCriteria.firstLastName" data-ng-change="showReset = true"></div>
			            	<div class="col-lg-2"><input type="date" class="form-control" placeholder="Date" data-ng-model="usersSearchCriteria.date"  style="min-width:95%" data-ng-change="showReset = true"></div>
		            		<input type="submit" data-ng-hide="true" />
		            	</form>
					</div>
					<div class="row search_controls">
						<div class="col-lg-12" align="right">
		            		<span>Found: {{totalResults}}&nbsp;</span>
							<a data-ng-if="showReset" href="" data-ng-click="resetSearchCriteria(); loadUsers(1);" class="clear-form danger">Reset&nbsp;<i class="fa fa-lg fa-times-circle"></i>&nbsp;</a>
							<a href="" data-ng-click="loadUsers(1)">Search&nbsp;<i class="fa fa-lg fa-arrow-circle-right"></i></a>
						</div>
					</div>
				</div>
            </div>
            <div class="run_result row" align="center" data-ng-show="totalResults == 0">
            	<div class="col-lg-12">No results</div>
            </div>
			<div class="run_result row " data-ng-repeat="user in users">
				<div class="col-lg-1">
				  	<span>{{user.id}}</span>
				</div>
				<div class="col-lg-3">
					<b>{{user.userName}}</b>
				</div>
				<div class="col-lg-3">
					<span>{{user.email}}</span>
				</div>
				<div  class="col-lg-3">
					<span>{{user.firstName}} {{user.lastName}}</span>
				</div>
				<div  class="col-lg-2" style="padding-right: 3px;">
					<span>{{user.createdAt | date:'MM/dd/yyyy'}}</span>
					&nbsp;
					<span data-ng-if="user.password != '' && user.password != null" class="label arrowed arrowed-in-right label-success">active</span>
					<span data-ng-if="user.password == '' || user.password == null" class="label arrowed arrowed-in-right label-danger">inactive</span>
					&nbsp;
					<i class="float_right fa fa-lg fa-gear pointer" style="line-height: 20px; margin-left: 10px; color: #333;" data-ng-click="openUserDetailsModal(user.id)"></i>
					<a data-ng-if="pefrDashboardId" href="#!/dashboards?id={{pefrDashboardId}}&userId={{user.id}}" aria-hidden="true" class="fa fa-lg fa-area-chart float_right pointer " style="line-height: 20px;  color: #333"></a>
				</div>
			</div>
			<paging class="float_right"
				  page="usersSearchCriteria.page" 
				  page-size="usersSearchCriteria.pageSize" 
				  total="totalResults"
				  show-prev-next="true"
				  show-first-last="true"
				  paging-action="loadUsers(page)" >
			</paging> 
		</div>
	</div>
</div>

              