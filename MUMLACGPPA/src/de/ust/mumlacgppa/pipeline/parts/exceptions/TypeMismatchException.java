package de.ust.mumlacgppa.pipeline.parts.exceptions;

// TODO: Maybe

public class TypeMismatchException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8353470727677363100L;
	
	public TypeMismatchException(String errorMessage){
		super(errorMessage);
	}
}
