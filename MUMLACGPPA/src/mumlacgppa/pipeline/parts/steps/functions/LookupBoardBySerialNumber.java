package mumlacgppa.pipeline.parts.steps.functions;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import arduinocliutilizer.worksteps.common.ArduinoCLICommandLineHandler;
import arduinocliutilizer.worksteps.common.ResponseFeedback;
import arduinocliutilizer.worksteps.exceptions.NoArduinoCLIConfigFileException;
import mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import mumlacgppa.pipeline.parts.exceptions.ParameterMismatchException;
import mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import mumlacgppa.pipeline.parts.exceptions.StructureException;
import mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import mumlacgppa.pipeline.parts.steps.PipelineStep;
import mumlacgppa.pipeline.parts.storage.VariableHandler;
import projectfolderpathstorageplugin.ProjectFolderPathNotSetException;

public class LookupBoardBySerialNumber extends PipelineStep {


	public static final String nameFlag = "LookupBoardBySerialNumber";
	private static ArrayList<Map<String, Object>> resultList; // For doing the search only once.

	
	protected void setRequiredInsAndOuts(){
		requiredInsAndOuts = new LinkedHashMap<String, HashSet<String>>();

		HashSet<String> ins = new LinkedHashSet<String>();
		ins.add("boardSerialNumber");
		requiredInsAndOuts.put(inFlag, ins);
		
		HashSet<String> outs = new LinkedHashSet<String>();
		outs.add("ifSuccessful");
		outs.add("portAddress");
		requiredInsAndOuts.put(outFlag, outs);
	}

	// Map<String, Map<String, String>> for
	// Map<InOrOut, Map<ParameterOrOneOutput, SourceOrSaveTarget>>
	public static Map<String, Map<String, String>> generateDefaultOrExampleValues(){
		Map<String, Map<String, String>> exampleSettings = new LinkedHashMap<String, Map<String,String>>();

		// Ins:
		Map<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("boardSerialNumber", "direct 85935333337351A0B051");
		exampleSettings.put(inFlag, ins);
		
		// Out:
		Map<String, String> outs = new LinkedHashMap<String, String>();
		outs.put("ifSuccessful", "ifSuccessful");
		outs.put("portAddress", "portAddress");
		exampleSettings.put(outFlag, outs);
		
		return exampleSettings;
	}

	
	public LookupBoardBySerialNumber(VariableHandler VariableHandlerInstance, Map<String, Map<String, String>> readData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, readData);
	}

	@SuppressWarnings("unchecked")
	public LookupBoardBySerialNumber(VariableHandler VariableHandlerInstance, String yamlData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, yamlData);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, 
			IOException, InterruptedException, NoArduinoCLIConfigFileException, ProjectFolderPathNotSetException {
		handleOutputByKey("ifSuccessful", false); // In case of exception.
		String wantedBoardSerialNumber = handleInputByKey("boardSerialNumber").getContent();
		
		if(resultList == null){ // For doing the search only once during a pipeline execution.
			ArduinoCLICommandLineHandler commandLineDoer = new ArduinoCLICommandLineHandler();
			String searchCommand = "arduino-cli board list --format yaml";
			ResponseFeedback ReceivedFeedback = commandLineDoer.doShellCommand(searchCommand);
			Yaml yaml = new Yaml();
			resultList = (ArrayList<Map<String, Object>>) yaml.load(ReceivedFeedback.normalFeedback);
		}
		
		for(Map<String, Object> currentEntry: resultList){
			Map<String, Object> portInfo = (Map<String, Object>) currentEntry.get("port");
			Map<String, String> foundProperties = (Map<String, String>) portInfo.get("Properties");
			if(foundProperties.get("serialNumber").equals(wantedBoardSerialNumber)){
				String foundAddress = (String) portInfo.get("address");
				handleOutputByKey("portAddress", foundAddress);
				handleOutputByKey("ifSuccessful", true);
				return;
			}
		}
	}

}
