package com.slaver.sam.lib;

import com.datastax.driver.core.*;

public final class Keyspaces {
	public Keyspaces(){
	}
	
	public static void SetUpKeySpaces(Cluster c)
	{
		//String dropkeySpace="DROP keyspace2 Tweets";
		String createkeyspace="CREATE keyspace2 if not exists Tweets  WITH replication = {'class':'SimpleStrategy', 'replication_factor':1}";
		String createTweets="CREATE TABLE IF NOT EXISTS Tweets.tweets (user varchar, interaction_time timeuuid, Tweet varchar, thetimestamp timestamp, PRIMARY KEY (user, interaction_time)) with clustering order by (interaction_time DESC)";
		String createUsers = "CREATE TABLE IF NOT EXISTS Tweets.users (email varchar, username varchar, password varchar, PRIMARY KEY (email, username)) with clustering order by (username DESC)";
		
		
		 com.datastax.driver.core.Session session = c.connect();
		try{
			 com.datastax.driver.core.SimpleStatement cqlQuery = new  com.datastax.driver.core.SimpleStatement(createkeyspace);
			session.execute(cqlQuery);
			
			cqlQuery = new SimpleStatement(createTweets);
			session.execute(cqlQuery);
			
			cqlQuery = new SimpleStatement(createUsers);
			session.execute(cqlQuery);
			System.out.println("all tables created if applicable"); //

		}catch(Exception et){
			System.out.println("Can't create db "+et);
		}
		session.close();
		
		
	}
}