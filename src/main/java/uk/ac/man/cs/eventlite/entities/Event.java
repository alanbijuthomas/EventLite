package uk.ac.man.cs.eventlite.entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.persistence.*;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "events")
public class Event implements Comparable<Event>{
	
	@Id
	@GeneratedValue
	private long id;
	
	@NotNull(message = "The event must have a date.")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Future(message = "The event must have a date in the future.")
	private LocalDate date;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@DateTimeFormat(pattern = "HH:mm")
	private LocalTime time;
	
	@NotNull(message = "The event must have a name.")
	@Size(max = 256, message = "The event's name must have 256 characters or less.")
	private String name;
	
	@Size(max = 500, message = "The event's description must have 500 characters or less.")
	private String description;
	
	@ManyToOne
	@NotNull(message = "The event must have a venue.")
	private Venue venue;
	

	public Event() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Venue getVenue() {
		return venue;
	}

	public void setVenue(Venue venue) {
		this.venue = venue;
	}
	
	public static int getIndexOfNextEventFromNow(List<Event> events) {
		for (int i = 0; i < events.size(); i++) {
			Event event = events.get(i);
			
			if(event.getDate().compareTo(LocalDate.now()) > 0)
				return i;
			else if (event.getDate().compareTo(LocalDate.now()) == 0)
				if (event.time == null)
					return i;
				else if(event.time.compareTo(LocalTime.now()) > 0)
					return i;
		}
		
		return events.size();
	}
	
	@Override
	public int compareTo(Event o) {
		if (this.date.compareTo(o.getDate()) == 0)
			if(this.time == null || o.getTime() == null || this.time.compareTo(o.getTime()) == 0)
				return 0;
			else 
				return this.time.compareTo(o.getTime());
		else
			return this.date.compareTo(o.getDate());
	}
}
