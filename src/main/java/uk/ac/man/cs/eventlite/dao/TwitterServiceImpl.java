package uk.ac.man.cs.eventlite.dao;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import uk.ac.man.cs.eventlite.entities.Tweet;

public class TwitterServiceImpl implements TwitterService {

	
	private final static Logger log = LoggerFactory.getLogger(TwitterServiceImpl.class);

	private final static String DATA = "data/tweets.json";

	@Autowired
	private TwitterRepository twitterRepository;
	
	@Override
	public <S extends Tweet> Iterable<S> saveAll(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<Tweet> findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean existsById(Long id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterable<Tweet> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<Tweet> findAllById(Iterable<Long> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteById(Long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Tweet entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll(Iterable<? extends Tweet> entities) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub

	}

	@Override
	public long count() {
		return twitterRepository.count();
	}

	@Override
	public <T extends Tweet> T save(T tweet) {
		return twitterRepository.save(tweet);
	}

}
