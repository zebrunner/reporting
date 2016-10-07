<%@ page 
    language="java"
    contentType="text/html; charset=UTF-8"
    trimDirectiveWhitespaces="true"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/fragments/taglibs.jsp" %>

<div class="modal-header">
	<h3>Dashboard settings <button data-ng-if="dashboard.id" class="btn btn-xs btn-danger" data-ng-really-message="Do you really want to delete dashboard?" data-ng-really-click="deleteDashboard(dashboard)"> <i class="fa fa-times-circle"></i> delete</button></h3>
</div>
<div class="modal-body">
	<form name="dashboardForm">
		<div class="form-group">
			<label>Title</label> 
			<input type="text" class="form-control" data-ng-model="dashboard.title" required></input>
		</div>
		<div class="form-group">
			<label>Position</label> 
			<input type="number" class="form-control" data-ng-model="dashboard.position" required></input>
		</div>
	</form>
</div>
<div class="modal-footer">
	<button data-ng-if="isNew" class="btn btn-success" data-ng-click="createDashboard(dashboard)"  data-ng-disabled="dashboardForm.$invalid">
    	Create
    </button>
	<button data-ng-if="!isNew" class="btn btn-success" data-ng-click="updateDashboard(dashboard)"  data-ng-disabled="dashboardForm.$invalid">
    	Save
    </button>
    <button class="btn btn-primary" data-ng-click="cancel()">
    	Cancel
    </button>
</div>