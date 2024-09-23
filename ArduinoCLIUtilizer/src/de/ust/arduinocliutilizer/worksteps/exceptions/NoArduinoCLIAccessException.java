package de.ust.arduinocliutilizer.worksteps.exceptions;

public class NoArduinoCLIAccessException extends Exception {
	private static final long serialVersionUID = -3823901017359603270L;

	/**
	 * 
	 */

	public NoArduinoCLIAccessException(String errorMessage){
		super(errorMessage);
	}
}
