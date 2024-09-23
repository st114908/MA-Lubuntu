package de.ust.arduinocliutilizer.worksteps.exceptions;

public class NoArduinoCLIConfigFileException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7489718021296767817L;

	public NoArduinoCLIConfigFileException(String errorMessage){
		super(errorMessage);
	}
}
