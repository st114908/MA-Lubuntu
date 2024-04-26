package de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import de.ust.arduinocliutilizer.worksteps.exceptions.FQBNErrorEception;
import de.ust.arduinocliutilizer.worksteps.exceptions.NoArduinoCLIConfigFileException;
import de.ust.arduinocliutilizer.worksteps.functions.UploadCall;
import de.ust.mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.InOrOutKeyNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ParameterMismatchException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StructureException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.steps.PipelineStep;
import de.ust.mumlacgppa.pipeline.parts.storage.VariableHandler;
import projectfolderpathstorageplugin.ProjectFolderPathNotSetException;

public class Upload extends PipelineStep {

	public static final String nameFlag = "Upload";

	/**
	 * @see de.ust.mumlacgppa.pipeline.parts.steps.PipelineStep#setRequiredInsAndOuts()
	 */
	@Override
	protected void setRequiredInsAndOuts() {
		requiredInsAndOuts = new LinkedHashMap<String, HashSet<String>>();

		HashSet<String> ins = new LinkedHashSet<String>();
		ins.add("portAddress");
		ins.add("boardTypeIdentifierFQBN");
		ins.add("targetInoOrHexFile");
		requiredInsAndOuts.put(inFlag, ins);

		HashSet<String> outs = new LinkedHashSet<String>();
		outs.add("ifSuccessful");
		outs.add("resultMessage");
		requiredInsAndOuts.put(outFlag, outs);
	}

	public static Map<String, Map<String, String>> generateDefaultOrExampleValues() {
		// Map<String, Map<String, String>> for
		// Map<InOrOut, Map<ParameterOrOneOutput, SourceOrSaveTarget>>
		Map<String, Map<String, String>> exampleSettings = new LinkedHashMap<String, Map<String, String>>();

		// Ins:
		Map<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("portAddress", "direct COM0");
		ins.put("boardTypeIdentifierFQBN", "direct arduino:avr:uno");
		ins.put("targetInoOrHexFile",
				"direct arduino-containers/fastCarDriverECU/CompiledFiles/fastCarDriverECU.ino.hex");
		exampleSettings.put(inFlag, ins);

		// Out:
		Map<String, String> outs = new LinkedHashMap<String, String>();
		outs.put("ifSuccessful", "ifSuccessful");
		outs.put("resultMessage", "resultMessage");
		exampleSettings.put(outFlag, outs);

		return exampleSettings;
	}

	public Upload(VariableHandler VariableHandlerInstance, Map<String, Map<String, String>> readData)
			throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, readData);
	}

	@SuppressWarnings("unchecked")
	public Upload(VariableHandler VariableHandlerInstance, String yamlData)
			throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, yamlData);
	}

	@Override
	public void execute() throws VariableNotDefinedException, StructureException, FaultyDataException,
			ParameterMismatchException, IOException, InterruptedException, NoArduinoCLIConfigFileException,
			FQBNErrorEception, ProjectFolderPathNotSetException, InOrOutKeyNotDefinedException {
		String foundPortAddress = handleInputByKey("portAddress").getContent();
		String targetFqbn = handleInputByKey("boardTypeIdentifierFQBN").getContent();
		Path targetFilePath = Paths.get(handleInputByKey("targetInoOrHexFile").getContent());

		UploadCall UploadCallInstance = new UploadCall(targetFilePath, foundPortAddress, targetFqbn);
		if (UploadCallInstance.isSuccessful()) {
			handleOutputByKey("ifSuccessful", true);
		} else {
			handleOutputByKey("ifSuccessful", false);
		}
		handleOutputByKey("resultMessage", UploadCallInstance.generateResultMessage());
	}

}
