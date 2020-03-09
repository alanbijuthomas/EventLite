package uk.ac.man.cs.eventlite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import uk.ac.man.cs.eventlite.dao.EventService;


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

	// Same as above method but, put into list and only return if event contains string
	@RequestMapping(value = "/search-by-name", method = RequestMethod.GET)
	public String searchEventName(@RequestParam (value = "search", required = false) String searchTerm, Model model) {
		model.addAttribute("search", eventService.findAllByNameContainingIgnoreCase(searchTerm));
		model.addAttribute("events", eventService.findAll());
		
		return "events/index";
	}

	
}

