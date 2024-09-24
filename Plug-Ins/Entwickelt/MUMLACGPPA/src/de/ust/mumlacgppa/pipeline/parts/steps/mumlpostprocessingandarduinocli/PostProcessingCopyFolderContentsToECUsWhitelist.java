/**
 * 
 */
package de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
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
import de.ust.mumlacgppa.pipeline.paths.PipelineSettingsDirectoryAndFilesPaths;

/**
 * @author muml
 *
 */
public class PostProcessingCopyFolderContentsToECUsWhitelist extends PipelineStep implements PipelineSettingsDirectoryAndFilesPaths, VariableTypes {

	public static final String nameFlag = "PostProcessingCopyFolderContentsToECUsWhitelist";

	
	/**
	 * @param readData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 */
	public PostProcessingCopyFolderContentsToECUsWhitelist(VariableHandler VariableHandlerInstance, Map<String, Map<String, String>> readData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, readData);
	}

	/**
	 * @param yamlData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 */
	public PostProcessingCopyFolderContentsToECUsWhitelist(VariableHandler VariableHandlerInstance, String yamlData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
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
		ins.put("ECUNameEnding", StringType);
		ins.put("whitelist", StringType);
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
		ins.put("sourceFolder", directValueKeyword + " arduino-containers/fastAndSlowCar_v2/lib");
		ins.put("destinationFolder", directValueKeyword + " arduino-containers");
		ins.put("ECUNameEnding", directValueKeyword + " ECU");
		ins.put("whitelist", directValueKeyword + " coordinatorComponent_Interface.h, coordinatorComponent.c");
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
		Path sourceFolder = resolveFullOrLocalPath( handleInputByKey("sourceFolder").getContent() );
		Path destinationPath = resolveFullOrLocalPath( handleInputByKey("destinationFolder").getContent() );
		String endingFilter = handleInputByKey("ECUNameEnding").getContent();
		String[] whitelistArray = handleInputByKeyAsArray("whitelist", ",");
		List<String> whitelist = Arrays.asList(whitelistArray);
		
		DirectoryStream<Path> arduinoContainersContent = Files.newDirectoryStream(destinationPath);
		for(Path currentDestinationEntryPath: arduinoContainersContent){
			if(currentDestinationEntryPath.getFileName().toString().endsWith(endingFilter)){
				DirectoryStream<Path> sourcePathContent = Files.newDirectoryStream(sourceFolder);
				for(Path currentSourcePathEntryPath: sourcePathContent){
					String currentFileName = currentSourcePathEntryPath.getFileName().toString();
					if( whitelist.contains(currentFileName) ){
						Files.copy(currentSourcePathEntryPath, currentDestinationEntryPath.resolve(currentFileName),
								StandardCopyOption.REPLACE_EXISTING);
					}
					else{
						// Do not copy
					}
				}
			}
		}
		
		handleOutputByKey("ifSuccessful", true);
	}

}
