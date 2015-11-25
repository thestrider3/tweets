package backend;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import twitter4j.FilterQuery;
import twitter4j.GeoLocation;
import twitter4j.Location;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

import org.bson.types.ObjectId;
import backend.*;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
@ServerEndpoint("/tweetGraph")
public class TwitterSocket 
{
	
	static Session Twittersession;
	static TwitterStream twitterStream;
	static ConfigurationBuilder cb;
	static String filterKeyword[] = {"general"};
	static int senti=0;
	//static NLP nlpObject;
	static
	{
		cb = new ConfigurationBuilder();

		cb.setDebugEnabled(true)
		.setOAuthConsumerKey("Qi3M3cBz5xnpm1DLkUdkX3mvu")
		.setOAuthConsumerSecret("FqhFj6Hl45ILqKNudvXIVF0nNdIy6Npkl0OD1aNSWRPJPd76OU")
		.setOAuthAccessToken("1037469638-ORCQ0XJ02PCfVZEar6jRt7KZGnsqRzkWqFhA14e")
		.setOAuthAccessTokenSecret("MEUA2f0FHYjU4FPyBfX00sgJQtcpFFsdNSCbG9LmAHDZG");
		twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
		twitterStream.addListener(new TweetGet());
		twitterStream.sample();//filter(filterKeyword);
	}
	@OnOpen
	public void open(Session session) throws UnknownHostException, JsonProcessingException
	{
		TwitterSocket.Twittersession = session;
		
		System.out.println("Session is opened");
		//nlpObject = new NLP();
		
		//get the trending tweets and push it to the UI so that the drop down is populated with the 
		//current trending tweets
		
		
		//open the database schema
		//pull all the existing tweets
		//run a for loop and call pustTweetToUI for each of the tweets retrieved from the database
		//add code to push tweets in the database to the UI
		//FetchTweet fetch = new FetchTweet();
		//fetch.Getdata(filterKeyword);
		//getData(filterKeyword);
	}
	
	@OnMessage
	public static void getData(String[] keyword)
	{
		System.out.println("Received the keyword " + keyword);
		filterKeyword = keyword;
		FetchTweet fetch = new FetchTweet();
		try {
			if(filterKeyword.equals("general"))
			{
				twitterStream.sample();
			}
			else
			{
				FilterQuery fq=new FilterQuery();
				fq.track(filterKeyword);
				twitterStream.filter(fq);
			}
			fetch.Getdata(filterKeyword);
		} catch (UnknownHostException | JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@OnClose
	public void close(Session session) {
		System.out.println("Session is closed " + session);
	}

	@OnError
	public void onError(Throwable error) 
	{
		
				error.printStackTrace();
		//System.out.println("An error has occurred " + error.printStackTrace());
	}
	
	public static void onTweet(String message, Status status)
	{
		System.out.println("Reached onTweet");
		TweetProperties newTweet = new TweetProperties();
		//push the tweet to database - addTweetToDatabase
		//push the tweet to UI
		//NLP.init();
		if(Twittersession.isOpen())
		{
			int sentiment = 0;
		    //put sentiment logic here
			/*if(senti == 0)
			{
				sentiment = 0;
				senti = 1;
			}
			else
			{
				sentiment = 1;
				senti = 0;
			}*/
			sentiment = NLP.findSentiment(status.getText());
			System.out.println("The sentiment of the tweet is " + sentiment);
			newTweet.setTweetProperties(status,sentiment);
			ObjectMapper jacksonObjectMapper = new ObjectMapper();
			try 
			{
				String tweetString = jacksonObjectMapper.writeValueAsString(newTweet);
				pushTweetToUI(tweetString);
			} 
			catch (JsonProcessingException e) 
			{
				e.printStackTrace();
			}
			addTweetToDatabase(status,sentiment);
			//System.out.println("Added to DB");
		}
		
	}
	public static void pushTweetToUI(String tweetString)
	{
		try 
		{
			System.out.println("Reached pushTweetToUI " + tweetString);
			Twittersession.getBasicRemote().sendText(tweetString);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}	
	
	
	public static void addTweetToDatabase(Status status, int sentiment)
	{		
		//MongoClient mongoClient;
		//System.out.println(" Reached try DB");
		//mongoClient = new MongoClient("localhost",27017);
		//DB db = mongoClient.getDB("TwitterDatabaseSports");
		//DBCollection dbCollection =  db.getCollection("twitterCollection");
		
		//String textURI = "mongodb://sid:aman@ds045714.mongolab.com:45714/heroku_m8s822fc";
        MongoClientURI uri = new MongoClientURI("127.0.0.1");
        MongoClient mongoClient = new MongoClient(uri);
        DB db = mongoClient.getDB("twitterDB");
        DBCollection dbCollection = db.getCollection("twitterCollection");
		
		
		
		BasicDBObject basicDBObject = new BasicDBObject();
		GeoLocation loc = status.getGeoLocation();            
		    //System.out.println("@" + status.getUser().getScreenName() + "using normal:" +status.getUser().getLocation()+"date&time" +status.getCreatedAt() +" now geo:" + loc + " - " + status.getText());
		    ObjectId id = new ObjectId();
		    basicDBObject.put("_id", id);
		    basicDBObject.put("User-name",status.getUser().getScreenName());
		    basicDBObject.put("Lat",loc.getLatitude());
		    basicDBObject.put("Lng",loc.getLongitude());            
		    basicDBObject.put("Location", status.getUser().getLocation());            
		    basicDBObject.put("Time", status.getCreatedAt());
		    basicDBObject.put("Text",status.getText());
		    basicDBObject.put("Category",filterKeyword);
		    basicDBObject.put("Sentiment",Integer.toString(sentiment));    
		    //System.out.println(" Reached b4 insert");
		    dbCollection.insert(basicDBObject);
		    //System.out.println(" Reached after insert");                
	}
}
