<%@ page 
    language="java"
    contentType="text/html; charset=UTF-8"
    trimDirectiveWhitespaces="true"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/fragments/taglibs.jsp" %>

<div class="modal-header">
	<i class="fa fa-times cancel-button" aria-hidden="true" ng-click="cancel()"></i>
	<h3>
		Known issues
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
					<input type="text" class="form-control validation" data-ng-model="newKnownIssue.jiraId" data-ng-change="createExistsKnowIssue(true)" required/>
					<span style="color: red" data-ng-show="! isJiraIdExists">Jira ID does not exist!</span>
					<span style="color: red" data-ng-show="isJiraIdClosed">Jira ID is closed!</span>
				</div>
				<div class="form-group">
					<label>Description</label> 
					<textarea class="form-control validation" rows="8" data-ng-model="newKnownIssue.description" data-ng-disabled="descriptionFieldIsDisabled" required></textarea>
				</div>
				<div>
					<label>Blocker</label>
					<input type="checkbox" data-ng-model="newKnownIssue.blocker"/>
				</div>
				<div data-ng-hide="isConnectedToJira" layout="row" layout-sm="column" layout-align="space-around">
					<md-progress-circular md-mode="indeterminate"></md-progress-circular>
				</div>
			</form>
		</div>
	</div>
</div>
<div class="modal-footer">
	<button class="btn btn-success" data-ng-click="createExistsKnowIssue(false)" data-ng-disabled="knownIssueForm.$invalid || descriptionFieldIsDisabled">
    	Create
    </button>
</div>