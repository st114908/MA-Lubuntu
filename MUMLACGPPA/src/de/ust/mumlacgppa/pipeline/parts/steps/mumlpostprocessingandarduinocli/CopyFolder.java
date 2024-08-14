/**
 * 
 */
package de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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

/**
 * @author muml
 *
 */
public class CopyFolder extends PipelineStep {

	public static final String nameFlag = "CopyFolder";

	
	/**
	 * @param readData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 */
	public CopyFolder(VariableHandler VariableHandlerInstance, Map<String, Map<String, String>> readData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, readData);
	}

	/**
	 * @param yamlData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 */
	public CopyFolder(VariableHandler VariableHandlerInstance, String yamlData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, yamlData);
	}

	/**
	 * @see mumlacga.pipeline.parts.steps.common.PipelineStep#getRequiredInsAndOuts()
	 */
	@Override
	protected Map<String, HashSet<String>> getRequiredInsAndOuts() {
		LinkedHashMap<String, HashSet<String>> requiredInsAndOuts = new LinkedHashMap<String, HashSet<String>>();

		HashSet<String> ins = new LinkedHashSet<String>();
		ins.add("sourcePath");
		ins.add("destinationPath");
		requiredInsAndOuts.put(inKeyword, ins);
		
		HashSet<String> outs = new LinkedHashSet<String>();
		outs.add("ifSuccessful");
		requiredInsAndOuts.put(outKeyword, outs);
		
		return requiredInsAndOuts;
	}

	public static Map<String, Map<String, String>> generateDefaultOrExampleValues(){
	// Map<String, Map<String, String>> for
	// Map<InOrOut, Map<ParameterOrOneOutput, SourceOrSaveTarget>>
		Map<String, Map<String, String>> exampleSettings = new LinkedHashMap<String, Map<String,String>>();

		// Ins:
		Map<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("sourcePath", "direct exampleFolderPath1");
		ins.put("destinationPath", "direct exampleFolderPath2");
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
		Path completeSourcePath = resolveFullOrLocalPath( handleInputByKey("sourcePath").getContent() );
		Path completeDestinationPath = resolveFullOrLocalPath( handleInputByKey("destinationPath").getContent() );
		if(Files.exists(completeSourcePath) && Files.isDirectory(completeSourcePath)){
			// https://www.baeldung.com/java-copy-directory
			Files.walk(completeSourcePath).forEach(source -> {
		          Path currentDestination = Paths.get(completeDestinationPath.toString(), source.toString()
		            .substring(completeSourcePath.toString().length()));
		          try {
		        	  Files.copy(source, currentDestination);
		          } catch (IOException e) {
		              e.printStackTrace();
		          }
		      });
		}
		handleOutputByKey("ifSuccessful", true); 
	}

}
