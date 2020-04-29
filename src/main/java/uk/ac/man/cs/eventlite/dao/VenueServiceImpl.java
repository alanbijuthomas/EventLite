package uk.ac.man.cs.eventlite.dao;

import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@Service
public class VenueServiceImpl implements VenueService {

	private final static Logger log = LoggerFactory.getLogger(VenueServiceImpl.class);

	private final static String DATA = "data/venues.json";

	@Autowired
	private VenueRepository venueRepository;
	
	@Override
	public long count() {

		return venueRepository.count();
	}

	@Override
	public Iterable<Venue> findAll() {
		
		return venueRepository.findAll();
	}
	
	@Override
	public Iterable<Venue> findAllByNameContainingIgnoreCase(String name)
	{
		return (venueRepository.findAllByNameContainingIgnoreCase(name));
	}
	
	public Optional<Venue> findById(Long id) {
		return(venueRepository.findById(id));
	}
	
	@Override
	public Venue findOne(long id) {		
		return findById(id).orElse(null);
	}
	
	@Override
	public <V extends Venue> V save(V venue)
	{
		return venueRepository.save(venue);
	}

	@Override
	public <S extends Venue> Iterable<S> saveAll(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean existsById(Long id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterable<Venue> findAllById(Iterable<Long> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteById(Long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Venue entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll(Iterable<? extends Venue> entities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void deleteById(long id) {
//		if (venueRepository.existsById(id)) {
//			System.out.println("Cannot delete this venue");
//			return;
//		}
		venueRepository.deleteById(id);
	}

	@Override
	public List<Venue> findMostPopular() {
		return venueRepository.findAll(Sort.by(Sort.Direction.DESC, "eventCount"));
	}

	@Override
	public List<Venue> findAll(Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

}
