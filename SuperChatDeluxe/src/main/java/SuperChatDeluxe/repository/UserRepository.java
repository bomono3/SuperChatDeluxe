package SuperChatDeluxe.repository;

import SuperChatDeluxe.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, String> {
		@Query(value ="select * from useres where username = ?1", nativeQuery = true )
		public Optional<User> findByUsername(String username);
}
