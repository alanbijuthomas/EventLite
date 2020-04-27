package uk.ac.man.cs.eventlite.dao;

import uk.ac.man.cs.eventlite.entities.Venue;

public interface VenueService extends VenueRepository {

	public long count();

	public Iterable<Venue> findAll();
	
	public Iterable<Venue> findAllByNameContainingIgnoreCase(String name);
	
	public <V extends Venue> V save(V venue);
	
	public Venue findOne(long id);
	
}
