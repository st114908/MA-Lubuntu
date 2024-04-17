package mumlacgppa.pipeline.parts.exceptions;

public class StepNotMatched extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2755244709021778112L;

	public StepNotMatched(String errorMessage){
		super(errorMessage);
	}
}
