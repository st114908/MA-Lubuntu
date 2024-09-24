/**
 * 
 */
package de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
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
public class PostProcessingMoveIncludeBefore_ifdef__cplusplus extends PipelineStep implements PipelineSettingsDirectoryAndFilesPaths, VariableTypes {

	public static final String nameFlag = "PostProcessingMoveIncludeBefore_ifdef__cplusplus";

	
	/**
	 * @param readData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 */
	public PostProcessingMoveIncludeBefore_ifdef__cplusplus(VariableHandler VariableHandlerInstance, Map<String, Map<String, String>> readData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, readData);
	}

	/**
	 * @param yamlData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 */
	public PostProcessingMoveIncludeBefore_ifdef__cplusplus(VariableHandler VariableHandlerInstance, String yamlData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, yamlData);
	}

	/**
	 * @see mumlacga.pipeline.parts.steps.common.PipelineStep#getRequiredInsAndOuts()
	 */
	@Override
	protected Map<String, Map<String, String>> getRequiredInsAndOuts() {
		LinkedHashMap<String, Map<String, String>> requiredInsAndOuts = new LinkedHashMap<String, Map<String, String>>();

		LinkedHashMap<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("componentsPath", FolderPathType);
		ins.put("targetInclude", StringType);
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
		ins.put("componentsPath", directValueKeyword + " arduino-containers/fastAndSlowCar_v2/components");
		ins.put("targetInclude", directValueKeyword + " \"clock.h\"");
		
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
		
		// Only load once.
		Path componentsCodePath = resolveFullOrLocalPath( handleInputByKey("componentsPath").getContent() );
		String targetInclude = "#include " + handleInputByKey("targetInclude").getContent();
		
		DirectoryStream<Path> currentFolder = Files.newDirectoryStream(componentsCodePath);
		for(Path currentEntryPath: currentFolder){
			if( (currentEntryPath.getFileName().toString().endsWith(".h")) || (currentEntryPath.getFileName().toString().endsWith(".c")) ){
				File currentFileIn = currentEntryPath.toFile();
				String intermediateFileName = currentEntryPath.toString() + ".editing";
				FileWriter workCopy = new FileWriter(intermediateFileName);
				Scanner currentFileReader = new Scanner(currentFileIn);
		    	while (currentFileReader.hasNextLine()) {
		    		String currentLine = currentFileReader.nextLine();
		    		
		    		if(currentLine.contains("#ifdef __cplusplus")){
		    			workCopy.write(targetInclude + "\n");
		    			workCopy.write(currentLine + "\n");
		    		}
		    		else if(currentLine.contains(targetInclude)){
		    			// Don't copy!
		    		}
		    		else{
		    			workCopy.write(currentLine + "\n");
		    		}
		    	}
			    currentFileReader.close();
			    workCopy.close();
			    Files.move(Paths.get(intermediateFileName), currentEntryPath, StandardCopyOption.REPLACE_EXISTING);
			}
		}
		
		handleOutputByKey("ifSuccessful", true);
	}

}
