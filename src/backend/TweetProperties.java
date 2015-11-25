package backend;

import twitter4j.Status;

public class TweetProperties 
{
	public String tweetText;
	public String tweetUser;
	public Double tweetLatitude;
	public Double tweetLongitude;
	public int sentiment;
	
	public void setTweetProperties(Status tweet,int sentiment)
	{
		this.tweetText = tweet.getText();
		this.tweetUser = tweet.getUser().getScreenName();
		this.tweetLatitude = tweet.getGeoLocation().getLatitude();
		this.tweetLongitude = tweet.getGeoLocation().getLongitude();
		this.sentiment = sentiment;
	}
	public void setTweetPropertiesDB(String text, String user, Double lat, Double lng, int sentiment)
	{
		this.tweetText = text;
		this.tweetUser = user;
		this.tweetLatitude = lat;
		this.tweetLongitude = lng;
		this.sentiment = sentiment;
	}
}
