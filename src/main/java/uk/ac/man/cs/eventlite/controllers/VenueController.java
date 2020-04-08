package uk.ac.man.cs.eventlite.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@Controller @RequestMapping(value = "/venues", produces = {MediaType.TEXT_HTML_VALUE}) 
public class VenueController {

	@Autowired
	private VenueService venueService;
	
    @RequestMapping(value = "new-venue", method = RequestMethod.GET)
    public String newVenue(Model model) {
        
    	if (!model.containsAttribute("venue")) {
            model.addAttribute("venue", new Venue());
        }
        
        if (!model.containsAttribute("venueList")) {
            model.addAttribute("venueList", venueService.findAll());
        }
        
        return "venues/new-venue";
    }
    
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String createVenue(@RequestBody @Valid @ModelAttribute Venue venue, BindingResult errors, Model model, RedirectAttributes redirectAttrs) {
        if (errors.hasErrors()) {
            model.addAttribute("venue", venue);
            model.addAttribute("venueList", venueService.findAll());
            return "venues/new-venue";
        }
        
        venueService.save(venue);
        redirectAttrs.addFlashAttribute("ok_message", "New Venue added.");
        
        return "redirect:/events";
    }
	 
    
    @GetMapping("/update/{id}")
	public String updateVenue(@PathVariable("id") long id, Model model)  {
		model.addAttribute("venue", venueService.findOne(id));
		return "venues/update-venue";
	}
	
	@PostMapping("/updated/{id}")
	public String venueUpdated(@PathVariable("id") long id, Venue venue, 
			  BindingResult result, Model model) {
		if (result.hasErrors()) {
			venue.setId(id);
			model.addAttribute("venue", venueService.findOne(id));
	        return "venues/update-venue";
	    }
		venueService.save(venue);
        model.addAttribute("venueList", venueService.findAll());
		return "redirect:/venues";
	}

}
