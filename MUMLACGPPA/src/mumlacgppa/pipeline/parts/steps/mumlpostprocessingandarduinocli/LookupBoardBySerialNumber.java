package mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli;

import java.io.IOException;
import java.util.ArrayList;
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

/**
 * @author muml Looks up which connected board has the matching serial number
 *         and returns the port to the board with the matching serial number.
 *         Only searches once and then stores the list of found boards for
 *         future searches in order to improve their execution times. Please use
 *         resetSearchState() to make sure that its state gets reset before
 *         starting an pipeline execution or after finishing one.
 */
public class LookupBoardBySerialNumber extends PipelineStep {
	public static final String nameFlag = "LookupBoardBySerialNumber";

	// For doing the search only once.
	private static boolean hasSearched = false;
	private static ArrayList<Map<String, Object>> foundBoardsStorage;

	/**
	 * @see mumlacgppa.pipeline.parts.steps.PipelineStep#setRequiredInsAndOuts()
	 */
	@Override
	protected void setRequiredInsAndOuts() {
		requiredInsAndOuts = new LinkedHashMap<String, HashSet<String>>();

		HashSet<String> ins = new LinkedHashSet<String>();
		ins.add("boardSerialNumber");
		requiredInsAndOuts.put(inFlag, ins);

		HashSet<String> outs = new LinkedHashSet<String>();
		outs.add("ifSuccessful");
		outs.add("foundPortAddress");
		requiredInsAndOuts.put(outFlag, outs);
	}

	public static Map<String, Map<String, String>> generateDefaultOrExampleValues() {
		// Map<String, Map<String, String>> for
		// Map<InOrOut, Map<ParameterOrOneOutput, SourceOrSaveTarget>>
		Map<String, Map<String, String>> exampleSettings = new LinkedHashMap<String, Map<String, String>>();

		// Ins:
		Map<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("boardSerialNumber", "direct 85935333337351A0B051");
		exampleSettings.put(inFlag, ins);

		// Out:
		Map<String, String> outs = new LinkedHashMap<String, String>();
		outs.put("ifSuccessful", "ifSuccessful");
		outs.put("foundPortAddress", "foundPortAddress");
		exampleSettings.put(outFlag, outs);

		return exampleSettings;
	}

	public LookupBoardBySerialNumber(VariableHandler VariableHandlerInstance, Map<String, Map<String, String>> readData)
			throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, readData);
	}

	@SuppressWarnings("unchecked")
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
			IOException, InterruptedException, NoArduinoCLIConfigFileException, ProjectFolderPathNotSetException {
		handleOutputByKey("ifSuccessful", false); // In case of exception.
		String wantedBoardSerialNumber = handleInputByKey("boardSerialNumber").getContent();

		// For doing the search only once during a pipeline execution.
		if (!hasSearched) {
			ArduinoCLICommandLineHandler commandLineDoer = new ArduinoCLICommandLineHandler();
			String searchCommand = "arduino-cli board list --format yaml";
			ResponseFeedback ReceivedFeedback = commandLineDoer.doShellCommand(searchCommand);
			Yaml yaml = new Yaml();
			foundBoardsStorage = (ArrayList<Map<String, Object>>) yaml.load(ReceivedFeedback.normalFeedback);
		}

		// Gets skipped if no boards have been found.
		for (Map<String, Object> currentEntry : foundBoardsStorage) {
			Map<String, Object> portInfo = (Map<String, Object>) currentEntry.get("port");
			Map<String, String> foundProperties = (Map<String, String>) portInfo.get("Properties");
			if (foundProperties.get("serialNumber").equals(wantedBoardSerialNumber)) {
				String foundAddress = (String) portInfo.get("address");
				handleOutputByKey("portAddress", foundAddress);
				handleOutputByKey("ifSuccessful", true);
				return;
			}
		}

		// ifSuccessful will still be set to false, which can be used for
		// messages (PopupWindowMessage and SelectableTextWindow) and aborting
		// the pipeline (OnlyContinueIfFulfilledEsleAbort).
	}

}