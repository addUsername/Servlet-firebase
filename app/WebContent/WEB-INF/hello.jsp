<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
   
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Success!</title>
<style><%@include file="/WEB-INF/resources/css/cssHello.css"%></style>
</head>
<body>
<div class="container">
<% if (request.getAttribute("signed").equals("true")) {
%> <h1><%= request.getAttribute("name") %>, Resgistro correcto!!</h1>
<%} else { %>
<h2 class="header">Bienvenido de nuevo <%= request.getAttribute("name") %> </h2>
<%} %>
<div class="subHeader"><h2>Jwt Presente en</h2></div>
<p>Request body?</p>
<p>Request headers?</p>
<p>Cookie?</p>
<p>httpOnly?</p>
<% if (request.getAttribute("auth") != null ) {
	%><div id="tick"><%@include file="/WEB-INF/resources/svg/check_circle-24px.svg"%></div>  <%} else{
	%><div id="cross"><%@include file="/WEB-INF/resources/svg/highlight_off-24px.svg"%></div> <%} %>

<% if ( response.containsHeader("auth") ) {
	%><div id="tick"><%@include file="/WEB-INF/resources/svg/check_circle-24px.svg"%></div> <%} else{
	%><div id="cross"><%@include file="/WEB-INF/resources/svg/highlight_off-24px.svg"%></div> <%} %>
	<div id="cookie"></div>
	 <div id="cookieHttpOnly"></div>
<div class="token"><p>jwt claim contains: <%=request.getAttribute("colorFromJwt")%></p></div>
<h3 class="row"><a href="${pageContext.request.contextPath}/doc/index.html">visit</a> Docs, (needs log in)</h3>
</div>
<h1 id="js"></h1>
<div class="github"  style="background-color: rgba(235,235,235, 0); text-align:right; width: 30%;">
	  		<h2><a href="https://github.com/addUsername/Servlet-firebase">Source code
	  		<img width="20%" src="https://github.githubassets.com/images/modules/logos_page/Octocat.png" ></img></a>
	  		</h2>
</div>
</body>
<div id="a"><%@include file="/WEB-INF/resources/svg/check_circle-24px.svg"%></div> 
<div id="b"><%@include file="/WEB-INF/resources/svg/highlight_off-24px.svg"%></div>
<script>
var x = document.getElementById("cookie");
var httpsOnly = document.cookie.split("=");
var out = "";
const white = ["#1e90ff","white"];
const black = ["#757575","#a4a4a4"];
const pink = ["#c48b9f","#f8bbd0"];
const blue = ["#5d99c6","#90caf9"];

		
var root = document.documentElement;
root.style.setProperty('--primary',<%=request.getAttribute("colorFromJwt")%>[0]);
root.style.setProperty('--secondary', <%=request.getAttribute("colorFromJwt")%>[1]);
if(httpsOnly.length > 1){
	    out="No, name = "+httpsOnly[0];
		x.innerHTML = document.getElementById("a").innerHTML;
		x.classList.add("tick");
		x.classList.remove('cross');
}else{
	out = "no cookie detected";
	x.innerHTML = document.getElementById("b").innerHTML;
	x.classList.add('cross');
}
document.getElementById("cookieHttpOnly").innerText=out;
</script>
</html>