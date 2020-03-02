package uk.ac.man.cs.eventlite.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;

@Controller
@RequestMapping(value = "/events", produces = { MediaType.TEXT_HTML_VALUE })
public class EventsController {

	@Autowired
	private EventService eventService;
	
	@Autowired
	private VenueService venueService;

	@RequestMapping(method = RequestMethod.GET)
	public String getAllEvents(Model model) {

		model.addAttribute("events", eventService.findAll());

		return "events/index";
	}
	
	@RequestMapping(value = "/add-event",method = RequestMethod.GET)
	public String newEvent(Model model)
	{
		if(!model.containsAttribute("event"))
		{
			model.addAttribute("event",new Event());
		}
		
		if(!model.containsAttribute("venueList"))
		{
			model.addAttribute("venueList", venueService.findAll());
		}
		
		return "events/add-event";
	}

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public String createEvent(@RequestBody @Validated @ModelAttribute Event event,
							  BindingResult errors, Model model, RedirectAttributes redirectAttrs)
	{
		if(errors.hasErrors())
		{
			model.addAttribute("event", event);
			model.addAttribute("venueList", venueService.findAll());
			
			return "events/add-event";
		}
		
		eventService.save(event);
		redirectAttrs.addFlashAttribute("ok_message", "New event added.");
		
		return "redirect:/events";
	}
}
