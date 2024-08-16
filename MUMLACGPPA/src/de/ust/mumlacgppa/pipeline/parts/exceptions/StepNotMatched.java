package de.ust.mumlacgppa.pipeline.parts.exceptions;

public class StepNotMatched extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1351403630494066029L;

	public StepNotMatched(String errorMessage){
		super(errorMessage);
	}
}
