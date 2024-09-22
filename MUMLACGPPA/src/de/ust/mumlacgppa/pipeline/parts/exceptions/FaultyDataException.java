package de.ust.mumlacgppa.pipeline.parts.exceptions;

public class FaultyDataException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4323765353038264805L;

	public FaultyDataException(String errorMessage){
		super(errorMessage);
	}
}
