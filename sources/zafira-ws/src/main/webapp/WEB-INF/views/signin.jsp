<%@ page
	language="java"
	contentType="text/html; charset=UTF-8"
	trimDirectiveWhitespaces="true"
	pageEncoding="UTF-8"
	session="false"%>
<%@ include file="/WEB-INF/fragments/taglibs.jsp" %>

<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<%@ include file="/WEB-INF/fragments/meta.jsp" %>

		<link href="<spring:url value="/resources/img/favicon.ico" />" rel="icon" type="image/x-icon" />
		<link href="<spring:url value="/resources/css/signin-form.css" />" rel="stylesheet" type="text/css" />
		<link href="<spring:url value="/resources/css/bootstrap.min.css" />" rel="stylesheet" type="text/css" />

		<title>Zafira</title>
	</head>
	<body class="signin">
		<div id="signin-form"> 
			<spring:url var="actionUrl" value="/login" />
			<form:form modelAttribute="signinForm" action="${actionUrl}" method="POST">
				<fieldset style="border: none;">
					<div class="title">Zafira</div>
					<c:if test="${signinForm.signinFailed == true}">
						<div class="errors">Invalid credentials</div>
					</c:if>
					<span>Username</span><br />
					<span  class="username">
						<input type="text" name="username" id="username"/>
					</span><br />
					<span>Password</span><br />
					<span class="password">
						<input type="password" name="password" id="password" />
					</span><br />
					<!--p>Rember me <input type="checkbox" name="remember-me" /></p -->
					<a class="button">
						<button type="submit">Signin</button>
					</a><br><br>
					<span><i>Note: Use LDAP 2.0 credentials</i></span><br>
					<span><i>Note: qamon/password as alternate user</i></span><br>
				</fieldset>
			</form:form>
		</div>
	
	</body>
</html>
