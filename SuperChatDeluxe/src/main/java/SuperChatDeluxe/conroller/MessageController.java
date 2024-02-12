package SuperChatDeluxe.conroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import SuperChatDeluxe.model.Message;
import SuperChatDeluxe.service.MessageService;

@RequestMapping("/api")
@RestController
public class MessageController {

	@Autowired
	MessageService service;
	
	@PostMapping("/message")
	public ResponseEntity<?> sendMessage(@RequestBody Message newMessage){
		Message newAddedMessage = service.sendMessage(newMessage);
		
		return ResponseEntity.status(200).body(newAddedMessage);
	}
	
	
}
