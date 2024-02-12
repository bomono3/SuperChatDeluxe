package SuperChatDeluxe.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
@Table(name = "message")
public class Message implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer messageId;
	
	@Column(nullable = false)
	private String message;
	
	@Column(columnDefinition = "boolean default false")
	private boolean isPrivate;
	
	@Column(nullable = false)
	private LocalDateTime timeSent;
	
	@ManyToOne
	@JoinColumn(name = "username", referencedColumnName = "username")
	private User username;
	
	@ManyToOne
	@JoinColumn(name = "sent_to", referencedColumnName = "username")
	@Column(nullable = true)
	private User sentTo;
	
	public Message() {
		
	}
	

	public Message(String message, boolean isPrivate, LocalDateTime timeSent, User username,
			User sentTo) {
		this.message = message;
		this.isPrivate = isPrivate;
		this.timeSent = timeSent;
		this.username = username;
		this.sentTo = sentTo;
	}

	public Integer getMessageId() {
		return messageId;
	}

	public void setMessageId(Integer messageId) {
		this.messageId = messageId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public void setPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	public LocalDateTime getTimeSent() {
		return timeSent;
	}

	public void setTimeSent(LocalDateTime timeSent) {
		this.timeSent = timeSent;
	}

	public User getUsername() {
		return username;
	}

	public void setUsername(User username) {
		this.username = username;
	}

	public User getSentTo() {
		return sentTo;
	}

	public void setSentTo(User sentTo) {
		this.sentTo = sentTo;
	}



	@Override
	public String toString() {
		return "Message [messageId=" + messageId + ", message=" + message + ", isPrivate=" + isPrivate + ", timeSent="
				+ timeSent + ", username=" + username + ", sentTo=" + sentTo + "]";
	}
	
}
