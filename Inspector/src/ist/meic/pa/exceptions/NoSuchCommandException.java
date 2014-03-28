package ist.meic.pa.exceptions;

public class NoSuchCommandException extends Exception {

	public NoSuchCommandException(String string) {
		super("Command named: " + string + " do not exist!!");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4876917756955179865L;

}
