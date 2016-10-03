<%@ page 
    language="java"
    contentType="text/html; charset=UTF-8"
    trimDirectiveWhitespaces="true"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/fragments/taglibs.jsp" %>

<div class="modal-header">
	<h3>Widget settings <button data-ng-if="!isNew" class="btn btn-xs btn-danger" data-ng-really-message="Do you really want to delete widget from dashboard?" data-ng-really-click="deleteDashboardWidget(widget)"> <i class="fa fa-times-circle"></i> delete</button></h3>
</div>
<div class="modal-body">
	<form name="dashboardWidgetForm">
		<div data-ng-if="isNew" class="form-group">
			<label>Widget</label> 
			<select class="form-control validation" data-ng-model="widget.id" required>
				<option data-ng-repeat="w in widgets | orderBy:'title'" value="{{w.id}}">{{w.title}}</option>
			</select>
		</div>
		<div data-ng-if="!isNew" class="form-group">
			<label>Widget</label> 
			<input type="text" class="form-control" data-ng-model="widget.title" required disabled></input>
		</div>
		<div class="form-group">
			<label>Size</label> 
			<select class="form-control validation" data-ng-model="widget.size" required>
				<option value=4>S</option>
				<option value=8>M</option>
				<option value=12>L</option>
			</select>
		</div>
		<div class="form-group">
			<label>Position</label> 
			<input type="text" class="form-control validation" data-ng-model="widget.position" required/>
		</div>
	</form>
</div>
<div class="modal-footer">
	<button data-ng-if="isNew" class="btn btn-success" data-ng-click="addDashboardWidget(widget)"  data-ng-disabled="dashboardWidgetForm.$invalid">
    	Add
    </button>
    <button data-ng-if="!isNew" class="btn btn-success" data-ng-click="updateDashboardWidget(widget)"  data-ng-disabled="dashboardWidgetForm.$invalid">
    	Save
    </button>
    <button class="btn btn-primary" data-ng-click="cancel()">
    	Cancel
    </button>
</div>