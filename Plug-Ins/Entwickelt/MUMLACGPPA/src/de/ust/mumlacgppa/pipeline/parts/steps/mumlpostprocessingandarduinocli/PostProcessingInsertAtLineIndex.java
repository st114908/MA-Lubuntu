/**
 * 
 */
package de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli;

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

/**
 * @author muml
 *
 */
public class PostProcessingInsertAtLineIndex extends PipelineStep implements VariableTypes {

	public static final String nameFlag = "PostProcessingInsertAtLineIndex";

	
	/**
	 * @param readData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 */
	public PostProcessingInsertAtLineIndex(VariableHandler VariableHandlerInstance, Map<String, Map<String, String>> readData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, readData);
	}

	/**
	 * @param yamlData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 */
	public PostProcessingInsertAtLineIndex(VariableHandler VariableHandlerInstance, String yamlData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, yamlData);
	}

	/**
	 * @see mumlacga.pipeline.parts.steps.common.PipelineStep#getRequiredInsAndOuts()
	 */
	@Override
	protected Map<String, Map<String, String>> getRequiredInsAndOuts() {
		LinkedHashMap<String, Map<String, String>> requiredInsAndOuts = new LinkedHashMap<String, Map<String, String>>();

		LinkedHashMap<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("filePath", FilePathType);
		ins.put("insertIndex", NumberType);
		ins.put("lineToInsert", StringType);
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
		ins.put("filePath", "direct arduino-containers/fastCarDriverECU/courseControlCourseControlComponentStateChart.c");
		ins.put("insertIndex", "direct 0");
		ins.put("lineToInsert", "direct // Comment as example.");
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
		
		Path targetFilePath = resolveFullOrLocalPath( handleInputByKey("filePath").getContent() );
		
		// Such structures don't appear anywhere else in that file, so it should be safe to search by this.
		String lineToInsert = handleInputByKey("lineToInsert").getContent();
		int insertIndex = handleInputByKey("insertIndex").getIntContent();
		
		Scanner targetSomethingStateChartFileScanner = new Scanner(targetFilePath.toFile());
		String intermediateFileName = targetFilePath.toString() + ".editing";
		FileWriter workCopy = new FileWriter(intermediateFileName);
		for(int i = 0; ( i < insertIndex && targetSomethingStateChartFileScanner.hasNextLine() ); i++ ){
			workCopy.write(targetSomethingStateChartFileScanner.nextLine() + "\n");
		}
		if(targetSomethingStateChartFileScanner.hasNextLine()) {
			workCopy.write(lineToInsert + "\n");
		}
		while (targetSomethingStateChartFileScanner.hasNextLine()) {
			workCopy.write(targetSomethingStateChartFileScanner.nextLine() + "\n");
    	}
		targetSomethingStateChartFileScanner.close();
	    workCopy.close();
		Files.move(Paths.get(intermediateFileName), targetFilePath, StandardCopyOption.REPLACE_EXISTING);
		
		handleOutputByKey("ifSuccessful", true);
	}
}
