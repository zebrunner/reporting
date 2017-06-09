<%@ page 
    language="java"
    contentType="text/html; charset=UTF-8"
    trimDirectiveWhitespaces="true"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/fragments/taglibs.jsp" %>

<div class="modal-header">
	<i class="fa fa-times cancel-button" aria-hidden="true" ng-click="cancel()"></i>
	<h3>
		{{title}}
	</h3>
</div>
<div class="modal-body">
	<div class="row">
		<div class="col-lg-12">
			<form name="emailForm" novalidate>
				<div class="form-group" data-ng-if="subjectRequired">
					<label>Subject</label> 
					<input name="value" type="text" class="form-control validation" data-ng-model="email.subject" required/>
				</div>
				<div class="form-group" data-ng-if="textRequired">
					<label>Text</label>
					<text-angular data-ng-model="email.text" options=""></text-angular> 
					<!-- textarea name="value" class="form-control validation" data-ng-model="email.text" required></textarea -->
				</div>
				<div class="form-group">
					<label>Recepients ({{email.recipients.length}})</label> 
					<%--<input name="value" type="text" class="form-control validation" data-ng-model="email.recipients" required/>--%>
					<%--<md-chips ng-model="email.recipients" name="value" placeholder="Space separated" md-separator-keys="keys"></md-chips>--%>
					<md-content class="md-padding autocomplete" layout="column" style="padding-top: 0px; background-color: white">
						<md-chips ng-model="users"
								  md-autocomplete-snap
								  md-transform-chip="checkAndTransformRecipient($chip)"
								  md-on-remove="removeRecipient($chip)"
								  md-separator-keys="keys">
							<md-chip-template>
								{{$chip.email}}
							</md-chip-template>
							<md-autocomplete
									md-search-text="searchText"
									md-items="user in querySearch(searchText)"
									md-item-text="user.email"
									md-selected-item="currentUser"
									md-no-cache="true"
									<%--md-autoselect--%>
									placeholder="add email">
								<md-item-template>
									<span>{{user.email}}</span>
								</md-item-template>
								<md-not-found>
									No users matching "{{searchText}}" were found.
								</md-not-found>
							</md-autocomplete>
						</md-chips>
					</md-content>
				</div>
			</form>
		</div>
	</div>
</div>
<div class="modal-footer">
	<button class="btn btn-success" data-ng-click="sendEmail()" data-ng-disabled="emailForm.$invalid">
    	Send
    </button>
</div>