package de.ust.arduinocliutilizer.worksteps.common;

/**
 * The call and its results (exit code, normal response and error response) get
 * stored together.
 */
public class CallAndResponses {
	private String usedCommand;
	private int exitCode;
	private String normalFeedback;
	private String errorFeedback;

	public CallAndResponses(String usedCommand, int exitCode, String normalFeedback, String errorFeedback) {
		this.usedCommand = usedCommand;
		this.exitCode = exitCode;
		this.normalFeedback = normalFeedback;
		this.errorFeedback = errorFeedback;
	}

	@Override
	public String toString() {
		return "ResponseFeedback [\nusedCommand=" + usedCommand + "\nexitCode=" + exitCode + ", \nNormalFeedback=\n"
				+ normalFeedback + ", \nErrorFeedback=\n" + errorFeedback + "\n]";
	}

	public String getUsedCommand(){
		return usedCommand;
	}
	
	public int getExitCode() {
		return exitCode;
	}

	public String getNormalFeedback() {
		return normalFeedback;
	}

	public String getErrorFeedback() {
		return errorFeedback;
	}

}
