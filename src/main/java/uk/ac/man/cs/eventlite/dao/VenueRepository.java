package uk.ac.man.cs.eventlite.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import uk.ac.man.cs.eventlite.entities.Venue;

public interface VenueRepository extends CrudRepository<Venue, Long>{

	public Iterable<Venue> findAllByNameContainingIgnoreCase(String name);

	public List<Venue> findAll(Sort sort);
	
}
