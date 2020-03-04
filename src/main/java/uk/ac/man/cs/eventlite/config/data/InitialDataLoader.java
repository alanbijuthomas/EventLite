package uk.ac.man.cs.eventlite.config.data;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

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
			Venue testVenueOne = new Venue();
			testVenueOne.setName("testVenueOne");
			testVenueOne.setCapacity(1);
			venueService.save(testVenueOne);
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
			exampleEvent3.setVenue(testVenueOne);
			eventService.save(exampleEvent3);
			
			Event exampleEvent4 = new Event();
			exampleEvent4.setId(4);
			exampleEvent4.setName("Example Event 4");
			
			
			LocalDate date4 = LocalDate.now().withMonth(1);
			LocalTime time4 = LocalTime.now();
			
			exampleEvent4.setTime(time4);
			exampleEvent4.setDate(date4);
			exampleEvent4.setVenue(testVenueOne);
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
