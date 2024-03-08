package arduinocliutilizer.steps.installation;

import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import arduinocliutilizer.steps.common.ArduinoCLICommandLineHandler;
import arduinocliutilizer.steps.common.ResponseFeedback;
import arduinocliutilizer.steps.common.SaveResponseInfoLocation;
import arduinocliutilizer.steps.common.StepSuperClass;
import arduinocliutilizer.steps.exceptions.NoArduinoCLIConfigFileException;

public class InstallCoreForBoard extends StepSuperClass implements SaveResponseInfoLocation{
	
	public boolean installDataForBoardByFQBN(String candidateID, String parentLocation) throws IOException, InterruptedException, NoArduinoCLIConfigFileException{
		ArduinoCLICommandLineHandler commandLineDoer = new ArduinoCLICommandLineHandler();
		String coreInstallationCommand = "arduino-cli core install " + candidateID + " --format yaml";
		ResponseFeedback feedbackCoreInstalation = commandLineDoer.doShellCommand(coreInstallationCommand);
		responseLocation = SaveResponseInfoLocation.saveShellResponseInfo(
			parentLocation, "CoreDownloadInfo.txt",
			coreInstallationCommand, feedbackCoreInstalation);
		if(feedbackCoreInstalation.exitCode == 0){
			return true;
		}
		else{
			return false;
		}
	}
	
	public void doErrorMessage(Shell shell){
		MessageDialog.openInformation(
			shell,
			"ArduinoCLIUtilizer: Core installation",
			"Error at the installation of the core for the connected board!\n"+
				"For more details see\n"+
				responseLocation);
	}
}
