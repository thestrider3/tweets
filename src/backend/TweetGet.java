package backend;


import twitter4j.GeoLocation;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

public class TweetGet implements StatusListener 
{

	@Override
	public void onException(Exception arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeletionNotice(StatusDeletionNotice arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScrubGeo(long arg0, long arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStallWarning(StallWarning arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatus(Status status) 
	{
		GeoLocation loc = status.getGeoLocation();
		if(loc!=null)
		{
			System.out.println("@" + status.getUser().getScreenName() + "using normal:" +status.getUser().getLocation()+"date&time" +status.getCreatedAt() +" now geo:" + loc + " - " + status.getText());
			TwitterSocket.onTweet("",status);
		}
		
		
	}

	@Override
	public void onTrackLimitationNotice(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
