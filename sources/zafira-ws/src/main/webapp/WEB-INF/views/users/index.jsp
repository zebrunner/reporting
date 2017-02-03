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
			<md-button aria-label="menu" class="md-fab md-raised md-mini" data-ng-click="openGroupDetailsModal(users)">
				<i class="fa fa-users" aria-hidden="true"></i>
			</md-button>
			<md-button aria-label="menu" class="md-fab md-raised md-mini" data-ng-click="openUserDetailsModal()">
				<i class="fa fa-plus" aria-hidden="true"></i>
			</md-button>
		</md-fab-actions>
	</md-fab-speed-dial>
	<div class="row">
		<div class="col-lg-12">
			<div class="row" align="right">
            	<div class="col-lg-12">
            		<span>Found: {{totalResults}}&nbsp;</span>
					<a href="" data-ng-click="resetSearchCriteria(); loadUsers(1);" class="clear-form danger">Reset&nbsp;<i class="fa fa-times-circle"></i>&nbsp;</a>
					<a href="" data-ng-click="loadUsers(1)">Search&nbsp;<i class="fa fa-arrow-circle-right"></i></a>
				</div>
            </div>
			<div class="row results_header">
            	<form data-ng-submit="loadUsers(1)">
	            	<div class="col-lg-1"><input type="text" class="form-control" placeholder="ID" data-ng-model="usersSearchCriteria.id"></div>
	            	<div class="col-lg-3"><input type="text" class="form-control" placeholder="Username" data-ng-model="usersSearchCriteria.userName"></div>
	            	<div class="col-lg-3"><input type="text" class="form-control" placeholder="Email" data-ng-model="usersSearchCriteria.email"></div>
	            	<div class="col-lg-3"><input type="text" class="form-control" placeholder="First/Last name" data-ng-model="usersSearchCriteria.firstLastName"></div>
	            	<div class="col-lg-2"><input type="date" class="form-control" placeholder="Date" data-ng-model="usersSearchCriteria.date"></div>
            	<input type="submit" data-ng-hide="true" />
            	</form>
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
					<i class="float_right fa fa-gear pointer" style="line-height: 20px; margin-left: 7px;" data-ng-click="openUserDetailsModal(user.id)"></i>
					<a data-ng-if="pefrDashboardId" href="#!/dashboards?id={{pefrDashboardId}}&userId={{user.id}}" aria-hidden="true" class="fa fa-sm fa-area-chart float_right pointer " style="line-height: 20px; color: black;"></a>
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

              