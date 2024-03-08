package arduinocliutilizer.steps.work;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.yaml.snakeyaml.Yaml;

import arduinocliutilizer.steps.common.ArduinoCLICommandLineHandler;
import arduinocliutilizer.steps.common.ResponseFeedback;
import arduinocliutilizer.steps.common.SaveResponseInfoLocation;
import arduinocliutilizer.steps.common.StepSuperClass;
import arduinocliutilizer.steps.exceptions.NoArduinoCLIConfigFileException;
import arduinocliutilizer.steps.installation.InstallCoreForBoard;

public class FQBNAndCoresHandler extends StepSuperClass implements SaveResponseInfoLocation{
	boolean coreFound;
	// boolean downloadSuccessful is not necessary, because the handlings ends after it.
	InstallCoreForBoard InstallCoreForBoardInstance;
	
	@SuppressWarnings("unchecked")
	public boolean handleFQBNAndCores(String foundFqbn, String parentLocation) throws IOException, InterruptedException, NoArduinoCLIConfigFileException {
		coreFound = false;
		// Check if the FQBN can be found in the list of available boards:
		ArduinoCLICommandLineHandler commandLineDoer = new ArduinoCLICommandLineHandler();
		ResponseFeedback feedbackKnownBoards = commandLineDoer.doShellCommand("arduino-cli board listall --format yaml");
		Yaml yamlKnownBoards = new Yaml();
		Map<String, Object> yamlKnownBoardsInterpreted = (Map<String, Object>) yamlKnownBoards.load(feedbackKnownBoards.normalFeedback);
		ArrayList<Map<String, Object>> installedInstalledBoardDataList = (ArrayList<Map<String, Object>>) yamlKnownBoardsInterpreted.get("boards");
		for(Map<String, Object> currentEntry: installedInstalledBoardDataList){
			String currentFqbn = (String) currentEntry.get("fqbn");
			boolean currentIsHidden = (boolean) currentEntry.get("ishidden");
			if( foundFqbn.equals(currentFqbn) && !currentIsHidden ){
				return true; // if yes then finish here.
			}
		}
		
		// Check if it can be found:
		String searchID = foundFqbn.substring(0, foundFqbn.lastIndexOf(":"));
		commandLineDoer.doShellCommand("arduino-cli core update-index");
		String searchCommand = "arduino-cli core search " + searchID + " --format yaml";
		ResponseFeedback feedbackCoreSearch = commandLineDoer.doShellCommand(searchCommand);
		responseLocation = SaveResponseInfoLocation.saveShellResponseInfo(
			parentLocation, "CoreSearch.txt",
			searchCommand, feedbackCoreSearch);
		if(feedbackCoreSearch.exitCode != 0){
			return false;
		}
		Yaml yaml = new Yaml();
		java.util.ArrayList<Map<String, Object>> coreList = (java.util.ArrayList<Map<String, Object>>) yaml.load(feedbackCoreSearch.normalFeedback);
		if(coreList.size() == 0){
			return false;
		}
		coreFound = true;
		Map<String, Object> coreCandidate = (Map<String, Object>) coreList.get(0);
		String candidateID = (String) coreCandidate.get("id");
		
		// Download core:
		//String coreInstallationCommand = "arduino-cli core install " + searchID + " --format yaml";
		InstallCoreForBoard InstallCoreForBoardInstance = new InstallCoreForBoard();
		boolean coreInstallationSuccessful = InstallCoreForBoardInstance.installDataForBoardByFQBN(candidateID, parentLocation);
		if(coreInstallationSuccessful){
			return true;
		}
		else{
			return false;
		}
	}
	
	public void doErrorMessage(Shell shell){
		if(!coreFound){
			MessageDialog.openInformation(
				shell,
				"ArduinoCLIUtilizer: Core search",
				"The automatic search couldn't find any core for the connected board!\n"+
					"For more details see\n"+
					responseLocation);
		}
		else{
			InstallCoreForBoardInstance.doErrorMessage(shell);
		}
	}
}
