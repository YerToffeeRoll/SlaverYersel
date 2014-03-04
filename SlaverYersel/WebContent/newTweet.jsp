<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
    <%@ page import="com.slaver.sam.stores.*" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head id="Header">
	
	<title>New Tweet</title>
	
	<!-- Le styles -->
    <link href="css/bootstrap.css" rel="stylesheet">
    <link href="css/bootstrap-responsive.css" rel="stylesheet">
</head>

<%
	//if the username is null, meaning there is no user logged in - redirect user to login screen
	HttpSession Session = request.getSession();
	LoginBean thisUser = (LoginBean) Session.getAttribute("currentUser"); //current user bean is now saved in session
		
	
	String userName = thisUser.getUsername();
	if(userName == null)
	{
		System.out.println("userName is null!");
		RequestDispatcher rd = request.getRequestDispatcher("/login.jsp"); 
		rd.forward(request, response);
	}else{
		System.out.println("userName: " + userName);
	}
%>

<body>

<div class="navbar-wrapper">
      <!-- Wrap the .navbar in .container to center it within the absolutely positioned parent. -->
      <div class="container">

        <div class="navbar navbar-inverse">
          <div class="navbar-inner">
            <!-- Responsive Navbar Part 1: Button for triggering responsive navbar (not covered in tutorial). Include responsive CSS to utilize. -->
            <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
              <span class="icon-bar"></span>
            </button>
            <a class="brand" href="#">SlaverYersel</a>
            <!-- Responsive Navbar Part 2: Place all navbar contents you want collapsed withing .navbar-collapse.collapse. -->
            <div class="nav-collapse collapse">
              <ul class="nav">
                <li class="active"><a href="#">Home</a></li>
					</form>
                <li><a href="Register.jsp">Register</a></li>
                <li><a href= "logoff.jsp">Logoff</a></li>
            </div><!--/.nav-collapse -->
          </div><!-- /.navbar-inner -->
        </div><!-- /.navbar -->

      </div> <!-- /.container -->
    </div><!-- /.navbar-wrapper -->


    <!-- Marketing messaging and featurettes
    ================================================== -->
    <!-- Wrap the rest of the page in another container to center all the content. -->

    <div class="container marketing">

      <!-- START THE FEATURETTES -->

      <hr class="featurette-divider">

      <div class="featurette">
        <img class="featurette-image pull-right" src="img/example-site/example1.jpg">
        <h2 class="featurette-heading">Slaver <span class="muted"> Yersel</span></h2>
        <p class="lead">What would you like to tweet?</p>
        
		<%
		if(userName != null)//check if null
		{
			%> 
			<form action="members" method="post">
			  	
			  	<input type="hidden" name="name" value="<%=userName%>" >
			  	<input type="text" size="35" name="bark">
			  	<br>
			  	<br>
			  	<input type="submit" value="Tweet tweet!" name="insertNewBark">
			</form>
			<%
		}else
		{
			%>
			<p>It seems like there was a problem with your session, please log back in</p>
			<%
		}
		%>
        
        </div>
	
	</div>
	
	
	

	
</body>
</html>