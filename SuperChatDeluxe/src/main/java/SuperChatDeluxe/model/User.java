package SuperChatDeluxe.model;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


@Entity
@Table(name = "users")
public class User implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
    private String username;

    private String password;
    
    @OneToMany(mappedBy = "username")
    private List<Message> messages;

    public User() {
    	
    }
    
    public User(String username) {
    	this.username = username;
    }
    
    public User(String username, String password) {
    	this.username = username;
    	this.password = password;
    }
    
    // Standard getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

	@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password + "]";
	}
    
	public String toJson() {
	    return "{\"username\" : " + username
	            + ", \"password\" : \"" + password + "\""
	            + "}";
	}
}
