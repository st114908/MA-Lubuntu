package arduinocliutilizer.steps.work;

import java.io.IOException;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import arduinocliutilizer.steps.common.ArduinoCLICommandLineHandler;
import arduinocliutilizer.steps.common.ResponseFeedback;
import arduinocliutilizer.steps.common.SaveResponseInfoLocation;
import arduinocliutilizer.steps.common.StepSuperClass;
import arduinocliutilizer.steps.exceptions.NoArduinoCLIConfigFileException;

public class ConnectedBoardsFinder extends StepSuperClass implements SaveResponseInfoLocation{
	public java.util.ArrayList<Map<String, Object>> findBoards(String parentLocation) throws IOException, InterruptedException, NoArduinoCLIConfigFileException{
		ArduinoCLICommandLineHandler commandLineDoer = new ArduinoCLICommandLineHandler();
		String searchCommand = "arduino-cli board list --format yaml";
		ResponseFeedback feedback = commandLineDoer.doShellCommand(searchCommand);
		responseLocation = SaveResponseInfoLocation.saveShellResponseInfo(
			parentLocation, "ConnectedBoardsFinder.txt",
			searchCommand, feedback);
		Yaml yaml = new Yaml();
		@SuppressWarnings("unchecked")
		java.util.ArrayList<Map<String, Object>> resultList = (java.util.ArrayList<Map<String, Object>>) yaml.load(feedback.normalFeedback);
		return resultList;
	}
}
