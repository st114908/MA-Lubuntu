package de.ust.arduinocliutilizer.popup.actions;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import de.ust.arduinocliutilizer.worksteps.common.FallbackForBoardsWithoutInternalFQBNDataHander;
import de.ust.arduinocliutilizer.worksteps.exceptions.NoArduinoCLIConfigFileException;
import de.ust.arduinocliutilizer.worksteps.functions.ConnectedBoardsFinder;
import de.ust.arduinocliutilizer.worksteps.functions.FQBNAndCoresHandler;
import projectfolderpathstorageplugin.ProjectFolderPathNotSetException;

public class BoardAutoSelectionAndInstallation {
	private String foundFqbn;
	private String foundPortAddress;
	
	@SuppressWarnings("unchecked")
	public boolean autoSelectAndInstal(Shell shell, Path targetFilePath)
			throws IOException, InterruptedException, NoArduinoCLIConfigFileException, ProjectFolderPathNotSetException{
		//Search board(s)
		ConnectedBoardsFinder ConnectedBoardsFinderInstance = new ConnectedBoardsFinder(targetFilePath);
		java.util.ArrayList<Map<String, Object>> connectedBoards = ConnectedBoardsFinderInstance.getResultList();
		if(ConnectedBoardsFinderInstance.getNumberOfBoards() == 0){
			MessageDialog.openInformation(
				shell,
				"ArduinoCLIUtilizer: Board search",
				"No boards found!");
			return false;
		}
		else if(ConnectedBoardsFinderInstance.getNumberOfBoards() > 1){
			MessageDialog.openInformation(
				shell,
				"ArduinoCLIUtilizer: Board search",
				"When used directly from the context menu the ArduinoCLIUtilizer can't handle more than one connected board at once!");
			return false;
		}
		
		Map<String, Object> connectedBoard = connectedBoards.get(0);
		ArrayList<Map<String, Object>> connectedBoardMatchingBoardsPart = (ArrayList<Map<String, Object>>) connectedBoard.get("matchingboards");
		
		if(connectedBoardMatchingBoardsPart.size() == 0){ // Check if the board knows its own data or not.
			foundFqbn = FallbackForBoardsWithoutInternalFQBNDataHander.getFallbackFQBN();
		}
		else{
			foundFqbn = (String) ( (Map<String, Object>) connectedBoardMatchingBoardsPart.get(0) ).get("fqbn");
		}
		
		foundPortAddress = (String) ( (Map<String, Object>) connectedBoard.get("port") ).get("address");
		
		// FQBN matching and potentially performing necessary installation:
		FQBNAndCoresHandler FQBNAndCoresHandlerInstance = new FQBNAndCoresHandler(targetFilePath, foundFqbn);
		if(!FQBNAndCoresHandlerInstance.isSuccessful()){
			FQBNAndCoresHandlerInstance.doErrorMessage(shell);
			return false;
		}
		
		return true;
	}

	public String getFoundFqbn() {
		return foundFqbn;
	}

	public String getFoundPortAddress() {
		return foundPortAddress;
	}
	
	
}
