package arduinocliutilizer.worksteps.installation;

import java.io.IOException;
import java.nio.file.Path;

import arduinocliutilizer.worksteps.common.ACLIWorkstep;
import arduinocliutilizer.worksteps.common.ArduinoCLICommandLineHandler;
import arduinocliutilizer.worksteps.common.SaveResponseInfoLocation;
import arduinocliutilizer.worksteps.exceptions.NoArduinoCLIConfigFileException;
import projectfolderpathstorageplugin.ProjectFolderPathNotSetException;

public class ZippedArduinoLibraryInstaller extends ACLIWorkstep implements SaveResponseInfoLocation{
	
	public static final String messageWindowTitle = "ArduinoCLIUtilizer";
	
	
	public ZippedArduinoLibraryInstaller(Path targetFilePath)
			throws IOException, InterruptedException, NoArduinoCLIConfigFileException, ProjectFolderPathNotSetException{
		ArduinoCLICommandLineHandler commandLineDoer = new ArduinoCLICommandLineHandler();
		commandLineDoer.doShellCommand("arduino-cli config set library.enable_unsafe_install true");
		String libraryInstallationCommand = "arduino-cli lib install --zip-path " + targetFilePath.toString() + " --no-overwrite";
		ReceivedFeedback = commandLineDoer.doShellCommand(libraryInstallationCommand);
		commandLineDoer.doShellCommand("arduino-cli config set library.enable_unsafe_install false");
		
		responseLocation = SaveResponseInfoLocation.saveShellResponseInfo(
			targetFilePath.getParent().toString(), "ZippedArduinoInstallerInfo.txt",
			libraryInstallationCommand, ReceivedFeedback);
		
		successful = (ReceivedFeedback.exitCode == 0);
	}
	
	@Override
	public String generateResultMessage(){
		if(successful){
			return "Nothing wrong.";
		}
		else{
			return "Installation did not end normal!\n"
					+ "For more details see\n" + responseLocation;
		}
	}
}
