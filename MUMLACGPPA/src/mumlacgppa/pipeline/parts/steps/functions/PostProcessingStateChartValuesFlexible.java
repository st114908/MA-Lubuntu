/**
 * 
 */
package mumlacgppa.pipeline.parts.steps.functions;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Scanner;

import org.yaml.snakeyaml.Yaml;

import arduinocliutilizer.worksteps.exceptions.FQBNErrorEception;
import arduinocliutilizer.worksteps.exceptions.NoArduinoCLIConfigFileException;
import mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import mumlacgppa.pipeline.parts.exceptions.ParameterMismatchException;
import mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import mumlacgppa.pipeline.parts.exceptions.StructureException;
import mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import mumlacgppa.pipeline.parts.steps.PipelineStep;
import mumlacgppa.pipeline.parts.storage.VariableHandler;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

/**
 * @author muml
 *
 */
public class PostProcessingStateChartValuesFlexible extends PipelineStep{

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
	protected void setRequiredInsAndOuts() {
		requiredInsAndOuts = new LinkedHashMap<String, HashSet<String>>();

		HashSet<String> ins = new LinkedHashSet<String>();
		ins.add("arduinoContainersPath");
		ins.add("ECUName");
		ins.add("fileName");
		ins.add("targetStateChartValueName");
		ins.add("valueToSet");
		requiredInsAndOuts.put(inFlag, ins);
		
		HashSet<String> outs = new LinkedHashSet<String>();
		outs.add("ifSuccessful");
		requiredInsAndOuts.put(outFlag, outs);
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
		exampleSettings.put(inFlag, ins);
		
		// Out:
		Map<String, String> outs = new LinkedHashMap<String, String>();
		outs.put("ifSuccessful", "ifSuccessful");
		exampleSettings.put(outFlag, outs);
		
		return exampleSettings;
	}

	
	/* (non-Javadoc)
	 * @see mumlacga.pipeline.parts.steps.common.PipelineStep#execute()
	 */
	@Override
	public void execute()
			throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException,
			IOException, InterruptedException, NoArduinoCLIConfigFileException, FQBNErrorEception {

		handleOutputByKey("ifSuccessful", false); // In case of exception.
		Path arduinoContainersPath = resolveFullOrLocalPath( handleInputByKey("arduinoContainersPath").getContent() );
		
		// From https://github.com/SQA-Robo-Lab/Overtaking-Cars/blob/hal_demo/arduino-containers_demo_hal/deployable-files-hal-test/README.md:
		// The comments are a rewritten summary to have the instructions/actions easier to read as comments in the code.  
		
		// For all the previous steps see PerformPostProcessingPartsUntilExceptConfig.
		
		setValueForStates(arduinoContainersPath);
		
        /*
        18. Compile and upload for/to the respective desired Arduino micro controller via Arduino IDE.
        This is done separately.
		*/
		
		handleOutputByKey("ifSuccessful", true);
	}

	
	/**
	 * @param arduinoContainersPathString
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws StructureException 
	 * @throws VariableNotDefinedException 
	 */
	private void setValueForStates(Path arduinoContainersPath)
			throws IOException, FileNotFoundException, VariableNotDefinedException, StructureException {

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
	}


	public static void main(String[] args) throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, IOException, InterruptedException, NoArduinoCLIConfigFileException, FQBNErrorEception, ProjectFolderPathNotSetExceptionMUMLACGPPA {
		ProjectFolderPathStorage.projectFolderPath = Paths.get("/home/muml/MUMLProjects/Overtaking-Cars-Baumfalk");
		String testYaml =
				inFlag + ":\n"
				+ "  arduinoContainersPath: direct /home/muml/MUMLProjects/Overtaking-Cars-Baumfalk/arduino-containers\n"
				+ "  ECUName: direct fastCarDriverECU\n"
				+ "  fileName: direct courseControlCourseControlComponentStateChart.c\n"
				+ "  targetStateChartValueName: direct desiredVelocity\n"
				+ "  valueToSet: direct 12\n"
				+ outFlag + ":\n"
				+ "  ifSuccessful: ifSuccessful\n";
		Yaml yaml = new Yaml(); // For yaml validity check.
		Map <String, Map <String, String>> testMap = yaml.load(testYaml);
		VariableHandler VariableHandlerInstance = new VariableHandler();
		PostProcessingStateChartValuesFlexible debugTest = new PostProcessingStateChartValuesFlexible(VariableHandlerInstance, testYaml);
		debugTest.execute();
	}
}
