package uk.ac.man.cs.eventlite.controllers;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.validation.Valid;
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
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;

@Controller @RequestMapping(value = "/events", produces = {MediaType.TEXT_HTML_VALUE}) 
public class EventsController {

	@Autowired
	private EventService eventService;
	
	@Autowired
	private VenueService venueService;

	@RequestMapping(method = RequestMethod.GET)
	public String getAllEvents(Model model) {

		model.addAttribute("eventsAll", eventService.findAll());
		
		/*model.addAttribute("eventsThree", 
				StreamSupport.stream(
						eventService.findAll().spliterator(), false)
								.limit(3).collect(Collectors.toList())
				);
		*/
		return "events/index";
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
		
	@RequestMapping(value = "/details-event/{id}", method = RequestMethod.GET)
	public String getEvent(@PathVariable("id") long id, Model model) {

		Event event = eventService.findOne(id);
		model.addAttribute("id", id);
		model.addAttribute("name", event.getName());
		model.addAttribute("time", event.getTime());
		model.addAttribute("date", event.getDate());
		model.addAttribute("description", event.getDescription());
		model.addAttribute("venue_name", event.getVenue().getName());

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
        
        eventService.save(event);
        redirectAttrs.addFlashAttribute("ok_message", "New event added.");
        
        return "redirect:/events";
    }
}