package uk.ac.man.cs.eventlite.dao;

import uk.ac.man.cs.eventlite.entities.Tweet;

public interface TwitterService extends TwitterRepository {

	public long count();
	
	public <T extends Tweet> T save(T tweet);

	
}
