package de.ust.arduinocliutilizer.worksteps.installation;

import java.io.IOException;
import java.nio.file.Path;

import de.ust.arduinocliutilizer.worksteps.common.ACLIWorkstep;
import de.ust.arduinocliutilizer.worksteps.common.ArduinoCLICommandLineHandler;
import de.ust.arduinocliutilizer.worksteps.common.SaveResponseInfo;
import de.ust.arduinocliutilizer.worksteps.exceptions.NoArduinoCLIConfigFileException;
import projectfolderpathstorageplugin.ProjectFolderPathNotSetException;

public class ZippedArduinoLibraryInstaller extends ACLIWorkstep {
	private boolean alreadyInstalled;
	public static final String messageWindowTitle = "ArduinoCLIUtilizer: Zipped arduino library installation";
	
	
	public ZippedArduinoLibraryInstaller(Path targetFilePath)
			throws IOException, InterruptedException, NoArduinoCLIConfigFileException, ProjectFolderPathNotSetException{
		ArduinoCLICommandLineHandler commandLineDoer = new ArduinoCLICommandLineHandler();
		commandLineDoer.doShellCommand("arduino-cli config set library.enable_unsafe_install true");
		String libraryInstallationCommand = "arduino-cli lib install --zip-path " + targetFilePath.toString() + " --no-overwrite";
		ReceivedFeedback = commandLineDoer.doShellCommand(libraryInstallationCommand);
		commandLineDoer.doShellCommand("arduino-cli config set library.enable_unsafe_install false");
		
		responseLocation = saveShellResponseInfo(
			targetFilePath.getParent(), "ZippedArduinoInstallerInfo.txt",
			libraryInstallationCommand, ReceivedFeedback);
		
		successful = (ReceivedFeedback.getExitCode() == 0);
		
		if(ReceivedFeedback.getErrorFeedback().contains("already installed")){
			alreadyInstalled = true;
		}
	}
	
	@Override
	public String generateResultMessage(){
		if(successful){
			return "Nothing wrong.";
		}
		else{
			if(alreadyInstalled){
				return "The targeted library got identified as alread installed.\n"
						+ "For more details see\n" + responseLocation;
			}
			else{
				return "Installation did not end normal!\n"
					+ "For more details see\n" + responseLocation;
			}
		}
	}
}
