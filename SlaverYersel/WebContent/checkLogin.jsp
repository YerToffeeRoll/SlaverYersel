<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Checking Login</title>

<!-- Le styles -->
    <link href="css/bootstrap.css" rel="stylesheet">
    <link href="css/bootstrap-responsive.css" rel="stylesheet">
    
</head>
<body>
<%
            String username = request.getParameter("username");
            String password = request.getParameter("password");
           out.println("Checking login<br>");
            if (username == null || password == null) {
 
                out.print("Invalid paramters ");
            }
 
            // Here you put the check on the username and password
            if (username.toLowerCase().trim().equals("admin") && password.toLowerCase().trim().equals("admin")) {
                out.println("Welcome " + username + " <a href=\"index.jsp\">Back to main</a>");
                session.setAttribute("username", username);
            }
           else 
               {
                out.println("<p> Invalid username and password </p>");
           }
            
           
 
 
 
 
%> 

</body>
</html>