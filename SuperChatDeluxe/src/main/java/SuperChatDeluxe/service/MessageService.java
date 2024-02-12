package SuperChatDeluxe.service;

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
		
		public List<Message> getMessageHistory(){
			
			return null;
		}
}
