package uk.ac.man.cs.eventlite.controllers;

import java.io.IOException;

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
        
        return "redirect:/events";
    }
    
    private void addLongLat(Venue v) throws IOException
	{
		String address = v.getAddress();
		String postcode = v.getPostcode();
		String query = address + " " + postcode;
		MapboxGeocoding client = MapboxGeocoding.builder()
				.accessToken(accessToken)
				.query(query)
				.mode(GeocodingCriteria.MODE_PLACES)
				.limit(1)
				.build();
		
		Response<GeocodingResponse> response = client.executeCall();
		GeocodingResponse gResponse = response.body();
		Point coords = gResponse.features().get(0).center();
		v.setLatitude(coords.latitude());
		v.setLongitude(coords.longitude());
		
	}
	 

}
