package SuperChatDeluxe.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import SuperChatDeluxe.model.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer>{
	
	@Query(value = "select * from message where (username is not null and (is_private = true and sent_to = ?1)) or (username is not null and is_private = false) order by time_sent asc", nativeQuery = true)
	public List<Message> getMessagesByLatest(String username);
	
	@Query(value = "select * from message where (username is not null and (is_private = true and sent_to = ?1)) or (username is not null and is_private = false) order by time_sent desc limit ?2", nativeQuery = true)
	public List<Message> getLastMessages(String username, int limit);
	
	@Query( value = "select * from message where ((username is not null and (is_private = true and (username != ?1 and sent_to = ?1))) or (username is not null and is_private = false)) and (time_sent >= ?2 and time_sent <= ?3) order by time_sent asc" , nativeQuery = true)
	public List<Message> getMessageWhileGone(String username,LocalDateTime begin, LocalDateTime end);
}
