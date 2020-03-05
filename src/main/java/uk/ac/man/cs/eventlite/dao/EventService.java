package uk.ac.man.cs.eventlite.dao;

import uk.ac.man.cs.eventlite.entities.Event;

public interface EventService extends EventRepository {

	public long count();

	public Iterable<Event> findAll();
	
	public <S extends Event> S save(S event);

	public void deleteEventById(long id);
	
	public Event findOne(long id);
}
