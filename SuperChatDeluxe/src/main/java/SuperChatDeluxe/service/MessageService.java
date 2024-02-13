package SuperChatDeluxe.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import SuperChatDeluxe.model.Message;
import SuperChatDeluxe.repository.MessageRepository;

@Service
public class MessageService {

		@Autowired 
		MessageRepository repo;
		
		public Message sendMessage(Message message) {
			message.setMessageId(null);
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
