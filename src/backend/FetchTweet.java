package backend;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import twitter4j.Status;
import twitter4j.Twitter;
import backend.TweetProperties;
public class FetchTweet {	
	public void Getdata(String[] filterKeyword) throws UnknownHostException, JsonProcessingException
    {
		
		String textURI = "mongodb://sid:aman@ds045714.mongolab.com:45714/heroku_m8s822fc";
        MongoClientURI uri = new MongoClientURI(textURI);
        MongoClient mongoClient = new MongoClient(uri);
        DB db = mongoClient.getDB("heroku_m8s822fc");
        DBCollection dbCollection = db.getCollection("twitterCollection");
		
    	 //MongoClient mongoClient = new MongoClient("localhost",27017);
         //DB db = mongoClient.getDB("TwitterDatabaseSports");
         //DBCollection dbCollection =  db.getCollection("twitterCollection");
         
         BasicDBObject basicDBObject = new BasicDBObject();
         basicDBObject.get("_id");
         DBCursor dbCursor = dbCollection.find(basicDBObject);
         ObjectMapper jacksonObjectMapper = new ObjectMapper();
         //BasicDBObject basicDBObjectServer = new BasicDBObject();
         while(dbCursor.hasNext())
         {
        	 DBObject object = dbCursor.next();
        	 TweetProperties newTweet = new TweetProperties();
        	 String text = (String) object.get("Text");
        	 String user = (String) object.get("User-name");
        	 Double lat = (double) object.get("Lat");
        	 Double lng = (double) object.get("Lng");
        	 String category = (String) object.get("Category");
        	 String sentiment = (String) object.get("Sentiment");
        	 System.out.println("Category = " + category + " filter = " + filterKeyword);
        	 if(category!=null)
        	 {
        		 if(category.equals(filterKeyword))
        		 {
        			 newTweet.setTweetPropertiesDB(text, user, lat, lng, Integer.parseInt(sentiment));
        			 String tweetString = jacksonObjectMapper.writeValueAsString(newTweet);
        			 TwitterSocket.pushTweetToUI(tweetString);
        		 }
        	 }
        	 
          }
    }	
}
