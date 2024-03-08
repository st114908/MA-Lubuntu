package arduinocliutilizer.steps.installation;

import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import arduinocliutilizer.steps.common.ArduinoCLICommandLineHandler;
import arduinocliutilizer.steps.common.ResponseFeedback;
import arduinocliutilizer.steps.common.SaveResponseInfoLocation;
import arduinocliutilizer.steps.common.StepSuperClass;
import arduinocliutilizer.steps.exceptions.NoArduinoCLIConfigFileException;

public class ZippedArduinoLibraryInstaller extends StepSuperClass implements SaveResponseInfoLocation{
	public boolean installZippedArduinoLibrary(String pathToZip, String parentLocation) throws IOException, InterruptedException, NoArduinoCLIConfigFileException{
		ArduinoCLICommandLineHandler commandLineDoer = new ArduinoCLICommandLineHandler();
		commandLineDoer.doShellCommand("arduino-cli config set library.enable_unsafe_install true");
		String libraryInstallationCommand = "arduino-cli lib install --zip-path " + pathToZip + " --no-overwrite";
		ResponseFeedback feedback = commandLineDoer.doShellCommand(libraryInstallationCommand);
		commandLineDoer.doShellCommand("arduino-cli config set library.enable_unsafe_install false");
		responseLocation = SaveResponseInfoLocation.saveShellResponseInfo(
			parentLocation, "ZippedArduinoInstallerInfo.txt",
			libraryInstallationCommand, feedback);
		if(feedback.exitCode == 0){
			return true;
		}
		else{
			return false;
		}
	}
	
	public void doErrorMessage(Shell shell){
		MessageDialog.openInformation(
			shell,
			"ArduinoCLIUtilizer",
			"Installation did not end normal!\n"+
				"For more details see\n"+
					responseLocation);
	}
}
