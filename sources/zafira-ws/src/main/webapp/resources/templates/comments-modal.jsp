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
			<form name="commentForm" novalidate>
				<div class="form-group">
					<label>Comments</label> 
					<textarea name="comment" rows=10 class="form-control validation" data-ng-model="testRun.comments"></textarea>
				</div>
			</form>
		</div>
	</div>
</div>
<div class="modal-footer">
	<button class="btn btn-success" data-ng-click="markReviewed()" data-ng-disabled="commentForm.$invalid">
    	Mark as reviewed
    </button>
</div>