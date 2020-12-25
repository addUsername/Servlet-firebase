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
	<form action="login" method = "post" class="form" id="form">
		
		<p id="way"> choose the way(s) for jwt token </p>
			<label><input type="checkbox" name="body" value="true"> body</label>
			<label><input type="checkbox" name="header" value="true"> header</label>
			<label><input type="checkbox" name="cookie" value="true" checked> cookie</label>
			<label><input type="checkbox" name="httponly" value="true"> http only cookie</label>
		
		<p>Name:</p> <input type = "text" name = "user"/>
		<p>Password:</p> <input type = "password" name = "pass" />
		<p>Password:</p> <input type = "password" name = "pass2" />		
		<p id="expand_this">check if you want to sign up</p>		
		<input id="button" type = "submit" value = "Submit" />
		<label><input type="checkbox" name="newUser" value="true"> Sign up! </label>
	</form>
	<div class="colorPicker">
		<div class="drop" onclick="addColor(white);" id="white"><%@include file="/WEB-INF/resources/svg/drop.svg"%></div>
		<div class="drop" onclick="addColor(black);" id="black"><%@include file="/WEB-INF/resources/svg/drop.svg"%></div>
		<div class="drop" onclick="addColor(pink);" id="pink"><%@include file="/WEB-INF/resources/svg/drop.svg"%></div>
		<div class="drop" onclick="addColor(blue);" id="blue"><%@include file="/WEB-INF/resources/svg/drop.svg"%></div>
	</div>
</div>

<div class="github"  style="background-color: rgba(235,235,235, 0); text-align:right; width: 30%;">
	  		<h2><a href="https://github.com/addUsername/Servlet-firebase">Source code
	  		<img width="20%" src="https://github.githubassets.com/images/modules/logos_page/Octocat.png" ></img> </a>
	  		</h2>
	  	</div>
</body>
<script>
const colors = {"white":["#1e90ff","white"],
		"black":["#757575","#a4a4a4"], 
		"pink":["#c48b9f","#f8bbd0"], 
		"blue":["#5d99c6","#90caf9"]};
var root = document.documentElement;
var form=document.getElementById('form');
var input = document.createElement('input');
input.setAttribute('id',"deleteThis");
input.setAttribute('name', "color");
input.setAttribute('value', "white");
input.setAttribute('type', "hidden");
form.appendChild(input);


function addColor(event){
	
	var form=document.getElementById('form');
	form.removeChild(document.getElementById('deleteThis'));
	var input = document.createElement('input');
	input.setAttribute('id',"deleteThis");
	input.setAttribute('name', "color");
	input.setAttribute('value', event.id);
	input.setAttribute('type', "hidden");
	form.appendChild(input);
	
	root.style.setProperty('--primary', colors[event.id][0]);
	root.style.setProperty('--secondary', colors[event.id][1]);
}
</script>
</html>