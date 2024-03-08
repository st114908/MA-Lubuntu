package arduinocliutilizer.popup.actions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import arduinocliutilizer.steps.exceptions.NoArduinoCLIConfigFileException;
import arduinocliutilizer.steps.work.ConnectedBoardsFinder;
import arduinocliutilizer.steps.work.FQBNAndCoresHandler;

public class BoardAutoSelectionAndInstallation {
	private String foundFqbn;
	private String foundPortAddress;
	
	@SuppressWarnings("unchecked")
	public boolean autoSelectAndInstal(Shell shell, String parentLocation) throws IOException, InterruptedException, NoArduinoCLIConfigFileException{
		//Search board(s)
		ConnectedBoardsFinder ConnectedBoardsFinderInstance = new ConnectedBoardsFinder();
		java.util.ArrayList<Map<String, Object>> connectedBoards = ConnectedBoardsFinderInstance.findBoards(parentLocation);
		if(connectedBoards.size() == 0){
			MessageDialog.openInformation(
				shell,
				"ArduinoCLIUtilizer: Board search",
				"No boards found!");
			return false;
		}
		else if(connectedBoards.size() > 1){
			MessageDialog.openInformation(
				shell,
				"ArduinoCLIUtilizer: Board search",
				"ArduinoCLIUtilizer can't handle more than one connected board at once yet!");
			return false;
		}
		
		Map<String, Object> connectedBoard = connectedBoards.get(0);
		ArrayList<Map<String, Object>> connectedBoardMatchingBoardsPart = (ArrayList<Map<String, Object>>) connectedBoard.get("matchingboards");
		foundFqbn = (String) ( (Map<String, Object>) connectedBoardMatchingBoardsPart.get(0) ).get("fqbn");
		foundPortAddress = (String) ( (Map<String, Object>) connectedBoard.get("port") ).get("address");
		
		// FQBN matching and potentially performing necessary installation:
		FQBNAndCoresHandler FQBNAndCoresHandlerInstance = new FQBNAndCoresHandler();
		boolean fqbnAndCoreStuffOK = FQBNAndCoresHandlerInstance.handleFQBNAndCores(foundFqbn, parentLocation);
		if(!fqbnAndCoreStuffOK){
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
