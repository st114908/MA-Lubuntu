package de.ust.mumlacgppa.pipeline.mumlpostprocessingandarduinocli.settingsgeneration;

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

		// Settings changes of the generated default/example settings here in
		// order to make the written entries and sequences more readable.

		Map<String, Map<String, String>> deleteDirectoryArduinoContainers = DeleteFolder
				.generateDefaultOrExampleValues();
		Map<String, String> deleteDirectoryArduinoContainersSettingsIns = deleteDirectoryArduinoContainers.get(inFlag);
		deleteDirectoryArduinoContainersSettingsIns.put("path", fromValueFlag + " arduinoContainersFolderName");
		deleteDirectoryArduinoContainers.put(inFlag, deleteDirectoryArduinoContainersSettingsIns);

		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortContainerTransformation = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful",
				directValueFlag + " " + ContainerTransformation.nameFlag + " has failed!");
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortContainerCodeGeneration = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful",
				directValueFlag + " " + ContainerCodeGeneration.nameFlag + " has failed!");
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortSettingsComponentCode = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful",
				directValueFlag + " " + ComponentCodeGeneration.nameFlag + " has failed!");
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortPostProcessingUntilConfig = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful",
				directValueFlag + " " + PostProcessingStepsUntilConfig.nameFlag + " has failed!");

		Map<String, Map<String, String>> postProcessingStateChartValuesFastDriver = generatePostProcessingStateChartValuesStepAndAdjustSettings(
				fromValueFlag + " arduinoContainersFolderName", fromValueFlag + " fastCarDriverECUFolderName");
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortPostProcessingStateChartValuesFastDriver = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful",
				directValueFlag + " " + PostProcessingStateChartValues.nameFlag + " for FastCarDriver has failed!");

		Map<String, Map<String, String>> postProcessingStateChartValuesSlowDriver = generatePostProcessingStateChartValuesStepAndAdjustSettings(
				fromValueFlag + " arduinoContainersFolderName", fromValueFlag + " slowCarDriverECUFolderName");
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortPostProcessingStateChartValuesSlowDriver = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful",
				directValueFlag + " " + PostProcessingStateChartValues.nameFlag + " for SlowCarDriver has failed!");

		Map<String, Map<String, String>> compileSettingsFastCoordinator = generateCompileStepAndAdjustSettings(
				fromValueFlag + " usedCoordinatorBoardIdentifierFQBN",
				fromValueFlag + " fastCarCoordinatorECUINOFilePath");
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortCompileFastCoordinator = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful",
				directValueFlag + " " + Compile.nameFlag + " for FastCarCoordinator has failed!");

		Map<String, Map<String, String>> compileSettingsFastDriver = generateCompileStepAndAdjustSettings(
				fromValueFlag + " usedDriverBoardIdentifierFQBN", fromValueFlag + " fastCarDriverECUINOFilePath");
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortCompileFastDriver = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful",
				directValueFlag + " " + Compile.nameFlag + " for FastCarDriver has failed!");

		Map<String, Map<String, String>> compileSettingsSlowCoordinator = generateCompileStepAndAdjustSettings(
				fromValueFlag + " usedCoordinatorBoardIdentifierFQBN",
				fromValueFlag + " slowCarCoordinatorECUINOFilePath");
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortCompileSlowCoordinator = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful",
				directValueFlag + " " + Compile.nameFlag + " for SlowCarCoordinator has failed!");

		Map<String, Map<String, String>> compileSettingsSlowDriver = generateCompileStepAndAdjustSettings(
				fromValueFlag + " usedDriverBoardIdentifierFQBN", fromValueFlag + " slowCarDriverECUINOFilePath");
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortCompileSlowDriver = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful",
				directValueFlag + " " + Compile.nameFlag + " for SlowCarDriver has failed!");

		Map<String, Map<String, String>> lookupBoardBySerialNumberStepAndAdjustSettingsFastCoordinator = generateLookupBoardBySerialNumberStepAndAdjustSettings(
				fromValueFlag + " fastCarCoordinatorECUBoardSerialNumber");
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortLookupBoardBySerialNumberStepAndAdjustSettingsFastCoordinator = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful",
				directValueFlag + " " + LookupBoardBySerialNumber.nameFlag + " for FastCarCoordinator has failed!");
		Map<String, Map<String, String>> uploadSettingsFastCoordinator = generateUploadStepAndAdjustSettings(
				fromValueFlag + " foundPortAddress", fromValueFlag + " usedCoordinatorBoardIdentifierFQBN",
				fromValueFlag + " fastCarCoordinatorECUINOFilePath");
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortUploadFastCoordinator = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful",
				directValueFlag + " " + Upload.nameFlag + " for FastCarCoordinator has failed!");

		Map<String, Map<String, String>> lookupBoardBySerialNumberStepAndAdjustSettingsFastDriver = generateLookupBoardBySerialNumberStepAndAdjustSettings(
				fromValueFlag + " fastCarDriverECUBoardSerialNumber");
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortLookupBoardBySerialNumberStepAndAdjustSettingsFastDriver = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful",
				directValueFlag + " " + LookupBoardBySerialNumber.nameFlag + " for FastCarDriver has failed!");
		Map<String, Map<String, String>> uploadSettingsFastDriver = generateUploadStepAndAdjustSettings(
				fromValueFlag + " foundPortAddress", fromValueFlag + " usedDriverBoardIdentifierFQBN",
				fromValueFlag + " fastCarDriverECUINOFilePath");
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortUploadFastDriver = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful",
				directValueFlag + " " + Upload.nameFlag + " for FastCarDriver has failed!");

		Map<String, Map<String, String>> lookupBoardBySerialNumberStepAndAdjustSettingsSlowCoordinator = generateLookupBoardBySerialNumberStepAndAdjustSettings(
				fromValueFlag + " slowCarCoordinatorECUBoardSerialNumber");
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortLookupBoardBySerialNumberStepAndAdjustSettingsSlowCoordinator = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful",
				directValueFlag + " " + LookupBoardBySerialNumber.nameFlag + " for SlowCarCoordinator has failed!");
		Map<String, Map<String, String>> uploadSettingsSlowCoordinator = generateUploadStepAndAdjustSettings(
				fromValueFlag + " foundPortAddress", fromValueFlag + " usedCoordinatorBoardIdentifierFQBN",
				fromValueFlag + " slowCarCoordinatorECUINOFilePath");
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortUploadSlowCoordinator = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful",
				directValueFlag + " " + Upload.nameFlag + " for SlowCarCoordinator has failed!");

		Map<String, Map<String, String>> lookupBoardBySerialNumberStepAndAdjustSettingsSlowDriver = generateLookupBoardBySerialNumberStepAndAdjustSettings(
				fromValueFlag + " slowCarDriverECUBoardSerialNumber");
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortLookupBoardBySerialNumberStepAndAdjustSettingsSlowDriver = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful",
				directValueFlag + " " + LookupBoardBySerialNumber.nameFlag + " for SlowCarDriver has failed!");
		Map<String, Map<String, String>> uploadSettingsSlowDriver = generateUploadStepAndAdjustSettings(
				fromValueFlag + " foundPortAddress", fromValueFlag + " usedDriverBoardIdentifierFQBN",
				fromValueFlag + " slowCarDriverECUINOFilePath");
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortUploadSlowDriver = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromValueFlag + " ifSuccessful",
				directValueFlag + " " + Upload.nameFlag + " for SlowCarDriver has failed!");

		Map<String, Map<String, String>> popupWindowMessageFinishedPipeline = PopupWindowMessage
				.generateDefaultOrExampleValues();
		Map<String, String> popupWindowMessageFinishedPipelineSettingsIns = popupWindowMessageFinishedPipeline
				.get(inFlag);
		popupWindowMessageFinishedPipelineSettingsIns.put("message", directValueFlag + " Pipeline execution completed!");
		popupWindowMessageFinishedPipeline.put(inFlag, popupWindowMessageFinishedPipelineSettingsIns);

		// Now the pipeline settings and sequence itself.
		Map<String, Object> mapForPipelineSettings = new LinkedHashMap<String, Object>();

		// Use default values and generate the config file with default values.
		Map<String, String> exampleVariableDefs = new LinkedHashMap<String, String>();
		exampleVariableDefs.put("ExampleNumberVariableName", "direct 12");
		exampleVariableDefs.put("ExampleStringVariableName", "direct ExampleString");
		exampleVariableDefs.put("ExampleBooleanVariableName", "direct true");

		exampleVariableDefs.put("arduinoContainersFolderName", "direct arduino-containers");

		exampleVariableDefs.put("usedDriverBoardIdentifierFQBN", "direct arduino:avr:mega");
		exampleVariableDefs.put("usedCoordinatorBoardIdentifierFQBN", "direct arduino:avr:nano");

		exampleVariableDefs.put("fastCarCoordinatorECUFolderName", "direct fastCarCoordinatorECU");
		exampleVariableDefs.put("fastCarCoordinatorECUINOFilePath",
				"direct arduino-containers/fastCarCoordinatorECU/fastCarCoordinatorECU.ino");
		exampleVariableDefs.put("fastCarCoordinatorECUBoardSerialNumber", "direct DummySerialFastCarCoordinator");

		exampleVariableDefs.put("fastCarDriverECUFolderName", "direct fastCarDriverECU");
		exampleVariableDefs.put("fastCarDriverECUINOFilePath",
				"direct arduino-containers/fastCarDriverECU/fastCarDriverECU.ino");
		exampleVariableDefs.put("fastCarDriverECUBoardSerialNumber", "direct DummySerialFastCarDriver");

		exampleVariableDefs.put("slowCarCoordinatorECUFolderName", "direct slowCarCoordinatorECU");
		exampleVariableDefs.put("slowCarCoordinatorECUINOFilePath",
				"direct arduino-containers/slowCarCoordinatorECU/slowCarCoordinatorECU.ino");
		exampleVariableDefs.put("slowCarCoordinatorECUBoardSerialNumber", "direct DummySerialSlowCarCoordinator");

		exampleVariableDefs.put("slowCarDriverECUFolderName", "direct slowCarDriverECU");
		exampleVariableDefs.put("slowCarDriverECUINOFilePath",
				"direct arduino-containers/slowCarDriverECU/slowCarDriverECU.ino");
		exampleVariableDefs.put("slowCarDriverECUBoardSerialNumber", "direct DummySerialSlowCarDriver");

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
				deleteDirectoryArduinoContainers));

		defaultPipelineSequenceDefs
				.add(fromValueFlag + " " + standaloneUsageDefsFlag + ": " + ContainerTransformation.nameFlag);
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueFlag + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortContainerTransformation));

		defaultPipelineSequenceDefs
				.add(fromValueFlag + " " + standaloneUsageDefsFlag + ": " + ContainerCodeGeneration.nameFlag);
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueFlag + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortContainerCodeGeneration));

		defaultPipelineSequenceDefs
				.add(fromValueFlag + " " + standaloneUsageDefsFlag + ": " + ComponentCodeGeneration.nameFlag);
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueFlag + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortSettingsComponentCode));

		defaultPipelineSequenceDefs
				.add(fromValueFlag + " " + standaloneUsageDefsFlag + ": " + PostProcessingStepsUntilConfig.nameFlag);
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
