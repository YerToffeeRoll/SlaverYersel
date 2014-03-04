<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
    <%@ page import="com.slaver.sam.stores.*" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head id="Header">
	
	<title>Login Page</title>
	
	<!-- Le styles -->
    <link href="css/bootstrap.css" rel="stylesheet">
    <link href="css/bootstrap-responsive.css" rel="stylesheet">
</head>

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
                
                 <li><a href="Register.jsp">Create new Account</a></li>
            <!--   <li><a href="logout.jsp">Logoff</a></li> -->
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
        <p class="lead">Please sign in</p>
      <% /* Display error message to user - if applicable */
		String message = (String)request.getAttribute("warningMessage");
		if(message != null)
		if(message.equals("null"))
		{
			//do nothing
		}else{ //do only if there is a content
			%>
			<FONT COLOR="FF0000"><%=message%></FONT>
			<%
		}
		%>
		
			<form action="members" method="post" name="logMeIn">
			<table>
				<tr>
					<td>
						Email:
					</td>
					<td>
						<input type="text" name="emailAddress">
					</td>
				</tr>
				<tr>
					<td>
						Password:
					</td>
					<td>
						<input type="password" name="password">
					</td>
				</tr>
				<tr>
					<td>
					</td>
					<td>
						<input type="submit" value="Submit" name="logMeIn">
					</td>
				</tr>
			</table>
		</form>
	</div>
        </div>

</body>
</html>