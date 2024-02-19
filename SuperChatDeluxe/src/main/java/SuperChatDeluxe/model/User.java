package SuperChatDeluxe.model;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
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
    
    @Column(length = 1024)
    private String publicKey;

    public User() {
    	
    }
    
    public User(String username) {
    	this.username = username;
    }
    
    public User(String username, String password) {
    	this.username = username;
    	this.password = password;
    }
    
    public User(String username, String password, String publicKey) {
    	this.username = username;
    	this.password = password;
    	this.publicKey = publicKey;
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
    

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

	
	@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password + ", messages=" + messages + ", publicKey="
				+ publicKey + "]";
	}

	  public String toJson() {
	        return "{" +
	                "\"username\" : \"" + username + "\"," +
	                "\"password\" : \"" + password + "\"," +
	                "\"publicKey\" : \"" + publicKey + "\"," +
	                "\"messages\" : \"" + messages + "\"" +
	                "}";
	    }
}
