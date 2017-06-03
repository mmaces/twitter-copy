package de.hska.persistence.interfaces;

import de.hska.persistence.domain.User;

public interface MainRepositoryInterface {
	// user operations
	boolean addUser(User user);
	User getUser(String username);
	User updateUser(String username);
	boolean deleteUser(String username);
	
	// piep operations

}
