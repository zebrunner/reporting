<%@ page 
    language="java"
    contentType="text/html; charset=UTF-8"
    trimDirectiveWhitespaces="true"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/fragments/taglibs.jsp" %>

<div class="modal-header">
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
					<textarea name="value" class="form-control validation" data-ng-model="email.text" required></textarea>
				</div>
				<div class="form-group">
					<label>Recepients</label> 
					<input name="value" type="text" class="form-control validation" data-ng-model="email.recipients" required/>
				</div>
			</form>
		</div>
	</div>
</div>
<div class="modal-footer">
	<button class="btn btn-success" data-ng-click="sendEmail()" data-ng-disabled="emailForm.$invalid">
    	Send
    </button>
     <button class="btn btn-primary" data-ng-click="cancel()">
    	Cancel
    </button>
</div>