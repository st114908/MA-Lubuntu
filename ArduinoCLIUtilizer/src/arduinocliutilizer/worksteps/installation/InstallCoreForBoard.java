package arduinocliutilizer.worksteps.installation;

import java.io.IOException;
import java.nio.file.Path;

import arduinocliutilizer.worksteps.common.ACLIWorkstep;
import arduinocliutilizer.worksteps.common.ArduinoCLICommandLineHandler;
import arduinocliutilizer.worksteps.common.SaveResponseInfo;
import arduinocliutilizer.worksteps.exceptions.NoArduinoCLIConfigFileException;
import projectfolderpathstorageplugin.ProjectFolderPathNotSetException;

public class InstallCoreForBoard extends ACLIWorkstep implements SaveResponseInfo{
	
	public static final String messageWindowTitle = "ArduinoCLIUtilizer: Core installation";
	
	
	public InstallCoreForBoard(Path targetFilePath, String candidateID)
			throws IOException, InterruptedException, NoArduinoCLIConfigFileException, ProjectFolderPathNotSetException{
		ArduinoCLICommandLineHandler commandLineDoer = new ArduinoCLICommandLineHandler();
		String coreInstallationCommand = "arduino-cli core install " + candidateID + " --format yaml";
		ReceivedFeedback = commandLineDoer.doShellCommand(coreInstallationCommand);
		Path parentPath =  targetFilePath.getParent();
		responseLocation = SaveResponseInfo.saveShellResponseInfo(
			parentPath, "CoreDownloadInfo.txt",
			coreInstallationCommand, ReceivedFeedback);
		
		successful = (ReceivedFeedback.exitCode == 0);
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
