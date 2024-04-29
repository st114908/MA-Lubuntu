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
		Map<String, String> deleteDirectoryArduinoContainersSettingsIns = deleteDirectoryArduinoContainers.get(inFlag);
		deleteDirectoryArduinoContainersSettingsIns.put("path", path);
		deleteDirectoryArduinoContainers.put(inFlag, deleteDirectoryArduinoContainersSettingsIns);
		return deleteDirectoryArduinoContainers;
	}

	public Map<String, Map<String, String>> generatePostProcessingStateChartValuesStepAndAdjustSettings(
			String arduinoContainersPath, String ECUName) {
		Map<String, Map<String, String>> postProcessingStateChartValuesSettings = PostProcessingStateChartValues
				.generateDefaultOrExampleValues();
		Map<String, String> postProcessingStateChartValuesSettingsIns = postProcessingStateChartValuesSettings
				.get(inFlag);
		postProcessingStateChartValuesSettingsIns.put("arduinoContainersPath", arduinoContainersPath);
		postProcessingStateChartValuesSettingsIns.put("ECUName", ECUName);
		postProcessingStateChartValuesSettings.put(inFlag, postProcessingStateChartValuesSettingsIns);
		return postProcessingStateChartValuesSettings;
	}

	public Map<String, Map<String, String>> generateCompileStepAndAdjustSettings(String boardTypeIdentifierFQBN,
			String targetInoFile) {
		Map<String, Map<String, String>> compileSettings = Compile.generateDefaultOrExampleValues();
		Map<String, String> compileSettingsIns = compileSettings.get(inFlag);
		compileSettingsIns.put("boardTypeIdentifierFQBN", boardTypeIdentifierFQBN);
		compileSettingsIns.put("targetInoFile", targetInoFile);
		compileSettings.put(inFlag, compileSettingsIns);
		return compileSettings;
	}

	public Map<String, Map<String, String>> generateLookupBoardBySerialNumberStepAndAdjustSettings(
			String boardSerialNumber) {
		Map<String, Map<String, String>> lookupBoardBySerialNumberSettings = LookupBoardBySerialNumber
				.generateDefaultOrExampleValues();
		Map<String, String> lookupBoardBySerialNumberSettingsIns = lookupBoardBySerialNumberSettings.get(inFlag);
		lookupBoardBySerialNumberSettingsIns.put("boardSerialNumber", boardSerialNumber);
		lookupBoardBySerialNumberSettings.put(inFlag, lookupBoardBySerialNumberSettingsIns);
		return lookupBoardBySerialNumberSettings;
	}

	public Map<String, Map<String, String>> generateUploadStepAndAdjustSettings(String portAddress,
			String boardTypeIdentifierFQBN, String targetInoOrHexFile) {
		Map<String, Map<String, String>> uploadSettings = Upload.generateDefaultOrExampleValues();
		Map<String, String> uploadSettingsIns = uploadSettings.get(inFlag);
		uploadSettingsIns.put("portAddress", portAddress);
		uploadSettingsIns.put("boardTypeIdentifierFQBN", boardTypeIdentifierFQBN);
		uploadSettingsIns.put("targetInoOrHexFile", targetInoOrHexFile);
		uploadSettings.put(inFlag, uploadSettingsIns);
		return uploadSettings;
	}

	public Map<String, Map<String, String>> generateOnlyContinueIfFulfilledElseAbortSettings(String condition,
			String message) {
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortSettings = OnlyContinueIfFulfilledElseAbort
				.generateDefaultOrExampleValues();
		Map<String, String> onlyContinueIfFulfilledElseAbortSettingsIns = onlyContinueIfFulfilledElseAbortSettings
				.get(inFlag);
		onlyContinueIfFulfilledElseAbortSettingsIns.put("condition", condition);
		onlyContinueIfFulfilledElseAbortSettingsIns.put("message", message);
		onlyContinueIfFulfilledElseAbortSettings.put(inFlag, onlyContinueIfFulfilledElseAbortSettingsIns);
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
		String generatedCodeFolderNameVariableValue = directValueFlag + " generated-files";
		String muml_containerFilePathVariableName = "muml_containerFilePath";
		String muml_containerFilePathVariableValue = generatedCodeFolderNameVariableValue + "/MUML_Container.muml_container";
		String deployableCodeFolderNameVariableName = "deployableFilesFolderPath";
		String deployableCodeFolderNameVariableValue = directValueFlag + " deployable-files";
		String componentCodeFolderNameVariableName = "componentCodeFilesFolderPath";
		String componentCodeFolderNameVariableValue = deployableCodeFolderNameVariableValue + "/fastAndSlowCar_v2";
		String apiMappingsFolderNameVariableName = "apiMappingsFolderPath";
		String apiMappingsFolderNameVariableValue = deployableCodeFolderNameVariableValue + "/APImappings";

		String usedDriverBoardIdentifierFQBNVariableName = "usedDriverBoardIdentifierFQBN";
		String usedDriverBoardIdentifierFQBNVariableValue = directValueFlag + " arduino:avr:mega";

		String usedCoordinatorBoardIdentifierFQBNVariableName = "usedCoordinatorBoardIdentifierFQBN";
		String usedCoordinatorBoardIdentifierFQBNVariableValue = directValueFlag + " arduino:avr:nano";

		String fastCarCoordinatorECUName = "fastCarCoordinatorECU";
		String fastCarCoordinatorECUFolderPathVariableName = fastCarCoordinatorECUName + "FolderPath";
		String fastCarCoordinatorECUFolderPathVariableValue = deployableCodeFolderNameVariableValue + "/"
				+ fastCarCoordinatorECUName;
		String fastCarCoordinatorECUINOFilePathVariableName = fastCarCoordinatorECUName + "INOFilePath";
		String fastCarCoordinatorECUINOFilePathVariableValue = deployableCodeFolderNameVariableValue + "/"
				+ fastCarCoordinatorECUName + "/" + fastCarCoordinatorECUName + ".ino";
		String fastCarCoordinatorECUBoardSerialNumberVariableName = "fastCarCoordinatorECUBoardSerialNumber";
		String fastCarCoordinatorECUBoardSerialNumberVariableValue = directValueFlag + " DummySerialFastCarCoordinator";

		String fastCarDriverECUName = "fastCarDriverECU";
		String fastCarDriverECUFolderPathVariableName = fastCarDriverECUName + "FolderPath";
		String fastCarDriverECUFolderPathVariableValue = deployableCodeFolderNameVariableValue + "/"
				+ fastCarDriverECUName;
		String fastCarDriverECUINOFilePathVariableName = fastCarDriverECUName + "INOFilePath";
		String fastCarDriverECUINOFilePathVariableValue = deployableCodeFolderNameVariableValue + "/"
				+ fastCarDriverECUName + "/" + fastCarDriverECUName + ".ino";
		String fastCarDriverECUBoardSerialNumberVariableName = "fastCarDriverECUBoardSerialNumber";
		String fastCarDriverECUBoardSerialNumberVariableValue = directValueFlag + " DummySerialFastCarDriver";

		String slowCarCoordinatorECUName = "slowCarCoordinatorECU";
		String slowCarCoordinatorECUFolderPathVariableName = slowCarCoordinatorECUName + "FolderPath";
		String slowCarCoordinatorECUFolderPathVariableValue = deployableCodeFolderNameVariableValue + "/"
				+ slowCarCoordinatorECUName;
		String slowCarCoordinatorECUINOFilePathVariableName = slowCarCoordinatorECUName + "INOFilePath";
		String slowCarCoordinatorECUINOFilePathVariableValue = deployableCodeFolderNameVariableValue + "/"
				+ slowCarCoordinatorECUName + "/" + slowCarCoordinatorECUName + ".ino";
		String slowCarCoordinatorECUBoardSerialNumberVariableName = "slowCarCoordinatorECUBoardSerialNumber";
		String slowCarCoordinatorECUBoardSerialNumberVariableValue = directValueFlag + " DummySerialSlowCarCoordinator";

		String slowCarDriverECUName = "slowCarDriverECU";
		String slowCarDriverECUFolderPathVariableName = slowCarDriverECUName + "FolderPath";
		String slowCarDriverECUFolderPathVariableValue = deployableCodeFolderNameVariableValue + "/"
				+ slowCarDriverECUName;
		String slowCarDriverECUINOFilePathVariableName = slowCarDriverECUName + "INOFilePath";
		String slowCarDriverECUINOFilePathVariableValue = deployableCodeFolderNameVariableValue + "/"
				+ slowCarDriverECUName + "/" + slowCarDriverECUName + ".ino";
		String slowCarDriverECUBoardSerialNumberVariableName = "slowCarDriverECUBoardSerialNumber";
		String slowCarDriverECUBoardSerialNumberVariableValue = directValueFlag + " DummySerialSlowCarDriver";

		// Settings changes of the generated default/example settings here in
		// order to make the written entries and sequences more readable.

		Map<String, Map<String, String>> deleteDirectoryGeneratedFiles = generateDeleteFolder(
				fromValueFlag + " " + generatedCodeFolderNameVariableName);
		Map<String, Map<String, String>> deleteDirectoryDeployableFiles = generateDeleteFolder(
				fromValueFlag + " " + deployableCodeFolderNameVariableName);

		Map<String, Map<String, String>> containerTransformationSettings = ContainerTransformation
				.generateDefaultOrExampleValues();
		Map<String, String> containerTransformationSettingsIns = containerTransformationSettings.get(inFlag);
		containerTransformationSettingsIns.put("muml_containerFileDestination",
				fromValueFlag + " " + muml_containerFilePathVariableName);
		containerTransformationSettings.put(inFlag, containerTransformationSettingsIns);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortContainerTransformation = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful",
				directValueFlag + " " + ContainerTransformation.nameFlag + " has failed!");

		Map<String, Map<String, String>> containerCodeGenerationSettings = ContainerCodeGeneration
				.generateDefaultOrExampleValues();
		Map<String, String> containerCodeGenerationSettingsIns = containerCodeGenerationSettings.get(inFlag);
		containerCodeGenerationSettingsIns.put("muml_containerSourceFile",
				fromValueFlag + " " + muml_containerFilePathVariableName);
		containerCodeGenerationSettingsIns.put("arduinoContainersDestinationFolder",
				fromValueFlag + " " + generatedCodeFolderNameVariableName);
		containerCodeGenerationSettings.put(inFlag, containerCodeGenerationSettingsIns);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortContainerCodeGeneration = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful",
				directValueFlag + " " + ContainerCodeGeneration.nameFlag + " has failed!");

		Map<String, Map<String, String>> componentCodeGenerationSettings = ComponentCodeGeneration
				.generateDefaultOrExampleValues();
		Map<String, String> componentCodeGenerationSettingsIns = componentCodeGenerationSettings.get(inFlag);
		componentCodeGenerationSettingsIns.put("arduinoContainersDestinationFolder",
				fromValueFlag + " " + generatedCodeFolderNameVariableName);
		componentCodeGenerationSettings.put(inFlag, componentCodeGenerationSettingsIns);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortComponentCode = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful",
				directValueFlag + " " + ComponentCodeGeneration.nameFlag + " has failed!");

		Map<String, Map<String, String>> copyFolderSettings = CopyFolder.generateDefaultOrExampleValues();
		Map<String, String> copyFolderSettingsIns = copyFolderSettings.get(inFlag);
		copyFolderSettingsIns.put("sourcePath", fromValueFlag + " " + generatedCodeFolderNameVariableName);
		copyFolderSettingsIns.put("destinationPath", fromValueFlag + " " + deployableCodeFolderNameVariableName);
		copyFolderSettings.put(inFlag, copyFolderSettingsIns);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortCopyFolder = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful",
				directValueFlag + " " + CopyFolder.nameFlag + " has failed!");

		Map<String, Map<String, String>> postProcessingStepsUntilConfigSettings = PostProcessingStepsUntilConfig
				.generateDefaultOrExampleValues();
		Map<String, String> postProcessingStepsUntilConfigSettingsIns = postProcessingStepsUntilConfigSettings
				.get(inFlag);
		postProcessingStepsUntilConfigSettingsIns.put("arduinoContainersPath",
				fromValueFlag + " " + deployableCodeFolderNameVariableName);
		postProcessingStepsUntilConfigSettingsIns.put("componentCodePath",
				fromValueFlag + " " + componentCodeFolderNameVariableName );
		postProcessingStepsUntilConfigSettings.put(inFlag, postProcessingStepsUntilConfigSettingsIns);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortPostProcessingUntilConfig = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful",
				directValueFlag + " " + PostProcessingStepsUntilConfig.nameFlag + " has failed!");

		Map<String, Map<String, String>> postProcessingStateChartValuesFastDriver = generatePostProcessingStateChartValuesStepAndAdjustSettings(
				fromValueFlag + " " + deployableCodeFolderNameVariableName,
				directValueFlag + " " + fastCarDriverECUName);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortPostProcessingStateChartValuesFastDriver = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful", directValueFlag + " " + PostProcessingStateChartValues.nameFlag
						+ " for " + fastCarDriverECUName + " has failed!");

		Map<String, Map<String, String>> postProcessingStateChartValuesSlowDriver = generatePostProcessingStateChartValuesStepAndAdjustSettings(
				fromValueFlag + " " + deployableCodeFolderNameVariableName,
				directValueFlag + " " + slowCarDriverECUName);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortPostProcessingStateChartValuesSlowDriver = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful", directValueFlag + " " + PostProcessingStateChartValues.nameFlag
						+ " for " + slowCarDriverECUName + " has failed!");


		Map<String, Map<String, String>> deleteFolderComponentCodeFolder = generateDeleteFolder(
				fromValueFlag + " " + componentCodeFolderNameVariableName);
		Map<String, Map<String, String>> deleteFolderAPImappings = generateDeleteFolder(
				fromValueFlag + " " + apiMappingsFolderNameVariableName);

		
		Map<String, Map<String, String>> compileSettingsFastCoordinator = generateCompileStepAndAdjustSettings(
				fromValueFlag + " " + usedCoordinatorBoardIdentifierFQBNVariableName,
				fromValueFlag + " " + fastCarCoordinatorECUINOFilePathVariableName);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortCompileFastCoordinator = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful",
				directValueFlag + " " + Compile.nameFlag + " for " + fastCarCoordinatorECUName + " has failed!");

		Map<String, Map<String, String>> compileSettingsFastDriver = generateCompileStepAndAdjustSettings(
				fromValueFlag + " " + usedDriverBoardIdentifierFQBNVariableName,
				fromValueFlag + " " + fastCarDriverECUINOFilePathVariableName);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortCompileFastDriver = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful",
				directValueFlag + " " + Compile.nameFlag + " for " + fastCarDriverECUName + " has failed!");

		Map<String, Map<String, String>> compileSettingsSlowCoordinator = generateCompileStepAndAdjustSettings(
				fromValueFlag + " " + usedCoordinatorBoardIdentifierFQBNVariableName,
				fromValueFlag + " " + slowCarCoordinatorECUINOFilePathVariableName);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortCompileSlowCoordinator = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful",
				directValueFlag + " " + Compile.nameFlag + " for " + slowCarCoordinatorECUName + " has failed!");

		Map<String, Map<String, String>> compileSettingsSlowDriver = generateCompileStepAndAdjustSettings(
				fromValueFlag + " " + usedDriverBoardIdentifierFQBNVariableName,
				fromValueFlag + " " + slowCarDriverECUINOFilePathVariableName);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortCompileSlowDriver = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful",
				directValueFlag + " " + Compile.nameFlag + " for " + slowCarDriverECUName + " has failed!");

		Map<String, Map<String, String>> lookupBoardBySerialNumberStepAndAdjustSettingsFastCoordinator = generateLookupBoardBySerialNumberStepAndAdjustSettings(
				fromValueFlag + " " + fastCarCoordinatorECUBoardSerialNumberVariableName);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortLookupBoardBySerialNumberStepAndAdjustSettingsFastCoordinator = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful", directValueFlag + " " + LookupBoardBySerialNumber.nameFlag + " for "
						+ fastCarCoordinatorECUName + " has failed!");
		Map<String, Map<String, String>> uploadSettingsFastCoordinator = generateUploadStepAndAdjustSettings(
				fromValueFlag + " foundPortAddress", fromValueFlag + " usedCoordinatorBoardIdentifierFQBN",
				fromValueFlag + " fastCarCoordinatorECUINOFilePath");
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortUploadFastCoordinator = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful",
				directValueFlag + " " + Upload.nameFlag + " for " + fastCarCoordinatorECUName + " has failed!");

		Map<String, Map<String, String>> lookupBoardBySerialNumberStepAndAdjustSettingsFastDriver = generateLookupBoardBySerialNumberStepAndAdjustSettings(
				fromValueFlag + " fastCarDriverECUBoardSerialNumber");
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortLookupBoardBySerialNumberStepAndAdjustSettingsFastDriver = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful", directValueFlag + " " + LookupBoardBySerialNumber.nameFlag + " for "
						+ fastCarDriverECUName + " has failed!");
		Map<String, Map<String, String>> uploadSettingsFastDriver = generateUploadStepAndAdjustSettings(
				fromValueFlag + " foundPortAddress", fromValueFlag + " " + usedDriverBoardIdentifierFQBNVariableName,
				fromValueFlag + " " + fastCarDriverECUINOFilePathVariableName);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortUploadFastDriver = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful",
				directValueFlag + " " + Upload.nameFlag + " for " + fastCarDriverECUName + " has failed!");

		Map<String, Map<String, String>> lookupBoardBySerialNumberStepAndAdjustSettingsSlowCoordinator = generateLookupBoardBySerialNumberStepAndAdjustSettings(
				fromValueFlag + " " + slowCarCoordinatorECUBoardSerialNumberVariableName);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortLookupBoardBySerialNumberStepAndAdjustSettingsSlowCoordinator = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful", directValueFlag + " " + LookupBoardBySerialNumber.nameFlag + " for "
						+ slowCarCoordinatorECUName + " has failed!");
		Map<String, Map<String, String>> uploadSettingsSlowCoordinator = generateUploadStepAndAdjustSettings(
				fromValueFlag + " foundPortAddress", fromValueFlag + " usedCoordinatorBoardIdentifierFQBN",
				fromValueFlag + " " + slowCarCoordinatorECUINOFilePathVariableName);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortUploadSlowCoordinator = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful",
				directValueFlag + " " + Upload.nameFlag + " for " + slowCarCoordinatorECUName + " has failed!");

		Map<String, Map<String, String>> lookupBoardBySerialNumberStepAndAdjustSettingsSlowDriver = generateLookupBoardBySerialNumberStepAndAdjustSettings(
				fromValueFlag + " " + slowCarDriverECUBoardSerialNumberVariableName);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortLookupBoardBySerialNumberStepAndAdjustSettingsSlowDriver = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful", directValueFlag + " " + LookupBoardBySerialNumber.nameFlag + " for "
						+ slowCarDriverECUName + " has failed!");
		Map<String, Map<String, String>> uploadSettingsSlowDriver = generateUploadStepAndAdjustSettings(
				fromValueFlag + " foundPortAddress", fromValueFlag + " " + usedDriverBoardIdentifierFQBNVariableName,
				fromValueFlag + " " + slowCarDriverECUINOFilePathVariableName);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortUploadSlowDriver = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful",
				directValueFlag + " " + Upload.nameFlag + " for " + slowCarDriverECUName + " has failed!");

		Map<String, Map<String, String>> popupWindowMessageFinishedPipeline = PopupWindowMessage
				.generateDefaultOrExampleValues();
		Map<String, String> popupWindowMessageFinishedPipelineSettingsIns = popupWindowMessageFinishedPipeline
				.get(inFlag);
		popupWindowMessageFinishedPipelineSettingsIns.put("message",
				directValueFlag + " Pipeline execution completed!");
		popupWindowMessageFinishedPipeline.put(inFlag, popupWindowMessageFinishedPipelineSettingsIns);

		// Now the pipeline settings and sequence itself.
		Map<String, Object> mapForPipelineSettings = new LinkedHashMap<String, Object>();

		// Use default values and generate the config file with default values.
		Map<String, String> exampleVariableDefs = new LinkedHashMap<String, String>();
		exampleVariableDefs.put("ExampleNumberVariableName", directValueFlag + " 12");
		exampleVariableDefs.put("ExampleStringVariableName", directValueFlag + " ExampleString");
		exampleVariableDefs.put("ExampleBooleanVariableName", directValueFlag + " true");

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

		mapForPipelineSettings.put(variableDefsFlag, exampleVariableDefs);

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
		mapForPipelineSettings.put(standaloneUsageDefsFlag, defaultStandaloneUsageDefs);

		// For better/easier readability blank lines inbetween:
		settingsWriter.write(yaml.dump(mapForPipelineSettings));
		mapForPipelineSettings.clear();
		settingsWriter.write("\n\n");

		ArrayList<Object> defaultPipelineSequenceDefs = new ArrayList<Object>();

		defaultPipelineSequenceDefs.add(pipelineSegmentHelper(yaml, directValueFlag + " " + DeleteFolder.nameFlag,
				deleteDirectoryDeployableFiles));
		defaultPipelineSequenceDefs.add(pipelineSegmentHelper(yaml, directValueFlag + " " + DeleteFolder.nameFlag,
				deleteDirectoryGeneratedFiles));

		defaultPipelineSequenceDefs.add(pipelineSegmentHelper(yaml,
				directValueFlag + " " + ContainerTransformation.nameFlag, containerTransformationSettings));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueFlag + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortContainerTransformation));

		defaultPipelineSequenceDefs.add(pipelineSegmentHelper(yaml,
				directValueFlag + " " + ContainerCodeGeneration.nameFlag, containerCodeGenerationSettings));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueFlag + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortContainerCodeGeneration));

		defaultPipelineSequenceDefs.add(pipelineSegmentHelper(yaml,
				directValueFlag + " " + ComponentCodeGeneration.nameFlag, componentCodeGenerationSettings));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueFlag + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortComponentCode));

		defaultPipelineSequenceDefs.add(pipelineSegmentHelper(yaml,
				directValueFlag + " " + CopyFolder.nameFlag, copyFolderSettings));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueFlag + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortCopyFolder));
		
		defaultPipelineSequenceDefs.add(pipelineSegmentHelper(yaml,
				directValueFlag + " " + PostProcessingStepsUntilConfig.nameFlag, postProcessingStepsUntilConfigSettings));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueFlag + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortPostProcessingUntilConfig));

		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueFlag + " " + PostProcessingStateChartValues.nameFlag,
						postProcessingStateChartValuesFastDriver));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueFlag + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortPostProcessingStateChartValuesFastDriver));

		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueFlag + " " + PostProcessingStateChartValues.nameFlag,
						postProcessingStateChartValuesSlowDriver));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueFlag + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortPostProcessingStateChartValuesSlowDriver));

		defaultPipelineSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueFlag + " " + DeleteFolder.nameFlag, deleteFolderAPImappings));
		defaultPipelineSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueFlag + " " + DeleteFolder.nameFlag, deleteFolderComponentCodeFolder));
		
		defaultPipelineSequenceDefs.add(
				pipelineSegmentHelper(yaml, directValueFlag + " " + Compile.nameFlag, compileSettingsFastCoordinator));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueFlag + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortCompileFastCoordinator));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueFlag + " " + Compile.nameFlag, compileSettingsFastDriver));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueFlag + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortCompileFastDriver));
		defaultPipelineSequenceDefs.add(
				pipelineSegmentHelper(yaml, directValueFlag + " " + Compile.nameFlag, compileSettingsSlowCoordinator));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueFlag + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortCompileSlowCoordinator));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueFlag + " " + Compile.nameFlag, compileSettingsSlowDriver));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueFlag + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortCompileSlowDriver));

		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueFlag + " " + LookupBoardBySerialNumber.nameFlag,
						lookupBoardBySerialNumberStepAndAdjustSettingsFastCoordinator));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueFlag + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortLookupBoardBySerialNumberStepAndAdjustSettingsFastCoordinator));
		defaultPipelineSequenceDefs.add(
				pipelineSegmentHelper(yaml, directValueFlag + " " + Upload.nameFlag, uploadSettingsFastCoordinator));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueFlag + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortUploadFastCoordinator));

		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueFlag + " " + LookupBoardBySerialNumber.nameFlag,
						lookupBoardBySerialNumberStepAndAdjustSettingsFastDriver));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueFlag + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortLookupBoardBySerialNumberStepAndAdjustSettingsFastDriver));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueFlag + " " + Upload.nameFlag, uploadSettingsFastDriver));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueFlag + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortUploadFastDriver));

		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueFlag + " " + LookupBoardBySerialNumber.nameFlag,
						lookupBoardBySerialNumberStepAndAdjustSettingsSlowCoordinator));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueFlag + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortLookupBoardBySerialNumberStepAndAdjustSettingsSlowCoordinator));
		defaultPipelineSequenceDefs.add(
				pipelineSegmentHelper(yaml, directValueFlag + " " + Upload.nameFlag, uploadSettingsSlowCoordinator));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueFlag + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortUploadSlowCoordinator));

		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueFlag + " " + LookupBoardBySerialNumber.nameFlag,
						lookupBoardBySerialNumberStepAndAdjustSettingsSlowDriver));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueFlag + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortLookupBoardBySerialNumberStepAndAdjustSettingsSlowDriver));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueFlag + " " + Upload.nameFlag, uploadSettingsSlowDriver));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueFlag + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortUploadSlowDriver));

		defaultPipelineSequenceDefs.add(pipelineSegmentHelper(yaml, directValueFlag + " " + PopupWindowMessage.nameFlag,
				popupWindowMessageFinishedPipeline));

		mapForPipelineSettings.put(pipelineSequenceDefFlag, defaultPipelineSequenceDefs);

		settingsWriter.write(yaml.dump(mapForPipelineSettings));
		settingsWriter.close();

		// Cleanup of unwanted additions: ' chars around from entries in the
		// pipeline sequence and after the "in:" flags the &id...s and *id...s.
		String inSequenceForCleanup = " " + inFlag + ":";

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
			} else if (currentLine.contains(" " + inFlag + ":")) {
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
