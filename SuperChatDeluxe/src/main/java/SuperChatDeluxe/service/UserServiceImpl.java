package SuperChatDeluxe.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import SuperChatDeluxe.exception.AlreadyInUseException;
import SuperChatDeluxe.model.User;
import SuperChatDeluxe.repository.UserRepository;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerNewUser(User user) throws AlreadyInUseException {
    	Optional<User> foundUser = userRepository.findById(user.getUsername());
    	
    	if(foundUser.isPresent()) {
    		throw new AlreadyInUseException("username", user.getUsername());
    	}
    	
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypt the password
        return userRepository.save(user);
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findById(username).orElse(null);
    }

    // Implement more methods as needed
}
