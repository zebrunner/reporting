<%@ page 
    language="java"
    contentType="text/html; charset=UTF-8"
    trimDirectiveWhitespaces="true"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/fragments/taglibs.jsp" %>

<div class="modal-header">
	<i class="fa fa-times cancel-button" aria-hidden="true" ng-click="cancel()"></i>
	<h3>
		Known issues <br/><span data-ng-class="{'success_text': isConnectedToJira == true, 'danger_text': isConnectedToJira == false}"
								data-ng-show="isDataLoaded"><span data-ng-show="isConnectedToJira">Connected </span><span data-ng-hide="isConnectedToJira">Unconnected </span>to Jira</span>
					 <span data-ng-show="!isDataLoaded || !isIssueFound"><md-progress-circular md-mode="indeterminate" md-diameter="20"></md-progress-circular></span>
	</h3>
</div>
<div class="modal-body">
	<div class="row" data-ng-repeat="issue in knownIssues">
		<div class="col-lg-12">
			<div class="row">
				<div class="col-lg-12"><b class="settings-line">{{issue.jiraId}}</b> {{issue.description}} <i class="float_right fa fa-times pointer settings-line" data-ng-really-message="Do you really want to delete known issue?" data-ng-really-click="deleteKnownIssue(issue.id)"></i></div>
			</div>
			<hr/>
		</div>
	</div>
	<div class="row">
		<div class="col-lg-12">
			<form name="knownIssueForm" novalidate>
				<div class="form-group">
					<label>Jira ID</label>
					<input type="text" class="form-control validation" data-ng-model="newKnownIssue.jiraId" data-ng-disabled="isFieldsDisabled" data-ng-change="onChangeAction()" required/>
					<span style="color: red" data-ng-show="! isJiraIdExists">'{{newKnownIssue.jiraId}}' does not exist!</span>
					<span style="color: red" data-ng-show="isJiraIdClosed">'{{newKnownIssue.jiraId}}' is closed!</span>
				</div>
				<div class="form-group">
					<label>Description</label> 
					<textarea class="form-control validation" rows="8" data-ng-model="newKnownIssue.description" data-ng-disabled="isFieldsDisabled" required></textarea>
				</div>
				<div>
					<label>Blocker</label>
					<input type="checkbox" data-ng-model="newKnownIssue.blocker"/>
					<span class="success_text" style="float: right">{{newKnownIssue.assigneeMessage}}</span>
				</div>
			</form>
		</div>
	</div>
</div>
<div class="modal-footer">
	<button class="btn btn-success" data-ng-click="createKnownIssue()" data-ng-disabled="knownIssueForm.$invalid">
    	Create
    </button>
</div>