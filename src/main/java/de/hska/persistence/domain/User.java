package de.hska.persistence.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable{
	private static final long serialVersionUID = 1482472917L;
	
	private int uid;
	private String username;
	private String email;
	private String password;
	private ArrayList<String> follower;
	private ArrayList<String> following;
	private ArrayList<String> piepIds;
	
	public List<String> getPiepIds() {
		return piepIds;
	}
	public void setPiepIds(ArrayList<String> piepIds) {
		this.piepIds = piepIds;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public ArrayList<String> getFollower() {
		return follower;
	}
	public void setFollower(ArrayList<String> follower) {
		this.follower = follower;
	}
	public ArrayList<String> getFollowing() {
		return following;
	}
	public void setFollowing(ArrayList<String> following) {
		this.following = following;
	}
}
