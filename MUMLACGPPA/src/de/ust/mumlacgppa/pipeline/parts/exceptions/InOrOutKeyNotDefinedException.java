package de.ust.mumlacgppa.pipeline.parts.exceptions;

public class InOrOutKeyNotDefinedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5119950469009613011L;

	public InOrOutKeyNotDefinedException(String errorMessage){
		super(errorMessage);
	}
}
