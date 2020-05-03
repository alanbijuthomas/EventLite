package uk.ac.man.cs.eventlite.controllers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;

import javax.servlet.Filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import uk.ac.man.cs.eventlite.EventLite;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;
import uk.ac.man.cs.eventlite.config.Security;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = EventLite.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class EventsControllerTest {

	private MockMvc mvc;

	@Autowired
	private Filter springSecurityFilterChain;

	@Mock
	private Event event;

	@Mock
	private Venue venue;

	@Mock
	private EventService eventService;
	
	@Mock
	private VenueService venueService;

	@InjectMocks
	private EventsController eventsController;

	private Venue v1;
	
	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(eventsController).apply(springSecurity(springSecurityFilterChain))
				.build();
		v1 = new Venue();
		v1.setAddress("v1");
		v1.setCapacity(1);
		v1.setId(1);
		v1.setName("v1");
		v1.setPostcode("v1");
	}

	@Test
	public void getIndexWhenNoEvents() throws Exception {
		when(eventService.findAll()).thenReturn(Collections.<Event> emptyList());

		mvc.perform(get("/events").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/index")).andExpect(handler().methodName("getAllEvents"));

		verify(eventService).findAll();
		verifyZeroInteractions(event);
		verifyZeroInteractions(venue);
	}

	@Test
	public void getIndexWithEvents() throws Exception {
		Venue v = new Venue();
		v.setName("Venue");
		v.setCapacity(1);
		
		Event e = new Event();
		e.setId(0);
		e.setName("Event");
		e.setDate(LocalDate.now());
		e.setTime(LocalTime.now());
		e.setVenue(v);

		when(eventService.findAll()).thenReturn(Collections.<Event>singletonList(e));

		mvc.perform(get("/events").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("events/index")).andExpect(handler().methodName("getAllEvents"));

		verify(eventService).findAll();
		verifyZeroInteractions(event);
		verifyZeroInteractions(venue);
	}
	
	@Test
	public void deleteEvent() throws Exception {
		mvc.perform(MockMvcRequestBuilders.delete("/events/1").with(user("Rob").roles(Security.ADMIN_ROLE))
				.contentType(MediaType.APPLICATION_FORM_URLENCODED).with(csrf()))
		.andExpect(status().isFound())
		.andExpect(handler().methodName("deleteEventById"));
		
		verify(eventService).deleteEventById(1);
	}
	
	@Test
	public void searchNoEventsTest() throws Exception
	{
		when(eventService.findAllByNameContainingIgnoreCase(null)).thenReturn(Collections.<Event> emptyList());
		
		mvc.perform(get("/events/search-by-name?search=t").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("events/index")).andExpect(handler().methodName("searchEventName"))
		.andExpect(model().attributeExists("search"))
		.andExpect(model().attributeExists("future_events_search"))
		.andExpect(model().attributeExists("past_events_search"));
		
		verify(eventService).findAllByNameContainingIgnoreCase("t");
		verifyZeroInteractions(event);
		verifyZeroInteractions(venue);
	}
	
	@Test
	public void searchEventsTest() throws Exception
	{
		Event e = new Event();
		e.setId(0);
		e.setName("Event");
		e.setDate(LocalDate.now());
		e.setTime(LocalTime.now());
		when(eventService.findAll()).thenReturn(Collections.<Event>singletonList(e));
		
		mvc.perform(get("/events/search-by-name?search=t").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("events/index")).andExpect(handler().methodName("searchEventName"))
		.andExpect(model().attributeExists("search"))
		.andExpect(model().attributeExists("future_events_search"))
		.andExpect(model().attributeExists("past_events_search"));
		//.andExpect(model().attribute("search", hasItem(hasProperty("event.id", is("0")))));
		
		
		verify(eventService).findAllByNameContainingIgnoreCase("t");
		verifyZeroInteractions(event);
		verifyZeroInteractions(venue);
	}
	
	@Test
	public void viewEventsTest() throws Exception
	{
		when(eventService.findAllByNameContainingIgnoreCase(null)).thenReturn(Collections.<Event> emptyList());
		
		mvc.perform(get("/events").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("events/index")).andExpect(handler().methodName("getAllEvents"))
		.andExpect(model().attributeExists("future_events"))
		.andExpect(model().attributeExists("past_events"));
		
		verify(eventService).findAll();
		verifyZeroInteractions(event);
		verifyZeroInteractions(venue);
	}
	
	@Test
	public void detailedEventsTest() throws Exception
	{
		Venue v = new Venue();
		v.setName("Venue");
		v.setCapacity(1);
		
		Event e = new Event();
		e.setId(0);
		e.setName("Event");
		e.setDate(LocalDate.now());
		e.setTime(LocalTime.now());
		e.setVenue(v);
		when(eventService.findOne(0)).thenReturn(e);
		
		mvc.perform(get("/events/details-event/0").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("events/details-event")).andExpect(handler().methodName("getEvent"))
		.andExpect(model().attributeExists("id")).andExpect(model().attributeExists("name"))
		.andExpect(model().attributeExists("time")).andExpect(model().attributeExists("date"));

		verify(eventService).findOne(0);
		verifyZeroInteractions(event);
		verifyZeroInteractions(venue);
	}
	
	@Test
	public void addEventMethodTest() throws Exception
	{
		Venue v = new Venue();
		v.setName("Venue");
		v.setCapacity(1);
		
		when(venueService.findAll()).thenReturn(Collections.<Venue>singletonList(v));
		
		mvc.perform(get("/events/new").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("events/new")).andExpect(handler().methodName("newEvent"));
		
	}
	
	@Test
    public void addInvalidEventTest() throws Exception {
		
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String,String>();
        // Some parameters, with an incorrect date
        params.add("name", "Event");
        params.add("time", "11:00");
        params.add("description", "Event Description");
        params.add("venue", "0");
        params.add("date", "00-00-1101010");

        mvc.perform(MockMvcRequestBuilders.post("/events").with(user("Rob").roles(Security.ADMIN_ROLE))
        .contentType(MediaType.APPLICATION_FORM_URLENCODED).params(params).accept(MediaType.TEXT_HTML).with(csrf()))
        .andExpect(status().isOk()).andExpect(view().name("events/new"))
        .andExpect(handler().methodName("createEvent"))
        .andExpect(model().attributeHasFieldErrors("event", "date"));

        verify(eventService, never()).save(event);
    }

	
	@Test
	public void updateEventMethodTest() throws Exception
	{
		Venue v = new Venue();
		v.setName("Venue");
		v.setCapacity(1);
		
		when(venueService.findAll()).thenReturn(Collections.<Venue>singletonList(v));
		
		mvc.perform(get("/events/update/0").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("events/update-event")).andExpect(handler().methodName("updateEvent"));
		
	}

}
