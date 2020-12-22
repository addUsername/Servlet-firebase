<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Loging</title>
<!-- link href="../resources/css/css.css" rel="stylesheet" type="text/css"> -->
<style><%@include file="/WEB-INF/resources/css/css.css"%></style>

</head>
<body>
	<div class="center_this">
	<div class="header"><h1>Q1 | Actividad 1</h1></div>	
	<form action = "MyServlet" method = "POST" class="form">
		<p>Name:</p> <input type = "text" name = "user"/>
		<p>Password:</p> <input type = "password" name = "pass" />
		<p>Password:</p> <input type = "password" name = "pass2" />		
		<p id="expand_this">check if you want to sign up</p>
		<input type = "submit" value = "Submit" />
		<input type="checkbox" id="newUser" value="true"/>
	</form>
	<div class="colorPicker">
		<div class="drop" id="white"><%@include file="/WEB-INF/resources/svg/drop.svg"%></div>
		<div class="drop" id="black"><%@include file="/WEB-INF/resources/svg/drop.svg"%></div>
		<div class="drop" id="pink"><%@include file="/WEB-INF/resources/svg/drop.svg"%></div>
		<div class="drop" id="blue"><%@include file="/WEB-INF/resources/svg/drop.svg"%></div>
	</div>
</div>
</body>
</html>