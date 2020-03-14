package uk.ac.man.cs.eventlite.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.entities.Event;


@Controller
@RequestMapping(value = "/events", produces = { MediaType.TEXT_HTML_VALUE })
public class EventsController {

	@Autowired
	private EventService eventService;


	@GetMapping
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



	
}

