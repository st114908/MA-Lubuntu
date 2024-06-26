package de.ust.mumlacgppa.pipeline.settingsgeneration.mumlpostprocessingandarduinocli;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import de.ust.mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import de.ust.mumlacgppa.pipeline.parts.steps.Keywords;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.Compile;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ComponentCodeGeneration;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ContainerCodeGeneration;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ContainerTransformation;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.CopyFolder;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.DeleteFolder;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.LookupBoardBySerialNumber;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.OnlyContinueIfFulfilledElseAbort;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PopupWindowMessage;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingStateChartValues;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingStepsUntilConfig;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.Upload;
import de.ust.mumlacgppa.pipeline.paths.PipelineSettingsDirectoryAndFilePaths;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

public class PipelineSettingsGenerator implements PipelineSettingsDirectoryAndFilePaths, Keywords {
	private Path completeMUMLACGPPASettingsDirectoryPath;
	private Path completeMUMLACGPPASettingsFilePath;

	public PipelineSettingsGenerator() throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super();
		if (ProjectFolderPathStorage.projectFolderPath == null) {
			throw new ProjectFolderPathNotSetExceptionMUMLACGPPA(
					"The static field projectFolderPath in ProjectFolderPathStorage has "
							+ "to be set to a complete file system path to the project's folder!");
		}
		completeMUMLACGPPASettingsDirectoryPath = ProjectFolderPathStorage.projectFolderPath
				.resolve(PIPELINE_SETTINGS_DIRECTORY_FOLDER);
		completeMUMLACGPPASettingsFilePath = completeMUMLACGPPASettingsDirectoryPath
				.resolve(PIPELINE_SETTINGS_FILE_NAME);
	}

	public Path getCompleteSettingsFilePath() {
		return completeMUMLACGPPASettingsFilePath;
	}

	private Map<String, Map<String, String>> generateDeleteFolder(String path) {
		Map<String, Map<String, String>> deleteDirectoryArduinoContainers = DeleteFolder
				.generateDefaultOrExampleValues();
		Map<String, String> deleteDirectoryArduinoContainersSettingsIns = deleteDirectoryArduinoContainers
				.get(inKeyword);
		deleteDirectoryArduinoContainersSettingsIns.put("path", path);
		deleteDirectoryArduinoContainers.put(inKeyword, deleteDirectoryArduinoContainersSettingsIns);
		return deleteDirectoryArduinoContainers;
	}

	private Map<String, Map<String, String>> generatePostProcessingStateChartValuesStepAndAdjustSettings(
			String arduinoContainersPath, String ECUName, String desiredVelocity) {
		Map<String, Map<String, String>> postProcessingStateChartValuesSettings = PostProcessingStateChartValues
				.generateDefaultOrExampleValues();
		Map<String, String> postProcessingStateChartValuesSettingsIns = postProcessingStateChartValuesSettings
				.get(inKeyword);
		postProcessingStateChartValuesSettingsIns.put("arduinoContainersPath", arduinoContainersPath);
		postProcessingStateChartValuesSettingsIns.put("ECUName", ECUName);
		postProcessingStateChartValuesSettingsIns.put("desiredVelocity", desiredVelocity);
		postProcessingStateChartValuesSettings.put(inKeyword, postProcessingStateChartValuesSettingsIns);
		return postProcessingStateChartValuesSettings;
	}

	private Map<String, Map<String, String>> generateCompileStepAndAdjustSettings(String boardTypeIdentifierFQBN,
			String targetInoFile) {
		Map<String, Map<String, String>> compileSettings = Compile.generateDefaultOrExampleValues();
		Map<String, String> compileSettingsIns = compileSettings.get(inKeyword);
		compileSettingsIns.put("boardTypeIdentifierFQBN", boardTypeIdentifierFQBN);
		compileSettingsIns.put("targetInoFile", targetInoFile);
		compileSettings.put(inKeyword, compileSettingsIns);
		return compileSettings;
	}

	private Map<String, Map<String, String>> generateLookupBoardBySerialNumberStepAndAdjustSettings(
			String boardSerialNumber, String boardTypeIdentifierFQBN) {
		Map<String, Map<String, String>> lookupBoardBySerialNumberSettings = LookupBoardBySerialNumber
				.generateDefaultOrExampleValues();
		Map<String, String> lookupBoardBySerialNumberSettingsIns = lookupBoardBySerialNumberSettings.get(inKeyword);
		lookupBoardBySerialNumberSettingsIns.put("boardSerialNumber", boardSerialNumber);
		lookupBoardBySerialNumberSettingsIns.put("boardTypeIdentifierFQBN", boardTypeIdentifierFQBN);
		lookupBoardBySerialNumberSettings.put(inKeyword, lookupBoardBySerialNumberSettingsIns);
		return lookupBoardBySerialNumberSettings;
	}

	private Map<String, Map<String, String>> generateUploadStepAndAdjustSettings(String portAddress,
			String boardTypeIdentifierFQBN, String targetInoOrHexFile) {
		Map<String, Map<String, String>> uploadSettings = Upload.generateDefaultOrExampleValues();
		Map<String, String> uploadSettingsIns = uploadSettings.get(inKeyword);
		uploadSettingsIns.put("portAddress", portAddress);
		uploadSettingsIns.put("boardTypeIdentifierFQBN", boardTypeIdentifierFQBN);
		uploadSettingsIns.put("targetInoOrHexFile", targetInoOrHexFile);
		uploadSettings.put(inKeyword, uploadSettingsIns);
		return uploadSettings;
	}

	private Map<String, Map<String, String>> generateOnlyContinueIfFulfilledElseAbortSettings(String condition,
			String message) {
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortSettings = OnlyContinueIfFulfilledElseAbort
				.generateDefaultOrExampleValues();
		Map<String, String> onlyContinueIfFulfilledElseAbortSettingsIns = onlyContinueIfFulfilledElseAbortSettings
				.get(inKeyword);
		onlyContinueIfFulfilledElseAbortSettingsIns.put("condition", condition);
		onlyContinueIfFulfilledElseAbortSettingsIns.put("message", message);
		onlyContinueIfFulfilledElseAbortSettings.put(inKeyword, onlyContinueIfFulfilledElseAbortSettingsIns);
		return onlyContinueIfFulfilledElseAbortSettings;
	}

	private Map<String, Map<String, Map<String, String>>> pipelineSegmentHelper(Yaml yaml, String key,
			Map<String, Map<String, String>> settings) {
		Map<String, Map<String, Map<String, String>>> exampleUsageDef = new LinkedHashMap<String, Map<String, Map<String, String>>>();
		exampleUsageDef.put(key, settings);
		return exampleUsageDef;
	}

	public boolean generatePipelineConfigFile() throws IOException {
		File directoryCheck = completeMUMLACGPPASettingsDirectoryPath.toFile();
		if (!directoryCheck.exists()) {
			directoryCheck.mkdirs();
		}

		File configExistenceCheck = completeMUMLACGPPASettingsFilePath.toFile();
		if (configExistenceCheck.exists() && configExistenceCheck.isFile()) {
			return false;
		}

		// For DumperOptions examples see
		// https://www.tabnine.com/code/java/methods/org.yaml.snakeyaml.DumperOptions$LineBreak/getPlatformLineBreak
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		options.setPrettyFlow(true);
		options.setLineBreak(DumperOptions.LineBreak.getPlatformLineBreak());
		Yaml yaml = new Yaml(options);
		FileWriter settingsWriter = new FileWriter(completeMUMLACGPPASettingsFilePath.toFile());

		// Variable names and potentially their values:

		String generatedCodeFolderNameVariableName = "generatedRawFilesFolderPath";
		String generatedCodeFolderNameVariableValue = directValueKeyword + " generated-files";
		String muml_containerFilePathVariableName = "muml_containerFilePath";
		String muml_containerFilePathVariableValue = generatedCodeFolderNameVariableValue
				+ "/MUML_Container.muml_container";
		String deployableCodeFolderNameVariableName = "deployableFilesFolderPath";
		String deployableCodeFolderNameVariableValue = directValueKeyword + " deployable-files";
		String componentCodeFolderNameVariableName = "componentCodeFilesFolderPath";
		String componentCodeFolderNameVariableValue = deployableCodeFolderNameVariableValue + "/fastAndSlowCar_v2";
		String apiMappingsFolderNameVariableName = "apiMappingsFolderPath";
		String apiMappingsFolderNameVariableValue = deployableCodeFolderNameVariableValue + "/APImappings";

		String fastCarDesiredVelocityVariableName = "fastCarDesiredVelocity";
		String fastCarDesiredVelocityVariableValue =  directValueKeyword + " 65";
		String slowCarDesiredVelocityVariableName = "slowCarDesiredVelocity";
		String slowCarDesiredVelocityVariableValue = directValueKeyword + " 55";
		
		String usedDriverBoardIdentifierFQBNVariableName = "usedDriverBoardIdentifierFQBN";
		String usedDriverBoardIdentifierFQBNVariableValue = directValueKeyword + " arduino:avr:mega";
		String usedCoordinatorBoardIdentifierFQBNVariableName = "usedCoordinatorBoardIdentifierFQBN";
		String usedCoordinatorBoardIdentifierFQBNVariableValue = directValueKeyword + " arduino:avr:nano";
		
		String fastCarCoordinatorECUBoardSerialNumberVariableName = "fastCarCoordinatorECUBoardSerialNumber";
		String fastCarCoordinatorECUBoardSerialNumberVariableValue = directValueKeyword + " DummySerialFastCarCoordinator";
		String fastCarDriverECUBoardSerialNumberVariableName = "fastCarDriverECUBoardSerialNumber";
		String fastCarDriverECUBoardSerialNumberVariableValue = directValueKeyword + " DummySerialFastCarDriver";
		String slowCarCoordinatorECUBoardSerialNumberVariableName = "slowCarCoordinatorECUBoardSerialNumber";
		String slowCarCoordinatorECUBoardSerialNumberVariableValue = directValueKeyword + " DummySerialSlowCarCoordinator";
		String slowCarDriverECUBoardSerialNumberVariableName = "slowCarDriverECUBoardSerialNumber";
		String slowCarDriverECUBoardSerialNumberVariableValue = directValueKeyword + " DummySerialSlowCarDriver";

		String fastCarCoordinatorECUName = "fastCarCoordinatorECU";
		String fastCarCoordinatorECUFolderPathVariableName = fastCarCoordinatorECUName + "FolderPath";
		String fastCarCoordinatorECUFolderPathVariableValue = deployableCodeFolderNameVariableValue + "/"
				+ fastCarCoordinatorECUName;
		String fastCarCoordinatorECUINOFilePathVariableName = fastCarCoordinatorECUName + "INOFilePath";
		String fastCarCoordinatorECUINOFilePathVariableValue = deployableCodeFolderNameVariableValue + "/"
				+ fastCarCoordinatorECUName + "/" + fastCarCoordinatorECUName + ".ino";
		String fastCarCoordinatorECUINOHEXFilePathVariableName = fastCarCoordinatorECUName + "INOHEXFilePath";
		String fastCarCoordinatorECUINOHEXFilePathVariableValue = deployableCodeFolderNameVariableValue + "/"
				+ fastCarCoordinatorECUName + "/CompiledFiles/" + fastCarCoordinatorECUName + ".ino.hex";

		String fastCarDriverECUName = "fastCarDriverECU";
		String fastCarDriverECUFolderPathVariableName = fastCarDriverECUName + "FolderPath";
		String fastCarDriverECUFolderPathVariableValue = deployableCodeFolderNameVariableValue + "/"
				+ fastCarDriverECUName;
		String fastCarDriverECUINOFilePathVariableName = fastCarDriverECUName + "INOFilePath";
		String fastCarDriverECUINOFilePathVariableValue = deployableCodeFolderNameVariableValue + "/"
				+ fastCarDriverECUName + "/" + fastCarDriverECUName + ".ino";
		String fastCarDriverECUINOHEXFilePathVariableName = fastCarDriverECUName + "INOHEXFilePath";
		String fastCarDriverECUINOHEXFilePathVariableValue = deployableCodeFolderNameVariableValue + "/"
				+ fastCarDriverECUName + "/CompiledFiles/" + fastCarDriverECUName + ".ino.hex";

		String slowCarCoordinatorECUName = "slowCarCoordinatorECU";
		String slowCarCoordinatorECUFolderPathVariableName = slowCarCoordinatorECUName + "FolderPath";
		String slowCarCoordinatorECUFolderPathVariableValue = deployableCodeFolderNameVariableValue + "/"
				+ slowCarCoordinatorECUName;
		String slowCarCoordinatorECUINOFilePathVariableName = slowCarCoordinatorECUName + "INOFilePath";
		String slowCarCoordinatorECUINOFilePathVariableValue = deployableCodeFolderNameVariableValue + "/"
				+ slowCarCoordinatorECUName + "/" + slowCarCoordinatorECUName + ".ino";
		String slowCarCoordinatorECUINOHEXFilePathVariableName = slowCarCoordinatorECUName + "INOHEXFilePath";
		String slowCarCoordinatorECUINOHEXFilePathVariableValue = deployableCodeFolderNameVariableValue + "/"
				+ slowCarCoordinatorECUName + "/CompiledFiles/" + slowCarCoordinatorECUName + ".ino.hex";

		String slowCarDriverECUName = "slowCarDriverECU";
		String slowCarDriverECUFolderPathVariableName = slowCarDriverECUName + "FolderPath";
		String slowCarDriverECUFolderPathVariableValue = deployableCodeFolderNameVariableValue + "/"
				+ slowCarDriverECUName;
		String slowCarDriverECUINOFilePathVariableName = slowCarDriverECUName + "INOFilePath";
		String slowCarDriverECUINOFilePathVariableValue = deployableCodeFolderNameVariableValue + "/"
				+ slowCarDriverECUName + "/" + slowCarDriverECUName + ".ino";
		String slowCarDriverECUINOHEXFilePathVariableName = slowCarDriverECUName + "INOHEXFilePath";
		String slowCarDriverECUINOHEXFilePathVariableValue = deployableCodeFolderNameVariableValue + "/"
				+ slowCarDriverECUName + "/CompiledFiles/" + slowCarDriverECUName + ".ino.hex";

		// Settings changes of the generated default/example settings here in
		// order to make the written entries and sequences more readable.

		Map<String, Map<String, String>> deleteDirectoryGeneratedFiles = generateDeleteFolder(
				fromKeyword + " " + generatedCodeFolderNameVariableName);
		Map<String, Map<String, String>> deleteDirectoryDeployableFiles = generateDeleteFolder(
				fromKeyword + " " + deployableCodeFolderNameVariableName);

		Map<String, Map<String, String>> containerTransformationSettings = ContainerTransformation
				.generateDefaultOrExampleValues();
		Map<String, String> containerTransformationSettingsIns = containerTransformationSettings.get(inKeyword);
		containerTransformationSettingsIns.put("muml_containerFileDestination",
				fromKeyword + " " + muml_containerFilePathVariableName);
		containerTransformationSettings.put(inKeyword, containerTransformationSettingsIns);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortContainerTransformation = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromKeyword + " ifSuccessful",
				directValueKeyword + " " + ContainerTransformation.nameFlag + " has failed!");

		Map<String, Map<String, String>> containerCodeGenerationSettings = ContainerCodeGeneration
				.generateDefaultOrExampleValues();
		Map<String, String> containerCodeGenerationSettingsIns = containerCodeGenerationSettings.get(inKeyword);
		containerCodeGenerationSettingsIns.put("muml_containerSourceFile",
				fromKeyword + " " + muml_containerFilePathVariableName);
		containerCodeGenerationSettingsIns.put("arduinoContainersDestinationFolder",
				fromKeyword + " " + generatedCodeFolderNameVariableName);
		containerCodeGenerationSettings.put(inKeyword, containerCodeGenerationSettingsIns);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortContainerCodeGeneration = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromKeyword + " ifSuccessful",
				directValueKeyword + " " + ContainerCodeGeneration.nameFlag + " has failed!");

		Map<String, Map<String, String>> componentCodeGenerationSettings = ComponentCodeGeneration
				.generateDefaultOrExampleValues();
		Map<String, String> componentCodeGenerationSettingsIns = componentCodeGenerationSettings.get(inKeyword);
		componentCodeGenerationSettingsIns.put("arduinoContainersDestinationFolder",
				fromKeyword + " " + generatedCodeFolderNameVariableName);
		componentCodeGenerationSettings.put(inKeyword, componentCodeGenerationSettingsIns);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortComponentCode = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromKeyword + " ifSuccessful",
				directValueKeyword + " " + ComponentCodeGeneration.nameFlag + " has failed!");

		Map<String, Map<String, String>> copyFolderSettings = CopyFolder.generateDefaultOrExampleValues();
		Map<String, String> copyFolderSettingsIns = copyFolderSettings.get(inKeyword);
		copyFolderSettingsIns.put("sourcePath", fromKeyword + " " + generatedCodeFolderNameVariableName);
		copyFolderSettingsIns.put("destinationPath", fromKeyword + " " + deployableCodeFolderNameVariableName);
		copyFolderSettings.put(inKeyword, copyFolderSettingsIns);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortCopyFolder = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromKeyword + " ifSuccessful", directValueKeyword + " " + CopyFolder.nameFlag + " has failed!");

		Map<String, Map<String, String>> postProcessingStepsUntilConfigSettings = PostProcessingStepsUntilConfig
				.generateDefaultOrExampleValues();
		Map<String, String> postProcessingStepsUntilConfigSettingsIns = postProcessingStepsUntilConfigSettings
				.get(inKeyword);
		postProcessingStepsUntilConfigSettingsIns.put("arduinoContainersPath",
				fromKeyword + " " + deployableCodeFolderNameVariableName);
		postProcessingStepsUntilConfigSettingsIns.put("componentCodePath",
				fromKeyword + " " + componentCodeFolderNameVariableName);
		postProcessingStepsUntilConfigSettings.put(inKeyword, postProcessingStepsUntilConfigSettingsIns);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortPostProcessingUntilConfig = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromKeyword + " ifSuccessful",
				directValueKeyword + " " + PostProcessingStepsUntilConfig.nameFlag + " has failed!");

		Map<String, Map<String, String>> postProcessingStateChartValuesFastDriver = generatePostProcessingStateChartValuesStepAndAdjustSettings(
				fromKeyword + " " + deployableCodeFolderNameVariableName,
				directValueKeyword + " " + fastCarDriverECUName,
				fromKeyword + " " + fastCarDesiredVelocityVariableName);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortPostProcessingStateChartValuesFastDriver = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromKeyword + " ifSuccessful", directValueKeyword + " " + PostProcessingStateChartValues.nameFlag
						+ " for " + fastCarDriverECUName + " has failed!");

		Map<String, Map<String, String>> postProcessingStateChartValuesSlowDriver = generatePostProcessingStateChartValuesStepAndAdjustSettings(
				fromKeyword + " " + deployableCodeFolderNameVariableName,
				directValueKeyword + " " + slowCarDriverECUName, 
				fromKeyword + " " + slowCarDesiredVelocityVariableName);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortPostProcessingStateChartValuesSlowDriver = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromKeyword + " ifSuccessful", directValueKeyword + " " + PostProcessingStateChartValues.nameFlag
						+ " for " + slowCarDriverECUName + " has failed!");

		Map<String, Map<String, String>> deleteFolderComponentCodeFolder = generateDeleteFolder(
				fromKeyword + " " + componentCodeFolderNameVariableName);
		Map<String, Map<String, String>> deleteFolderAPImappings = generateDeleteFolder(
				fromKeyword + " " + apiMappingsFolderNameVariableName);

		Map<String, Map<String, String>> compileSettingsFastCoordinator = generateCompileStepAndAdjustSettings(
				fromKeyword + " " + usedCoordinatorBoardIdentifierFQBNVariableName,
				fromKeyword + " " + fastCarCoordinatorECUINOFilePathVariableName);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortCompileFastCoordinator = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromKeyword + " ifSuccessful",
				directValueKeyword + " " + Compile.nameFlag + " for " + fastCarCoordinatorECUName + " has failed!");

		Map<String, Map<String, String>> compileSettingsFastDriver = generateCompileStepAndAdjustSettings(
				fromKeyword + " " + usedDriverBoardIdentifierFQBNVariableName,
				fromKeyword + " " + fastCarDriverECUINOFilePathVariableName);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortCompileFastDriver = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromKeyword + " ifSuccessful",
				directValueKeyword + " " + Compile.nameFlag + " for " + fastCarDriverECUName + " has failed!");

		Map<String, Map<String, String>> compileSettingsSlowCoordinator = generateCompileStepAndAdjustSettings(
				fromKeyword + " " + usedCoordinatorBoardIdentifierFQBNVariableName,
				fromKeyword + " " + slowCarCoordinatorECUINOFilePathVariableName);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortCompileSlowCoordinator = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromKeyword + " ifSuccessful",
				directValueKeyword + " " + Compile.nameFlag + " for " + slowCarCoordinatorECUName + " has failed!");

		Map<String, Map<String, String>> compileSettingsSlowDriver = generateCompileStepAndAdjustSettings(
				fromKeyword + " " + usedDriverBoardIdentifierFQBNVariableName,
				fromKeyword + " " + slowCarDriverECUINOFilePathVariableName);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortCompileSlowDriver = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromKeyword + " ifSuccessful",
				directValueKeyword + " " + Compile.nameFlag + " for " + slowCarDriverECUName + " has failed!");

		Map<String, Map<String, String>> lookupBoardBySerialNumberStepAndAdjustSettingsFastCoordinator = generateLookupBoardBySerialNumberStepAndAdjustSettings(
				fromKeyword + " " + fastCarCoordinatorECUBoardSerialNumberVariableName, 
				fromKeyword + " " + usedCoordinatorBoardIdentifierFQBNVariableName);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortLookupBoardBySerialNumberStepAndAdjustSettingsFastCoordinator = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromKeyword + " ifSuccessful", directValueKeyword + " " + LookupBoardBySerialNumber.nameFlag + " for "
						+ fastCarCoordinatorECUName + " has failed!");
		Map<String, Map<String, String>> uploadSettingsFastCoordinator = generateUploadStepAndAdjustSettings(
				fromKeyword + " foundPortAddress", fromKeyword + " usedCoordinatorBoardIdentifierFQBN",
				fromKeyword + " "+ fastCarCoordinatorECUINOHEXFilePathVariableName);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortUploadFastCoordinator = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromKeyword + " ifSuccessful",
				directValueKeyword + " " + Upload.nameFlag + " for " + fastCarCoordinatorECUName + " has failed!");

		Map<String, Map<String, String>> lookupBoardBySerialNumberStepAndAdjustSettingsFastDriver = generateLookupBoardBySerialNumberStepAndAdjustSettings(
				fromKeyword + " " + fastCarDriverECUBoardSerialNumberVariableName, 
				fromKeyword + " " + usedDriverBoardIdentifierFQBNVariableName);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortLookupBoardBySerialNumberStepAndAdjustSettingsFastDriver = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromKeyword + " ifSuccessful", directValueKeyword + " " + LookupBoardBySerialNumber.nameFlag + " for "
						+ fastCarDriverECUName + " has failed!");
		Map<String, Map<String, String>> uploadSettingsFastDriver = generateUploadStepAndAdjustSettings(
				fromKeyword + " foundPortAddress", fromKeyword + " " + usedDriverBoardIdentifierFQBNVariableName,
				fromKeyword + " " + fastCarDriverECUINOHEXFilePathVariableName);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortUploadFastDriver = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromKeyword + " ifSuccessful",
				directValueKeyword + " " + Upload.nameFlag + " for " + fastCarDriverECUName + " has failed!");

		Map<String, Map<String, String>> lookupBoardBySerialNumberStepAndAdjustSettingsSlowCoordinator = generateLookupBoardBySerialNumberStepAndAdjustSettings(
				fromKeyword + " " + slowCarCoordinatorECUBoardSerialNumberVariableName, 
				fromKeyword + " " + usedCoordinatorBoardIdentifierFQBNVariableName);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortLookupBoardBySerialNumberStepAndAdjustSettingsSlowCoordinator = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromKeyword + " ifSuccessful", directValueKeyword + " " + LookupBoardBySerialNumber.nameFlag + " for "
						+ slowCarCoordinatorECUName + " has failed!");
		Map<String, Map<String, String>> uploadSettingsSlowCoordinator = generateUploadStepAndAdjustSettings(
				fromKeyword + " foundPortAddress", fromKeyword + " usedCoordinatorBoardIdentifierFQBN",
				fromKeyword + " " + slowCarCoordinatorECUINOHEXFilePathVariableName);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortUploadSlowCoordinator = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromKeyword + " ifSuccessful",
				directValueKeyword + " " + Upload.nameFlag + " for " + slowCarCoordinatorECUName + " has failed!");

		Map<String, Map<String, String>> lookupBoardBySerialNumberStepAndAdjustSettingsSlowDriver = generateLookupBoardBySerialNumberStepAndAdjustSettings(
				fromKeyword + " " + slowCarDriverECUBoardSerialNumberVariableName, 
				fromKeyword + " " + usedDriverBoardIdentifierFQBNVariableName);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortLookupBoardBySerialNumberStepAndAdjustSettingsSlowDriver = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromKeyword + " ifSuccessful", directValueKeyword + " " + LookupBoardBySerialNumber.nameFlag + " for "
						+ slowCarDriverECUName + " has failed!");
		Map<String, Map<String, String>> uploadSettingsSlowDriver = generateUploadStepAndAdjustSettings(
				fromKeyword + " foundPortAddress", fromKeyword + " " + usedDriverBoardIdentifierFQBNVariableName,
				fromKeyword + " " + slowCarDriverECUINOHEXFilePathVariableName);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortUploadSlowDriver = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromKeyword + " ifSuccessful",
				directValueKeyword + " " + Upload.nameFlag + " for " + slowCarDriverECUName + " has failed!");

		Map<String, Map<String, String>> popupWindowMessageFinishedPipeline = PopupWindowMessage
				.generateDefaultOrExampleValues();
		Map<String, String> popupWindowMessageFinishedPipelineSettingsIns = popupWindowMessageFinishedPipeline
				.get(inKeyword);
		popupWindowMessageFinishedPipelineSettingsIns.put("message",
				directValueKeyword + " Pipeline execution completed!");
		popupWindowMessageFinishedPipeline.put(inKeyword, popupWindowMessageFinishedPipelineSettingsIns);

		// Now the pipeline settings and sequences:
		Map<String, Object> mapForPipelineSettings = new LinkedHashMap<String, Object>();

		// Use default values and generate the config file with default values.
		Map<String, String> exampleVariableDefs = new LinkedHashMap<String, String>();
		exampleVariableDefs.put("ExampleNumberVariableName", directValueKeyword + " 12");
		exampleVariableDefs.put("ExampleStringVariableName", directValueKeyword + " ExampleString");
		exampleVariableDefs.put("ExampleBooleanVariableName", directValueKeyword + " true");

		exampleVariableDefs.put(generatedCodeFolderNameVariableName, generatedCodeFolderNameVariableValue);
		exampleVariableDefs.put(muml_containerFilePathVariableName, muml_containerFilePathVariableValue);
		exampleVariableDefs.put(componentCodeFolderNameVariableName, componentCodeFolderNameVariableValue);
		exampleVariableDefs.put(apiMappingsFolderNameVariableName, apiMappingsFolderNameVariableValue);
		exampleVariableDefs.put(deployableCodeFolderNameVariableName, deployableCodeFolderNameVariableValue);

		exampleVariableDefs.put(fastCarDesiredVelocityVariableName, fastCarDesiredVelocityVariableValue);
		exampleVariableDefs.put(slowCarDesiredVelocityVariableName, slowCarDesiredVelocityVariableValue);
		
		exampleVariableDefs.put(usedDriverBoardIdentifierFQBNVariableName, usedDriverBoardIdentifierFQBNVariableValue);
		exampleVariableDefs.put(usedCoordinatorBoardIdentifierFQBNVariableName, usedCoordinatorBoardIdentifierFQBNVariableValue);

		exampleVariableDefs.put(fastCarCoordinatorECUBoardSerialNumberVariableName, fastCarCoordinatorECUBoardSerialNumberVariableValue);
		exampleVariableDefs.put(fastCarDriverECUBoardSerialNumberVariableName, fastCarDriverECUBoardSerialNumberVariableValue);
		exampleVariableDefs.put(slowCarCoordinatorECUBoardSerialNumberVariableName, slowCarCoordinatorECUBoardSerialNumberVariableValue);
		exampleVariableDefs.put(slowCarDriverECUBoardSerialNumberVariableName, slowCarDriverECUBoardSerialNumberVariableValue);
		
		exampleVariableDefs.put(fastCarCoordinatorECUFolderPathVariableName, fastCarCoordinatorECUFolderPathVariableValue);
		exampleVariableDefs.put(fastCarCoordinatorECUINOFilePathVariableName, fastCarCoordinatorECUINOFilePathVariableValue);
		exampleVariableDefs.put(fastCarCoordinatorECUINOHEXFilePathVariableName, fastCarCoordinatorECUINOHEXFilePathVariableValue);

		exampleVariableDefs.put(fastCarDriverECUFolderPathVariableName, fastCarDriverECUFolderPathVariableValue);
		exampleVariableDefs.put(fastCarDriverECUINOFilePathVariableName, fastCarDriverECUINOFilePathVariableValue);
		exampleVariableDefs.put(fastCarDriverECUINOHEXFilePathVariableName, fastCarDriverECUINOHEXFilePathVariableValue);

		exampleVariableDefs.put(slowCarCoordinatorECUFolderPathVariableName, slowCarCoordinatorECUFolderPathVariableValue);
		exampleVariableDefs.put(slowCarCoordinatorECUINOFilePathVariableName, slowCarCoordinatorECUINOFilePathVariableValue);
		exampleVariableDefs.put(slowCarCoordinatorECUINOHEXFilePathVariableName, slowCarCoordinatorECUINOHEXFilePathVariableValue);

		exampleVariableDefs.put(slowCarDriverECUFolderPathVariableName, slowCarDriverECUFolderPathVariableValue);
		exampleVariableDefs.put(slowCarDriverECUINOFilePathVariableName, slowCarDriverECUINOFilePathVariableValue);
		exampleVariableDefs.put(slowCarDriverECUINOHEXFilePathVariableName, slowCarDriverECUINOHEXFilePathVariableValue);

		mapForPipelineSettings.put(variableDefsKeyword, exampleVariableDefs);

		// For better/easier readability blank lines inbetween:
		settingsWriter.write(yaml.dump(mapForPipelineSettings));
		mapForPipelineSettings.clear();
		settingsWriter.write("\n\n");

		// StandaloneTransformationAndGenerationsDefs
		Map<String, Map<String, Map<String, String>>> defaultStandaloneTransformationAndGenerationsDefsKeyword = new LinkedHashMap<String, Map<String, Map<String, String>>>();
		defaultStandaloneTransformationAndGenerationsDefsKeyword.put(ContainerTransformation.nameFlag,
				containerTransformationSettings);
		defaultStandaloneTransformationAndGenerationsDefsKeyword.put(ContainerCodeGeneration.nameFlag,
				containerCodeGenerationSettings);
		defaultStandaloneTransformationAndGenerationsDefsKeyword.put(ComponentCodeGeneration.nameFlag,
				componentCodeGenerationSettings);
		defaultStandaloneTransformationAndGenerationsDefsKeyword.put(PostProcessingStepsUntilConfig.nameFlag,
				PostProcessingStepsUntilConfig.generateDefaultOrExampleValues());
		mapForPipelineSettings.put(standaloneTransformationAndCodeGenerationsDefsKeyword,
				defaultStandaloneTransformationAndGenerationsDefsKeyword);

		// For better/easier readability blank lines inbetween:
		settingsWriter.write(yaml.dump(mapForPipelineSettings));
		mapForPipelineSettings.clear();
		settingsWriter.write("\n\n");

		ArrayList<Object> defaultPostProcessingSequenceDefs = new ArrayList<Object>();

		defaultPostProcessingSequenceDefs.add(pipelineSegmentHelper(yaml,
				directValueKeyword + " " + DeleteFolder.nameFlag, deleteDirectoryDeployableFiles));

		defaultPostProcessingSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + CopyFolder.nameFlag, copyFolderSettings));
		defaultPostProcessingSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortCopyFolder));

		defaultPostProcessingSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingStepsUntilConfig.nameFlag,
						postProcessingStepsUntilConfigSettings));
		defaultPostProcessingSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortPostProcessingUntilConfig));

		defaultPostProcessingSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingStateChartValues.nameFlag,
						postProcessingStateChartValuesFastDriver));
		defaultPostProcessingSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortPostProcessingStateChartValuesFastDriver));

		defaultPostProcessingSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingStateChartValues.nameFlag,
						postProcessingStateChartValuesSlowDriver));
		defaultPostProcessingSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortPostProcessingStateChartValuesSlowDriver));

		defaultPostProcessingSequenceDefs.add(
				pipelineSegmentHelper(yaml, directValueKeyword + " " + DeleteFolder.nameFlag, deleteFolderAPImappings));
		defaultPostProcessingSequenceDefs.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + DeleteFolder.nameFlag,
				deleteFolderComponentCodeFolder));

		mapForPipelineSettings.put(standalonePostProcessingSequenceDefKeyword, defaultPostProcessingSequenceDefs);

		// For better/easier readability blank lines inbetween:
		settingsWriter.write(yaml.dump(mapForPipelineSettings));
		mapForPipelineSettings.clear();
		settingsWriter.write("\n\n");

		ArrayList<Object> defaultPipelineSequenceDefs = new ArrayList<Object>();

		defaultPipelineSequenceDefs.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + DeleteFolder.nameFlag,
				deleteDirectoryGeneratedFiles));
		defaultPipelineSequenceDefs.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + DeleteFolder.nameFlag,
				deleteDirectoryDeployableFiles));

		defaultPipelineSequenceDefs.add(fromKeyword + " " + standaloneTransformationAndCodeGenerationsDefsKeyword + ": "
				+ ContainerTransformation.nameFlag);
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortContainerTransformation));

		defaultPipelineSequenceDefs.add(fromKeyword + " " + standaloneTransformationAndCodeGenerationsDefsKeyword + ": "
				+ ContainerCodeGeneration.nameFlag);
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortContainerCodeGeneration));

		defaultPipelineSequenceDefs.add(fromKeyword + " " + standaloneTransformationAndCodeGenerationsDefsKeyword + ": "
				+ ComponentCodeGeneration.nameFlag);
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortComponentCode));

		defaultPipelineSequenceDefs.add(
				fromKeyword + " " + standalonePostProcessingSequenceDefKeyword + ": " + allKeyword);

		defaultPipelineSequenceDefs.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + Compile.nameFlag,
				compileSettingsFastCoordinator));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortCompileFastCoordinator));
		defaultPipelineSequenceDefs.add(
				pipelineSegmentHelper(yaml, directValueKeyword + " " + Compile.nameFlag, compileSettingsFastDriver));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortCompileFastDriver));
		defaultPipelineSequenceDefs.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + Compile.nameFlag,
				compileSettingsSlowCoordinator));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortCompileSlowCoordinator));
		defaultPipelineSequenceDefs.add(
				pipelineSegmentHelper(yaml, directValueKeyword + " " + Compile.nameFlag, compileSettingsSlowDriver));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortCompileSlowDriver));

		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + LookupBoardBySerialNumber.nameFlag,
						lookupBoardBySerialNumberStepAndAdjustSettingsFastCoordinator));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortLookupBoardBySerialNumberStepAndAdjustSettingsFastCoordinator));
		defaultPipelineSequenceDefs.add(
				pipelineSegmentHelper(yaml, directValueKeyword + " " + Upload.nameFlag, uploadSettingsFastCoordinator));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortUploadFastCoordinator));

		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + LookupBoardBySerialNumber.nameFlag,
						lookupBoardBySerialNumberStepAndAdjustSettingsFastDriver));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortLookupBoardBySerialNumberStepAndAdjustSettingsFastDriver));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + Upload.nameFlag, uploadSettingsFastDriver));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortUploadFastDriver));

		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + LookupBoardBySerialNumber.nameFlag,
						lookupBoardBySerialNumberStepAndAdjustSettingsSlowCoordinator));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortLookupBoardBySerialNumberStepAndAdjustSettingsSlowCoordinator));
		defaultPipelineSequenceDefs.add(
				pipelineSegmentHelper(yaml, directValueKeyword + " " + Upload.nameFlag, uploadSettingsSlowCoordinator));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortUploadSlowCoordinator));

		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + LookupBoardBySerialNumber.nameFlag,
						lookupBoardBySerialNumberStepAndAdjustSettingsSlowDriver));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortLookupBoardBySerialNumberStepAndAdjustSettingsSlowDriver));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + Upload.nameFlag, uploadSettingsSlowDriver));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortUploadSlowDriver));

		defaultPipelineSequenceDefs.add(pipelineSegmentHelper(yaml,
				directValueKeyword + " " + PopupWindowMessage.nameFlag, popupWindowMessageFinishedPipeline));

		mapForPipelineSettings.put(pipelineSequenceDefKeyword, defaultPipelineSequenceDefs);

		settingsWriter.write(yaml.dump(mapForPipelineSettings));
		settingsWriter.close();

		// Cleanup of unwanted additions: ' chars around from entries in the
		// pipeline sequence and after the "in:" flags the &id...s and *id...s.
		String inSequenceForCleanup = " " + inKeyword + ":";

		String intermediateFileName = completeMUMLACGPPASettingsFilePath.toString() + ".editing";
		FileWriter workCopy = new FileWriter(intermediateFileName);
		Scanner currentFileReader = new Scanner(completeMUMLACGPPASettingsFilePath.toFile());
		while (currentFileReader.hasNextLine()) {
			String currentLine = currentFileReader.nextLine();
			if (currentLine.contains("'")) {
				String intermediate = currentLine.replace("'", "");
				workCopy.write(intermediate + "\n");
			} else if (currentLine.contains("'")) {
				String intermediate = currentLine.replace("'", "");
				workCopy.write(intermediate + "\n");
			} else if (currentLine.contains(" " + inKeyword + ":")) {
				int index = currentLine.indexOf(inSequenceForCleanup);
				String intermediate = currentLine.substring(0, index + inSequenceForCleanup.length());
				workCopy.write(intermediate + "\n");
			} else {
				workCopy.write(currentLine + "\n");
			}
		}
		currentFileReader.close();
		workCopy.close();
		Files.move(Paths.get(intermediateFileName), completeMUMLACGPPASettingsFilePath,
				StandardCopyOption.REPLACE_EXISTING);

		return true;
	}

}
