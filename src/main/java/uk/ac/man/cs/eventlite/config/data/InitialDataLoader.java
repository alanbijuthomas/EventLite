package uk.ac.man.cs.eventlite.config.data;

import java.time.LocalDate;
import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;


@Component
@Profile({ "default", "test" })
public class InitialDataLoader implements ApplicationListener<ContextRefreshedEvent> {

	private final static Logger log = LoggerFactory.getLogger(InitialDataLoader.class);

	@Autowired
	private EventService eventService;

	@Autowired
	private VenueService venueService;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		
	
		if (eventService.count() > 0) {
			log.info("Database already populated. Skipping data initialization.");
			return;
		} else {
			// Create example event and add to database
			Event exampleEvent = new Event();
			exampleEvent.setId(1);
			exampleEvent.setName("Example Event");
			exampleEvent.setTime(LocalTime.now());
			exampleEvent.setDate(LocalDate.now());
			exampleEvent.setVenue(1);
			eventService.save(exampleEvent);
			log.info("Attempting to add an example event.");
			
			// Create example venue and add to database
			Venue testVenueOne = new Venue();
			testVenueOne.setId(1);
			testVenueOne.setName("testVenueOne");
			testVenueOne.setCapacity(1);
			venueService.save(testVenueOne);
			log.info("Attempting to add an example venue.");
			return;
		}


	}
}
