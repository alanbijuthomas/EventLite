package uk.ac.man.cs.eventlite.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Venue;

@Controller @RequestMapping(value = "/venues", produces = {MediaType.TEXT_HTML_VALUE}) 
public class VenueController {

	@Autowired
	private VenueService venueService;

	
	@RequestMapping(value = "new-venue.html",method = RequestMethod.POST)
    public String addVenue(Model model) {
       
        Venue genericVenue = new Venue();
        genericVenue.setId(1);
        genericVenue.setName("genericManchester");
        genericVenue.setCapacity(100);
        genericVenue.setAddress("Kilburn building, Oxford Road");
        genericVenue.setPostcode("M99 9KB");
        venueService.save(genericVenue);
       
        return "events/index";
    }
	
	
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
	 

}
