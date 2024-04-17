package arduinocliutilizer.worksteps.functions;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.ArrayList;

import org.yaml.snakeyaml.Yaml;

import arduinocliutilizer.worksteps.common.ArduinoCLICommandLineHandler;
import arduinocliutilizer.worksteps.common.SaveResponseInfoLocation;
import arduinocliutilizer.worksteps.common.ACLIWorkstep;
import arduinocliutilizer.worksteps.exceptions.NoArduinoCLIConfigFileException;
import projectfolderpathstorageplugin.ProjectFolderPathNotSetException;

public class ConnectedBoardsFinder extends ACLIWorkstep implements SaveResponseInfoLocation{
	private ArrayList<Map<String, Object>> resultList;
	private int numberOfBoards;
	
	@SuppressWarnings("unchecked")
	public ConnectedBoardsFinder(Path targetFilePath)
			throws IOException, InterruptedException, NoArduinoCLIConfigFileException, ProjectFolderPathNotSetException{
		super();
		ArduinoCLICommandLineHandler commandLineDoer = new ArduinoCLICommandLineHandler();
		String searchCommand = "arduino-cli board list --format yaml";
		ReceivedFeedback = commandLineDoer.doShellCommand(searchCommand);
		Path parentPath =  targetFilePath.getParent();
		responseLocation = SaveResponseInfoLocation.saveShellResponseInfo(
			parentPath.toString(), "ConnectedBoardsFinder.txt",
			searchCommand, ReceivedFeedback);
		Yaml yaml = new Yaml();
		resultList = (ArrayList<Map<String, Object>>) yaml.load(ReceivedFeedback.normalFeedback);
		numberOfBoards = resultList.size();
		
		successful = true;
	}

	
	public ArrayList<Map<String, Object>> getResultList() {
		return resultList;
	}
	
	
	public int getNumberOfBoards(){
		return numberOfBoards;
	}
	
	
	@Override
	public String generateResultMessage(){
		if(successful){
			return "Nothing wrong.";
		}
		else{
			return "Something went wrong.";
		}
	}
		
}
