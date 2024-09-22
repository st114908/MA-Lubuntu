package de.ust.mumlacgppa.pipeline.parts.exceptions;

public class AbortPipelineException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2973886930238931368L;

	public AbortPipelineException(String errorMessage){
		super(errorMessage);
	}
}
