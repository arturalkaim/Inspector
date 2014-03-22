package ist.meic.pa.exceptions;

public class ObjectNotExistsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8389100690785716152L;

	public ObjectNotExistsException() {
	}

	public ObjectNotExistsException(String message) {
		super("The Object "+message+ " not exists!");
		// TODO Auto-generated constructor stub
	}


}
