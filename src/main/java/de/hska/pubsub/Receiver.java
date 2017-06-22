package de.hska.pubsub;


import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.hska.controller.MainController;
import de.hska.persistence.repository.MainRepository;

public class Receiver {
	@Autowired 
	private MainRepository mainRepo;
	private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);
	
	public void receiveMessage(String postId) {
		MainController mainController = new MainController();
		mainController.sendInfoToFollower(mainRepo.getPost(postId));
	}

}
