<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Success!</title>
<style type="text/css">
*{
	margin: auto;
	padding: 0;
	box-sizing: border-box;
}
.container{
	width: 50%;
	overflow-wrap: break-word;
}
</style>
</head>
<body>
<div class="container">
<% if (request.getAttribute("signed").equals("true")) {
%> <h2><%= request.getAttribute("name") %>, Resgistro correcto!!</h2>
<%} else { %>
<h2>Bienvenido de nuevo <%= request.getAttribute("name") %> </h2>
<%} %>
<h2>Jwt</h2>
<p>request body</p>
<% if (request.getHeader("auth") != null ) {
%> <p>request headers</p> <%} %>
<p>value</p>
<p><%=request.getAttribute("jwt")%></p>
</div>
</body>
</html>