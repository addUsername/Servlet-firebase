<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<form action = "MyServlet" method = "POST">
         Name: <input type = "text" name = "user"/>
         <br />
         Password: <input type = "password" name = "pass" />
         <br />
         Password: <input type = "password" name = "pass2" />
         <br />
         <input type="checkbox" name="newUser" value="true"> check if you want to sign up 
         <br />
         <input type = "submit" value = "Submit" />
      </form>
</body>
</html>