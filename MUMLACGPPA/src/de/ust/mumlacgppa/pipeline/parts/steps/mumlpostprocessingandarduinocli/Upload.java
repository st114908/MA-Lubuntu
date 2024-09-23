package de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import de.ust.arduinocliutilizer.worksteps.exceptions.FQBNErrorEception;
import de.ust.arduinocliutilizer.worksteps.exceptions.NoArduinoCLIConfigFileException;
import de.ust.arduinocliutilizer.worksteps.functions.FQBNAndCoresHandler;
import de.ust.arduinocliutilizer.worksteps.functions.UploadCall;
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

public class Upload extends PipelineStep implements VariableTypes {

	public static final String nameFlag = "Upload";

	/**
	 * @see de.ust.mumlacgppa.pipeline.parts.steps.PipelineStep#getRequiredInsAndOuts()
	 */
	@Override
	protected Map<String, Map<String, String>> getRequiredInsAndOuts() {
		LinkedHashMap<String, Map<String, String>> requiredInsAndOuts = new LinkedHashMap<String, Map<String, String>>();

		LinkedHashMap<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("portAddress", ConnectionPortType);
		ins.put("boardTypeIdentifierFQBN", BoardIdentifierFQBNType);
		ins.put("targetInoOrHexFile", FilePathType);
		requiredInsAndOuts.put(inKeyword, ins);

		LinkedHashMap<String, String> outs = new LinkedHashMap<String, String>();
		outs.put("ifSuccessful", BooleanType);
		outs.put("resultMessage", StringType);
		requiredInsAndOuts.put(outKeyword, outs);
		
		return requiredInsAndOuts;
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
		exampleSettings.put(inKeyword, ins);

		// Out:
		Map<String, String> outs = new LinkedHashMap<String, String>();
		outs.put("ifSuccessful", "ifSuccessful");
		outs.put("resultMessage", "resultMessage");
		exampleSettings.put(outKeyword, outs);

		return exampleSettings;
	}

	public Upload(VariableHandler VariableHandlerInstance, Map<String, Map<String, String>> readData)
			throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, readData);
	}

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
		Path targetFilePath = resolveFullOrLocalPath( handleInputByKey("targetInoOrHexFile").getContent() );

		FQBNAndCoresHandler FQBNAndCoresHandlerInstance = new FQBNAndCoresHandler(targetFilePath, targetFqbn);
		if(!FQBNAndCoresHandlerInstance.isSuccessful()){
			handleOutputByKey("ifSuccessful", false);
			handleOutputByKey("resultMessage", FQBNAndCoresHandlerInstance.generateResultMessage());
			return;
		}
		
		UploadCall UploadCallInstance = new UploadCall(targetFilePath, foundPortAddress, targetFqbn);
		if (UploadCallInstance.isSuccessful()) {
			handleOutputByKey("ifSuccessful", true);
		} else {
			handleOutputByKey("ifSuccessful", false);
		}
		handleOutputByKey("resultMessage", UploadCallInstance.generateResultMessage());
	}

}
