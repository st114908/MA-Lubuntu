/**
 * 
 */
package de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import de.ust.arduinocliutilizer.worksteps.exceptions.FQBNErrorEception;
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

/**
 * @author muml
 *
 */
public class CopyFiles extends PipelineStep implements VariableTypes {

	public static final String nameFlag = "CopyFiles";

	/**
	 * @param readData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 */
	public CopyFiles(VariableHandler VariableHandlerInstance, Map<String, Map<String, String>> readData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, readData);
	}

	/**
	 * @param yamlData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 */
	public CopyFiles(VariableHandler VariableHandlerInstance, String yamlData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, yamlData);
	}

	/**
	 * @see mumlacga.pipeline.parts.steps.common.PipelineStep#getRequiredInsAndOuts()
	 */
	@Override
	protected Map<String, Map<String, String>> getRequiredInsAndOuts() {
		LinkedHashMap<String, Map<String, String>> requiredInsAndOuts = new LinkedHashMap<String, Map<String, String>>();

		LinkedHashMap<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("sourceFolder", FolderPathType);
		ins.put("destinationFolder", FolderPathType);
		ins.put("files", StringType);
		requiredInsAndOuts.put(inKeyword, ins);
		
		LinkedHashMap<String, String> outs = new LinkedHashMap<String, String>();
		outs.put("ifSuccessful", BooleanType);
		requiredInsAndOuts.put(outKeyword, outs);
		
		return requiredInsAndOuts;
	}

	public static Map<String, Map<String, String>> generateDefaultOrExampleValues(){
	// Map<String, Map<String, String>> for
	// Map<InOrOut, Map<ParameterOrOneOutput, SourceOrSaveTarget>>
		Map<String, Map<String, String>> exampleSettings = new LinkedHashMap<String, Map<String,String>>();

		// Ins:
		Map<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("sourceFolder", "direct exampleFilePath1");
		ins.put("destinationFolder", "direct exampleFilePath2");
		ins.put("files", "dummy1.txt, dummy2.txt");
		/*
		// Fixed version, not active in order to stay matching with the submitted thesis.
		ins.put("files", "direct dummy1.txt, dummy2.txt");
		*/
		exampleSettings.put(inKeyword, ins);
		
		// Out:
		Map<String, String> outs = new LinkedHashMap<String, String>();
		outs.put("ifSuccessful", "ifSuccessful");
		exampleSettings.put(outKeyword, outs);
		
		return exampleSettings;
	}
	
	/**
	 * @see mumlacga.pipeline.parts.steps.common.PipelineStep#execute()
	 */
	@Override
	public void execute()
			throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException,
			IOException, InterruptedException, NoArduinoCLIConfigFileException, FQBNErrorEception, InOrOutKeyNotDefinedException {
		handleOutputByKey("ifSuccessful", false); // In case of exception.
		Path completeSourceFolderPath = resolveFullOrLocalPath( handleInputByKey("sourceFolder").getContent() );
		Path completeDestinationFolderPath = resolveFullOrLocalPath( handleInputByKey("destinationFolder").getContent() );
		String[] fileList = handleInputByKeyAsArray("files", ",");
		for(int i = 0; i < fileList.length; i++){
			Path completeSourcePath = completeSourceFolderPath.resolve(fileList[i]);
			Path completeDestinationPath = completeDestinationFolderPath.resolve(fileList[i]);
			if(Files.exists(completeSourcePath) && Files.isRegularFile(completeSourcePath)){
				Files.copy(completeSourcePath, completeDestinationPath);
			}
		}
		handleOutputByKey("ifSuccessful", true); 
	}

}
