package com.slaver.sam.models;


import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.datastax.driver.core.Cluster;
import com.slaver.sam.lib.*;
import com.slaver.sam.models.*;
import com.slaver.sam.stores.LoginBean;
import com.slaver.sam.stores.TweetStore;
import com.slaver.sam.stores.usersStore;

/**
 * Servlet implementation class login
 * handles all IO for login screen
 */
@WebServlet(urlPatterns={"/members" })
public class members extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
    private Cluster cluster;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public members() 
    {
        super();
    }
    
    public void init(ServletConfig config) throws ServletException 
    {
		cluster = CassandraHosts.getCluster();
	}
    
    private void openMemberHome(HttpServletRequest request,	HttpServletResponse response) throws ServletException, IOException
    {
    	TweetModel mM = new TweetModel();
		mM.setCluster(cluster);
		
		HttpSession Session = request.getSession();
		LoginBean thisUser = (LoginBean) Session.getAttribute("currentUser"); //current user bean is now saved in session
		
		LinkedList<usersStore> userList = new LinkedList<usersStore>();//new empty userlist

		if(thisUser != null) //check if user is logged in
		{
			//the following gets a list of users that match the search term, if any
						//check if there was a user search involved
						if(request.getParameter("searchUsernameUser") != null || Session.getAttribute("searchedUser") != null)
						{
							Session.setAttribute("searchedUser", request.getParameter("searchUsernameUser")); //save searched user in session
							
							String searchterm = "";
							try{
								searchterm = (String)Session.getAttribute("searchedUser");
							}catch(Exception e)
							{
								System.out.println("failed to parse username searchterm");
							}
							
							try{
								searchterm = searchterm.toLowerCase(); //get requested username, but lower case
							}catch(Exception e)
							{
								System.out.println("cannot lowercase");
							}
							
							if(searchterm == null)//on repeated searched, it's "Null" frequently
							{
								searchterm = "";
							}else{
								System.out.println("searchterm not null");
							}
			
							userList = mM.getUsers(searchterm, thisUser.getUsername());
							//System.out.println("userlist is populated");
							Session.setAttribute("memberList", userList); //send list of users that search returned
							
						}else{
							System.out.println("no user search, continue without: " + request.getParameter("searchUsernameUser"));
						}
				
	    	RequestDispatcher rd = request.getRequestDispatcher("members.jsp"); 
			rd.forward(request, response);
		}else{
			openLoginScreen(request, response);
		}
	}
    
    /*
     * Open screen which shows own Tweets
     */
    private void openOwnTweetScreen(HttpServletRequest request,	HttpServletResponse response)  throws ServletException, IOException
    {
		//get list of own Tweets
    	TweetModel mM = new TweetModel();
		mM.setCluster(cluster);
		
		LinkedList<TweetStore> ownTweets = new LinkedList<TweetStore>(); //new empty list
		
		HttpSession Session = request.getSession();
		LoginBean thisUser = (LoginBean) Session.getAttribute("currentUser"); //current user bean is now saved in session
		
		String userName = thisUser.getUsername();
		ownTweets = mM.getOwnTweets(userName);
    	RequestDispatcher rd = request.getRequestDispatcher("ownTweets.jsp");
		Session.setAttribute("ownTweets", ownTweets);
    	rd.forward(request, response);
	}
   
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * gets the Tweet from the jsp page, from the user and sends it on to the Tweetmodel where it is stored in the db
	 * gets the login data from the login screen and directs it to the login procedure
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		TweetModel mM = new TweetModel();
		mM.setCluster(cluster);
		//request.setAttribute("userloggedIn", userloggedIn); //set bean whether user is logged in or not

		if(request.getParameter("logMeIn") != null) 
		{// login a user
			LoginBean thisUser = new LoginBean();
			thisUser.setEmail(request.getParameter("emailAddress"));
			thisUser.setPassword(request.getParameter("password"));
			
			String userName = mM.login(thisUser.getEmail(),thisUser.getPassword());
			
			if(userName != null) //user is valid
			{
				thisUser.setUserName(userName);
				thisUser.setLoggedIn(true);
				
				HttpSession Session = request.getSession();
				Session.setAttribute("currentUser", thisUser); //current user bean is now saved in session
				openMemberHome(request, response);//open members page
			}
			else
			{
				thisUser.setLoggedIn(false);
				request.setAttribute("warningMessage", "username or password are incorrect"); 
				openLoginScreen(request, response);//back to login screen
			}
		}
		else if (request.getParameter("NewEmailAddress")!= null &&
				 request.getParameter("NewPassword") 	!= null &&
				 request.getParameter("RepeatPassword") != null &&
				 request.getParameter("userName") 		!= null)
		{//if a new user is to be created
			String email = 			request.getParameter("NewEmailAddress");
			String password = 		request.getParameter("NewPassword");
			String repeatPassword = request.getParameter("RepeatPassword");
			String username = 		request.getParameter("userName");
			
			String warningMessage = ""; //the message the user gets displayed
			boolean success = false;
			if(password.equals(repeatPassword))
			{
				success = mM.newUser(email, username, repeatPassword);
			}else{
				warningMessage = "The passwords did not match";
			}
			
			if(success) //if the user was added successfully
			{
				LoginBean thisUser = new LoginBean();
				thisUser.setEmail(email);
				thisUser.setPassword(password);
				thisUser.setLoggedIn(true);
				thisUser.setUserName(username);
				
				HttpSession Session = request.getSession();
				Session.setAttribute("currentUser", thisUser); //current user bean is now saved in session
				
				System.out.println("account created successfully");
				openMemberHome(request, response);//open members page
			}
			else
			{
				System.out.println("not successful");
				if(warningMessage.equals(""))
				{//some other problem occured
					warningMessage = "Maybe your email/username combination exists already?";
				}else{
					//the message is already stating that passwords are not alike, leave it that way
				}
				request.setAttribute("warningMessage", warningMessage); 
				openRegisterScreen(request, response);//open register page again
			}
		}
		else if(request.getParameter("insertNewTweet") != null && request.getParameter("name") != null && request.getParameter("Tweet") != null)
		{ //post a new Tweet
			
			System.out.println("Add Tweet");
			HttpSession Session = request.getSession();
			LoginBean thisUser = (LoginBean) Session.getAttribute("currentUser");
			
			mM.addTweet(thisUser.getUsername(), request.getParameter("Tweet"));
			
			openOwnTweetScreen(request, response);
		}
		else if(request.getParameter("searchUsernameUser") != null) //if the request is to search for a user, by a user
		{
    		openMemberHome(request, response);
		}
		else if(request.getParameter("logOff") != null)												//post
		{//log user off
			
			LoginBean thisUser = new LoginBean();
			thisUser.setUserName(null);
			HttpSession Session = request.getSession();
			Session.setAttribute("currentUser", thisUser); //current user bean is now saved in session
			
			openLoginScreen(request, response);
		}
		else if(request.getParameter("deleteUser") != null) //if delete user was requested
		{//deleteUser
			HttpSession Session = request.getSession();
			System.out.println("Delete user: " + request.getParameter("deleteUser"));
			LinkedList<usersStore> userList = (LinkedList<usersStore>) Session.getAttribute("memberList");

			String userToBeDeleted = request.getParameter("deleteUser");
			int foo = Integer.parseInt(userToBeDeleted); //find which Tweet to delete
			mM.deleteUser(userList.get(foo).getEmail(), userList.get(foo).getUsername()); //delete user in list at foo
			openMemberHome(request, response);
		
		}
		else if(request.getParameter("deleteTweet") != null) //if the request is to delete a own Tweet
		{
			HttpSession Session = request.getSession();
			LinkedList<TweetStore> ownTweets = (LinkedList<TweetStore>) Session.getAttribute("ownTweets");			
			String TweetToBeDeleted = request.getParameter("deleteTweet");
			int foo = Integer.valueOf(TweetToBeDeleted); //find which Tweet to delete
			mM.deleteTweet(ownTweets.get(foo).getUUID().toString(), ownTweets.get(foo).getUser().toString()); //delete Tweet
			openOwnTweetScreen(request, response);
		}
		else{ //if nothing was selected or there was some problem - go to login screen
			openLoginScreen(request, response);
		}
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * 
	 * if something should be deleted, use delete
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * 
	 * if its just a redirection, and the change should/can be shown in the URL, then use get
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		if(request.getParameter("viewOwnTweets") != null)
		{//user wants to see own Tweets
			openOwnTweetScreen(request, response);
		}else if(request.getParameter("TweetYourself") != null)
		{//user wants to Tweet
    		openTweetYourself(request, response);
		}else if(request.getParameter("goToMembers") != null)
		{//directly open members page
    		openMemberHome(request, response);
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//								The following only stupidly open pages, they wont ever need servicing
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/*
     * Open new login screen / dont pass anything, is going to be empty anyways
     */
    private void openLoginScreen(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
    	RequestDispatcher rd = request.getRequestDispatcher("login.jsp"); 
		rd.forward(request, response);
	}
    
    /*
     * Open register-new-member screen
     */
    private void openRegisterScreen(HttpServletRequest request,	HttpServletResponse response) throws ServletException, IOException
    {
    	RequestDispatcher rd = request.getRequestDispatcher("register.jsp"); 
		rd.forward(request, response);
	}
    
    /*
     * Open Tweet-yourself screen
     */
    private void openTweetYourself(HttpServletRequest request, HttpServletResponse response)  throws ServletException, IOException
    {
    	RequestDispatcher rd = request.getRequestDispatcher("newTweet.jsp");
		rd.forward(request, response);
	}
}
