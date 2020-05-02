package uk.ac.man.cs.eventlite.dao;

import org.springframework.data.repository.CrudRepository;

import uk.ac.man.cs.eventlite.entities.Tweet;

public interface TwitterRepository extends CrudRepository<Tweet, Long> {

}
