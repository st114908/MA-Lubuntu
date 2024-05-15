package de.ust.arduinocliutilizer.worksteps.installation;

import java.io.IOException;
import java.nio.file.Path;

import de.ust.arduinocliutilizer.worksteps.common.ACLIWorkstep;
import de.ust.arduinocliutilizer.worksteps.common.ArduinoCLICommandLineHandler;
import de.ust.arduinocliutilizer.worksteps.exceptions.NoArduinoCLIConfigFileException;
import projectfolderpathstorageplugin.ProjectFolderPathNotSetException;

public class InstallCoreForBoard extends ACLIWorkstep {
	
	public static final String messageWindowTitle = "ArduinoCLIUtilizer: Core installation";
	
	
	public InstallCoreForBoard(Path targetFilePath, String candidateID)
			throws IOException, InterruptedException, NoArduinoCLIConfigFileException, ProjectFolderPathNotSetException{
		ArduinoCLICommandLineHandler commandLineDoer = new ArduinoCLICommandLineHandler();
		String coreInstallationCommand = "arduino-cli core install " + candidateID + " --format yaml";
		ReceivedFeedback = commandLineDoer.doShellCommand(coreInstallationCommand);
		Path parentPath =  targetFilePath.getParent();
		responseLocation = saveShellResponseInfo(
			parentPath, "CoreDownloadInfo.txt", ReceivedFeedback);
		
		successful = (ReceivedFeedback.getExitCode() == 0);
	}
	

	@Override
	public String generateResultMessage(){
		if(successful){
			return "Nothing wrong.";
		}
		else{
			return "Error at the installation of the core for the connected board!\n"
					+ "For more details see\n" + responseLocation;
		}
	}


}
