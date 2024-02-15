package SuperChatDeluxe.exception;
public class AlreadyInUseException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public AlreadyInUseException(String resource, String value) {
		super(resource + " " + value + " is already in use.");
	}
	
	public AlreadyInUseException(String resource, String value, String reason) {
		super(resource + " " + value + " is already in use. " + reason);
	}
}