package SuperChatDeluxe.model;

import java.io.Serializable;

public class AuthenticationResponse implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String response;
	
	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public AuthenticationResponse(String response) {
		this.response = response;
	}
	
}
