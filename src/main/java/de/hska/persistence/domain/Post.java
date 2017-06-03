package de.hska.persistence.domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Post implements Serializable{
	private static final long serialVersionUID = 1482975917L;
	
	private String uuid;
	private long date;
	private String username;
	private String postText;
	
	public String getPostId() {
		return uuid;
	}
	public void setPostId(String string) {
		this.uuid = string;
	}
	public long getDate() {
		return date;
	}
	public void setDate(long l) {
		this.date = l;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPostText() {
		return postText;
	}
	public void setPostText(String piep) {
		this.postText = piep;
	}
	public String getDateText() {
		return new SimpleDateFormat("dd.M.yyyy HH:mm:ss").format(date);
	}
}
