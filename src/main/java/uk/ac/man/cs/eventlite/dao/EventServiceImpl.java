package uk.ac.man.cs.eventlite.dao;

import java.time.LocalDate;

// Remove unused packages when ready

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.man.cs.eventlite.entities.Event;

@Service
public class EventServiceImpl implements EventService {

	@Autowired
	private EventRepository eventRepository;
	
	@Override
	public long count() {
		return(eventRepository.count());
	}

	@Override
	public Iterable<Event> findAll() {
		return(this.findAllByOrderByDateAscNameAsc());
	}
	
	@Override
	public Iterable<Event> findAllByOrderByDateAscNameAsc()
	{
		return (eventRepository.findAllByOrderByDateAscNameAsc());
	}

	@Override
	public Iterable<Event> findAllByNameContainingIgnoreCase(String name)
	{
		return (eventRepository.findAllByNameContainingIgnoreCase(name));
	}
	
	@Override
	public <S extends Event> S save(S entity) {
		entity.getVenue().addEvent(entity);
		return(eventRepository.save(entity));
	}

	@Override
	public <S extends Event> Iterable<S> saveAll(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<Event> findById(Long id) {
		return(eventRepository.findById(id));
	}
	
	@Override
	public Event findOne(long id) {		
		return findById(id).orElse(null);
	}

	@Override
	public boolean existsById(Long id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterable<Event> findAllById(Iterable<Long> ids) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Iterable<Event> findAllUpcoming() {
		return eventRepository.findAllByDateAfterOrderByDateAscNameAsc(LocalDate.now().minusDays(1));
	}

	@Override
	public void deleteById(Long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Event entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll(Iterable<? extends Event> entities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteEventById(long id) {
		eventRepository.deleteById(id);
	}

	@Override
	public Iterable<Event> findAllByDateAfterOrderByDateAscNameAsc(LocalDate minusDays) {
		// TODO Auto-generated method stub
		return null;
	}

}
