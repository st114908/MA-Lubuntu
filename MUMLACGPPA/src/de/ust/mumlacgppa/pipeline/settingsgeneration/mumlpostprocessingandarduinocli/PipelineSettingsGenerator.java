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

import de.ust.mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ParameterMismatchException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StepNotMatched;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StructureException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.steps.Keywords;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.Compile;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ComponentCodeGeneration;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ContainerCodeGeneration;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ContainerTransformation;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.CopyFolder;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.DeleteFolder;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.LookupBoardBySerialNumber;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.OnlyContinueIfFulfilledElseAbort;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PopupWindowMessage;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingStateChartValues;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingStepsUntilConfig;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.Upload;
import de.ust.mumlacgppa.pipeline.paths.PipelineSettingsDirectoryAndFilePaths;
import de.ust.mumlacgppa.pipeline.reader.PipelineSettingsReader;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

public class PipelineSettingsGenerator implements PipelineSettingsDirectoryAndFilePaths, Keywords {
	private Path completeSettingsDirectoryPath;
	private Path completeSettingsFilePath;

	public PipelineSettingsGenerator() throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super();
		if (ProjectFolderPathStorage.projectFolderPath == null) {
			throw new ProjectFolderPathNotSetExceptionMUMLACGPPA(
					"The static field projectFolderPath in ProjectFolderPathStorage has "
							+ "to be set to a complete file system path to the project's folder!");
		}
		completeSettingsDirectoryPath = ProjectFolderPathStorage.projectFolderPath
				.resolve(PIPELINE_SETTINGS_DIRECTORY_FOLDER);
		completeSettingsFilePath = completeSettingsDirectoryPath.resolve(PIPELINE_SETTINGS_FILE_NAME);
	}

	public Path getCompleteSettingsFilePath() {
		return completeSettingsFilePath;
	}

	private Map<String, Map<String, String>> generateDeleteFolder(String path) {
		Map<String, Map<String, String>> deleteDirectoryArduinoContainers = DeleteFolder
				.generateDefaultOrExampleValues();
		Map<String, String> deleteDirectoryArduinoContainersSettingsIns = deleteDirectoryArduinoContainers.get(inKeyword);
		deleteDirectoryArduinoContainersSettingsIns.put("path", path);
		deleteDirectoryArduinoContainers.put(inKeyword, deleteDirectoryArduinoContainersSettingsIns);
		return deleteDirectoryArduinoContainers;
	}

	private Map<String, Map<String, String>> generatePostProcessingStateChartValuesStepAndAdjustSettings(
			String arduinoContainersPath, String ECUName) {
		Map<String, Map<String, String>> postProcessingStateChartValuesSettings = PostProcessingStateChartValues
				.generateDefaultOrExampleValues();
		Map<String, String> postProcessingStateChartValuesSettingsIns = postProcessingStateChartValuesSettings
				.get(inKeyword);
		postProcessingStateChartValuesSettingsIns.put("arduinoContainersPath", arduinoContainersPath);
		postProcessingStateChartValuesSettingsIns.put("ECUName", ECUName);
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
			String boardSerialNumber) {
		Map<String, Map<String, String>> lookupBoardBySerialNumberSettings = LookupBoardBySerialNumber
				.generateDefaultOrExampleValues();
		Map<String, String> lookupBoardBySerialNumberSettingsIns = lookupBoardBySerialNumberSettings.get(inKeyword);
		lookupBoardBySerialNumberSettingsIns.put("boardSerialNumber", boardSerialNumber);
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
		File directoryCheck = completeSettingsDirectoryPath.toFile();
		if (!directoryCheck.exists()) {
			directoryCheck.mkdirs();
		}

		File configExistenceCheck = completeSettingsFilePath.toFile();
		if (configExistenceCheck.exists() && !configExistenceCheck.isDirectory()) {
			return false;
		}

		// For DumperOptions examples see
		// https://www.tabnine.com/code/java/methods/org.yaml.snakeyaml.DumperOptions$LineBreak/getPlatformLineBreak
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		options.setPrettyFlow(true);
		options.setLineBreak(DumperOptions.LineBreak.getPlatformLineBreak());
		Yaml yaml = new Yaml(options);
		FileWriter settingsWriter = new FileWriter(completeSettingsFilePath.toFile());

		// Variable names and potentially their values:

		String generatedCodeFolderNameVariableName = "generatedRawFilesFolderPath";
		String generatedCodeFolderNameVariableValue = directValueKeyword + " generated-files";
		String muml_containerFilePathVariableName = "muml_containerFilePath";
		String muml_containerFilePathVariableValue = generatedCodeFolderNameVariableValue + "/MUML_Container.muml_container";
		String deployableCodeFolderNameVariableName = "deployableFilesFolderPath";
		String deployableCodeFolderNameVariableValue = directValueKeyword + " deployable-files";
		String componentCodeFolderNameVariableName = "componentCodeFilesFolderPath";
		String componentCodeFolderNameVariableValue = deployableCodeFolderNameVariableValue + "/fastAndSlowCar_v2";
		String apiMappingsFolderNameVariableName = "apiMappingsFolderPath";
		String apiMappingsFolderNameVariableValue = deployableCodeFolderNameVariableValue + "/APImappings";

		String usedDriverBoardIdentifierFQBNVariableName = "usedDriverBoardIdentifierFQBN";
		String usedDriverBoardIdentifierFQBNVariableValue = directValueKeyword + " arduino:avr:mega";

		String usedCoordinatorBoardIdentifierFQBNVariableName = "usedCoordinatorBoardIdentifierFQBN";
		String usedCoordinatorBoardIdentifierFQBNVariableValue = directValueKeyword + " arduino:avr:nano";

		String fastCarCoordinatorECUName = "fastCarCoordinatorECU";
		String fastCarCoordinatorECUFolderPathVariableName = fastCarCoordinatorECUName + "FolderPath";
		String fastCarCoordinatorECUFolderPathVariableValue = deployableCodeFolderNameVariableValue + "/"
				+ fastCarCoordinatorECUName;
		String fastCarCoordinatorECUINOFilePathVariableName = fastCarCoordinatorECUName + "INOFilePath";
		String fastCarCoordinatorECUINOFilePathVariableValue = deployableCodeFolderNameVariableValue + "/"
				+ fastCarCoordinatorECUName + "/" + fastCarCoordinatorECUName + ".ino";
		String fastCarCoordinatorECUBoardSerialNumberVariableName = "fastCarCoordinatorECUBoardSerialNumber";
		String fastCarCoordinatorECUBoardSerialNumberVariableValue = directValueKeyword + " DummySerialFastCarCoordinator";

		String fastCarDriverECUName = "fastCarDriverECU";
		String fastCarDriverECUFolderPathVariableName = fastCarDriverECUName + "FolderPath";
		String fastCarDriverECUFolderPathVariableValue = deployableCodeFolderNameVariableValue + "/"
				+ fastCarDriverECUName;
		String fastCarDriverECUINOFilePathVariableName = fastCarDriverECUName + "INOFilePath";
		String fastCarDriverECUINOFilePathVariableValue = deployableCodeFolderNameVariableValue + "/"
				+ fastCarDriverECUName + "/" + fastCarDriverECUName + ".ino";
		String fastCarDriverECUBoardSerialNumberVariableName = "fastCarDriverECUBoardSerialNumber";
		String fastCarDriverECUBoardSerialNumberVariableValue = directValueKeyword + " DummySerialFastCarDriver";

		String slowCarCoordinatorECUName = "slowCarCoordinatorECU";
		String slowCarCoordinatorECUFolderPathVariableName = slowCarCoordinatorECUName + "FolderPath";
		String slowCarCoordinatorECUFolderPathVariableValue = deployableCodeFolderNameVariableValue + "/"
				+ slowCarCoordinatorECUName;
		String slowCarCoordinatorECUINOFilePathVariableName = slowCarCoordinatorECUName + "INOFilePath";
		String slowCarCoordinatorECUINOFilePathVariableValue = deployableCodeFolderNameVariableValue + "/"
				+ slowCarCoordinatorECUName + "/" + slowCarCoordinatorECUName + ".ino";
		String slowCarCoordinatorECUBoardSerialNumberVariableName = "slowCarCoordinatorECUBoardSerialNumber";
		String slowCarCoordinatorECUBoardSerialNumberVariableValue = directValueKeyword + " DummySerialSlowCarCoordinator";

		String slowCarDriverECUName = "slowCarDriverECU";
		String slowCarDriverECUFolderPathVariableName = slowCarDriverECUName + "FolderPath";
		String slowCarDriverECUFolderPathVariableValue = deployableCodeFolderNameVariableValue + "/"
				+ slowCarDriverECUName;
		String slowCarDriverECUINOFilePathVariableName = slowCarDriverECUName + "INOFilePath";
		String slowCarDriverECUINOFilePathVariableValue = deployableCodeFolderNameVariableValue + "/"
				+ slowCarDriverECUName + "/" + slowCarDriverECUName + ".ino";
		String slowCarDriverECUBoardSerialNumberVariableName = "slowCarDriverECUBoardSerialNumber";
		String slowCarDriverECUBoardSerialNumberVariableValue = directValueKeyword + " DummySerialSlowCarDriver";

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
				fromKeyword + " ifSuccessful",
				directValueKeyword + " " + CopyFolder.nameFlag + " has failed!");

		Map<String, Map<String, String>> postProcessingStepsUntilConfigSettings = PostProcessingStepsUntilConfig
				.generateDefaultOrExampleValues();
		Map<String, String> postProcessingStepsUntilConfigSettingsIns = postProcessingStepsUntilConfigSettings
				.get(inKeyword);
		postProcessingStepsUntilConfigSettingsIns.put("arduinoContainersPath",
				fromKeyword + " " + deployableCodeFolderNameVariableName);
		postProcessingStepsUntilConfigSettingsIns.put("componentCodePath",
				fromKeyword + " " + componentCodeFolderNameVariableName );
		postProcessingStepsUntilConfigSettings.put(inKeyword, postProcessingStepsUntilConfigSettingsIns);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortPostProcessingUntilConfig = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromKeyword + " ifSuccessful",
				directValueKeyword + " " + PostProcessingStepsUntilConfig.nameFlag + " has failed!");

		Map<String, Map<String, String>> postProcessingStateChartValuesFastDriver = generatePostProcessingStateChartValuesStepAndAdjustSettings(
				fromKeyword + " " + deployableCodeFolderNameVariableName,
				directValueKeyword + " " + fastCarDriverECUName);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortPostProcessingStateChartValuesFastDriver = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromKeyword + " ifSuccessful", directValueKeyword + " " + PostProcessingStateChartValues.nameFlag
						+ " for " + fastCarDriverECUName + " has failed!");

		Map<String, Map<String, String>> postProcessingStateChartValuesSlowDriver = generatePostProcessingStateChartValuesStepAndAdjustSettings(
				fromKeyword + " " + deployableCodeFolderNameVariableName,
				directValueKeyword + " " + slowCarDriverECUName);
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
				fromKeyword + " " + fastCarCoordinatorECUBoardSerialNumberVariableName);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortLookupBoardBySerialNumberStepAndAdjustSettingsFastCoordinator = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromKeyword + " ifSuccessful", directValueKeyword + " " + LookupBoardBySerialNumber.nameFlag + " for "
						+ fastCarCoordinatorECUName + " has failed!");
		Map<String, Map<String, String>> uploadSettingsFastCoordinator = generateUploadStepAndAdjustSettings(
				fromKeyword + " foundPortAddress", fromKeyword + " usedCoordinatorBoardIdentifierFQBN",
				fromKeyword + " fastCarCoordinatorECUINOFilePath");
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortUploadFastCoordinator = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromKeyword + " ifSuccessful",
				directValueKeyword + " " + Upload.nameFlag + " for " + fastCarCoordinatorECUName + " has failed!");

		Map<String, Map<String, String>> lookupBoardBySerialNumberStepAndAdjustSettingsFastDriver = generateLookupBoardBySerialNumberStepAndAdjustSettings(
				fromKeyword + " fastCarDriverECUBoardSerialNumber");
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortLookupBoardBySerialNumberStepAndAdjustSettingsFastDriver = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromKeyword + " ifSuccessful", directValueKeyword + " " + LookupBoardBySerialNumber.nameFlag + " for "
						+ fastCarDriverECUName + " has failed!");
		Map<String, Map<String, String>> uploadSettingsFastDriver = generateUploadStepAndAdjustSettings(
				fromKeyword + " foundPortAddress", fromKeyword + " " + usedDriverBoardIdentifierFQBNVariableName,
				fromKeyword + " " + fastCarDriverECUINOFilePathVariableName);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortUploadFastDriver = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromKeyword + " ifSuccessful",
				directValueKeyword + " " + Upload.nameFlag + " for " + fastCarDriverECUName + " has failed!");

		Map<String, Map<String, String>> lookupBoardBySerialNumberStepAndAdjustSettingsSlowCoordinator = generateLookupBoardBySerialNumberStepAndAdjustSettings(
				fromKeyword + " " + slowCarCoordinatorECUBoardSerialNumberVariableName);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortLookupBoardBySerialNumberStepAndAdjustSettingsSlowCoordinator = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromKeyword + " ifSuccessful", directValueKeyword + " " + LookupBoardBySerialNumber.nameFlag + " for "
						+ slowCarCoordinatorECUName + " has failed!");
		Map<String, Map<String, String>> uploadSettingsSlowCoordinator = generateUploadStepAndAdjustSettings(
				fromKeyword + " foundPortAddress", fromKeyword + " usedCoordinatorBoardIdentifierFQBN",
				fromKeyword + " " + slowCarCoordinatorECUINOFilePathVariableName);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortUploadSlowCoordinator = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromKeyword + " ifSuccessful",
				directValueKeyword + " " + Upload.nameFlag + " for " + slowCarCoordinatorECUName + " has failed!");

		Map<String, Map<String, String>> lookupBoardBySerialNumberStepAndAdjustSettingsSlowDriver = generateLookupBoardBySerialNumberStepAndAdjustSettings(
				fromKeyword + " " + slowCarDriverECUBoardSerialNumberVariableName);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortLookupBoardBySerialNumberStepAndAdjustSettingsSlowDriver = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromKeyword + " ifSuccessful", directValueKeyword + " " + LookupBoardBySerialNumber.nameFlag + " for "
						+ slowCarDriverECUName + " has failed!");
		Map<String, Map<String, String>> uploadSettingsSlowDriver = generateUploadStepAndAdjustSettings(
				fromKeyword + " foundPortAddress", fromKeyword + " " + usedDriverBoardIdentifierFQBNVariableName,
				fromKeyword + " " + slowCarDriverECUINOFilePathVariableName);
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

		// Now the pipeline settings and sequence itself.
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

		exampleVariableDefs.put(usedDriverBoardIdentifierFQBNVariableName, usedDriverBoardIdentifierFQBNVariableValue);
		exampleVariableDefs.put(usedCoordinatorBoardIdentifierFQBNVariableName,
				usedCoordinatorBoardIdentifierFQBNVariableValue);

		exampleVariableDefs.put(fastCarCoordinatorECUFolderPathVariableName,
				fastCarCoordinatorECUFolderPathVariableValue);
		exampleVariableDefs.put(fastCarCoordinatorECUINOFilePathVariableName,
				fastCarCoordinatorECUINOFilePathVariableValue);
		exampleVariableDefs.put(fastCarCoordinatorECUBoardSerialNumberVariableName,
				fastCarCoordinatorECUBoardSerialNumberVariableValue);

		exampleVariableDefs.put(fastCarDriverECUFolderPathVariableName, fastCarDriverECUFolderPathVariableValue);
		exampleVariableDefs.put(fastCarDriverECUINOFilePathVariableName, fastCarDriverECUINOFilePathVariableValue);
		exampleVariableDefs.put(fastCarDriverECUBoardSerialNumberVariableName,
				fastCarDriverECUBoardSerialNumberVariableValue);

		exampleVariableDefs.put(slowCarCoordinatorECUFolderPathVariableName,
				slowCarCoordinatorECUFolderPathVariableValue);
		exampleVariableDefs.put(slowCarCoordinatorECUINOFilePathVariableName,
				slowCarCoordinatorECUINOFilePathVariableValue);
		exampleVariableDefs.put(slowCarCoordinatorECUBoardSerialNumberVariableName,
				slowCarCoordinatorECUBoardSerialNumberVariableValue);

		exampleVariableDefs.put(slowCarDriverECUFolderPathVariableName, slowCarDriverECUFolderPathVariableValue);
		exampleVariableDefs.put(slowCarDriverECUINOFilePathVariableName, slowCarDriverECUINOFilePathVariableValue);
		exampleVariableDefs.put(slowCarDriverECUBoardSerialNumberVariableName,
				slowCarDriverECUBoardSerialNumberVariableValue);

		mapForPipelineSettings.put(variableDefsKeyword, exampleVariableDefs);

		// For better/easier readability blank lines inbetween:
		settingsWriter.write(yaml.dump(mapForPipelineSettings));
		mapForPipelineSettings.clear();
		settingsWriter.write("\n\n");

		Map<String, Map<String, Map<String, String>>> defaultStandaloneUsageDefs = new LinkedHashMap<String, Map<String, Map<String, String>>>();
		defaultStandaloneUsageDefs.put(ContainerTransformation.nameFlag,
				ContainerTransformation.generateDefaultOrExampleValues());
		defaultStandaloneUsageDefs.put(ContainerCodeGeneration.nameFlag,
				ContainerCodeGeneration.generateDefaultOrExampleValues());
		defaultStandaloneUsageDefs.put(ComponentCodeGeneration.nameFlag,
				ComponentCodeGeneration.generateDefaultOrExampleValues());
		defaultStandaloneUsageDefs.put(PostProcessingStepsUntilConfig.nameFlag,
				PostProcessingStepsUntilConfig.generateDefaultOrExampleValues());
		mapForPipelineSettings.put(standaloneUsageDefsKeyword, defaultStandaloneUsageDefs);

		// For better/easier readability blank lines inbetween:
		settingsWriter.write(yaml.dump(mapForPipelineSettings));
		mapForPipelineSettings.clear();
		settingsWriter.write("\n\n");

		ArrayList<Object> defaultPipelineSequenceDefs = new ArrayList<Object>();

		defaultPipelineSequenceDefs.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + DeleteFolder.nameFlag,
				deleteDirectoryDeployableFiles));
		defaultPipelineSequenceDefs.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + DeleteFolder.nameFlag,
				deleteDirectoryGeneratedFiles));

		defaultPipelineSequenceDefs.add(pipelineSegmentHelper(yaml,
				directValueKeyword + " " + ContainerTransformation.nameFlag, containerTransformationSettings));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortContainerTransformation));

		defaultPipelineSequenceDefs.add(pipelineSegmentHelper(yaml,
				directValueKeyword + " " + ContainerCodeGeneration.nameFlag, containerCodeGenerationSettings));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortContainerCodeGeneration));

		defaultPipelineSequenceDefs.add(pipelineSegmentHelper(yaml,
				directValueKeyword + " " + ComponentCodeGeneration.nameFlag, componentCodeGenerationSettings));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortComponentCode));

		defaultPipelineSequenceDefs.add(pipelineSegmentHelper(yaml,
				directValueKeyword + " " + CopyFolder.nameFlag, copyFolderSettings));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortCopyFolder));
		
		defaultPipelineSequenceDefs.add(pipelineSegmentHelper(yaml,
				directValueKeyword + " " + PostProcessingStepsUntilConfig.nameFlag, postProcessingStepsUntilConfigSettings));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortPostProcessingUntilConfig));

		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingStateChartValues.nameFlag,
						postProcessingStateChartValuesFastDriver));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortPostProcessingStateChartValuesFastDriver));

		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingStateChartValues.nameFlag,
						postProcessingStateChartValuesSlowDriver));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortPostProcessingStateChartValuesSlowDriver));

		defaultPipelineSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + DeleteFolder.nameFlag, deleteFolderAPImappings));
		defaultPipelineSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + DeleteFolder.nameFlag, deleteFolderComponentCodeFolder));
		
		defaultPipelineSequenceDefs.add(
				pipelineSegmentHelper(yaml, directValueKeyword + " " + Compile.nameFlag, compileSettingsFastCoordinator));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortCompileFastCoordinator));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + Compile.nameFlag, compileSettingsFastDriver));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortCompileFastDriver));
		defaultPipelineSequenceDefs.add(
				pipelineSegmentHelper(yaml, directValueKeyword + " " + Compile.nameFlag, compileSettingsSlowCoordinator));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortCompileSlowCoordinator));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + Compile.nameFlag, compileSettingsSlowDriver));
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

		defaultPipelineSequenceDefs.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PopupWindowMessage.nameFlag,
				popupWindowMessageFinishedPipeline));

		mapForPipelineSettings.put(pipelineSequenceDefKeyword, defaultPipelineSequenceDefs);

		settingsWriter.write(yaml.dump(mapForPipelineSettings));
		settingsWriter.close();

		// Cleanup of unwanted additions: ' chars around from entries in the
		// pipeline sequence and after the "in:" flags the &id...s and *id...s.
		String inSequenceForCleanup = " " + inKeyword + ":";

		String intermediateFileName = completeSettingsFilePath.toString() + ".editing";
		FileWriter workCopy = new FileWriter(intermediateFileName);
		Scanner currentFileReader = new Scanner(completeSettingsFilePath.toFile());
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
		Files.move(Paths.get(intermediateFileName), completeSettingsFilePath, StandardCopyOption.REPLACE_EXISTING);

		return true;
	}

}
