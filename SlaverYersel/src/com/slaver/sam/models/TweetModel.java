package com.slaver.sam.models;

/*
 * Expects a cassandra columnfamily defined as
 * use keyspace2;
  CREATE TABLE Tweets (
  user varchar,
  interaction_time varchar,
   Tweet varchar,
   PRIMARY KEY (user,interaction_time)
  ) WITH CLUSTERING ORDER BY (interaction_time DESC);
 * To manually generate a UUID use:
 * http://www.famkruithof.net/uuid/uuidgen
 */


import java.util.Collections;
import java.util.LinkedList;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.slaver.sam.stores.TweetStore;
import com.slaver.sam.stores.usersStore;

public class TweetModel 
{
	Cluster cluster;
	public TweetModel()
	{
	}

	public void setCluster(Cluster cluster)
	{
		this.cluster=cluster;
	}
	
	/*
	 * Populate linked list of users
	 */
	public LinkedList<usersStore> getUsers(String term, String user) 
	{
		LinkedList<usersStore> userList = new LinkedList<usersStore>(); //future list of users
		Session session = createSession();
		String searchterm = term;
		String currentUser = user;
		//create statement
		String prepared = "SELECT * from users";
		System.out.println("searchterm: " + searchterm);
		if(searchterm.equals("") || searchterm == null)//if searchterm is empty - "" was entered as a default
		{			//conduct empty search
			prepared += " LIMIT 100";
		}
		else{ //if a searchterm was entered
			prepared += " WHERE username='" + searchterm + "' LIMIT 100 ALLOW FILTERING";
		}
		
		PreparedStatement statement = session.prepare(prepared); //get all users that resemble search term

				
		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet rs = session.execute(boundStatement); //execute command
		if (rs.isExhausted()) //if there are no Tweets
		{
			System.out.println("No users found!");
		} 
		else 
		{
			for (Row row : rs) //for each row in user table, create a user object
			{
				boolean isFound = false;
			}
		}
		session.close();
		
		Collections.sort(userList); //sort linked list alphabetically by username
		return userList;
	}

	/*
	 * Creates new session
	 */
	private Session createSession() 
	{
		Session session = cluster.connect("Tweets");
		return session;
	}	
	/*
	 * Add new user to db
	 */
	public boolean newUser(String email, String username, String password){
		boolean success = false;
		Session session = createSession();
		email=email.replaceAll("'", "''");
		username=username.replaceAll("'", "''");
		password=password.replaceAll("'", "''");
		email=email.replaceAll("<", "");
		username=username.replaceAll("<", "");
		password=password.replaceAll("<", "");
		email=email.replaceAll(">", "");
		username=username.replaceAll(">", "");
		password=password.replaceAll(">", "");
		//first find out whether the username or email exists already, 
		//while the combination must be unique, they could exist as long as one of the two is different
		PreparedStatement statement = session.prepare("SELECT * FROM users WHERE username = '" + username + "' ALLOW FILTERING"); //create select query
		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet rs = session.execute(boundStatement); //execute command
		if (rs.isExhausted()) //if there are no users with the same username
		{
			statement = session.prepare("SELECT * FROM users WHERE email = '" + email + "' ALLOW FILTERING"); //create select query
			boundStatement = new BoundStatement(statement);
			rs = session.execute(boundStatement); //execute command
			if (rs.isExhausted()) //if there are no users with the same email address
			{
				statement = session.prepare("INSERT INTO users (email, password, username) VALUES ('" + email + "', '" + password + "', '" + username + "')"); //create add query
				try{
					boundStatement = new BoundStatement(statement);
					session.execute(boundStatement);
					success = true;
				}catch(Exception e)
				{
					System.out.println("something else went wrong");
					success = false;
				}
			}else{
				System.out.println("email Address already exists");
				success = false;
			}
		} else{
			System.out.println("username already exists!");
			success = false;
		}
		session.close();
		return success;
	}
	
