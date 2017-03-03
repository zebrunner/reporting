<%@ page 
    language="java"
    contentType="text/html; charset=UTF-8"
    trimDirectiveWhitespaces="true"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/fragments/taglibs.jsp" %>

<div class="modal-header">
	<i class="fa fa-times cancel-button" aria-hidden="true" ng-click="cancel()"></i>
	<h3>Job view settings
	</h3>
</div>
<div class="modal-body">
	<form name="jobsViewForm">
		<div class="form-group">
			<label>Environment</label> 
			<input type="text" class="form-control" data-ng-model="jobView.env" required></input>
		</div>
		<div class="form-group">
			<label>Size</label> 
			<select class="form-control validation" data-ng-model="jobView.size" required>
				<option data-ng-value=4>1/3 screen</option>
				<option data-ng-value=6>1/2 screen</option>
				<option data-ng-value=12>Full-screen</option>
			</select>
		</div>
		<div class="form-group">
			<label>Position</label> 
			<input type="number" class="form-control validation" data-ng-model="jobView.position" required/>
		</div>
		<div class="form-group">
        	<label>Jobs ({{jobsSelected.length}})</label>
        	<input class="form-control" name="jobFilter" data-ng-model="jobFilter" placeholder="Job filter" />
       		<div class="modal-body ng-scope scrollable">
       			<div data-ng-repeat="job in jobs | orderBy:'name' | filter:{name:jobFilter}:false">
                    <input type="checkbox" id="{{job.id}}"
                            data-ng-model="isChecked"
                            data-ng-checked="{{jobsSelected.indexOf(job.id) >= 0}}"
                            data-ng-true-value="true"
                            data-ng-false-value="false"
                            data-ng-click="selectJob(job.id, isChecked)"
                            value="{{job.id}}"
                            name="{{job.id}}">
                    <label for="{{job.name}}"> {{job.name}}</label>
                </div>
       		</div>
        </div>
	</form>
</div>
<div class="modal-footer">
	<button data-ng-if="edit" class="btn btn-danger" data-ng-really-message="Do you really want to delete job view?" data-ng-really-click="deleteJobView(jobView.env)">Delete</button>
    <button data-ng-if="!edit" class="btn btn-success" data-ng-click="createJobView()" data-ng-disabled="jobsViewForm.$invalid || jobsSelected.length == 0">
    	Create
    </button><button data-ng-if="edit" class="btn btn-success" data-ng-click="updateJobView(jobView.env)" data-ng-disabled="jobsViewForm.$invalid || jobsSelected.length == 0">
    	Save
    </button>
</div>