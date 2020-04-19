package uk.ac.man.cs.eventlite.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "venues")
public class Venue {

	@Id
	@GeneratedValue
	private long id;
	
	@NotNull(message = "The event must have a name.")
	@Size(max = 256, message = "The venue's name must have 256 characters or less.")
	private String name;
	
	@NotNull(message = "The event must have an address.")
	@Size(max = 500, message = "The venue's address must have 500 characters or less.")
	private String address;

	@NotNull(message = "The event must have a capacity number.")
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
}
