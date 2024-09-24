package de.ust.mumlacgppa.pipeline.parts.exceptions;

public class StepNotDefinedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5119950469009613011L;

	public StepNotDefinedException(String errorMessage){
		super(errorMessage);
	}
}
