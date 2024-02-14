package SuperChatDeluxe.conroller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

	@GetMapping("/message/{username}")
	public List<Message> getMessageHistory(@PathVariable String username){

		return service.getMessageHistory(username);
	}

	@GetMapping("/message/last/{limit}")
	public List<Message> getLastMessages(@PathVariable int limit){
		return service.getLastMessages(limit);
	}

	@GetMapping("/message/gone/{username}/{startDate}/{endDate}")
	public List<Message> getMessageWhileGone(@PathVariable String username, @PathVariable LocalDateTime startDate, @PathVariable LocalDateTime endDate){

		return service.getMessageWhileGone(username, startDate, endDate);
	}
}
