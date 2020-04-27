package uk.ac.man.cs.eventlite.dao;

import java.util.Optional;

import uk.ac.man.cs.eventlite.entities.Venue;

public interface VenueService extends VenueRepository {

	public long count();

	public Iterable<Venue> findAll();
	
	public Venue findOne(long id);
	
	public <V extends Venue> V save(V venue);

	public void deleteById(long id);
}
