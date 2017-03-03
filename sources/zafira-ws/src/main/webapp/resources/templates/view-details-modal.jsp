<%@ page 
    language="java"
    contentType="text/html; charset=UTF-8"
    trimDirectiveWhitespaces="true"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/fragments/taglibs.jsp" %>

<div class="modal-header">
	<i class="fa fa-times cancel-button" aria-hidden="true" ng-click="cancel()"></i>
	<h3>View settings
	</h3>
</div>
<div class="modal-body">
	<form name="viewForm">
		<div class="form-group">
			<label>Name</label> 
			<input type="text" class="form-control" data-ng-model="view.name" required></input>
		</div>
		<div class="form-group">
			<label>Project</label>
			<select class="form-control" id="projectId" name="projectId"
		                        data-ng-model="view.projectId" 
		                        data-ng-options="project.id as project.name for project in projects" required>
		    </select>
		</div>
	</form>
</div>
<div class="modal-footer">
	<button data-ng-if="view.id" class="btn btn-danger" data-ng-really-message="Do you really want to delete view?" data-ng-really-click="deleteView(view)">Delete</button>
	<button class="btn btn-success" data-ng-click="createView(view)"  data-ng-disabled="viewForm.$invalid" data-ng-if="view.id == null">
    	Create
    </button>
    <button class="btn btn-success" data-ng-click="updateView(view)"  data-ng-disabled="viewForm.$invalid" data-ng-if="view.id != null">
    	Save
    </button>
</div>