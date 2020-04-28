package uk.ac.man.cs.eventlite.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "venues")
public class Venue {

	@Id
	@GeneratedValue
	private long id;
	
	@NotNull(message = "The event must have a name.")
	@Size(max = 256, message = "The venue's name must have 256 characters or less.")

	@NotNull(message = "The venue must have a name.")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@Size(max = 256, message = "The event's description must have 256 characters or less.")
	private String name;
	
	@NotNull(message = "The venue must have an address.")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@Size(max = 300, message = "The venue's roadname must have 300 characters or less.")
	private String address;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@NotNull(message = "The venue must have a postcode.")
	private String postcode;

	@NotNull(message = "The event must have a capacity number.")
	@Min(0)
	private int capacity;

	@OneToMany(targetEntity=Event.class, mappedBy="venue",cascade=CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Event> events = new ArrayList<Event>();
	
	public Venue() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	
	public void addEvent(Event event) {
		this.events.add(event);
	}
	
	public List<Event> getEvents() {
		return events;
	}
}
