<%@ page 
    language="java"
    contentType="text/html; charset=UTF-8"
    trimDirectiveWhitespaces="true"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/fragments/taglibs.jsp" %>

<div class="modal-header">
	<i class="fa fa-times cancel-button" aria-hidden="true" ng-click="cancel()"></i>
	<h3>
		Setting details
	</h3>
</div>
<div class="modal-body">
	<div class="row">
		<div class="col-lg-12">
			<form name="createForm" novalidate>
				<div class="form-group">
					<label>Name</label> 
					<input name="name" type="text" class="form-control validation" data-ng-model="setting.name" required/>
				</div>
				<div class="form-group">
					<label>Value</label> 
					<input name="value" type="text" class="form-control validation" data-ng-model="setting.value"/>
				</div>
			</form>
		</div>
	</div>
</div>
<div class="modal-footer">
	<button data-ng-if="setting.id" class="btn btn-danger" data-ng-really-message="Do you really want to delete setting?" data-ng-really-click="delete(setting)">Delete</button>
	<button class="btn btn-success" data-ng-if="!setting.id" data-ng-click="create()" data-ng-disabled="createForm.$invalid">
    	Create
    </button>
    <button class="btn btn-success"  data-ng-if="setting.id" data-ng-click="update(setting)" data-ng-disabled="createForm.$invalid">
    	Save
    </button>
</div>