	/*
	 * handles login routine
	 */
	public String login(String email, String password) 
	{
		String username = null;
		email = email.replaceAll("'", "''");
		password = password.replaceAll("'", "''");
		email=email.replaceAll("<", "");
		password=password.replaceAll("<", "");
		email=email.replaceAll(">", "");
		password=password.replaceAll(">", "");
		
		Session session = createSession();
		PreparedStatement statement = session.prepare("SELECT * FROM users WHERE email = '" + email + "'"); //create add query
		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet rs = session.execute(boundStatement); //execute command
		if (rs.isExhausted()) //if there are no Tweets
		{
			System.out.println("No user found with matching credentials!");
		} 
		else 
		{
			for (Row row : rs) //for each row in Tweets table
			{
				System.out.println("user found, db password is:" + row.getString("password") + " entered password is:" + password);
				if(row.getString("password").equals(password))
				{
					username = row.getString("username");
				}
			}
		}
		session.close();
		//System.out.println("username is: " + username + " password is: " + dbpassword);
		return username;
	}
	
	/*
	 * takes care of all deletion necessary when removing a user
	 */
	public boolean deleteUser(String email, String nameOfUser) 
	{
		boolean first = false;
		boolean second = false;
		boolean third = false;
		boolean success = false;
		
		Session session = createSession();
		System.out.println("deleting user: " + nameOfUser);

		//first: delete the user
		try{
			System.out.println("trying to delete");
			PreparedStatement statement = session.prepare("DELETE FROM users WHERE email = '" + email + "'"); //create delete query
			BoundStatement boundStatement = new BoundStatement(statement);
			session.execute(boundStatement); //execute command
			first = true;
		}catch(Exception e)
		{
			System.out.println("deleting user: " + e);
		}
		
		//second: delete the followers table
		try{
			System.out.println("deleting followers");
			PreparedStatement statement = session.prepare("DELETE FROM followers WHERE user = '" + nameOfUser + "'"); //create delete query
			BoundStatement boundStatement = new BoundStatement(statement);
			session.execute(boundStatement);
			second = true;
		}catch(Exception e)
		{
			System.out.println("deleting followers: " + e);
		}
		
		//third: delete Tweets
		try{
			System.out.println("deleting Tweets");
			PreparedStatement statement = session.prepare("DELETE FROM Tweets WHERE user = '" + nameOfUser + "'"); //create delete query
			BoundStatement boundStatement = new BoundStatement(statement);
			session.execute(boundStatement);
			third = true;
		}catch(Exception e)
		{
			System.out.println("deleting Tweets: " + e);
		}
		
		session.close();
		if(first && second && third) //only if all three succeeded
		{
			success = true;
			System.out.println("complete success!");

		}
		return success;
	}
	
	/*
	 * Delete Tweet from DB
	 */
	public void deleteTweet(String uuID, String user)
	{
		Session session = createSession();
		System.out.println("uuid: " + uuID);
		System.out.println("username: " + user);

		PreparedStatement statement = session.prepare("DELETE FROM Tweets.Tweets WHERE interaction_time = " + uuID + " AND user= '" + user + "'"); //create delete query (must include both keys)
		
		BoundStatement boundStatement = new BoundStatement(statement);
		session.execute(boundStatement); //execute command
		session.close();
	}
	
	/*
	 * Adds Tweet to db
	 */
	public void addTweet(String name, String Tweet) //takes in Tweet information
	{
		Session session = createSession();
		long currentDate = System.currentTimeMillis();
		System.out.println("name: " + name);
		System.out.println("Tweet: " + Tweet);

		//whenever there is a ' in the string, it has to be replaced with ''
		Tweet = Tweet.replaceAll("'", "''");
		Tweet = Tweet.replaceAll("<", "");
		Tweet = Tweet.replaceAll(">", "");
		String st = "INSERT INTO Tweets (user, interaction_time, thetimestamp, Tweet) VALUES ('" + name + "', now(), " + currentDate + ", '" + Tweet + "')";
		PreparedStatement statement = session.prepare(st); //create add query
		BoundStatement boundStatement = new BoundStatement(statement);
		session.execute(boundStatement); //execute command
		session.close();
	}
	
