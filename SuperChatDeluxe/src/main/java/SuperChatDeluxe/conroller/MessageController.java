package SuperChatDeluxe.conroller;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.PropertyValueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import SuperChatDeluxe.model.Message;
import SuperChatDeluxe.service.MessageService;

@RequestMapping("/api")
@RestController
public class MessageController {

	@Autowired
	MessageService service;

	@PostMapping("/message")
	public ResponseEntity<?> sendMessage(@RequestBody Message newMessage) throws PropertyValueException{
		Message newAddedMessage = service.sendMessage(newMessage);

		return ResponseEntity.status(200).body(newAddedMessage);
	}

	@GetMapping("/message/{username}")
	public List<Message> getMessageHistory(@PathVariable String username) throws Exception{

		return service.getMessageHistory(username);
	}

	@GetMapping("/message/last/{username}/{limit}")
	public List<Message> getLastMessages(@PathVariable String username,@PathVariable int limit) throws Exception, MethodArgumentTypeMismatchException{
		return service.getLastMessages(username, limit);
	}

	@GetMapping("/message/gone/{username}/{startDateTime}/{endDateTime}")
	public List<Message> getMessageWhileGone(@PathVariable String username, @PathVariable LocalDateTime startDateTime, @PathVariable LocalDateTime endDateTime) throws Exception, MethodArgumentTypeMismatchException{

		return service.getMessageWhileGone(username, startDateTime, endDateTime);
	}
}
