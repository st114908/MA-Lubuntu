/**
 * 
 */
package de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import de.ust.arduinocliutilizer.worksteps.exceptions.NoArduinoCLIConfigFileException;
import de.ust.arduinocliutilizer.worksteps.functions.CompilationCall;
import de.ust.mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.InOrOutKeyNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StructureException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.steps.PipelineStep;
import de.ust.mumlacgppa.pipeline.parts.storage.VariableHandler;
import projectfolderpathstorageplugin.ProjectFolderPathNotSetException;

/**
 * @author muml
 *
 */
public class Compile extends PipelineStep {

	public static final String nameFlag = "Compile";

	
	public Compile(VariableHandler VariableHandlerInstance, Map<String, Map<String, String>> readData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, readData);
	}
	

	/**
	 * @param yamlData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 */
	public Compile(VariableHandler VariableHandlerInstance, String yamlData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, yamlData);
	}

	
	/**
	 * @see de.ust.mumlacgppa.pipeline.parts.steps.PipelineStep#getRequiredInsAndOuts()
	 */
	@Override
	protected Map<String, HashSet<String>> getRequiredInsAndOuts(){
		LinkedHashMap<String, HashSet<String>> requiredInsAndOuts = new LinkedHashMap<String, HashSet<String>>();

		HashSet<String> ins = new LinkedHashSet<String>();
		ins.add("boardTypeIdentifierFQBN");
		ins.add("targetInoFile");
    	ins.add("saveCompiledFilesNearby");
    	//ins.add("directoryContainingInoFile");
		requiredInsAndOuts.put(inKeyword, ins);
		
		HashSet<String> outs = new LinkedHashSet<String>();
		outs.add("ifSuccessful");
    	outs.add("resultMessage");
		requiredInsAndOuts.put(outKeyword, outs);
		
		return requiredInsAndOuts;
	}

	public static Map<String, Map<String, String>> generateDefaultOrExampleValues(){
		// Map<String, Map<String, String>> for
		// Map<InOrOut, Map<ParameterOrOneOutput, SourceOrSaveTarget>>
		Map<String, Map<String, String>> exampleSettings = new LinkedHashMap<String, Map<String,String>>();

		// Ins:
		Map<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("boardTypeIdentifierFQBN", "direct arduino:avr:uno");
		ins.put("targetInoFile", "direct arduino-containers/fastCarDriverECU/fastCarDriverECU.ino");
		ins.put("saveCompiledFilesNearby", "direct true");
		exampleSettings.put(inKeyword, ins);
		
		// Out:
		Map<String, String> outs = new LinkedHashMap<String, String>();
		outs.put("ifSuccessful", "ifSuccessful");
		outs.put("resultMessage", "resultMessage");
		exampleSettings.put(outKeyword, outs);
		
		return exampleSettings;
	}
	

	/**
	 * @see mumlarduinopipelineautomatisation.pipeline.parts.steps.common.PipelineStep#execute()
	 */
	@Override
	public void execute() 
			throws IOException, InterruptedException, NoArduinoCLIConfigFileException, VariableNotDefinedException,
			StructureException, ProjectFolderPathNotSetException, InOrOutKeyNotDefinedException, FaultyDataException{
		handleOutputByKey("ifSuccessful", false); // For Exceptions 
		String foundFqbn = handleInputByKey("boardTypeIdentifierFQBN").getContent();
		Path targetINOFilePath = resolveFullOrLocalPath( handleInputByKey("targetInoFile").getContent() );
		boolean saveCompiledFilesNearby = handleInputByKey("saveCompiledFilesNearby").getBooleanContent();
		CompilationCall CompilationStepInstance = new CompilationCall(targetINOFilePath, foundFqbn, saveCompiledFilesNearby);
		
		if(CompilationStepInstance.isSuccessful()){
			handleOutputByKey("ifSuccessful", true);
		}
		handleOutputByKey("resultMessage", CompilationStepInstance.generateResultMessage());
	}

}
