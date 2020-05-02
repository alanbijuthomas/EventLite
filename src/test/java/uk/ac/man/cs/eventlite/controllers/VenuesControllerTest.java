package uk.ac.man.cs.eventlite.controllers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.atLeastOnce;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import uk.ac.man.cs.eventlite.EventLite;
import uk.ac.man.cs.eventlite.dao.EventService;
import uk.ac.man.cs.eventlite.dao.VenueService;
import uk.ac.man.cs.eventlite.entities.Event;
import uk.ac.man.cs.eventlite.entities.Venue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = EventLite.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class VenuesControllerTest {

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
	private VenueController venueController;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mvc = MockMvcBuilders.standaloneSetup(venueController).apply(springSecurity(springSecurityFilterChain))
				.build();
	}

	@Test
	public void getIndexWithVenues() throws Exception {
		when(venueService.findAll()).thenReturn(Collections.<Venue> singletonList(venue));

		mvc.perform(get("/venues").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("venues/index")).andExpect(handler().methodName("getAllVenues"));

		verify(venueService, atLeastOnce()).findAll();
		verifyZeroInteractions(event);
	}
	
	@Test
	public void getIndexWhenNoVenues() throws Exception {
		when(venueService.findAll()).thenReturn(Collections.<Venue> emptyList());

		mvc.perform(get("/venues").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
				.andExpect(view().name("venues/index")).andExpect(handler().methodName("getAllVenues"));

		verify(venueService, atLeastOnce()).findAll();
		verifyZeroInteractions(event);
		verifyZeroInteractions(venue);
	}
	
	@Test
	public void searchNoVenuesTest() throws Exception
	{
		when(venueService.findAllByNameContainingIgnoreCase(null)).thenReturn(Collections.<Venue> singletonList(venue));
		
		mvc.perform(get("/venues/search-by-venue-name?search=t").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("venues/index")).andExpect(handler().methodName("searchVenueName"));
		
		verify(venueService, atLeastOnce()).findAllByNameContainingIgnoreCase("t");
		verifyZeroInteractions(event);
		verifyZeroInteractions(venue);
	}
	
	@Test
	public void searchVenuesTest() throws Exception
	{
		when(venueService.findAll()).thenReturn(Collections.<Venue> singletonList(venue));
		
		mvc.perform(get("/venues/search-by-venue-name?search=t").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
		.andExpect(view().name("venues/index")).andExpect(handler().methodName("searchVenueName"));
		
		verify(venueService, atLeastOnce()).findAllByNameContainingIgnoreCase("t");
		verifyZeroInteractions(event);
	}
	
	@Test
	@WithMockUser(roles= "ADMINISTRATOR")
	public void deleteVenueWithAdmin() throws Exception {
		long id = 1234;
		Venue venue = mock(Venue.class);
		
		when(venueService.findOne(id)).thenReturn(venue);
				
		mvc.perform(delete("/venues/{id}", id).accept(MediaType.TEXT_HTML).with(csrf()))
					.andExpect(status().isFound())
					.andExpect(view().name("redirect:/venues"))
					.andExpect(handler().methodName("deleteVenue"));
		
		verify(venueService).deleteById(id);
	}
	
	@Test
	public void deleteVenueWithNoAccess() throws Exception {
		long id = 1234;
		Venue venue = mock(Venue.class);
		
		when(venueService.findOne(id)).thenReturn(venue);
				
		mvc.perform(delete("/venues/{id}", id).accept(MediaType.TEXT_HTML).with(csrf()))
					.andExpect(status().isFound());
		
		verify(venueService, never()).deleteById(id);
	}
	
	@Test
	@WithMockUser(roles= "ADMINISTRATOR")
	public void deleteVenueWithEvents() throws Exception {
		long id = 1234;
		Venue venue = mock(Venue.class);
		Event event = mock(Event.class);
		venue.addEvent(event);
		
		when(venueService.findOne(id)).thenReturn(venue);
				
		mvc.perform(delete("/venues/{id}", id).accept(MediaType.TEXT_HTML).with(csrf()))
					.andExpect(status().isFound())
					.andExpect(view().name("redirect:/venues"))
					.andExpect(handler().methodName("deleteVenue"));
		
		verify(venueService).deleteById(id);
	}
}
