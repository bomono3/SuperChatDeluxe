package SuperChatDeluxe.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import SuperChatDeluxe.model.Message;
import SuperChatDeluxe.model.User;
import SuperChatDeluxe.repository.MessageRepository;
import SuperChatDeluxe.repository.UserRepository;

@Service
public class MessageService {

		@Autowired 
		MessageRepository repo;
		
		@Autowired
		UserRepository userRepo;
		
		public Message sendMessage(Message message) {
			message.setMessageId(null);
			Optional<User> user = userRepo.findByUsername(message.getUsername().getUsername());
			
			if(!user.isEmpty()) {
				message.setUsername(user.get());
			}
			Message newMessage = repo.save(message);
			
			return newMessage;
		}
		
		public List<Message> getMessageHistory(String username){
			
			return repo.getMessagesByLatest(username);
		}
		
		public List<Message> getLastMessages(int limit){
			return repo.getLastMessages(limit);
		}
		
		public List<Message> getMessageWhileGone(String username, LocalDateTime begin, LocalDateTime end){
			return repo.getMessageWhileGone(username, begin, end);
		}
}
