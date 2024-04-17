package mumlacgppa.pipeline.parts.exceptions;

public class ParameterMismatchException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2563070407656207780L;

	public ParameterMismatchException(String errorMessage){
		super(errorMessage);
	}
}
