package arduinocliutilizer.worksteps.common;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public abstract class ACLIWorkstep {
	protected String responseLocation;
	protected ResponseFeedback ReceivedFeedback;
	protected boolean successful;
	
	public static final String messageWindowTitle = "ArduinoCLIUtilizer";
	
	
	public String getResponseLocation(){
		return responseLocation;
	}
	
	
	public ResponseFeedback getResponseFeedback(){
		return ReceivedFeedback;
	}
	
	
	public boolean isSuccessful() {
		return successful;
	}
	
	
	public abstract String generateResultMessage();
	

	public void doErrorMessage(Shell shell){
		MessageDialog.openInformation(
			shell,
			messageWindowTitle,
			generateResultMessage());
	}
}
