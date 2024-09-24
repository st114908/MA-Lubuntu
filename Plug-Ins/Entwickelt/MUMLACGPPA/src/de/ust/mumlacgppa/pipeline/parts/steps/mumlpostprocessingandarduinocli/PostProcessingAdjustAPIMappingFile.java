/**
 * 
 */
package de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

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
import de.ust.mumlacgppa.pipeline.paths.PipelineSettingsDirectoryAndFilesPaths;

/**
 * @author muml
 *
 */
public class PostProcessingAdjustAPIMappingFile extends PipelineStep implements PipelineSettingsDirectoryAndFilesPaths, VariableTypes {

	public static final String nameFlag = "PostProcessingAdjustAPIMappingFile";

	
	/**
	 * @param readData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 */
	public PostProcessingAdjustAPIMappingFile(VariableHandler VariableHandlerInstance, Map<String, Map<String, String>> readData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, readData);
	}

	/**
	 * @param yamlData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 */
	public PostProcessingAdjustAPIMappingFile(VariableHandler VariableHandlerInstance, String yamlData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
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
		ins.put("fileName", StringType);
		ins.put("library", StringType);
		ins.put("instruction", StringType);
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
		ins.put("sourceFolder", directValueKeyword + " arduino-containers/APImappings");
		ins.put("destinationFolder", directValueKeyword + " arduino-containers/APImappings");
		ins.put("fileName", directValueKeyword + " CI_FRONTDISTANCESENSORFDISTANCESENSORdistancePortaccessCommand.c");
		ins.put("library", directValueKeyword + " SimpleHardwareController_Connector.h");
		ins.put("instruction", directValueKeyword + " *distance = SimpleHardwareController_DistanceSensor_GetDistanceToClosestMm(0);");
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
		
		String fileName = handleInputByKey("fileName").getContent();
		Path sourceFolder = resolveFullOrLocalPath( handleInputByKey("sourceFolder").getContent() ).resolve(fileName);
		Path destinationPath = resolveFullOrLocalPath( handleInputByKey("destinationFolder").getContent() ).resolve(fileName);
		String library = handleInputByKey("library").getContent();
		String instruction = handleInputByKey("instruction").getContent();
		
		File fileToRead = sourceFolder.toFile();
		Scanner fileReader = new Scanner(fileToRead);
		String intermediateFileName = destinationPath.toString() + ".editing";
		FileWriter workCopy = new FileWriter(intermediateFileName);

		workCopy.write("#include <" + library + ">\n");
    	while (fileReader.hasNextLine()) {
    		String currentLine = fileReader.nextLine();

    		if(currentLine.contains("// Start of user code API") ){
    			workCopy.write(currentLine + "\n");
    			workCopy.write("	" + instruction + "\n");
    		}
    		else{
    			workCopy.write(currentLine + "\n");
    		}
    	}
	    fileReader.close();
	    workCopy.close();
		Files.move(Paths.get(intermediateFileName), destinationPath, StandardCopyOption.REPLACE_EXISTING);
		
		handleOutputByKey("ifSuccessful", true);
	}

}
