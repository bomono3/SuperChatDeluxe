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
import SuperChatDeluxe.util.RSA;

@Service
public class MessageService {

		@Autowired 
		MessageRepository repo;
		
		@Autowired
		RSA rsa;
		
		@Autowired
		UserRepository userRepo;
		
		public Message sendMessage(Message message) {
			message.setMessageId(null);
			Optional<User> user = userRepo.findByUsername(message.getUsername().getUsername());
			Optional<User> sentUser = message.getSentTo() == null ? null : userRepo.findByUsername(message.getSentTo().getUsername());
			
			if(!user.isEmpty()) {
				message.setUsername(user.get());
			}
			if(sentUser != null && !sentUser.isEmpty()) {
				message.setSentTo(sentUser.get());
			}
			Message newMessage = repo.save(message);
			
			return newMessage;
		}
		
		public List<Message> getMessageHistory(String username) throws Exception{
			List<Message> messages = repo.getMessagesByLatest(username);
			
			return messages;
			
		}
		
		public List<Message> getLastMessages(int limit) throws Exception{
			List<Message> messages = repo.getLastMessages(limit);
			
			return messages;
		}
		
		public List<Message> getMessageWhileGone(String username, LocalDateTime begin, LocalDateTime end) throws Exception{
			List<Message> messages = repo.getMessageWhileGone(username, begin, end);
			
			return messages;
			
		}
}
