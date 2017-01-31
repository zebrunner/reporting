<%@ page 
    language="java"
    contentType="text/html; charset=UTF-8"
    trimDirectiveWhitespaces="true"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/fragments/taglibs.jsp" %>

<div class="modal-header">
	<i class="fa fa-times cancel-button" aria-hidden="true" ng-click="cancel()"></i>
	<h3>
		Device details
	</h3>
</div>
<div class="modal-body">
	<div class="row">
		<div class="col-lg-12">
			<form name="createForm" novalidate>
				<div class="form-group">
					<label>Model</label> 
					<input name="name" type="text" class="form-control validation" data-ng-model="device.model" required/>
				</div>
				<div class="form-group">
					<label>Serial</label> 
					<input name="value" type="text" class="form-control validation" data-ng-model="device.serial" required/>
				</div>
				<div class="form-group">
					<label>Enabled</label>
					<select class="form-control validation" data-ng-model="device.enabled" required>
						<option data-ng-value="true">TRUE</option>
						<option data-ng-value="false">FALSE</option>
					</select>
				</div>
				<div class="form-group">
					<label>Last status</label>
					<select class="form-control validation" data-ng-model="device.lastStatus" required>
						<option data-ng-value="true">CONNECTED</option>
						<option data-ng-value="false">DISCONNECTED</option>
					</select>
				</div>
			</form>
		</div>
	</div>
</div>
<div class="modal-footer">
	<button data-ng-if="device.id" class="btn btn-danger" data-ng-really-message="Do you really want to delete device?" data-ng-really-click="delete(device)">Delete</button>
	<button class="btn btn-success" data-ng-if="!device.id" data-ng-click="create()" data-ng-disabled="createForm.$invalid">
    	Create
    </button>
    <button class="btn btn-success"  data-ng-if="device.id" data-ng-click="update(device)" data-ng-disabled="createForm.$invalid">
    	Save
    </button>
</div>