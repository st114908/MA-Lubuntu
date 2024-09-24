/**
 * 
 */
package de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
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
public class DeleteFolder extends PipelineStep implements VariableTypes {

	public static final String nameFlag = "DeleteFolder";

	
	/**
	 * @param readData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 */
	public DeleteFolder(VariableHandler VariableHandlerInstance, Map<String, Map<String, String>> readData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, readData);
	}

	/**
	 * @param yamlData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 */
	public DeleteFolder(VariableHandler VariableHandlerInstance, String yamlData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, yamlData);
	}

	/**
	 * @see mumlacga.pipeline.parts.steps.common.PipelineStep#getRequiredInsAndOuts()
	 */
	@Override
	protected Map<String, Map<String, String>> getRequiredInsAndOuts() {
		LinkedHashMap<String, Map<String, String>> requiredInsAndOuts = new LinkedHashMap<String, Map<String, String>>();

		LinkedHashMap<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("path", FolderPathType);
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
		ins.put("path", "direct exampleFolderPath");
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
		Path completePath = resolveFullOrLocalPath( handleInputByKey("path").getContent() );
		if(Files.exists(completePath) && Files.isDirectory(completePath)){
			// https://www.baeldung.com/java-delete-directory
			Files.walkFileTree(completePath, 
				new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult postVisitDirectory(
						Path dir, IOException exc) throws IOException {
							Files.delete(dir);
							return FileVisitResult.CONTINUE;
						}
		    
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)throws IOException {
						Files.delete(file);
						return FileVisitResult.CONTINUE;
					}
				}
			);
		}
		handleOutputByKey("ifSuccessful", true); 
	}

}
