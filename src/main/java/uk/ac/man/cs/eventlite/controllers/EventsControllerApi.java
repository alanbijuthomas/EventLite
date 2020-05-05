package uk.ac.man.cs.eventlite.controllers;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.TwitterService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Tweet;

@RestController
@RequestMapping(value = "/api/events", produces = { MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE })
public class EventsControllerApi {

	@Autowired
	private TwitterService twitterService;
	
	@Autowired
	private EventService eventService;

	@RequestMapping(method = RequestMethod.GET)
	public Resources<Resource<Event>> getAllEvents() {

		return eventToResource(eventService.findAll());
	}
	
	@RequestMapping(value = "/details-event/{id}", method = RequestMethod.GET)
	public Resource<Event> eventDetails(@PathVariable("id") long id) {
		Event event = eventService.findOne(id);

		return eventToResource(event);
	}

	Resource<Event> eventToResource(Event event) {
		Link selfLink = linkTo(EventsControllerApi.class).slash(event.getId()).withSelfRel();
		Link venueLink = linkTo(EventsControllerApi.class).slash(event.getId()).slash("venue").withRel("venue");
		Link eventLink = linkTo(EventsControllerApi.class).slash(event.getId()).withRel("event");

		return new Resource<Event>(event, selfLink, eventLink, venueLink);
	}

	private Resources<Resource<Event>> eventToResource(Iterable<Event> events) {
		Link selfLink = linkTo(methodOn(EventsControllerApi.class).getAllEvents()).withSelfRel();

		List<Resource<Event>> resources = new ArrayList<Resource<Event>>();
		for (Event event : events) {
			resources.add(eventToResource(event));
		}

		return new Resources<Resource<Event>>(resources, selfLink);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteEventById(@PathVariable("id") long id){
		eventService.deleteEventById(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}


    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public ResponseEntity < ? > newEvent() {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
    }
    
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity < ? > createEvent(@RequestBody @Valid Event event, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.unprocessableEntity().build();
        }
        eventService.save(event);
        URI location = linkTo(EventsControllerApi.class).slash(event.getId()).toUri();
        return ResponseEntity.created(location).build();
    }
    
    @RequestMapping(value = "/details-event/{id}",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity< ? > postTweet(@RequestParam Tweet tweet, BindingResult result)
	{
		twitterService.save(tweet);
        URI location = linkTo(EventsControllerApi.class).slash(222).slash(0).toUri();    	
		return ResponseEntity.created(location).build();
	}

}
