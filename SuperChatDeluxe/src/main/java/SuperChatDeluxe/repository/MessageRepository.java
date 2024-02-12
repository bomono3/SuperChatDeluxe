package SuperChatDeluxe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import SuperChatDeluxe.model.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer>{

}
