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
public class PostProcessingAdjustIncludes extends PipelineStep implements PipelineSettingsDirectoryAndFilesPaths, VariableTypes {

	public static final String nameFlag = "PostProcessingAdjustIncludes";

	
	/**
	 * @param readData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 */
	public PostProcessingAdjustIncludes(VariableHandler VariableHandlerInstance, Map<String, Map<String, String>> readData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, readData);
	}

	/**
	 * @param yamlData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 */
	public PostProcessingAdjustIncludes(VariableHandler VariableHandlerInstance, String yamlData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, yamlData);
	}

	/**
	 * @see mumlacga.pipeline.parts.steps.common.PipelineStep#getRequiredInsAndOuts()
	 */
	@Override
	protected Map<String, Map<String, String>> getRequiredInsAndOuts() {
		LinkedHashMap<String, Map<String, String>> requiredInsAndOuts = new LinkedHashMap<String, Map<String, String>>();

		LinkedHashMap<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("componentCodePath", FolderPathType);
		ins.put("faultyInclude", StringType);
		ins.put("correctInclude", StringType);
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
		ins.put("componentCodePath", directValueKeyword + " arduino-containers/fastAndSlowCar_v2");
		ins.put("faultyInclude", directValueKeyword + " \"../components/");
		ins.put("correctInclude", directValueKeyword + " \"");
		
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
		Path componentCodePath = resolveFullOrLocalPath( handleInputByKey("componentCodePath").getContent() );
		String faultyInclude = "#include " + handleInputByKey("faultyInclude").getContent();
		String correctInclude = "#include " + handleInputByKey("correctInclude").getContent();
		
		DirectoryStream<Path> componentCodeFolder = Files.newDirectoryStream(componentCodePath);
		for(Path currentSubdirectoryPath: componentCodeFolder){
			if(Files.isRegularFile(currentSubdirectoryPath)){ // Just becasue of Doxyfile and CMakeLists.txt, which are not folders/directories.
				continue;
			}
			
			DirectoryStream<Path> currentFolder = Files.newDirectoryStream(currentSubdirectoryPath);
			for(Path currentEntryPath: currentFolder){
				if( (currentEntryPath.getFileName().toString().endsWith(".h")) || (currentEntryPath.getFileName().toString().endsWith(".c")) ){
					File currentFileIn = currentEntryPath.toFile();
					String intermediateFileName = currentEntryPath.toString() + ".editing";
					FileWriter workCopy = new FileWriter(intermediateFileName);
					Scanner currentFileReader = new Scanner(currentFileIn);
			    	while (currentFileReader.hasNextLine()) {
			    		String currentLine = currentFileReader.nextLine();
			    		
		    			// The following part could have been done more flexible using a detection of found folders,
			    		// but that would have increased the complexity and reduced readability for practically no gain.  
			    		if(currentLine.contains(faultyInclude)){
			    			String intermediate = currentLine.replace(faultyInclude, correctInclude);
			    			workCopy.write(intermediate + "\n");
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
		}
		
		handleOutputByKey("ifSuccessful", true);
	}

}
