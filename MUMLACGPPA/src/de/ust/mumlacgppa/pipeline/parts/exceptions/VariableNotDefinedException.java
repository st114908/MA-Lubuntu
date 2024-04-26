package de.ust.mumlacgppa.pipeline.parts.exceptions;

public class VariableNotDefinedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5119950469009613011L;

	public VariableNotDefinedException(String errorMessage){
		super(errorMessage);
	}
}
