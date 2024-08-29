package de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import de.ust.arduinocliutilizer.worksteps.common.ArduinoCLICommandLineHandler;
import de.ust.arduinocliutilizer.worksteps.common.CallAndResponses;
import de.ust.arduinocliutilizer.worksteps.common.FallbackForBoardsWithoutInternalFQBNDataHander;
import de.ust.arduinocliutilizer.worksteps.exceptions.NoArduinoCLIConfigFileException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.InOrOutKeyNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ParameterMismatchException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StructureException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.steps.PipelineStep;
import de.ust.mumlacgppa.pipeline.parts.storage.VariableHandler;
import de.ust.mumlacgppa.pipeline.parts.storage.VariableTypes;
import projectfolderpathstorageplugin.ProjectFolderPathNotSetException;

/**
 * @author muml Looks up which connected board has the matching serial number
 *         and returns the port to the board with the matching serial number.
 *         Only searches once and then stores the list of found boards for
 *         future searches in order to improve their execution times. Please use
 *         resetSearchState() to make sure that its state gets reset before
 *         starting an pipeline execution or after finishing one.
 */
public class LookupBoardBySerialNumber extends PipelineStep implements VariableTypes {
	public static final String nameFlag = "LookupBoardBySerialNumber";

	// For doing the search only once.
	private static boolean hasSearched = false;
	private static ArrayList<Map<String, Object>> foundBoardsStorage;

	/**
	 * @see de.ust.mumlacgppa.pipeline.parts.steps.PipelineStep#getRequiredInsAndOuts()
	 */
	@Override
	protected Map<String, Map<String, String>> getRequiredInsAndOuts() {
		LinkedHashMap<String, Map<String, String>> requiredInsAndOuts = new LinkedHashMap<String, Map<String, String>>();

		LinkedHashMap<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("boardSerialNumber", BoardSerialNumberType);
		ins.put("boardTypeIdentifierFQBN", BoardIdentifierFQBNType);
		requiredInsAndOuts.put(inKeyword, ins);

		LinkedHashMap<String, String> outs = new LinkedHashMap<String, String>();
		outs.put("ifSuccessful", BooleanType);
		outs.put("foundPortAddress", ConnectionPortType);
		requiredInsAndOuts.put(outKeyword, outs);
		
		return requiredInsAndOuts;
	}

	public static Map<String, Map<String, String>> generateDefaultOrExampleValues() {
		// Map<String, Map<String, String>> for
		// Map<InOrOut, Map<ParameterOrOneOutput, SourceOrSaveTarget>>
		Map<String, Map<String, String>> exampleSettings = new LinkedHashMap<String, Map<String, String>>();

		// Ins:
		Map<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("boardSerialNumber", "direct 85935333337351A0B051");
		ins.put("boardTypeIdentifierFQBN", "direct arduino:avr:uno");
		exampleSettings.put(inKeyword, ins);

		// Out:
		Map<String, String> outs = new LinkedHashMap<String, String>();
		outs.put("ifSuccessful", "ifSuccessful");
		outs.put("foundPortAddress", "foundPortAddress");
		exampleSettings.put(outKeyword, outs);

		return exampleSettings;
	}

	public LookupBoardBySerialNumber(VariableHandler VariableHandlerInstance, Map<String, Map<String, String>> readData)
			throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, readData);
	}

	public LookupBoardBySerialNumber(VariableHandler VariableHandlerInstance, String yamlData)
			throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, yamlData);
	}

	/**
	 * Resets the internal static fields to the starting state. (hasSearched to
	 * false and foundBoardsStorage to null.) Please use it/this before starting
	 * a new pipeline execution or after finishing one.
	 */
	public static void resetSearchState() {
		hasSearched = false;
		foundBoardsStorage = null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute()
			throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException,
			IOException, InterruptedException, NoArduinoCLIConfigFileException, ProjectFolderPathNotSetException, InOrOutKeyNotDefinedException {
		handleOutputByKey("ifSuccessful", false); // In case of exception.
		String wantedBoardSerialNumber = handleInputByKey("boardSerialNumber").getContent();

		// For doing the search only once during a pipeline execution.
		if (!hasSearched) {
			ArduinoCLICommandLineHandler commandLineDoer = new ArduinoCLICommandLineHandler();
			String searchCommand = "arduino-cli board list --format yaml";
			CallAndResponses ReceivedFeedback = commandLineDoer.doShellCommand(searchCommand);
			Yaml yaml = new Yaml();
			foundBoardsStorage = (ArrayList<Map<String, Object>>) yaml.load(ReceivedFeedback.getNormalFeedback());
		}

		String wantedBoardTypeIdentifierFQBN = handleInputByKey("boardTypeIdentifierFQBN").getContent();
		// Gets skipped if no boards have been found.
		for (Map<String, Object> currentBoardEntry : foundBoardsStorage) {
			Map<String, Object> portInfo = (Map<String, Object>) currentBoardEntry.get("port");
			Map<String, String> foundProperties = (Map<String, String>) portInfo.get("properties");
			String foundSerial = foundProperties.get("serialNumber");
			if (foundSerial.equals(wantedBoardSerialNumber)) {
				ArrayList<Map<String, String>> matchingboardsInfo = (ArrayList<Map<String, String>>) currentBoardEntry.get("matchingboards");
				String foundFQBN;
				
				if(matchingboardsInfo.size() == 0){ // Check if the board knows its own data or not.
					foundFQBN = FallbackForBoardsWithoutInternalFQBNDataHander.getFallbackFQBN();
				}
				else{
					Map<String, String> currentBoardData = matchingboardsInfo.get(0);
					foundFQBN = currentBoardData.get("fqbn");
				}
				
				if(foundFQBN.equals(wantedBoardTypeIdentifierFQBN)){
					String foundAddress = (String) portInfo.get("address");
					handleOutputByKey("foundPortAddress", foundAddress);
					handleOutputByKey("ifSuccessful", true);
					return;
				}
				
			}
		}

		// ifSuccessful will still be set to false, which can be used for
		// messages (DialogMessage and SelectableTextWindow) and aborting
		// the pipeline (OnlyContinueIfFulfilledEsleAbort).
	}

}
