<%@ page 
    language="java"
    contentType="text/html; charset=UTF-8"
    trimDirectiveWhitespaces="true"
    pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/fragments/taglibs.jsp" %>

<div data-ng-controller="UsersProfileCtrl">
	<div class="row">
         <div class="col-lg-12">
         	<h2><i class="fa fa-user fa-fw"></i> User profile</h2>
    	</div>
    </div>
	<div class="row">
		<div class="col-lg-4">
			<form name="userForm">
				<div class="form-group">
					<label>Username</label> 
					<input type="text" class="form-control" data-ng-model="user.username" required disabled></input>
				</div>
				<div class="form-group">
					<label>Email</label> 
					<input type="text" class="form-control" data-ng-model="user.email" required></input>
				</div>
				<div class="form-group">
					<label>First name</label> 
					<input type="text" class="form-control" data-ng-model="user.firstName"></input>
				</div>
				<div class="form-group">
					<label>Last name</label> 
					<input type="text" class="form-control" data-ng-model="user.lastName"></input>
				</div>
				<div class="form-group">
					<button class="btn btn-success float_right" data-ng-click="updateUser(user)"  data-ng-disabled="userForm.$invalid">
				    	Save
				    </button>
			    </div>
			    <div class="clearfix"></div>
			</form>
		</div>
		<div class="col-lg-4">
			<form name="userPasswordForm">
				<div class="form-group">
					<label>New password</label> 
					<input type="password" class="form-control" data-ng-model="newPassword" required data-ng-minlength="6"></input>
				</div>
				<div class="form-group">
					<label>Confirm password</label> 
					<input type="password" class="form-control" data-ng-model="confirmPassword" required data-ng-minlength="6"></input>
				</div>
				<button class="btn btn-warning float_right" data-ng-click="updatePassword(newPassword, confirmPassword)"  data-ng-disabled="userPasswordForm.$invalid">
			    	Update password
			    </button>
			</form>
		</div>
	</div>
	<br/>
</div>

              