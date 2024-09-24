package de.ust.arduinocliutilizer.worksteps.functions;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import de.ust.arduinocliutilizer.worksteps.common.ACLIWorkstep;
import de.ust.arduinocliutilizer.worksteps.common.ArduinoCLICommandLineHandler;
import de.ust.arduinocliutilizer.worksteps.exceptions.NoArduinoCLIConfigFileException;
import de.ust.arduinocliutilizer.worksteps.installation.InstallCoreForBoard;
import projectfolderpathstorageplugin.ProjectFolderPathNotSetException;

public class FQBNAndCoresHandler extends ACLIWorkstep{
	
	public static final String messageWindowTitle = "ArduinoCLIUtilizer: Core search";
	
	boolean coreFound;
	// boolean downloadSuccessful is not necessary, because the handling ends after it.
	InstallCoreForBoard InstallCoreForBoardInstance;

	@SuppressWarnings("unchecked")
	public FQBNAndCoresHandler(Path targetFilePath, String foundFqbn)
			throws IOException, InterruptedException, NoArduinoCLIConfigFileException, ProjectFolderPathNotSetException {
		coreFound = false;
		// Check if the FQBN can be found in the list of available boards:
		ArduinoCLICommandLineHandler commandLineDoer = new ArduinoCLICommandLineHandler();
		ReceivedFeedback = commandLineDoer.doShellCommand("arduino-cli board listall --format yaml");
		Yaml yamlKnownBoards = new Yaml();
		Map<String, Object> yamlKnownBoardsInterpreted = (Map<String, Object>) yamlKnownBoards.load(ReceivedFeedback.getNormalFeedback());
		ArrayList<Map<String, Object>> installedInstalledBoardDataList = (ArrayList<Map<String, Object>>) yamlKnownBoardsInterpreted.get("boards");
		for(Map<String, Object> currentEntry: installedInstalledBoardDataList){
			String currentFqbn = (String) currentEntry.get("fqbn");
			if( foundFqbn.equals(currentFqbn)){
				successful = true;
				return; // if yes then finish here.
			}
		}
		
		// Check if it can be found:
		String searchID = foundFqbn.substring(0, foundFqbn.lastIndexOf(":"));
		commandLineDoer.doShellCommand("arduino-cli core update-index");
		String searchCommand = "arduino-cli core search " + searchID + " --format yaml";
		ReceivedFeedback = commandLineDoer.doShellCommand(searchCommand);
		Path parentPath =  targetFilePath.getParent();
		responseLocation = saveShellResponseInfo(
			parentPath, "CoreSearch.txt", ReceivedFeedback);
		if(ReceivedFeedback.getExitCode() != 0){
			successful = false;
			return;
		}
		
		Yaml yaml = new Yaml();
		java.util.ArrayList<Map<String, Object>> coreList = (java.util.ArrayList<Map<String, Object>>) yaml.load(ReceivedFeedback.getNormalFeedback());
		if(coreList.size() == 0){
			successful = false;
			return;
		}
		coreFound = true;
		Map<String, Object> coreCandidate = (Map<String, Object>) coreList.get(0);
		String candidateID = (String) coreCandidate.get("id");
		
		// Download core:
		//String coreInstallationCommand = "arduino-cli core install " + searchID + " --format yaml";
		InstallCoreForBoardInstance = new InstallCoreForBoard(targetFilePath, candidateID);
		if(InstallCoreForBoardInstance.isSuccessful()){
			successful = true;
			return;
		}
		else{
			successful = false;
			return;
		}
	}
	
	
	@Override
	public String generateResultMessage() {
		if(successful){
			return "Nothing wrong.";
		}
		if(!coreFound){
			return "The automatic search couldn't find any core for the connected board!\n"
					+ "For more details see\n" + responseLocation;
		}
		else{
			return InstallCoreForBoardInstance.generateResultMessage();
		}
	}

}
