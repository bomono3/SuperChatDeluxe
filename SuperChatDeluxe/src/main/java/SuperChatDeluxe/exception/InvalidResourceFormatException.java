package SuperChatDeluxe.exception;

public class InvalidResourceFormatException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public InvalidResourceFormatException(String resource, String value) {
		super(resource + " " + value + " is formated incorrectly.");
	}
	
	public InvalidResourceFormatException(String resource, String value, String reason) {
		super(resource + " " + value + " is formated incorrectly. " + reason);
	}

}
