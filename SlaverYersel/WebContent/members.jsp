<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
    <%@ page import="com.slaver.sam.stores.*" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    
	<title>Members</title>
	
	<!-- Le styles -->
    <link href="css/bootstrap.css" rel="stylesheet">
    <link href="css/bootstrap-responsive.css" rel="stylesheet">
    
</head>

<%
	//if the username is null, meaning there is no user logged in - redirect user to login screen
	HttpSession Session = request.getSession();
	LoginBean thisUser = (LoginBean) Session.getAttribute("currentUser"); //current user bean is now saved in session
		
	String userName = thisUser.getUsername();
	//System.out.println("userName: " + userName);
	if(userName == null)
	{
		System.out.println("userName is null!");
		RequestDispatcher rd = request.getRequestDispatcher("/login.jsp"); 
		rd.forward(request, response);
	}else{
		System.out.println("logged in user: " + userName);
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
                <li><a href="#about">About</a></li>
                <li><a href="#contact">Contact</a></li>
            </div><!--/.nav-collapse -->
          </div><!-- /.navbar-inner -->
        </div><!-- /.navbar -->

      </div> <!-- /.container -->
    </div><!-- /.navbar-wrapper -->
	
    
	
	 <div class="container marketing">

      <!-- START THE FEATURETTES -->

      <hr class="featurette-divider">

      <div class="featurette">
        <img class="featurette-image pull-right" src="img/example-site/example1.jpg">
        <h2 class="featurette-heading">Slaver <span class="muted"> Yersel</span></h2>
     
       
				<td style="text-align:center;">
					<form name="changePassword" action="members"  method="get">
						<input type="submit" value="Change Password" name="changePassword"/>
					</form>
				</td>
				<td style="text-align:center;">
					<form name="viewOwnTweets" action="members"  method="get">
						<input type="submit" value="View Own Tweets" name="viewOwnTweets"/>
					</form>
				</td>
				<td style="text-align:center;">
					<form name="logOff" action="members"  method="POST">
						<input type="submit" value="log off" name="logOff"/>
					</form>
				</td>
			</tr>
		</table>
	</div>

 	<div id="mainContent">
		<H1>Welcome home <%=userName%></H1>
							<!-- Display followed users' Tweets-->
							<p>Tweets that have been posted</p>
	 	<%
		int i = 0;
	 
	 	try{
	 		if(Session.getAttribute("followedTweets") != null)
	 		{
	 			//Object O = request.getAttribute("followedTweets");
		 		List<TweetStore> followedTweets = (List<TweetStore>)Session.getAttribute("followedTweets"); //get list of Tweets from followed users
		 		if (followedTweets==null) //if the tweet list is empty
				{
					%><p>No tweets found</p><% 
				}
				else
				{
					for(TweetStore ts : followedTweets)
					{
						%>
						<div id=oneBark>
							<table width=100%>
								<tr>
									<td>
										<u><b><%=ts.getUser()%></b></u>
									</td>
									<td rowspan="2">
										<%=ts.getTweet()%>
									</td>
								</tr>
								<tr>
									<td>
										<%=ts.getDate()%>
									</td>
									<td>
										<!-- empty cell -->
									</td>
									<td style="text-align:left;">
										<!-- Show unfollow button next to bark -->
										<form name="unfollowUser" action="members"  method="POST" align="right">
										    <input type="hidden" name="unfollowThisUser" value="<%=i%>" >
										    <input type="hidden" name="unfollow" value="unfollow" >
											<input type="submit" value="Unfollow User" name="unfollowUser"/>
										</form>	
									</td>
								</tr>
							</table>
						</div>
						<% 
						i++; //increase counter which is added to deletebutton name
					}
				}
	 		}
	 	}catch(Exception e)
	 	{
	 		System.out.println("an error occured trying to print out the followed Tweets: " + e);
	 	}
		
	
	%>
		
					
						
	</div>
	</div>
	
	
</body>
</html>