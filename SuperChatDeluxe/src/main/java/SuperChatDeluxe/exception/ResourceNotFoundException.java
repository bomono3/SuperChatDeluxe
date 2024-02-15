package SuperChatDeluxe.exception;

public class ResourceNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public ResourceNotFoundException(String resource, int id) {
		super(resource + " with id = " + id + " was not found");
	}
	
	public ResourceNotFoundException(String resource) {
		super(resource + " could not be found");
	}
	
	public ResourceNotFoundException(String resource, String field, String value) {
		super(resource + " with " + field + " = " + value + " was not found");
	}
}
