package uk.ac.man.cs.eventlite.controllers;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@Controller @RequestMapping(value = "/venues", produces = {MediaType.TEXT_HTML_VALUE}) 
public class VenuesController {
	
	@Autowired
	private EventService eventService;
	
	@Autowired
	private VenueService venueService;
	
	@RequestMapping(value = "/details-venue/{id}", method = RequestMethod.GET)
	public String getVenue(@PathVariable("id") long id, Model model) {

		Venue venue = venueService.findOne(id);
		model.addAttribute("id", id);
		model.addAttribute("name", venue.getName());
		model.addAttribute("address", venue.getAddress());
		model.addAttribute("capacity", venue.getCapacity());
		
//		List<Event> futureListSearch = new ArrayList<Event>();
//		
//		Iterator<Event> allEventsSearch = eventService.findAllByNameContainingIgnoreCase(venue.getName()).iterator();
//		while(allEventsSearch.hasNext())
//		{
//			Event event = allEventsSearch.next();
//			if(event.getDate().compareTo(LocalDate.now()) >= 0)
//				futureListSearch.add(event);
//		}
//		model.addAttribute("future_events_search", futureListSearch);

		return "venues/details-venue";
	}
	
	@GetMapping
	    public String getAllVenues(Model model) {
	        model.addAttribute("venues", venueService.findAll());

	        return "venues/index";
	    }


}