package uk.ac.man.cs.eventlite.controllers;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.Link;

import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;


@RestController
@RequestMapping(value = "/api/venues", produces = { MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE })
public class VenueControllerApi {
	
	
	@Autowired
	private VenueService venueService;
	
	@RequestMapping(method = RequestMethod.GET)
	public Resources<Resource<Venue>> getAllVenues() {

		return venueToResource(venueService.findAll());
	}
 
    private Resource<Venue> venueToResource(Venue venue) {
        Link selfLink = linkTo(VenueControllerApi.class).slash(venue.getId()).withSelfRel();
        Link venueLink = linkTo(VenueControllerApi.class).slash(venue.getId()).withRel("venue");
		Link eventsLink = linkTo(VenueControllerApi.class).slash(venue.getId()).slash("events").withRel("events");
		Link next3Events = linkTo(VenueControllerApi.class).slash(venue.getId()).slash("next3events").withRel("next3events");

		return new Resource<Venue>(venue, selfLink, venueLink, eventsLink, next3Events);
    }
    
    
    private Resources<Resource<Venue>> venueToResource(Iterable<Venue> venues) {
		Link selfLink = linkTo(methodOn(VenueControllerApi.class).getAllVenues()).withSelfRel();

		List<Resource<Venue>> resources = new ArrayList<Resource<Venue>>();
		for (Venue venue : venues) {
			resources.add(venueToResource(venue));
		}

		return new Resources<Resource<Venue>>(resources, selfLink);
	}
    
    @RequestMapping(value = "new-venue", method = RequestMethod.GET)
    public ResponseEntity < ? > newVenue() {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
    }
    
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity < ? > createVenue(@RequestBody @Valid Venue venue, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.unprocessableEntity().build();
        }
        venueService.save(venue);
        URI location = linkTo(VenueControllerApi.class).slash(venue.getId()).toUri();
        return ResponseEntity.created(location).build();
    }
    
    @RequestMapping(value = "/details-venue/{id}", method = RequestMethod.GET)
	public Resource<Venue> venueDetails(@PathVariable("id") long id) {
		Venue venue = venueService.findOne(id);

		return venue2Resource(venue);
	}

	private Resource<Venue> venue2Resource(Venue venue) {
		Link selfLink = linkTo(VenueControllerApi.class).slash(venue.getId()).withSelfRel();

		return new Resource<Venue>(venue, selfLink);
	}
    
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteVenue(@PathVariable("id") long id){
		venueService.deleteById(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
	@RequestMapping(value = "/{id}/events", method = RequestMethod.GET)
	public Resources<Resource<Event>> venuesEvents(@PathVariable("id") long id) {
		Venue venue = venueService.findOne(id);
		Assert.notNull(venue, "Content must not be null!");
		
		Link selfLink = linkTo(VenueControllerApi.class).slash(venue.getId()).slash("events").withSelfRel();

		return new Resources<Resource<Event>>(eventToResource(venue.getEvents()), selfLink);
	}
	
	@RequestMapping(value = "/{id}/next3events", method = RequestMethod.GET)
	public Resources<Resource<Event>> venuesNext3Events(@PathVariable("id") long id) {
		Venue venue = venueService.findOne(id);
		Assert.notNull(venue, "Content must not be null!");
		
		List<Event> events = venue.getEvents();
		Collections.sort(events);
		List<Event> next3Events = new ArrayList<Event>();
		int indexOfNextEvent = Event.getIndexOfNextEventFromNow(events);
		for(int i = indexOfNextEvent; i < 3 + indexOfNextEvent && i < events.size(); i++)
			next3Events.add(events.get(i));
		
		Link selfLink = linkTo(VenueControllerApi.class).slash(venue.getId()).slash("next3events").withSelfRel();

		return new Resources<Resource<Event>>(eventToResource(next3Events), selfLink);
	}
	
	private List<Resource<Event>> eventToResource(Iterable<Event> events) {
		List<Resource<Event>> resources = new ArrayList<Resource<Event>>();
		EventsControllerApi eventController = new EventsControllerApi();
		
		for (Event event : events) {
			resources.add(eventController.eventToResource(event));
		}

		return resources;
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Resource<Venue> venue(@PathVariable("id") long id) {
		Venue venue = venueService.findOne(id);
		Assert.notNull(venue, "Content must not be null!");

		return venueToResource(venue);
	}
}
