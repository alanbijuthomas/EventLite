package uk.ac.man.cs.eventlite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.entities.Event;

@Controller
@RequestMapping(value = "/events", produces = { MediaType.TEXT_HTML_VALUE })
public class EventsController {

	@Autowired
	private EventService eventService;

	@RequestMapping(method = RequestMethod.GET)
	public String getAllEvents(Model model) {

		model.addAttribute("events", eventService.findAll());

		return "events/index";
	}
	
	@GetMapping("/update/{id}")
	public String updateEvent(@PathVariable("id") long id, Model model)  {
		model.addAttribute("event", eventService.findOne(id));
		return "events/update-event";
	}
	
	@PostMapping("/updated/{id}")
	public String eventUpdated(@PathVariable("id") long id, Event event, 
			  BindingResult result, Model model) {
		if (result.hasErrors()) {
	        event.setId(id);
	        return "events/update-event";
	    }
		eventService.save(event);
		model.addAttribute("events", eventService.findAll());
		return "redirect:/events";
	}

}
