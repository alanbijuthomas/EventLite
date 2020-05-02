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
			
			// Create example venue and add to database
			
			// This have 3 venues assigned
			Venue testVenueOne = new Venue();
			testVenueOne.setName("testVenueOne");
			testVenueOne.setCapacity(1);
			testVenueOne.setAddress("University of Manchester");
			testVenueOne.setPostcode("M13 9PL");
			testVenueOne.setId(1);
			venueService.save(testVenueOne);
			log.info("Attempting to add an example venue.");
			
			// This have 2 venues assigned
			Venue testVenueTwo = new Venue();
			testVenueTwo.setName("testVenueTwo");
			testVenueTwo.setCapacity(1);
			testVenueTwo.setAddress("Renold Building");
			testVenueTwo.setPostcode("M1 7JR");
			testVenueTwo.setId(2);
			venueService.save(testVenueTwo);
			log.info("Attempting to add an example venue.");
			
			// This one has no venues assigned e.g.deletable
			Venue testVenueThree = new Venue();
			testVenueThree.setName("testVenueThree");
			testVenueThree.setCapacity(1);
			testVenueThree.setAddress("Kilburn Building");
			testVenueThree.setPostcode("M13 9PL");
			testVenueThree.setId(3);
			venueService.save(testVenueThree);
			log.info("Attempting to add an example venue.");
			
			// Create example event and add to database
			Event exampleEvent = new Event();
			exampleEvent.setId(1);
			exampleEvent.setName("Example Event 1");
			exampleEvent.setTime(LocalTime.now());
			exampleEvent.setDate(LocalDate.now());
			exampleEvent.setVenue(testVenueOne);
			eventService.save(exampleEvent);
			log.info("Attempting to add an example event.");
			
			// Create example event and add to database
			Event exampleEvent2 = new Event();
			exampleEvent2.setId(2);
			exampleEvent2.setName("Example Event 2");
			
			
			LocalDate date2 = LocalDate.now();
			LocalTime time2 = LocalTime.now().withHour(0);
			
			exampleEvent2.setTime(time2);
			exampleEvent2.setDate(date2);
			exampleEvent2.setVenue(testVenueOne);
			eventService.save(exampleEvent2);
			
			Event exampleEvent3 = new Event();
			exampleEvent3.setId(3);
			exampleEvent3.setName("Example Event 3");
			
			
			LocalDate date3 = LocalDate.now();
			LocalTime time3 = LocalTime.now().withHour(23);
			
			exampleEvent3.setTime(time3);
			exampleEvent3.setDate(date3);
			exampleEvent3.setVenue(testVenueTwo);
			eventService.save(exampleEvent3);
			
			Event exampleEvent4 = new Event();
			exampleEvent4.setId(4);
			exampleEvent4.setName("Example Event 4");
			
			
			LocalDate date4 = LocalDate.now().withMonth(1);
			LocalTime time4 = LocalTime.now();
			
			exampleEvent4.setTime(time4);
			exampleEvent4.setDate(date4);
			exampleEvent4.setVenue(testVenueTwo);
			exampleEvent4.setDescription("This is a nice event");
			eventService.save(exampleEvent4);
			
			Event exampleEvent5 = new Event();
			exampleEvent5.setId(5);
			exampleEvent5.setName("Example Event 5");
			
			
			LocalDate date5 = LocalDate.now().withMonth(12);
			LocalTime time5 = LocalTime.now();
			
			exampleEvent5.setTime(time5);
			exampleEvent5.setDate(date5);
			exampleEvent5.setVenue(testVenueOne);
			eventService.save(exampleEvent5);
			return;
		}


	}
}
