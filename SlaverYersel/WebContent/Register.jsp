<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
    <%@ page import="com.slaver.sam.stores.*" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head id="Header">

	
	<title>Tweets</title>
</head>

<body background="BG2.jpg">

	<div id="header">
		<h1>Such Details</h1>
	</div>
	
	<div id=navigation>
		<a href="login.jsp">Back</a>
	</div>

 	<div id="mainContent">
		<% /* Display welcome message to user */
		String message = (String)request.getAttribute("warningMessage");
		if(message != null)
		if(message.equals("null"))
		{
			//do nothing
		}else{ //do only if there is a content
			%>
			<FONT COLOR="FF0000"><%=request.getAttribute("warningMessage")%></FONT>
			<%
		}
		%>
		
		<form action="members" method="post" name="signMeUp">
			<table>
				<tr>
					<td>
						Your Email Address:
					</td>
					<td>
						<input type="text" name="NewEmailAddress">
					</td>
				</tr>
				<tr>
					<td>
						Much username:
					</td>
					<td>
						<input type="text" name="userName">
					</td>
				</tr>
				<tr>
					<td>
						A Password:
					</td>
					<td>
						<input type="password" name="NewPassword">
					</td>
				</tr>
				<tr>
					<td>
						so Duplicate:
					</td>
					<td>
						<input type="password" name="RepeatPassword">
					</td>
				</tr>
				<tr>
					<td>
					</td>
					<td>
						<input type="submit" value="to the mooooon">
					</td>
				</tr>
			</table>
		</form>
	</div>
	

</body>
</html>