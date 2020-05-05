package uk.ac.man.cs.eventlite.controllers;

import org.springframework.hateoas.MediaTypes;
import java.util.ArrayList;
import java.util.List;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
@RestController
@RequestMapping(value = "/api", produces = { MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE })
public class HomeControllerApi {

	@RequestMapping(method = RequestMethod.GET)
	public Resources<Object> getLinks() {
		//get links
		List<Link> links = new ArrayList<Link>();
		Link venues = linkTo(VenueControllerApi.class).withRel("venues");
		Link events = linkTo(EventsControllerApi.class).withRel("events");
		
		links.add(venues);
		links.add(events);
		
		return new Resources<Object>(new ArrayList<Object>(), links);
	}
}


