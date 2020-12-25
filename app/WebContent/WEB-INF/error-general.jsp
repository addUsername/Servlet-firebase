<%@ page language="java" isErrorPage="true" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title><%=response.getHeader("CustomErrorTitle")%></title>
<style type="text/css"><%@include file="/WEB-INF/resources/css/cssError.css"%></style>
</head>
<body>
<div class="container">
<h2 class="header" id="servererror">oh no, <%=response.getStatus()%></h2>
<h2>Server says.. null</h2>
<h3> please  <a href="${pageContext.request.contextPath}"> try again</a></h3>
</div>
<div class="github"  style="background-color: rgba(235,235,235, 0); text-align:right; width: 30%;">
	  		<h2><a href="https://github.com/addUsername/Servlet-firebase">Source code
	  		<img width="20%" src="https://github.githubassets.com/images/modules/logos_page/Octocat.png" ></img></a>
	  		</h2>
	  	</div>
</body>
</html>