	/*
	 * returns a list of all Tweets
	 */
	public LinkedList<TweetStore> getTweets() 
	{
		LinkedList<TweetStore> TweetList = new LinkedList<TweetStore>(); //future list of Tweets
		Session session = createSession();

		PreparedStatement statement = session.prepare("SELECT * from Tweets LIMIT 100"); //get all Tweets

		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet rs = session.execute(boundStatement); //execute command
		if (rs.isExhausted()) //if there are no Tweets
		{
			System.out.println("No Tweets found!");
		} 
		else 
		{
			for (Row row : rs) //for each row in Tweet table
			{
				TweetStore ts = new TweetStore();
				ts.setTweet(row.getString("Tweet"));	//populate Tweet object
				ts.setUser(row.getString("user"));
				ts.setDate(row.getDate("thetimestamp"));
				ts.setUUID(row.getUUID("interaction_time"));
				TweetList.add(ts);//add Tweet to Tweet list
			}
		}
		session.close();
		Collections.sort(TweetList, Collections.reverseOrder()); //sort linked list
		return TweetList;
	}
	
	/*
	 * Polymorphism used to allow double naming: in this case we pass a username to get only Tweets from one user
	 * This method gets the Tweets from one specific user
	 */
	public LinkedList<TweetStore> getTweets(String username) 
	{
		LinkedList<TweetStore> TweetList = new LinkedList<TweetStore>(); //future list of Tweets
		Session session = createSession();

		PreparedStatement statement = session.prepare("SELECT * from Tweets WHERE user='"+ username +"' LIMIT 100"); //get all Tweets from that user

		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet rs = session.execute(boundStatement); //execute command
		if (rs.isExhausted()) //if there are no Tweets
		{
			System.out.println("No Tweets found!");
		} 
		else 
		{
			for (Row row : rs) //for each row in Tweet table
			{
				//System.out.println("Tweet found, adding to list");
				TweetStore ts = new TweetStore();
				ts.setTweet(row.getString("Tweet"));	//populate Tweet object
				ts.setUser(row.getString("user"));
				ts.setDate(row.getDate("thetimestamp"));
				ts.setUUID(row.getUUID("interaction_time"));
				TweetList.add(ts);//add Tweet to Tweet list
			}
		}
		session.close();
		Collections.sort(TweetList, Collections.reverseOrder()); //sort linked list
		return TweetList;
	}

	/*
	 * Return a list of own Tweets
	 */
	public LinkedList<TweetStore> getOwnTweets(String currentUserName) 
	{
		LinkedList<TweetStore> ownTweets = new LinkedList<TweetStore>();
		Session session = createSession();
		System.out.println("Showing Tweets for user: " + currentUserName);
		PreparedStatement statement = session.prepare("SELECT * FROM Tweets WHERE user = '" + currentUserName + "'"); //create select query
		BoundStatement boundStatement = new BoundStatement(statement);
		ResultSet rs = session.execute(boundStatement); //execute command

		if (rs.isExhausted()) //if there are no Tweets
		{
			//no Tweets found
		}
		else 
		{
			for (Row row : rs) //for each row in Tweets table
			{
				TweetStore ts = new TweetStore();
				ts.setTweet(row.getString("Tweet"));	//populate Tweet object
				ts.setUser(row.getString("user"));
				ts.setDate(row.getDate("thetimestamp"));
				ts.setUUID(row.getUUID("interaction_time"));
				ownTweets.add(ts);//add Tweet to Tweet list
			}
		}
		session.close();
		
		return ownTweets;
	}
}
