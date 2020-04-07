package uk.ac.man.cs.eventlite.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "venue")
public class Venue {

	@Id
	@GeneratedValue
	private long id;

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

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@NotNull
	@Min(0)
	private int capacity;

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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
