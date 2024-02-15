package SuperChatDeluxe.service;

import SuperChatDeluxe.exception.AlreadyInUseException;
import SuperChatDeluxe.model.User;

public interface UserService {
    User registerNewUser(User user) throws AlreadyInUseException;

    User findUserByUsername(String username);
    
}