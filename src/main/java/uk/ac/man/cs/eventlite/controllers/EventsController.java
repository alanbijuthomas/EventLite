package uk.ac.man.cs.eventlite.controllers;

import javax.validation.Valid;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;

import retrofit2.Response;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.man.cs.eventlite.dao.EventRepository;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.TwitterService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Tweet;
import uk.ac.man.cs.eventlite.util.TwitterUtils;

@Controller @RequestMapping(value = "/events", produces = {MediaType.TEXT_HTML_VALUE}) 
public class EventsController {
	
	@Autowired
	private EventService eventService;
	
	@Autowired
	private VenueService venueService;

	@Autowired
	private TwitterService twitterService;
	
	@GetMapping
	@RequestMapping(method = RequestMethod.GET)
	public String getAllEvents(Model model) {
		List<Event> futureList = new ArrayList<Event>();
		List<Event> pastList = new ArrayList<Event>();
		
		Iterator<Event> allEvents = eventService.findAll().iterator();
		while(allEvents.hasNext())
		{
			Event event = allEvents.next();
			if(event.getDate().compareTo(LocalDate.now()) < 0)
				pastList.add(event);
			else
				futureList.add(event);
		}
		
		model.addAttribute("future_events", futureList);
		model.addAttribute("past_events", pastList);

		return "events/index";
	}
	
	// Same as above method but, put into list and only return if event contains string
	@RequestMapping(value = "/search-by-name", method = RequestMethod.GET)
	public String searchEventName(@RequestParam (value = "search", required = false) String searchTerm, Model model) {
		
		List<Event> futureListSearch = new ArrayList<Event>();
		List<Event> pastListSearch = new ArrayList<Event>();
		
		Iterator<Event> allEventsSearch = eventService.findAllByNameContainingIgnoreCase(searchTerm).iterator();
		while(allEventsSearch.hasNext())
		{
			Event event = allEventsSearch.next();
			if(event.getDate().compareTo(LocalDate.now()) < 0)
				pastListSearch.add(event);
			else
				futureListSearch.add(event);
		}
		model.addAttribute("search", allEventsSearch);
		model.addAttribute("future_events_search", futureListSearch);
		model.addAttribute("past_events_search", pastListSearch);
		
		// Get list = eventService.findAllByNameContainingIgnoreCase(searchTerm)
		// Then loop through to find future / past
		// Add to past / future lists and then model.add
		//model.addAttribute("search", eventService.findAllByNameContainingIgnoreCase(searchTerm));
		//model.addAttribute("events", eventService.findAll());
		getAllEvents(model);
		return "events/index";
	} // searchEventName
		
	@RequestMapping(value = "/details-event/{id}", method = RequestMethod.GET)
	public String getEvent(@PathVariable("id") long id, Model model) {

		Event event = eventService.findOne(id);
		model.addAttribute("id", id);
		model.addAttribute("name", event.getName());
		model.addAttribute("time", event.getTime());
		model.addAttribute("date", event.getDate());
		model.addAttribute("description", event.getDescription());
		model.addAttribute("venue_name", event.getVenue().getName());
		model.addAttribute("longitude",event.getVenue().getLongitude());
		model.addAttribute("latitude",event.getVenue().getLatitude());
		
		return "events/details-event";
	}
    
    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String newEvent(Model model) {
        
    	if (!model.containsAttribute("event")) {
            model.addAttribute("event", new Event());
        }
        
        if (!model.containsAttribute("venueList")) {
            model.addAttribute("venueList", venueService.findAll());
        }
        
        return "events/new";
    }
    
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String createEvent(@RequestBody @Valid @ModelAttribute Event event, BindingResult errors, Model model, RedirectAttributes redirectAttrs) {
        if (errors.hasErrors()) {
            model.addAttribute("event", event);
            model.addAttribute("venueList", venueService.findAll());
            return "events/new";
        }
        //Decoding for the longitude and latitude
        eventService.save(event);
        redirectAttrs.addFlashAttribute("ok_message", "New event added.");
        
        return "redirect:/events";
    }
    
	@GetMapping("/update/{id}")
	public String updateEvent(@PathVariable("id") long id, Model model)  {
		model.addAttribute("event", eventService.findOne(id));
        model.addAttribute("venueList", venueService.findAll());       
		return "events/update-event";
	}
	
	@PostMapping("/updated/{id}")
	public String eventUpdated(@PathVariable("id") long id, Event event, 
			  BindingResult result, Model model) {
		if (result.hasErrors()) {
	        event.setId(id);
			model.addAttribute("event", eventService.findOne(id));
	        model.addAttribute("venueList", venueService.findAll()); 
	        return "events/update-event";
	    }
		eventService.save(event);
		model.addAttribute("events", eventService.findAll());
		return "redirect:/events";
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public String deleteEventById(@PathVariable("id") long id) {
		eventService.deleteEventById(id);

		return "redirect:/events";
	}
	
	@RequestMapping(value = "/details-event/{id}", method = RequestMethod.POST,  consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String postTweet(@PathVariable("id") long id, @RequestBody @Valid @ModelAttribute Tweet tweet, Model model) throws TwitterException
	{
		Twitter twitter = TwitterUtils.getTwitterInstance();
		boolean sent_success = false, sent_failure = false;
		String message;
		try
		{
			Status status = twitter.updateStatus(tweet.getContent());
			sent_success = true;
			message = "Your Tweet: " + status.getText() + " was posted";
		}
		catch(TwitterException e)
		{
			message = "Twitter could not be posted: " + e.getErrorMessage();
			sent_failure = true;
		}
		tweet.setTime(java.time.LocalTime.now());
		tweet.setDate(java.time.LocalDate.now());
		twitterService.save(tweet);
		model.addAttribute("tweet_content", message);
		model.addAttribute("tweet_success", sent_success);
		model.addAttribute("tweet_failure", sent_failure);
		return getEvent(id, model);
	}
	
}