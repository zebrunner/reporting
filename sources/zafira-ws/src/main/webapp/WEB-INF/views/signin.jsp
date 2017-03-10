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
		
		<script src="<spring:url value='/resources/js/3rd_party/jquery-2.0.3.min.js'/>" type="text/javascript"></script>
		<script type="text/javascript">
			$(document).ready(function(){
				  $('#auth-form').submit(function() {
				    var el = $(this);
				    var hash = window.location.hash;
				    if (hash) el.prop('action', el.prop('action') + '#' + unescape(hash.substring(1)));
				    return true;
				  });
				});
		</script>

		<title>Zafira</title>
	</head>
	<body class="signin">
		<div id="signin-form"> 
			<spring:url var="actionUrl" value="/login" />
			<form:form id="auth-form" modelAttribute="signinForm" action="${actionUrl}" method="POST">
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
					</a>
				</fieldset>
			</form:form>
		</div>
	
	</body>
</html>
