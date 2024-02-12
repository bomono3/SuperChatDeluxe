package SuperChatDeluxe.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import SuperChatDeluxe.model.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer>{
	
	@Query(value = "select * from message where username is not null and (is_private = true and (username = ?1 or sent_to = ?1)) order by time_sent desc", nativeQuery = true)
	public List<Message> getMessagesByLatest(String username);
}
