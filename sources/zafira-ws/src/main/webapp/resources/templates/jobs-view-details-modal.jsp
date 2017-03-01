<%@ page 
    language="java"
    contentType="text/html; charset=UTF-8"
    trimDirectiveWhitespaces="true"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/fragments/taglibs.jsp" %>

<div class="modal-header">
	<i class="fa fa-times cancel-button" aria-hidden="true" ng-click="cancel()"></i>
	<h3>Jobs view settings
	</h3>
</div>
<div class="modal-body">
	<form name="jobsViewForm">
		<div class="form-group">
			<label>Environment</label> 
			<input type="text" class="form-control" data-ng-model="jobsView.env" required></input>
		</div>
		<div class="form-group">
			<label>Size</label> 
			<select class="form-control validation" data-ng-model="jobsView.size" required>
				<option data-ng-value=4>1/3 screen</option>
				<option data-ng-value=6>1/2 screen</option>
				<option data-ng-value=12>Full-screen</option>
			</select>
		</div>
		<!--  div class="form-group">
			<label>Position</label> 
			<input type="number" class="form-control validation" data-ng-model="jobsView.position" required/>
		</div-->
		<div class="form-group">
        	<label>Jobs</label>
       		<div class="modal-body ng-scope scrollable">
       			<div data-ng-repeat="job in jobs | orderBy:'name'">
                    <input type="checkbox" id="{{job.id}}"
                            data-ng-model="isChecked"
                            data-ng-true-value="true"
                            data-ng-false-value="false"
                            data-ng-change="selectJob(job.id, isChecked)"
                            value="{{job.id}}"
                            name="{{job.id}}">
                    <label for="{{job.name}}"> {{job.name}} </label>
                </div>
       		</div>
        </div>
	</form>
</div>
<div class="modal-footer">
    <button class="btn btn-success" data-ng-click="create()" data-ng-disabled="jobsViewForm.$invalid">
    	Create
    </button>
</div>