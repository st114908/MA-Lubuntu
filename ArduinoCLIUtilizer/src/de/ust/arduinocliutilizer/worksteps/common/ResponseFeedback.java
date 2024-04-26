package de.ust.arduinocliutilizer.worksteps.common;

/**
 * @author muml
 * The Results (exit code, normal response and error response)
 * get stored together in a ResponseFeedback object.
 */
public class ResponseFeedback {
	private int exitCode;
	private String normalFeedback;
	private String errorFeedback;
	
	public ResponseFeedback(int exitCode, String normalFeedback, String errorFeedback) {
		this.exitCode = exitCode;
		this.normalFeedback = normalFeedback;
		this.errorFeedback = errorFeedback;
	}

	@Override
	public String toString() {
		return "ResponseFeedback [\nexitCode=" + exitCode + ", \nNormalFeedback=\n" + normalFeedback + ", \nErrorFeedback=\n"
				+ errorFeedback + "\n]";
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
