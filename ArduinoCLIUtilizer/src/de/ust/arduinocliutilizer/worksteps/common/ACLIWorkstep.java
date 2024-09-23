package de.ust.arduinocliutilizer.worksteps.common;

import java.nio.file.Path;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

public abstract class ACLIWorkstep implements SaveResponseInfo{
	public static final String messageWindowTitle = "ArduinoCLIUtilizer";
	
	protected Path responseLocation;
	protected CallAndResponses ReceivedFeedback;
	protected boolean successful;
	
	
	public Path getResponseLocation(){
		return responseLocation;
	}
	
	
	public CallAndResponses getResponseFeedback(){
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
