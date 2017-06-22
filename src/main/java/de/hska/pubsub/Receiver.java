package de.hska.pubsub;

import de.hska.persistence.domain.Post;
import org.springframework.beans.factory.annotation.Autowired;

import de.hska.persistence.repository.MainRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;

public class Receiver {
	@Autowired 
	private MainRepository mainRepo;
	@Autowired
	private SimpMessagingTemplate msgtemplate;

	// Got message from subscribed Channel -> send via redisTemplate
	public void receiveMessage(String postId) {
		sendInfoToFollower(mainRepo.getPost(postId.substring(7,postId.length())));
	}

	public void sendInfoToFollower(Post post) {
		ArrayList<String> users = mainRepo.getFollower(post.getUsername());

		for(String username : users) {
			String dateText = post.getDateText();
			String date = dateText.split(" ")[0];
			String time = dateText.split(" ")[1];
			msgtemplate.convertAndSend("/user/" + username + "/message", post.getUsername() +
					" hat etwas gepostet am " + date + " um " + time);
		}
	}
}