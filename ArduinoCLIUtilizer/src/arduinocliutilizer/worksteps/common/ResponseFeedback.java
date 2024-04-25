package arduinocliutilizer.worksteps.common;

/**
 * @author muml
 * The Results (exit code, normal response and error response)
 * get stored together in a ResponseFeedback object.
 */
public class ResponseFeedback {
	public int exitCode;
	public String normalFeedback;
	public String errorFeedback;
	
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
	
	
}
