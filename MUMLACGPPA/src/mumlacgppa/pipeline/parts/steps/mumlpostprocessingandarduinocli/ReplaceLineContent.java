/**
 * 
 */
package mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli;

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
public class ReplaceLineContent extends PipelineStep{

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
	 * @see mumlacga.pipeline.parts.steps.common.PipelineStep#setRequiredInsAndOuts()
	 */
	@Override
	protected void setRequiredInsAndOuts() {
		requiredInsAndOuts = new LinkedHashMap<String, HashSet<String>>();

		HashSet<String> ins = new LinkedHashSet<String>();
		ins.add("filePath");
		ins.add("targetLineContent");
		ins.add("contentReplacement");
		requiredInsAndOuts.put(inFlag, ins);
		
		HashSet<String> outs = new LinkedHashSet<String>();
		outs.add("ifSuccessful");
		requiredInsAndOuts.put(outFlag, outs);
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

	
	/**
	 * @param arduinoContainersPathString
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws StructureException 
	 * @throws VariableNotDefinedException 
	 */
	private void replaceContent()
			throws IOException, FileNotFoundException, VariableNotDefinedException, StructureException {

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


	public static void main(String[] args) throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, IOException, InterruptedException, NoArduinoCLIConfigFileException, FQBNErrorEception, ProjectFolderPathNotSetExceptionMUMLACGPPA {
		ProjectFolderPathStorage.projectFolderPath = Paths.get("/home/muml/MUMLProjects/Overtaking-Cars-Baumfalk");
		String testYaml =
				inFlag + ":\n"
				+ "  filePath: direct arduino-containers/fastCarDriverECU/courseControlCourseControlComponentStateChart.c\n"
				+ "  targetLineContent: direct stateChart->distanceLimit = stateChart->distanceLimit = 1;\n"
				+ "  contentReplacement: direct stateChart->distanceLimit = stateChart->distanceLimit = 0;\n"
				+ outFlag + ":\n"
				+ "  ifSuccessful: ifSuccessful\n";
		Yaml yaml = new Yaml(); // For yaml validity check.
		Map <String, Map <String, String>> testMap = yaml.load(testYaml);
		VariableHandler VariableHandlerInstance = new VariableHandler();
		ReplaceLineContent debugTest = new ReplaceLineContent(VariableHandlerInstance, testYaml);
		debugTest.execute();
	}
}
