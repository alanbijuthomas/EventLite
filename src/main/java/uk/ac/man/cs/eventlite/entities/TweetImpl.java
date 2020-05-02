package uk.ac.man.cs.eventlite.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TweetImpl {
	
	/////////////////////////////////////////////////////////////////////
	/// This field needs to be updated once the new tokens been given ///
	/////////////////////////////////////////////////////////////////////

	private String key = "5id5Sn88Q4LLC4ItpfbHARaGG";
	
	private String secretKey = "XT59TNHSDwghmtVVmuGgV09Cf0c5NHo2vV9eczLHEQAmSBLcs8";
	
	private String token = "1250096820484702216-M4etLlaX3TWfgmC8NcVrq4ZGKkWs4I";
	
	private String secretToken = "SZerAzxhlRH6GDGSJtgl96K1hIzZnNW0UQRG4OyoWXESf";
	
	private Twitter twitter;
	
	public TweetImpl() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey(key)
		  .setOAuthConsumerSecret(secretKey)
		  .setOAuthAccessToken(token)
		  .setOAuthAccessTokenSecret(secretToken);
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();
	}
	
	public String sendTweet(String tweet) throws TwitterException {
	    Status status = twitter.updateStatus(tweet);
	    return status.getText();
	}
	
	public List<Tweet> getTimeLine() throws TwitterException {
	    List<Status> tweets = twitter.getHomeTimeline().stream().collect(Collectors.toList());
	
	    List<Tweet> result = new ArrayList<Tweet>();
	    
	    for (Status s : tweets)
	    	result.add(new Tweet(s.getId(), s.getText(), s.getCreatedAt(), s.getUser().getScreenName()));
	    
	    return result;
		
	}
	
	public String getKey() {
		return this.key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getSecretKey() {
		return secretKey;
	}
	
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	
	public String getToken() {
		return this.token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public String getSecretToken() {
		return secretToken;
	}
	
	public void setSecretToken(String secretToken) {
		this.secretToken = secretToken;
	}
	
	public Twitter getTwitter() {
		return twitter;
	}
	
	public void setTwitter(Twitter twitter) {
		this.twitter = twitter;
	}
}
