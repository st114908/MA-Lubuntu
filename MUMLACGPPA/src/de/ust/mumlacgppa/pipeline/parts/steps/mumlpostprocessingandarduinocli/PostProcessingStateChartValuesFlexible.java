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
public class PostProcessingStateChartValuesFlexible extends PipelineStep implements VariableTypes {

	public static final String nameFlag = "PostProcessingStateChartValuesFlexible";

	
	/**
	 * @param readData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 */
	public PostProcessingStateChartValuesFlexible(VariableHandler VariableHandlerInstance, Map<String, Map<String, String>> readData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, readData);
	}

	/**
	 * @param yamlData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 */
	public PostProcessingStateChartValuesFlexible(VariableHandler VariableHandlerInstance, String yamlData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, yamlData);
	}

	/* (non-Javadoc)
	 * @see mumlacga.pipeline.parts.steps.common.PipelineStep#setRequiredInsAndOuts()
	 */
	@Override
	protected Map<String, Map<String, String>> getRequiredInsAndOuts() {
		LinkedHashMap<String, Map<String, String>> requiredInsAndOuts = new LinkedHashMap<String, Map<String, String>>();

		LinkedHashMap<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("arduinoContainersPath", FolderPathType);
		ins.put("ECUName", StringType);
		ins.put("fileName", StringType);
		ins.put("targetStateChartValueName", NumberType);
		ins.put("valueToSet", NumberType);
		requiredInsAndOuts.put(inKeyword, ins);
		
		LinkedHashMap<String, String> outs = new LinkedHashMap<String, String>();
		outs.put("ifSuccessful", BooleanType);
		requiredInsAndOuts.put(outKeyword, outs);
		
		return requiredInsAndOuts;
	}

	// Map<String, Map<String, String>> for
	// Map<InOrOut, Map<ParameterOrOneOutput, SourceOrSaveTarget>>
	public static Map<String, Map<String, String>> generateDefaultOrExampleValues(){
		Map<String, Map<String, String>> exampleSettings = new LinkedHashMap<String, Map<String,String>>();

		// Ins:
		Map<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("arduinoContainersPath", "direct arduino-containers");
		ins.put("ECUName", "direct FastCarDriverECU");
		ins.put("fileName", "direct courseControlCourseControlComponentStateChart.c");
		ins.put("targetStateChartValueName", "direct desiredVelocity");
		ins.put("valueToSet", "direct 12");
		exampleSettings.put(inKeyword, ins);
		
		// Out:
		Map<String, String> outs = new LinkedHashMap<String, String>();
		outs.put("ifSuccessful", "ifSuccessful");
		exampleSettings.put(outKeyword, outs);
		
		return exampleSettings;
	}

	
	/* (non-Javadoc)
	 * @see mumlacga.pipeline.parts.steps.common.PipelineStep#execute()
	 */
	@Override
	public void execute()
			throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException,
			IOException, InterruptedException, NoArduinoCLIConfigFileException, FQBNErrorEception, InOrOutKeyNotDefinedException {

		handleOutputByKey("ifSuccessful", false); // In case of exception.
		Path arduinoContainersPath = resolveFullOrLocalPath( handleInputByKey("arduinoContainersPath").getContent() );
		
		// From https://github.com/SQA-Robo-Lab/Overtaking-Cars/blob/hal_demo/arduino-containers_demo_hal/deployable-files-hal-test/README.md:
		// The comments are a rewritten summary to have the instructions/actions easier to read as comments in the code.  
		
		// For all the previous steps see PerformPostProcessingPartsUntilExceptConfig.
		
		/*
        17. the values for the ```desiredVelocity``` and ```slowVelocity``` can be set individually in
        the ```driveControlDriveControlComponentStateChart.c``` of the ```...DriverECU``` directories.
            1. Also set distance und lanedistance.
        For our modifications: The targeted file is "courseControlCourseControlComponentStateChart.c".
        */
		Path targetECUFolderPath = arduinoContainersPath.resolve( handleInputByKey("ECUName").getContent() );
		Path targetStateChartFilePath = targetECUFolderPath.resolve( handleInputByKey("fileName").getContent() );
		String intermediateFileName = targetStateChartFilePath.toString() + ".editing";
		FileWriter workCopy = new FileWriter(intermediateFileName);
		
		String targetStateChartValueName = handleInputByKey("targetStateChartValueName").getContent();
		// Such structures don't appear anywhere else in that file, so it should be safe to search by this.
		String sequenceToLookFor = "stateChart->"+targetStateChartValueName+" = stateChart->"+targetStateChartValueName+" = 0;";
		String sequenceToReplaceWith =
				"stateChart->"+targetStateChartValueName+" = stateChart->"+targetStateChartValueName
				+" = "+handleInputByKey("valueToSet").getIntContent()+";\n";
		
		Scanner targetSomethingStateChartFileScanner = new Scanner(targetStateChartFilePath.toFile());
		while (targetSomethingStateChartFileScanner.hasNextLine()) {
    		String currentLine = targetSomethingStateChartFileScanner.nextLine();
			if(currentLine.contains(sequenceToLookFor)){
				workCopy.write( currentLine.replace(sequenceToLookFor, sequenceToReplaceWith) ); // To keep the indentation.
    		}
    		else{
    			workCopy.write(currentLine + "\n");
    		}
    	}
		targetSomethingStateChartFileScanner.close();
	    workCopy.close();
		Files.move(Paths.get(intermediateFileName), targetStateChartFilePath, StandardCopyOption.REPLACE_EXISTING);
		
        /*
        18. Compile and upload for/to the respective desired Arduino micro controller via Arduino IDE.
        This is done separately.
		*/
		
		handleOutputByKey("ifSuccessful", true);
	}

}
