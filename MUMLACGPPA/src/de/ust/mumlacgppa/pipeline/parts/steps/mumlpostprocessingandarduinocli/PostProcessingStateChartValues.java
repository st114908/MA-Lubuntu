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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Scanner;

import org.yaml.snakeyaml.Yaml;

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
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

/**
 * @author muml
 *
 */
public class PostProcessingStateChartValues extends PipelineStep {

	public static final String nameFlag = "PostProcessingStateChartValues";

	/**
	 * @param readData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA
	 */
	public PostProcessingStateChartValues(VariableHandler VariableHandlerInstance,
			Map<String, Map<String, String>> readData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, readData);
	}

	/**
	 * @param yamlData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA
	 */
	public PostProcessingStateChartValues(VariableHandler VariableHandlerInstance, String yamlData)
			throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, yamlData);
	}

	/**
	 * @see mumlacga.pipeline.parts.steps.common.PipelineStep#setRequiredInsAndOuts()
	 */
	@Override
	protected void setRequiredInsAndOuts() {
		requiredInsAndOuts = new LinkedHashMap<String, HashSet<String>>();

		HashSet<String> ins = new LinkedHashSet<String>();
		ins.add("arduinoContainersPath");
		ins.add("ECUName");
		ins.add("distanceLimit");
		ins.add("desiredVelocity");
		ins.add("slowVelocity");
		ins.add("laneDistance");
		requiredInsAndOuts.put(inKeyword, ins);

		HashSet<String> outs = new LinkedHashSet<String>();
		outs.add("ifSuccessful");
		requiredInsAndOuts.put(outKeyword, outs);
	}

	public static Map<String, Map<String, String>> generateDefaultOrExampleValues() {
		// Map<String, Map<String, String>> for
		// Map<InOrOut, Map<ParameterOrOneOutput, SourceOrSaveTarget>>
		Map<String, Map<String, String>> exampleSettings = new LinkedHashMap<String, Map<String, String>>();

		// Ins:
		Map<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("arduinoContainersPath", "direct arduino-containers");
		ins.put("ECUName", "direct arduino-containers/fastCarCoordinatorECU/fastCarDriverECU.ino");
		ins.put("distanceLimit", "direct 1");
		ins.put("desiredVelocity", "direct 2");
		ins.put("slowVelocity", "direct 3");
		ins.put("laneDistance", "direct 4");
		exampleSettings.put(inKeyword, ins);

		// Out:
		Map<String, String> outs = new LinkedHashMap<String, String>();
		outs.put("ifSuccessful", "ifSuccessful");
		exampleSettings.put(outKeyword, outs);

		return exampleSettings;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mumlacga.pipeline.parts.steps.common.PipelineStep#execute()
	 */
	@Override
	public void execute() throws VariableNotDefinedException, StructureException, FaultyDataException,
			ParameterMismatchException, IOException, InterruptedException, NoArduinoCLIConfigFileException,
			FQBNErrorEception, InOrOutKeyNotDefinedException {

		handleOutputByKey("ifSuccessful", false); // In case of exception.
		Path arduinoContainersPath = resolveFullOrLocalPath(handleInputByKey("arduinoContainersPath").getContent());

		// From
		// https://github.com/SQA-Robo-Lab/Overtaking-Cars/blob/hal_demo/arduino-containers_demo_hal/deployable-files-hal-test/README.md:
		// The comments are a rewritten summary to have the instructions/actions
		// easier to read as comments in the code.

		// For all the previous steps see PerformPostProcessingPartsUntilConfig.

		setVelocitiesAndDistancesForStates(arduinoContainersPath);

		/*
		 * 18. Compile and upload for/to the respective desired Arduino micro
		 * controller via Arduino IDE. This is done separately.
		 */

		handleOutputByKey("ifSuccessful", true);
	}

	private void setVelocitiesAndDistancesForStates(Path arduinoContainersPath) throws IOException,
			FileNotFoundException, VariableNotDefinedException, StructureException, InOrOutKeyNotDefinedException {
		/*
		 * 17. the values for the ```desiredVelocity``` and ```slowVelocity```
		 * can be set individually in the
		 * ```driveControlDriveControlComponentStateChart.c``` of the
		 * ```...DriverECU``` directories. 1. Also set distance und
		 * lanedistance. For our modifications: The targeted file is
		 * "courseControlCourseControlComponentStateChart.c".
		 */
		Path targetECUFolderPath = arduinoContainersPath.resolve(handleInputByKey("ECUName").getContent());
		Path targetStateChartFilePath = targetECUFolderPath.resolve("courseControlCourseControlComponentStateChart.c");
		String intermediateFileName = targetStateChartFilePath.toString() + ".editing";
		FileWriter workCopy = new FileWriter(intermediateFileName);

		Scanner targetSomethingStateChartFileScanner = new Scanner(targetStateChartFilePath.toFile());
		while (targetSomethingStateChartFileScanner.hasNextLine()) {
			String currentLine = targetSomethingStateChartFileScanner.nextLine();
			// Such structures don't appear anywhere else in that file, so it
			// should be safe.
			if (currentLine.contains("stateChart->distanceLimit = stateChart->distanceLimit = 0;")) {
				workCopy.write("			stateChart->distanceLimit = stateChart->distanceLimit = "
						+ handleInputByKey("distanceLimit").getIntContent() + ";\n");
			} else if (currentLine.contains("stateChart->desiredVelocity = stateChart->desiredVelocity = 0;")) {
				workCopy.write("			stateChart->desiredVelocity = stateChart->desiredVelocity = "
						+ handleInputByKey("desiredVelocity").getIntContent() + ";\n");
			} else if (currentLine.contains("stateChart->slowVelocity = stateChart->slowVelocity = 0;")) {
				workCopy.write("			stateChart->slowVelocity = stateChart->slowVelocity = "
						+ handleInputByKey("slowVelocity").getIntContent() + ";\n");
			} else if (currentLine.contains("stateChart->laneDistance = stateChart->laneDistance = 0;")) {
				workCopy.write("			stateChart->laneDistance = stateChart->laneDistance = "
						+ handleInputByKey("laneDistance").getIntContent() + ";\n");
			} else {
				workCopy.write(currentLine + "\n");
			}
		}
		targetSomethingStateChartFileScanner.close();
		workCopy.close();
		Files.move(Paths.get(intermediateFileName), targetStateChartFilePath, StandardCopyOption.REPLACE_EXISTING);
	}
	
}
