package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.ac.man.cs.eventlite.testutil.MessageConverterUtil.getMessageConverters;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;

import javax.servlet.Filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BeanPropertyBindingResult;

import com.fasterxml.jackson.databind.ObjectMapper;

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
public class VenuesControllerApiTest {

	private MockMvc mvc;

	@Autowired
	private Filter springSecurityFilterChain;

	@Mock
	private EventService eventService;
	
	@Mock
	private VenueService venueService;

	@InjectMocks
	private EventsControllerApi eventsController;
	
	@InjectMocks
	private VenueControllerApi venuesController;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(venuesController).apply(springSecurity(springSecurityFilterChain))
				.setMessageConverters(getMessageConverters()).build();
	}

	@Test
	public void getIndexWhenNoVenues() throws Exception {
		when(venueService.findAll()).thenReturn(Collections.<Venue> emptyList());

		mvc.perform(get("/api/venues").accept(MediaType.APPLICATION_JSON).with(csrf()))
				.andExpect(status().isOk())
				.andExpect(handler().methodName("getAllVenues")).andExpect(jsonPath("$.length()", equalTo(1)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues")));

		verify(venueService).findAll();
	}
	
	@Test
	public void getIndexWithVenues() throws Exception {
		Venue v = new Venue();
		v.setId(0);
		v.setName("Venue");
		v.setCapacity(0);
		
		Event e = new Event();
		e.setId(0);
		e.setName("Event");
		e.setDate(LocalDate.now());
		e.setTime(LocalTime.now());
		e.setVenue(v);
		when(venueService.findAll()).thenReturn(Collections.<Venue>singletonList(v));

		mvc.perform(get("/api/venues").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("getAllVenues")).andExpect(jsonPath("$.length()", equalTo(2)))
				.andExpect(jsonPath("$._links.self.href", endsWith("/api/venues")))
				.andExpect(jsonPath("$._embedded.venues.length()", equalTo(1)))
				.andExpect(jsonPath("$._embedded.venues[0]._links.self.href", not(empty())))
				.andExpect(jsonPath("$._embedded.venues[0]._links.self.href", endsWith("venues/0")));

		verify(venueService).findAll();
	}

	@Test
	public void getExistingVenueTest() throws Exception {
		long id = 1234;
		Venue venue = new Venue();
		venue.setName("testname");
		venue.setCapacity(80);
		venue.setId(id);
		when(venueService.findOne(id)).thenReturn(venue);

		mvc.perform(get("/api/venues/1234").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("venue")).andExpect(jsonPath("$.length()", equalTo(8)))
				.andExpect(jsonPath("$._links.length()", equalTo(4)))
				.andExpect(jsonPath("$._links.self.href", endsWith("api/venues/1234")))
				.andExpect(jsonPath("$._links.venue.href", endsWith("api/venues/1234")))
				.andExpect(jsonPath("$._links.events.href", endsWith("api/venues/1234/events")))
				.andExpect(jsonPath("$._links.next3events.href", endsWith("api/venues/1234/next3events")))
				.andExpect(jsonPath("$.name", endsWith("testname")))
				.andExpect(jsonPath("$.capacity", equalTo(80)));

		verify(venueService).findOne(id);
	}
	
	@Test
	public void getVenuesEventsTest() throws Exception {
		long id = 1234;
		Venue venue = new Venue();
		venue.setName("testname");
		venue.setCapacity(80);
		venue.setId(id);
		for(int i = 0; i < 3; i++) {
			Event e = new Event();
			e.setId(i);
			e.setName("Event" + i);
			venue.addEvent(e);
		}
		
		when(venueService.findOne(id)).thenReturn(venue);

		mvc.perform(get("/api/venues/1234/events").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("venuesEvents")).andExpect(jsonPath("$.length()", equalTo(2)))
				.andExpect(jsonPath("$._links.length()", equalTo(1)))
				.andExpect(jsonPath("$._links.self.href", endsWith("api/venues/1234/events")))
				.andExpect(jsonPath("$._embedded.events.length()", equalTo(3)))
				.andExpect(jsonPath("$._embedded.events[1].name", endsWith("Event1")));

		verify(venueService).findOne(id);
	}
	
	@Test
	public void getVenuesEventsWithNoEventsTest() throws Exception {
		long id = 1234;
		Venue venue = new Venue();
		venue.setName("testname");
		venue.setCapacity(80);
		venue.setId(id);
		
		when(venueService.findOne(id)).thenReturn(venue);

		mvc.perform(get("/api/venues/1234/events").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("venuesEvents")).andExpect(jsonPath("$.length()", equalTo(1)))
				.andExpect(jsonPath("$._links.length()", equalTo(1)))
				.andExpect(jsonPath("$._links.self.href", endsWith("api/venues/1234/events")));

		verify(venueService).findOne(id);
	}
	
	@Test
	public void getVenuesNext3EventsFor5Events4AfterTodayTest() throws Exception {
		long id = 1234;
		Venue venue = new Venue();
		venue.setName("testname");
		venue.setCapacity(80);
		venue.setId(id);
		for(int i = 0; i < 5; i++) {
			Event e = new Event();
			e.setId(i);
			e.setName("Event" + i);
			e.setDate(LocalDate.now().minusDays(1).plusDays(i));
			venue.addEvent(e);
		}
		
		when(venueService.findOne(id)).thenReturn(venue);

		mvc.perform(get("/api/venues/1234/next3events").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("venuesNext3Events")).andExpect(jsonPath("$.length()", equalTo(2)))
				.andExpect(jsonPath("$._links.length()", equalTo(1)))
				.andExpect(jsonPath("$._links.self.href", endsWith("api/venues/1234/next3events")))
				.andExpect(jsonPath("$._embedded.events.length()", equalTo(3)))
				.andExpect(jsonPath("$._embedded.events[0].name", endsWith("Event1")))
				.andExpect(jsonPath("$._embedded.events[1].name", equalTo("Event2")))
				.andExpect(jsonPath("$._embedded.events[2].name", endsWith("Event3")));

		verify(venueService).findOne(id);
	}
	
	@Test
	public void getVenuesNext3Events1AfterTodayTest() throws Exception {
		long id = 1234;
		Venue venue = new Venue();
		venue.setName("testname");
		venue.setCapacity(80);
		venue.setId(id);
		for(int i = 0; i < 5; i++) {
			Event e = new Event();
			e.setId(i);
			e.setName("Event" + i);
			e.setDate(LocalDate.now().minusDays(4).plusDays(i));
			venue.addEvent(e);
		}
		
		when(venueService.findOne(id)).thenReturn(venue);

		mvc.perform(get("/api/venues/1234/next3events").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("venuesNext3Events")).andExpect(jsonPath("$.length()", equalTo(2)))
				.andExpect(jsonPath("$._links.length()", equalTo(1)))
				.andExpect(jsonPath("$._links.self.href", endsWith("api/venues/1234/next3events")))
				.andExpect(jsonPath("$._embedded.events.length()", equalTo(1)))
				.andExpect(jsonPath("$._embedded.events[0].name", endsWith("Event4")));

		verify(venueService).findOne(id);
	}
	
	@Test
	public void getVenuesNext3EventsAllBeforeTodayTest() throws Exception {
		long id = 1234;
		Venue venue = new Venue();
		venue.setName("testname");
		venue.setCapacity(80);
		venue.setId(id);
		for(int i = 0; i < 5; i++) {
			Event e = new Event();
			e.setId(i);
			e.setName("Event" + i);
			e.setDate(LocalDate.now().minusDays(6).plusDays(i));
			e.setTime(LocalTime.now().minusHours(1));
			venue.addEvent(e);
		}
		
		when(venueService.findOne(id)).thenReturn(venue);

		mvc.perform(get("/api/venues/1234/next3events").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("venuesNext3Events")).andExpect(jsonPath("$.length()", equalTo(1)))
				.andExpect(jsonPath("$._links.length()", equalTo(1)))
				.andExpect(jsonPath("$._links.self.href", endsWith("api/venues/1234/next3events")));

		verify(venueService).findOne(id);
	}
	
	@Test
	public void getVenuesNext3EventsWithNoEventsTest() throws Exception {
		long id = 1234;
		Venue venue = new Venue();
		venue.setName("testname");
		venue.setCapacity(80);
		venue.setId(id);
		
		when(venueService.findOne(id)).thenReturn(venue);

		mvc.perform(get("/api/venues/1234/next3events").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(handler().methodName("venuesNext3Events")).andExpect(jsonPath("$.length()", equalTo(1)))
				.andExpect(jsonPath("$._links.length()", equalTo(1)))
				.andExpect(jsonPath("$._links.self.href", endsWith("api/venues/1234/next3events")));

		verify(venueService).findOne(id);
	}
	
}
