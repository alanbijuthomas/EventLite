package uk.ac.man.cs.eventlite.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.MapboxGeocoding.Builder;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;

import retrofit2.Response;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@Controller @RequestMapping(value = "/venues", produces = {MediaType.TEXT_HTML_VALUE}) 
public class VenueController {

	private final String accessToken = "pk.eyJ1IjoiZXZlbnRlbGl0ZWYxNTIwIiwiYSI6ImNrOWZyNzJ1NTA5NnQzbm1rOXhxcjlua3cifQ.qxpn8OXHCZFD00ydAIVM8w";
	
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
        
        try {
			addLongLat(venue);
		} catch (IOException e) {
			e.printStackTrace();
		}

        venueService.save(venue);
        redirectAttrs.addFlashAttribute("ok_message", "New Venue added.");
        
        return "redirect:/venues";
    }
    
    
    // Method to add longitude and latitude to a given event
    private void addLongLat(Venue v) throws IOException
	{
		String address = v.getAddress();
		String postcode = v.getPostcode();
		String query = address + " " + postcode;
		MapboxGeocoding client = MapboxGeocoding.builder()
				.accessToken(accessToken)
				.query(query)
				.mode(GeocodingCriteria.MODE_PLACES)
				.limit(1) // limited to one search result to preserve API requests
				.build();
		
		Response<GeocodingResponse> response = client.executeCall();
		GeocodingResponse gResponse = response.body();
		Point coords = gResponse.features().get(0).center();
		v.setLatitude(coords.latitude());
		v.setLongitude(coords.longitude());
		
	}
	 
    
    @GetMapping("/update/{id}")
	public String updateVenue(@PathVariable("id") long id, Model model)  {
		//model.addAttribute("venue", venueService.findOne(id));
		Venue venue = venueService.findOne(id);
		model.addAttribute("venue", venue);
		model.addAttribute("name", venue.getName());
		model.addAttribute("address", venue.getAddress());
		model.addAttribute("postcode", venue.getPostcode());
		model.addAttribute("capacity", venue.getCapacity());
		
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
		
		// update the longitude and latitude
		try {
			addLongLat(venue);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		venueService.save(venue);
        model.addAttribute("venueList", venueService.findAll());
		return "redirect:/venues";
	}
	
	@RequestMapping(method = RequestMethod.GET)
    public String getAllVenues(Model model) {
        model.addAttribute("venues", venueService.findAll());
        
        List<Venue> venues_with_events = new ArrayList<Venue>();
        List<Venue> empty_venues = new ArrayList<Venue>();
        Iterator<Venue> venues = venueService.findAll().iterator();
        
        while (venues.hasNext()) {
        	Venue v = venues.next();
        	// venue has events
        	if (!v.getEvents().isEmpty()) {
        		venues_with_events.add(v);
        	} else { // venue does not have events
        		empty_venues.add(v);
        	}
        }
        
        model.addAttribute("venues_with_events", venues_with_events);
        model.addAttribute("empty_venues", empty_venues);

        return "venues/index";
    }
	
	// Searches venues by searchTerm query.
	@RequestMapping(value = "/search-by-venue-name", method = RequestMethod.GET)
	public String searchVenueName(@RequestParam (value = "search", required = false) String searchTerm, Model model) {
		model.addAttribute("venue_search", venueService.findAllByNameContainingIgnoreCase(searchTerm));
		getAllVenues(model);
		
		List<Venue> venues_with_events_searched = new ArrayList<Venue>();
        List<Venue> empty_venues_searched = new ArrayList<Venue>();
        Iterator<Venue> venues_searched = venueService.findAllByNameContainingIgnoreCase(searchTerm).iterator();
        
        while (venues_searched.hasNext()) {
        	Venue v = venues_searched.next();
        	// venue has events
        	if (!v.getEvents().isEmpty()) {
        		venues_with_events_searched.add(v);
        	} else { // venue does not have events
        		empty_venues_searched.add(v);
        	}
        }
        
        model.addAttribute("venues_with_events_searched", venues_with_events_searched);
        model.addAttribute("empty_venues_searched", empty_venues_searched);
		
		return "venues/index";
	} // searchVenueName

	
	
	    
    @RequestMapping(value = "/details-venue/{id}", method = RequestMethod.GET)
	public String getVenue(@PathVariable("id") long id, Model model) {

		Venue venue = venueService.findOne(id);
		model.addAttribute("id", id);
		model.addAttribute("name", venue.getName());
		model.addAttribute("address", venue.getAddress());
		model.addAttribute("postcode", venue.getPostcode());
		model.addAttribute("capacity", venue.getCapacity());
		model.addAttribute("longitude",venue.getLongitude());
		model.addAttribute("latitude",venue.getLatitude());
		
		
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

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public String deleteVenue(@PathVariable("id") long id) {
		if (!venueService.findOne(id).getEvents().isEmpty()) {
			String infoMessage = "Cannot delete this venue";
			System.out.println(infoMessage);
			return "redirect:/venues/details-venue/" + id;
		}
		
		venueService.deleteById(id);

		return "redirect:/venues";
	}
}
