package uk.ac.man.cs.eventlite.util;

import java.util.List;
import java.util.stream.Collectors;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public abstract class TwitterUtils {
	
	    private static final String ACCESS_TOKEN = "1250096820484702216-6FzwKkJTVgtoVYBuMd99CbZXvv1NDu";
	    private static final String ACCESS_TOKEN_SECRET = "NtEKHm414ghQ6kBzUMboP1PglV8O4Uj61fh7GwHTU0CYK";
	    private static final String API_KEY = "5id5Sn88Q4LLC4ItpfbHARaGG";
	    private static final String API_KEY_SECRET = "XT59TNHSDwghmtVVmuGgV09Cf0c5NHo2vV9eczLHEQAmSBLcs8";
	   
	    public static Status createTweet(String tweet) throws TwitterException {
	        Twitter twitter = getTwitterInstance();
	        return twitter.updateStatus(tweet);        
	    }
	    
	    public static List<String> getTimeLine() throws TwitterException {
	        Twitter twitter = getTwitterInstance();
	         
	        return (List<String>) twitter.getHomeTimeline().stream()
	          .map(item -> item.getText())
	          .collect(Collectors.toList());
	    }
	    
	    public static Twitter getTwitterInstance() {
	        ConfigurationBuilder cb = new ConfigurationBuilder();
	        cb.setDebugEnabled(true)
	          .setOAuthConsumerKey(API_KEY)
	          .setOAuthConsumerSecret(API_KEY_SECRET)
	          .setOAuthAccessToken(ACCESS_TOKEN)
	          .setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
	        TwitterFactory tf = new TwitterFactory(cb.build());
	        Twitter twitter = tf.getInstance();
	        return twitter;
	    }
	    
}
