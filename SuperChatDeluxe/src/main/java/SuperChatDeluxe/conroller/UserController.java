package SuperChatDeluxe.conroller;


import SuperChatDeluxe.exception.AlreadyInUseException;
import SuperChatDeluxe.exception.ResourceNotFoundException;
import SuperChatDeluxe.model.User;
import SuperChatDeluxe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RestController
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) throws AlreadyInUseException {
        User registeredUser = userService.registerNewUser(user);
        return ResponseEntity.ok(registeredUser);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) throws ResourceNotFoundException {
        User user = userService.findUserByUsername(username);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            throw new ResourceNotFoundException("Username with name: " + username);
        }
    }
}
