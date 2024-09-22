/**
 * 
 */
package de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli;

import java.io.FileNotFoundException;
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
public class ReplaceLineContent extends PipelineStep implements VariableTypes {

	public static final String nameFlag = "ReplaceLineContent";

	
	/**
	 * @param readData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 */
	public ReplaceLineContent(VariableHandler VariableHandlerInstance, Map<String, Map<String, String>> readData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, readData);
	}

	/**
	 * @param yamlData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 */
	public ReplaceLineContent(VariableHandler VariableHandlerInstance, String yamlData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
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
		ins.put("targetLineContent", StringType);
		ins.put("contentReplacement", StringType);
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
		ins.put("targetLineContent", "direct stateChart->distanceLimit = stateChart->distanceLimit = 1;");
		ins.put("contentReplacement", "direct stateChart->distanceLimit = stateChart->distanceLimit = 0;");
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
		
		// From https://github.com/SQA-Robo-Lab/Overtaking-Cars/blob/hal_demo/arduino-containers_demo_hal/deployable-files-hal-test/README.md:
		// The comments are a rewritten summary to have the instructions/actions easier to read as comments in the code.  
		
		// For all the previous steps see PerformPostProcessingPartsUntilExceptConfig.
		
		replaceContent();
		
        /*
        18. Compile and upload for/to the respective desired Arduino micro controller via Arduino IDE.
        This is done separately.
		*/
		
		handleOutputByKey("ifSuccessful", true);
	}

	
	private void replaceContent()
			throws IOException, FileNotFoundException, VariableNotDefinedException, StructureException, InOrOutKeyNotDefinedException, FaultyDataException {

        /*
        17. the values for the ```desiredVelocity``` and ```slowVelocity``` can be set individually in
        the ```driveControlDriveControlComponentStateChart.c``` of the ```...DriverECU``` directories.
            1. Also set distance und lanedistance.
        For our modifications: The targeted file is "courseControlCourseControlComponentStateChart.c".
        */
		Path targetFilePath = resolveFullOrLocalPath( handleInputByKey("filePath").getContent() );
		String intermediateFileName = targetFilePath.toString() + ".editing";
		FileWriter workCopy = new FileWriter(intermediateFileName);
		
		// Such structures don't appear anywhere else in that file, so it should be safe to search by this.
		String sequenceToLookFor = handleInputByKey("targetLineContent").getContent();
		String sequenceToReplaceWith = handleInputByKey("contentReplacement").getContent();
		
		Scanner targetSomethingStateChartFileScanner = new Scanner(targetFilePath.toFile());
		while (targetSomethingStateChartFileScanner.hasNextLine()) {
    		String currentLine = targetSomethingStateChartFileScanner.nextLine();
			if(currentLine.contains(sequenceToLookFor)){
				workCopy.write( currentLine.replace(sequenceToLookFor, sequenceToReplaceWith) + "\n"); // To keep the indentation.
    		}
    		else{
    			workCopy.write(currentLine + "\n");
    		}
    	}
		targetSomethingStateChartFileScanner.close();
	    workCopy.close();
		Files.move(Paths.get(intermediateFileName), targetFilePath, StandardCopyOption.REPLACE_EXISTING);
	}
}
