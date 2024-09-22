package de.ust.mumlacgppa.pipeline.parts.exceptions;

public class ParameterMismatchException extends Exception {


	/**
	 * 
	 */
	private static final long serialVersionUID = -2192628370457239367L;

	public ParameterMismatchException(String errorMessage){
		super(errorMessage);
	}
}
