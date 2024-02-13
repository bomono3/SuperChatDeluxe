package SuperChatDeluxe.service;

import SuperChatDeluxe.model.User;

public interface UserService {
    User registerNewUser(User user);

    User findUserByUsername(String username);
    
}