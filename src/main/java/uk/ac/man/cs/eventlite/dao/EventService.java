package uk.ac.man.cs.eventlite.dao;

import uk.ac.man.cs.eventlite.entities.Event;

public interface EventService extends EventRepository {

	public long count();

	public Iterable<Event> findAll();
	
	public Iterable<Event> findAllUpcoming();
	
	public Iterable<Event> findAllByNameContainingIgnoreCase(String name);
	
	public <S extends Event> S save(S event);
	
	public Event findOne(long id);
	
	public void deleteEventById(long id);

}
