package uk.ac.man.cs.eventlite.entities;

import java.util.Date;


public class Tweet {

	private String text;
	
	private Date date;
	
	private String url;
	
	private String user;

	private long id;
	
	public Tweet(long id, String text, Date date, String user) {
		this.id = id;
		this.text = text;
		this.date = date;
		this.user = user;
		this.url ="https://twitter.com/" + user + "/status/" + id;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
}
