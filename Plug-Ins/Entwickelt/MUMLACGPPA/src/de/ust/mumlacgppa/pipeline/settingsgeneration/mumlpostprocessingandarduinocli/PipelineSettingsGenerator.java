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
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.CopyFiles;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.CopyFolder;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.DeleteFolder;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.LookupBoardBySerialNumber;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.OnlyContinueIfFulfilledElseAbort;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.DialogMessage;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingAddHALPartsIntoCarDriverInoFiles;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingAdjustAPIMappingFile;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingAdjustIncludes;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingAdjustSerialCommunicationSizes;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingConfigureMQTTSettings;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingConfigureWLANSettings;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingCopyFolderContentsToECUsAndExcept;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingCopyFolderContentsToECUsWhitelist;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingCopyLocalConfig_hppToCarDeriverECUs;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingFillOutMethodStubs;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingMoveIncludeBefore_ifdef__cplusplus;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingStateChartValues;
//import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingStepsUntilConfig;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.Upload;
import de.ust.mumlacgppa.pipeline.parts.storage.VariableTypes;
import de.ust.mumlacgppa.pipeline.paths.PipelineSettingsDirectoryAndFilesPaths;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

public class PipelineSettingsGenerator implements PipelineSettingsDirectoryAndFilesPaths, Keywords, VariableTypes{
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
	
	private String generateVariableDefStructure(String name, String value, String type){
		String currentVariableDef = type + " " + name + " " + value;
		return currentVariableDef;
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
	
	private Map<String, Map<String, String>> generatePostProcessingAdjustIncludes(
			String componentCodePath, String faulty, String correct){
		Map<String, Map<String, String>> postProcessingAdjustInlcudesSettings = PostProcessingAdjustIncludes.generateDefaultOrExampleValues();
		Map<String, String> postProcessingAdjustInlcudesSettingsIns = postProcessingAdjustInlcudesSettings.get(inKeyword);
		postProcessingAdjustInlcudesSettingsIns.put("componentCodePath", componentCodePath);
		postProcessingAdjustInlcudesSettingsIns.put("faultyInclude", faulty);
		postProcessingAdjustInlcudesSettingsIns.put("correctInclude", correct);
		postProcessingAdjustInlcudesSettings.put(inKeyword, postProcessingAdjustInlcudesSettingsIns);
		return postProcessingAdjustInlcudesSettings;
	}
	
	private Map<String, Map<String, String>> generatePostProcessingCopyFolderContentsToECUsWhitelist(
			String sourceFolder, String destinationFolder, String ECUNameEnding, String whitelist){
		Map<String, Map<String, String>> instanceSettings = PostProcessingCopyFolderContentsToECUsWhitelist.generateDefaultOrExampleValues();
		Map<String, String> instanceSettingsIns = instanceSettings.get(inKeyword);
		instanceSettingsIns.put("sourceFolder", sourceFolder);
		instanceSettingsIns.put("destinationFolder", destinationFolder);
		instanceSettingsIns.put("ECUNameEnding", ECUNameEnding);
		instanceSettingsIns.put("whitelist", whitelist);
		instanceSettings.put(inKeyword, instanceSettingsIns);
		return instanceSettings;
	}

	private Map<String, Map<String, String>> generatePostProcessingCopyFolderContentsToECUsAndExcept(
			String sourceFolder, String destinationFolder, String ECUNameEnding, String except){
		Map<String, Map<String, String>> instanceSettings = PostProcessingCopyFolderContentsToECUsAndExcept.generateDefaultOrExampleValues();
		Map<String, String> instanceSettingsIns = instanceSettings.get(inKeyword);
		instanceSettingsIns.put("sourceFolder", sourceFolder);
		instanceSettingsIns.put("destinationFolder", destinationFolder);
		instanceSettingsIns.put("ECUNameEnding", ECUNameEnding);
		instanceSettingsIns.put("except", except);
		instanceSettings.put(inKeyword, instanceSettingsIns);
		return instanceSettings;
	}
	
	private Map<String, Map<String, String>> generatePostProcessingCopyFiles(
			String sourceFolder, String destinationFolder, String files){
		Map<String, Map<String, String>> instanceSettings = CopyFiles.generateDefaultOrExampleValues();
		Map<String, String> instanceSettingsIns = instanceSettings.get(inKeyword);
		instanceSettingsIns.put("sourceFolder", sourceFolder);
		instanceSettingsIns.put("destinationFolder", destinationFolder);
		instanceSettingsIns.put("files", files);
		instanceSettings.put(inKeyword, instanceSettingsIns);
		return instanceSettings;
	}
	

	private Map<String, Map<String, String>> generatePostProcessingAdjustAPIMappingFile(
			String sourceFolder, String destinationFolder, String fileName, String library, String instruction){
		Map<String, Map<String, String>> instanceSettings = PostProcessingAdjustAPIMappingFile.generateDefaultOrExampleValues();
		Map<String, String> instanceSettingsIns = instanceSettings.get(inKeyword);
		instanceSettingsIns.put("sourceFolder", sourceFolder);
		instanceSettingsIns.put("destinationFolder", destinationFolder);
		instanceSettingsIns.put("fileName", fileName);
		instanceSettingsIns.put("library", library);
		instanceSettingsIns.put("instruction", instruction);
		instanceSettings.put(inKeyword, instanceSettingsIns);
		return instanceSettings;
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

		// Information text about the variable types and their limits or formats to keep in mind:
		String variableTypeAndLimitsInfoText = ""
				+ "#In general you have to adhere to the YAML format regardless of the type,\n"
				+ "#so if there is a # symbol in the data, then you have to wrap the data in ' symbols.\n"
				+ "#This way the whole data entry is seen as a string which includes a # and following.\n"
				+ "#Now the types with their respective limits and formats:\n"
				+ "#Any: This type is only internally allowed for reading parameter entries,\n"
				+ "#but not for variable definitions or output parameters.\n"
				+ "#Number: Whole numbers / integer values that Java can handle as 'int' values.\n"
				+ "#String: character sequences that Java can handle as 'String' values.\n"
				+ "#Boolean: 'true' or 'false' values.\n"
				+ "#(In general for paths written or given in FolderPath und FilePath): The path format as used by Linux or Ubuntu.\n"
				+ "#    If there is a '\\' symbol at the beginning, then it will be interpreted as absolute path.\n"
				+ "#    Else the path will be handled relative to the project folder.\n"
				+ "#FolderPath: Additionally the path is supposed to be a folder path.\n"
				+ "#FolderPath: Additionally the path is supposed to be a file path.\n"
				+ "#BoardSerianNumber: A serial number as string, probably consisting of letters and numbers.\n"
				+ "#BoardIdentifierFQBN: The board type. Due to the used Arduino-CLI estimated to be '[producer]:[processortype]:[model type]' and in lower case letters\n"
				+ "#    If a producer is using a different format then it will also work as long as the Arduino-CLI accepts it.\n"
				+ "#ConnectionPort: The shape of the port addresses is depending on the used system and virtual machine (if used).\n"
				+ "#    Recommendation: don't write it manually, but get it through pipeline step 'LookupBoardBySerialNumber'.\n"
				+ "#WLANName: In principle the same limits that exist for naming WLAN networks.\n"
				+ "#WLANPassword: In principle the same limits that exist for setting the password of WLAN networks.\n"
				+ "#ServerIPAddress: Addresses as used in internet browsers, but without 'https://' oder 'http://'.\n"
				+ "#ServerPort: Adhere to the standards of server ports , see https://www.cloudflare.com/learning/network-layer/what-is-a-computer-port/";
		
		settingsWriter.write(variableTypeAndLimitsInfoText);
		settingsWriter.write("\n\n");
		
		// Back to the pipeline configuration itself:
		// Variable names and potentially their values:

		String generatedCodeFolderNameVariableName = "generatedIncompleteFilesFolderPath";
		String generatedCodeFolderNameVariableValue = "generated-files";
		String muml_containerFilePathVariableName = "muml_containerFilePath";
		String muml_containerFilePathVariableValue = generatedCodeFolderNameVariableValue
				+ "/MUML_Container.muml_container";
		String deployableCodeFolderNameVariableName = "deployableFilesFolderPath";
		String deployableCodeFolderNameVariableValue ="deployable-files";
		String componentCodeFolderNameVariableName = "componentCodeFilesFolderPath";
		String componentCodeFolderNameVariableValue = deployableCodeFolderNameVariableValue + "/fastAndSlowCar_v2";
		String apiMappingsFolderNameVariableName = "apiMappingsFolderPath";
		String apiMappingsFolderNameVariableValue = deployableCodeFolderNameVariableValue + "/APImappings";
		String componentsFolderNameVariableName = "componentsFolderPath";
		String componentsFolderNameVariableValue = componentCodeFolderNameVariableValue + "/components";
		String libFolderNameVariableName = "libFolderPath";
		String libFolderNameVariableValue = componentCodeFolderNameVariableValue + "/lib";
		String messagesFolderNameVariableName = "messagesFolderPath";
		String messagesFolderNameVariableValue = componentCodeFolderNameVariableValue + "/messages";
		String operationsFolderNameVariableName = "operationsFolderPath";
		String operationsFolderNameVariableValue = componentCodeFolderNameVariableValue + "/operations";
		String RTSCsFolderNameVariableName = "RTSCsFolderPath";
		String RTSCsFolderNameVariableValue = componentCodeFolderNameVariableValue + "/RTSCs";
		String typesFolderNameVariableName = "typesFolderPath";
		String typesFolderNameVariableValue = componentCodeFolderNameVariableValue + "/types";

		String WLANNameOrSSIDVariableName = "WLANNameOrSSID";
		String WLANNameOrSSIDVariableValue = "DummyWLANNameOrSSID";
		String WLANPasswordVariableName = "WLANPassword_MakeSureThisStaysSafe"; 
		String WLANPasswordVariableValue = "DummyWLANPassword";
		
		String MQTTServerIPAddressVariableName = "MQTTServerIPAddress";
		String MQTTServerIPAddressVariableValue = "DummyMQTTServerIPAddress";
		String MQTTServerPortVariableName = "MQTTServerPort";
		String MQTTServerPortVariableValue = "1883";
		
		String fastCarDesiredVelocityVariableName = "fastCarDesiredVelocity";
		String fastCarDesiredVelocityVariableValue = "65";
		String slowCarDesiredVelocityVariableName = "slowCarDesiredVelocity";
		String slowCarDesiredVelocityVariableValue = "55";
		
		String usedDriverBoardIdentifierFQBNVariableName = "usedDriverBoardIdentifierFQBN";
		String usedDriverBoardIdentifierFQBNVariableValue = "arduino:avr:mega";
		String usedCoordinatorBoardIdentifierFQBNVariableName = "usedCoordinatorBoardIdentifierFQBN";
		String usedCoordinatorBoardIdentifierFQBNVariableValue = "arduino:avr:nano";

		String fastCarCoordinatorECUNameVariableName = "fastCarCoordinatorECUName";
		String fastCarCoordinatorECUNameVariableValue = "fastCarCoordinatorECU";
		String fastCarCoordinatorECUBoardSerialNumberVariableName = "fastCarCoordinatorECUBoardSerialNumber";
		String fastCarCoordinatorECUBoardSerialNumberVariableValue = "DummyFastCarCoordinatorSerialNumber";
		String fastCarDriverECUBoardSerialNumberVariableName = "fastCarDriverECUBoardSerialNumber";
		String fastCarDriverECUBoardSerialNumberVariableValue = "DummyFastCarDriverSerialNumber";
		
		String slowCarCoordinatorECUNameVariableName = "slowCarCoordinatorECUName";
		String slowCarCoordinatorECUNameVariableValue = "slowCarCoordinatorECU";
		String slowCarCoordinatorECUBoardSerialNumberVariableName = "slowCarCoordinatorECUBoardSerialNumber";
		String slowCarCoordinatorECUBoardSerialNumberVariableValue = "DummySlowCarCoordinatorSerialNumber";
		String slowCarDriverECUBoardSerialNumberVariableName = "slowCarDriverECUBoardSerialNumber";
		String slowCarDriverECUBoardSerialNumberVariableValue = "DummySlowCarDriverSerialNumber";

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

		
		
		
		//PostProcessing:
		
		Map<String, Map<String, String>> copyFolderGeneratedDeployable = CopyFolder.generateDefaultOrExampleValues();
		Map<String, String> copyFolderSettingsIns = copyFolderGeneratedDeployable.get(inKeyword);
		copyFolderSettingsIns.put("sourcePath", fromKeyword + " " + generatedCodeFolderNameVariableName);
		copyFolderSettingsIns.put("destinationPath", fromKeyword + " " + deployableCodeFolderNameVariableName);
		copyFolderGeneratedDeployable.put(inKeyword, copyFolderSettingsIns);
		Map<String, Map<String, String>> onlyContinueIfFulfilledElseAbortCopyFolder = generateOnlyContinueIfFulfilledElseAbortSettings(
				fromKeyword + " ifSuccessful", directValueKeyword + " " + CopyFolder.nameFlag + " has failed!");

		// The following steps are oriented on
		// https://github.com/SQA-Robo-Lab/Overtaking-Cars/blob/hal_demo/arduino-containers_demo_hal/deployable-files-hal-test/README.md.
		// The comments are summaries to have the instructions/actions easier to read as comments in the code.  
		
		// For all files in "fastAndSlowCar_v2":
		// 1: Adjust #include calls for all header files.
		// 2: Move up "# include clock.h" from C++ check.
		Map<String, Map<String, String>> postProcessingAdjustInlcudesComponents = generatePostProcessingAdjustIncludes(
				fromKeyword + " " + componentCodeFolderNameVariableName, 
				directValueKeyword + " \"../components/",
				directValueKeyword + " \"");
		Map<String, Map<String, String>> postProcessingAdjustInlcudesLib = generatePostProcessingAdjustIncludes(
				fromKeyword + " " + componentCodeFolderNameVariableName, 
				directValueKeyword + " \"../lib/",
				directValueKeyword + " \"");
		Map<String, Map<String, String>> postProcessingAdjustInlcudesMessages = generatePostProcessingAdjustIncludes(
				fromKeyword + " " + componentCodeFolderNameVariableName, 
				directValueKeyword + " \"../messages/",
				directValueKeyword + " \"");
		Map<String, Map<String, String>> postProcessingAdjustInlcudesOperations = generatePostProcessingAdjustIncludes(
				fromKeyword + " " + componentCodeFolderNameVariableName, 
				directValueKeyword + " \"../operations/",
				directValueKeyword + " \"");
		Map<String, Map<String, String>> postProcessingAdjustInlcudesRTSCs = generatePostProcessingAdjustIncludes(
				fromKeyword + " " + componentCodeFolderNameVariableName, 
				directValueKeyword + " \"../RTSCs/",
				directValueKeyword + " \"");
		Map<String, Map<String, String>> postProcessingAdjustInlcudesTypes = generatePostProcessingAdjustIncludes(
				fromKeyword + " " + componentCodeFolderNameVariableName, 
				directValueKeyword + " \"../types/",
				directValueKeyword + " \"");
		Map<String, Map<String, String>> postProcessingMoveIncludeBefore_ifdef__cplusplusClock = PostProcessingMoveIncludeBefore_ifdef__cplusplus.generateDefaultOrExampleValues();
		Map<String, String> postProcessingMoveIncludeBefore_ifdef__cplusplusClockIns = postProcessingMoveIncludeBefore_ifdef__cplusplusClock.get(inKeyword);
		postProcessingMoveIncludeBefore_ifdef__cplusplusClockIns.put("componentsPath", 
				fromKeyword + " " + componentsFolderNameVariableName);
		postProcessingMoveIncludeBefore_ifdef__cplusplusClock.put(inKeyword, postProcessingMoveIncludeBefore_ifdef__cplusplusClockIns);
		
		// "fastAndSlowCar_v2/components":
		// Copy „coordinatorComponent_Interface.h“ and "coordinatorComponent.c" files into each  *CarCoordinatorECU folder.
		// Copy other files into each *CarDriverECU folder.
		Map<String, Map<String, String>> postProcessingCopyFolderContentsToECUsWhitelistComponents1 = generatePostProcessingCopyFolderContentsToECUsWhitelist(
				fromKeyword + " " + componentsFolderNameVariableName,
				fromKeyword + " " + deployableCodeFolderNameVariableName,
				directValueKeyword + " CarCoordinatorECU",
				directValueKeyword + " coordinatorComponent_Interface.h, coordinatorComponent.c");
		Map<String, Map<String, String>> postProcessingCopyFolderContentsToECUsAndExceptComponents2 = generatePostProcessingCopyFolderContentsToECUsAndExcept(
				fromKeyword + " " + componentsFolderNameVariableName,
				fromKeyword + " " + deployableCodeFolderNameVariableName,
				directValueKeyword + " CarDriverECU",
				directValueKeyword + " coordinatorComponent_Interface.h, coordinatorComponent.c");
		
		// fastAndSlowCar_v2/lib: Copy all files except „clock.h“ and „standardTypes.h“ into the *ECU folders.
		Map<String, Map<String, String>> postProcessingCopyFolderContentsToECUsAndExceptLib = generatePostProcessingCopyFolderContentsToECUsAndExcept(
				fromKeyword + " " + libFolderNameVariableName,
				fromKeyword + " " + deployableCodeFolderNameVariableName,
				directValueKeyword + " ECU",
				directValueKeyword + " clock.h, standardTypes.h");
		
		// fastAndSlowCar_v2/messages: Copy "messages_types.h" into the *ECU folders.
		// (Only "messages_types.h" is in the messages folder, but for more flexibility a copy everything search is still done.)
		Map<String, Map<String, String>> postProcessingCopyFolderContentsToECUsAndExceptMessages = generatePostProcessingCopyFolderContentsToECUsAndExcept(
				fromKeyword + " " + messagesFolderNameVariableName,
				fromKeyword + " " + deployableCodeFolderNameVariableName,
				directValueKeyword + " ECU",
				directValueKeyword + " none");
		
		// fastAndSlowCar_v2/operations: Copy all files into the *CarDriverECU folders.
		Map<String, Map<String, String>> postProcessingCopyFolderContentsToECUsAndExceptOperations = generatePostProcessingCopyFolderContentsToECUsAndExcept(
				fromKeyword + " " + operationsFolderNameVariableName,
				fromKeyword + " " + deployableCodeFolderNameVariableName,
				directValueKeyword + " CarDriverECU",
				directValueKeyword + " none");
		
		// fastAndSlowCar_v2/RTSCs:
	    //1. Copy „coordinatorCoordinatorComponentStateChart.c“  into the *CarCoordinatorECU folders.
	    //2. Copy „driveControlDriveControlComponentStateChart.c“ into the *CarDriverECU folders.
		Map<String, Map<String, String>> postProcessingCopyFolderContentsToECUsWhitelistRTSCs1 = generatePostProcessingCopyFolderContentsToECUsWhitelist(
				fromKeyword + " " + RTSCsFolderNameVariableName,
				fromKeyword + " " + deployableCodeFolderNameVariableName,
				directValueKeyword + " CarCoordinatorECU",
				directValueKeyword + " coordinatorCoordinatorComponentStateChart.c");
		Map<String, Map<String, String>> postProcessingCopyFolderContentsToECUsWhitelistRTSCs2 = generatePostProcessingCopyFolderContentsToECUsWhitelist(
				fromKeyword + " " + RTSCsFolderNameVariableName,
				fromKeyword + " " + deployableCodeFolderNameVariableName,
				directValueKeyword + " CarDriverECU",
				directValueKeyword + " courseControlCourseControlComponentStateChart.c");
		
		// fastAndSlowCar_v2/types: Copy all files into the *ECU folders.
		Map<String, Map<String, String>> postProcessingCopyFolderContentsToECUsAndExceptTypes = generatePostProcessingCopyFolderContentsToECUsAndExcept(
				fromKeyword + " " + typesFolderNameVariableName,
				fromKeyword + " " + deployableCodeFolderNameVariableName,
				directValueKeyword + " ECU",
				directValueKeyword + " none");
		

		/*
		 APImappings: From later: 
		 Fill the method stubs in the API Mapping files
           1. In every API-Mapping include: (recommended in the `.c` file, not the `.h` file to only have the import localy) `SimpleHardwareController_Connector.h`:
           2. frontDistance: ```*distance = SimpleHardwareController_DistanceSensor_GetDistanceToClosestMm(0);```
           3. rearDistance: ```*distance = SimpleHardwareController_DistanceSensor_GetDistanceToClosestMm(1);```
           4. velocity: ```*velocity = SimpleHardwareController_DriveController_GetSpeed();```
           [For our modifications: 5. angle: "*angle = SimpleHardwareController_DriveController_GetAngle();"]
		
		 APImappings:
		 Old:
           1. Copy all files with „*F*“ into the folder fastCarDriverECU.
           CI_FRONTDISTANCESENSORFDISTANCESENSORdistancePortaccessCommand.c
           CI_FRONTDISTANCESENSORFDISTANCESENSORdistancePortaccessCommand.h
           CI_POWERTRAINFPOWERTRAINvelocityPortaccessCommand.c
           CI_POWERTRAINFPOWERTRAINvelocityPortaccessCommand.h
           CI_REARDISTANCESENSORFDISTANCESENSORdistancePortaccessCommand.c
           CI_REARDISTANCESENSORFDISTANCESENSORdistancePortaccessCommand.h

           2. Copy all files with „*S*“ into the folder fastCarDriverECU.
           CI_FRONTDISTANCESENSORSDISTANCESENSORdistancePortaccessCommand.c
           CI_FRONTDISTANCESENSORSDISTANCESENSORdistancePortaccessCommand.h
           CI_POWERTRAINSPOWERTRAINvelocityPortaccessCommand.c
           CI_POWERTRAINSPOWERTRAINvelocityPortaccessCommand.h
           CI_REARDISTANCESENSORSDISTANCESENSORdistancePortaccessCommand.c
           CI_REARDISTANCESENSORSDISTANCESENSORdistancePortaccessCommand.h
           
		 For our modifications on the MUML models:
           1. Copy all files with „*F*“ into the folder fastCarDriverECU.
           CI_FRONTDISTANCESENSORFDISTANCESENSORdistancePortaccessCommand.c
			CI_FRONTDISTANCESENSORFDISTANCESENSORdistancePortaccessCommand.h
			CI_DRIVECONTROLLERFDRIVECONTROLLERvelocityPortaccessCommand.c
			CI_DRIVECONTROLLERFDRIVECONTROLLERvelocityPortaccessCommand.h
			CI_DRIVECONTROLLERFDRIVECONTROLLERanglePortaccessCommand.c
			CI_DRIVECONTROLLERFDRIVECONTROLLERanglePortaccessCommand.h
			CI_REARDISTANCESENSORFDISTANCESENSORdistancePortaccessCommand.c
			CI_REARDISTANCESENSORFDISTANCESENSORdistancePortaccessCommand.h

           2. Copy all files with „*S*“ into the folder slowCarDriverECU.
           CI_FRONTDISTANCESENSORSDISTANCESENSORdistancePortaccessCommand.c
			CI_FRONTDISTANCESENSORSDISTANCESENSORdistancePortaccessCommand.h
			CI_DRIVECONTROLLERSDRIVECONTROLLERvelocityPortaccessCommand.c
			CI_DRIVECONTROLLERSDRIVECONTROLLERvelocityPortaccessCommand.h
			CI_DRIVECONTROLLERSDRIVECONTROLLERanglePortaccessCommand.c
			CI_DRIVECONTROLLERSDRIVECONTROLLERanglePortaccessCommand.h
			CI_REARDISTANCESENSORSDISTANCESENSORdistancePortaccessCommand.c
			CI_REARDISTANCESENSORSDISTANCESENSORdistancePortaccessCommand.h
		*/
		
		Map<String, Map<String, String>> postProcessingFrontDistanceSensorFastCarAPI = generatePostProcessingAdjustAPIMappingFile(
				fromKeyword + " " + apiMappingsFolderNameVariableName,
				fromKeyword + " " + fastCarDriverECUFolderPathVariableName,
				directValueKeyword + " CI_FRONTDISTANCESENSORFDISTANCESENSORdistancePortaccessCommand.c",
				directValueKeyword + " SimpleHardwareController_Connector.h", 
				directValueKeyword + " *distance = SimpleHardwareController_DistanceSensor_GetDistanceToClosestMm(0);");

		Map<String, Map<String, String>> postProcessingRearDistanceSensorFastCarAPI = generatePostProcessingAdjustAPIMappingFile(
				fromKeyword + " " + apiMappingsFolderNameVariableName,
				fromKeyword + " " + fastCarDriverECUFolderPathVariableName,
				directValueKeyword + " CI_REARDISTANCESENSORFDISTANCESENSORdistancePortaccessCommand.c",
				directValueKeyword + " SimpleHardwareController_Connector.h", 
				directValueKeyword + " *distance = SimpleHardwareController_DistanceSensor_GetDistanceToClosestMm(1);");

		Map<String, Map<String, String>> postProcessingDriveControllerVelocityFastCarAPI = generatePostProcessingAdjustAPIMappingFile(
				fromKeyword + " " + apiMappingsFolderNameVariableName,
				fromKeyword + " " + fastCarDriverECUFolderPathVariableName,
				directValueKeyword + " CI_DRIVECONTROLLERFDRIVECONTROLLERvelocityPortaccessCommand.c",
				directValueKeyword + " SimpleHardwareController_Connector.h", 
				directValueKeyword + " *velocity = SimpleHardwareController_DriveController_GetSpeed();");

		Map<String, Map<String, String>> postProcessingDriveControllerAngleFastCarAPI = generatePostProcessingAdjustAPIMappingFile(
				fromKeyword + " " + apiMappingsFolderNameVariableName,
				fromKeyword + " " + fastCarDriverECUFolderPathVariableName,
				directValueKeyword + " CI_DRIVECONTROLLERFDRIVECONTROLLERanglePortaccessCommand.c",
				directValueKeyword + " SimpleHardwareController_Connector.h", 
				directValueKeyword + " *angle = SimpleHardwareController_DriveController_GetAngle();");

		
		Map<String, Map<String, String>> postProcessingFrontDistanceSensorSlowCarAPI = generatePostProcessingAdjustAPIMappingFile(
				fromKeyword + " " + apiMappingsFolderNameVariableName,
				fromKeyword + " " + slowCarDriverECUFolderPathVariableName,
				directValueKeyword + " CI_FRONTDISTANCESENSORSDISTANCESENSORdistancePortaccessCommand.c",
				directValueKeyword + " SimpleHardwareController_Connector.h", 
				directValueKeyword + " *distance = SimpleHardwareController_DistanceSensor_GetDistanceToClosestMm(0);");

		Map<String, Map<String, String>> postProcessingRearDistanceSensorSlowCarAPI = generatePostProcessingAdjustAPIMappingFile(
				fromKeyword + " " + apiMappingsFolderNameVariableName,
				fromKeyword + " " + slowCarDriverECUFolderPathVariableName,
				directValueKeyword + " CI_REARDISTANCESENSORSDISTANCESENSORdistancePortaccessCommand.c",
				directValueKeyword + " SimpleHardwareController_Connector.h", 
				directValueKeyword + " *distance = SimpleHardwareController_DistanceSensor_GetDistanceToClosestMm(1);");

		Map<String, Map<String, String>> postProcessingDriveControllerVelocitySlowCarAPI = generatePostProcessingAdjustAPIMappingFile(
				fromKeyword + " " + apiMappingsFolderNameVariableName,
				fromKeyword + " " + slowCarDriverECUFolderPathVariableName,
				directValueKeyword + " CI_DRIVECONTROLLERSDRIVECONTROLLERvelocityPortaccessCommand.c",
				directValueKeyword + " SimpleHardwareController_Connector.h", 
				directValueKeyword + " *velocity = SimpleHardwareController_DriveController_GetSpeed();");

		Map<String, Map<String, String>> postProcessingDriveControllerAngleSlowCarAPI = generatePostProcessingAdjustAPIMappingFile(
				fromKeyword + " " + apiMappingsFolderNameVariableName,
				fromKeyword + " " + slowCarDriverECUFolderPathVariableName,
				directValueKeyword + " CI_DRIVECONTROLLERSDRIVECONTROLLERanglePortaccessCommand.c",
				directValueKeyword + " SimpleHardwareController_Connector.h", 
				directValueKeyword + " *angle = SimpleHardwareController_DriveController_GetAngle();");
		
		
		
		Map<String, Map<String, String>> postProcessingCopyAPIMappingsToFastCarDriverECU = generatePostProcessingCopyFiles(
				fromKeyword + " " + apiMappingsFolderNameVariableName,
				fromKeyword + " " + fastCarDriverECUFolderPathVariableName,
				directValueKeyword + " CI_FRONTDISTANCESENSORFDISTANCESENSORdistancePortaccessCommand.h, "
						+ "CI_DRIVECONTROLLERFDRIVECONTROLLERvelocityPortaccessCommand.h, "
						+ "CI_DRIVECONTROLLERFDRIVECONTROLLERanglePortaccessCommand.h, "
						+ "CI_REARDISTANCESENSORFDISTANCESENSORdistancePortaccessCommand.h");

		Map<String, Map<String, String>> postProcessingCopyAPIMappingsToSlowCarDriverECU = generatePostProcessingCopyFiles(
				fromKeyword + " " + apiMappingsFolderNameVariableName,
				fromKeyword + " " + slowCarDriverECUFolderPathVariableName,
				directValueKeyword + " CI_FRONTDISTANCESENSORSDISTANCESENSORdistancePortaccessCommand.h, "
						+ "CI_DRIVECONTROLLERSDRIVECONTROLLERvelocityPortaccessCommand.h, "
						+ "CI_DRIVECONTROLLERSDRIVECONTROLLERanglePortaccessCommand.h, "
						+ "CI_REARDISTANCESENSORSDISTANCESENSORdistancePortaccessCommand.h");

		Map<String, Map<String, String>> postProcessingCopyLocalConfig_hppToCarDeriverECUs = PostProcessingCopyLocalConfig_hppToCarDeriverECUs.generateDefaultOrExampleValues();
		Map<String, String> postProcessingCopyLocalConfig_hppToCarDeriverECUsIns = postProcessingCopyLocalConfig_hppToCarDeriverECUs.get(inKeyword);
		postProcessingCopyLocalConfig_hppToCarDeriverECUsIns.put("arduinoContainersPath", fromKeyword + " " + deployableCodeFolderNameVariableName);
		postProcessingCopyLocalConfig_hppToCarDeriverECUs.put(inKeyword, postProcessingCopyLocalConfig_hppToCarDeriverECUsIns);

		Map<String, Map<String, String>> postProcessingAddHALPartsIntoCarDriverInoFiles = PostProcessingAddHALPartsIntoCarDriverInoFiles.generateDefaultOrExampleValues();
		Map<String, String> postProcessingAddHALPartsIntoCarDriverInoFilesIns = postProcessingAddHALPartsIntoCarDriverInoFiles.get(inKeyword);
		postProcessingAddHALPartsIntoCarDriverInoFilesIns.put("arduinoContainersPath", fromKeyword + " " + deployableCodeFolderNameVariableName);
		postProcessingAddHALPartsIntoCarDriverInoFiles.put(inKeyword, postProcessingAddHALPartsIntoCarDriverInoFilesIns);

		Map<String, Map<String, String>> postProcessingFillOutMethodStubs = PostProcessingFillOutMethodStubs.generateDefaultOrExampleValues();
		Map<String, String> postProcessingFillOutMethodStubsIns = postProcessingFillOutMethodStubs.get(inKeyword);
		postProcessingFillOutMethodStubsIns.put("arduinoContainersPath", fromKeyword + " " + deployableCodeFolderNameVariableName);
		postProcessingFillOutMethodStubs.put(inKeyword, postProcessingFillOutMethodStubsIns);
		
		
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
		
		/*
		 * Additional postprocessing for communication settings.
		 */
		
		Map<String, Map<String, String>> postProcessingAdjustSerialCommunicationSizes = PostProcessingAdjustSerialCommunicationSizes.generateDefaultOrExampleValues();
		Map<String, String> postProcessingAdjustSerialCommunicationSizesIns = postProcessingAdjustSerialCommunicationSizes.get(inKeyword);
		postProcessingAdjustSerialCommunicationSizesIns.put("arduinoContainersPath", fromKeyword + " " + deployableCodeFolderNameVariableName);
		postProcessingAdjustSerialCommunicationSizes.put(inKeyword, postProcessingAdjustSerialCommunicationSizesIns);
		
		Map<String, Map<String, String>> postProcessingConfigureWLAN = PostProcessingConfigureWLANSettings.generateDefaultOrExampleValues();
		Map<String, String> postProcessingConfigureWLANIns = postProcessingConfigureWLAN.get(inKeyword);
		postProcessingConfigureWLANIns.put("arduinoContainersPath", fromKeyword + " " + deployableCodeFolderNameVariableName);
		postProcessingConfigureWLANIns.put("nameOrSSID", fromKeyword + " " + WLANNameOrSSIDVariableName);
		postProcessingConfigureWLANIns.put("password", fromKeyword + " " + WLANPasswordVariableName);
		postProcessingConfigureWLAN.put(inKeyword, postProcessingConfigureWLANIns);
		
		Map<String, Map<String, String>> postProcessingConfigureMQTTFastCoordinator = PostProcessingConfigureMQTTSettings.generateDefaultOrExampleValues();
		Map<String, String> postProcessingConfigureMQTTFastCoordinatorIns = postProcessingConfigureMQTTFastCoordinator.get(inKeyword);
		postProcessingConfigureMQTTFastCoordinatorIns.put("arduinoContainersPath", fromKeyword + " " + deployableCodeFolderNameVariableName);
		postProcessingConfigureMQTTFastCoordinatorIns.put("ecuName", fromKeyword + " " + fastCarCoordinatorECUNameVariableName);
		postProcessingConfigureMQTTFastCoordinatorIns.put("serverIPAddress", fromKeyword + " " + MQTTServerIPAddressVariableName);
		postProcessingConfigureMQTTFastCoordinatorIns.put("serverPort", fromKeyword + " " + MQTTServerPortVariableName);
		postProcessingConfigureMQTTFastCoordinatorIns.put("clientName", fromKeyword + " " + fastCarCoordinatorECUNameVariableName);
		postProcessingConfigureMQTTFastCoordinator.put(inKeyword, postProcessingConfigureMQTTFastCoordinatorIns);

		Map<String, Map<String, String>> postProcessingConfigureMQTTSlowCoordinator = PostProcessingConfigureMQTTSettings.generateDefaultOrExampleValues();
		Map<String, String> postProcessingConfigureMQTTSlowCoordinatorIns = postProcessingConfigureMQTTSlowCoordinator.get(inKeyword);
		postProcessingConfigureMQTTSlowCoordinatorIns.put("arduinoContainersPath", fromKeyword + " " + deployableCodeFolderNameVariableName);
		postProcessingConfigureMQTTSlowCoordinatorIns.put("ecuName", fromKeyword + " " + slowCarCoordinatorECUNameVariableName);
		postProcessingConfigureMQTTSlowCoordinatorIns.put("serverIPAddress", fromKeyword + " " + MQTTServerIPAddressVariableName);
		postProcessingConfigureMQTTSlowCoordinatorIns.put("serverPort", fromKeyword + " " + MQTTServerPortVariableName);
		postProcessingConfigureMQTTSlowCoordinatorIns.put("clientName", fromKeyword + " " + slowCarCoordinatorECUNameVariableName);
		postProcessingConfigureMQTTSlowCoordinator.put(inKeyword, postProcessingConfigureMQTTSlowCoordinatorIns);
		
		
		
		//PipelineSequence
		
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

		Map<String, Map<String, String>> windowMessageFinishedPostProcessing = DialogMessage
				.generateDefaultOrExampleValues();
		Map<String, String> windowMessageFinishedPostProcessingSettingsIns = windowMessageFinishedPostProcessing
				.get(inKeyword);
		windowMessageFinishedPostProcessingSettingsIns.put("message",
				directValueKeyword + " Post-Processing execution completed!");
		windowMessageFinishedPostProcessing.put(inKeyword, windowMessageFinishedPostProcessingSettingsIns);
		
		Map<String, Map<String, String>> windowMessageFinishedPipeline = DialogMessage
				.generateDefaultOrExampleValues();
		Map<String, String> windowMessageFinishedPipelineSettingsIns = windowMessageFinishedPipeline
				.get(inKeyword);
		windowMessageFinishedPipelineSettingsIns.put("message",
				directValueKeyword + " Pipeline execution completed!");
		windowMessageFinishedPipeline.put(inKeyword, windowMessageFinishedPipelineSettingsIns);
		
		// Now the pipeline settings and sequences:
		Map<String, Object> mapForPipelineSettings = new LinkedHashMap<String, Object>();

		// Use default values and generate the config file with default values.
		ArrayList<String> defaultVariableDefs = new ArrayList<String>();
		
		defaultVariableDefs.add(generateVariableDefStructure("ExampleNumberVariableName", "12", NumberType));
		defaultVariableDefs.add(generateVariableDefStructure("ExampleStringVariableName", "ExampleString", StringType));
		defaultVariableDefs.add(generateVariableDefStructure("ExampleBooleanVariableName", "true", BooleanType));

		defaultVariableDefs.add(generateVariableDefStructure(generatedCodeFolderNameVariableName, generatedCodeFolderNameVariableValue, FolderPathType));
		defaultVariableDefs.add(generateVariableDefStructure(muml_containerFilePathVariableName, muml_containerFilePathVariableValue, FilePathType));
		defaultVariableDefs.add(generateVariableDefStructure(componentCodeFolderNameVariableName, componentCodeFolderNameVariableValue, FolderPathType));
		defaultVariableDefs.add(generateVariableDefStructure(apiMappingsFolderNameVariableName, apiMappingsFolderNameVariableValue, FolderPathType));
		defaultVariableDefs.add(generateVariableDefStructure(componentsFolderNameVariableName, componentsFolderNameVariableValue, FolderPathType));
		defaultVariableDefs.add(generateVariableDefStructure(libFolderNameVariableName, libFolderNameVariableValue, FolderPathType));
		defaultVariableDefs.add(generateVariableDefStructure(messagesFolderNameVariableName, messagesFolderNameVariableValue, FolderPathType));
		defaultVariableDefs.add(generateVariableDefStructure(operationsFolderNameVariableName, operationsFolderNameVariableValue, FolderPathType));
		defaultVariableDefs.add(generateVariableDefStructure(RTSCsFolderNameVariableName, RTSCsFolderNameVariableValue, FolderPathType));
		defaultVariableDefs.add(generateVariableDefStructure(typesFolderNameVariableName, typesFolderNameVariableValue, FolderPathType));
		defaultVariableDefs.add(generateVariableDefStructure(deployableCodeFolderNameVariableName, deployableCodeFolderNameVariableValue, FolderPathType));

		defaultVariableDefs.add(generateVariableDefStructure(WLANNameOrSSIDVariableName, WLANNameOrSSIDVariableValue, WLANNameType));
		defaultVariableDefs.add(generateVariableDefStructure(WLANPasswordVariableName, WLANPasswordVariableValue, WLANPasswordType));
		defaultVariableDefs.add(generateVariableDefStructure(MQTTServerIPAddressVariableName, MQTTServerIPAddressVariableValue, ServerIPAddressType));
		defaultVariableDefs.add(generateVariableDefStructure(MQTTServerPortVariableName, MQTTServerPortVariableValue, ServerPortType));
		
		defaultVariableDefs.add(generateVariableDefStructure(fastCarDesiredVelocityVariableName, fastCarDesiredVelocityVariableValue, NumberType));
		defaultVariableDefs.add(generateVariableDefStructure(slowCarDesiredVelocityVariableName, slowCarDesiredVelocityVariableValue, NumberType));
		
		defaultVariableDefs.add(generateVariableDefStructure(usedDriverBoardIdentifierFQBNVariableName, usedDriverBoardIdentifierFQBNVariableValue, BoardIdentifierFQBNType));
		defaultVariableDefs.add(generateVariableDefStructure(usedCoordinatorBoardIdentifierFQBNVariableName, usedCoordinatorBoardIdentifierFQBNVariableValue, BoardIdentifierFQBNType));

		defaultVariableDefs.add(generateVariableDefStructure(fastCarCoordinatorECUNameVariableName, fastCarCoordinatorECUNameVariableValue, StringType));
		defaultVariableDefs.add(generateVariableDefStructure(fastCarCoordinatorECUBoardSerialNumberVariableName, fastCarCoordinatorECUBoardSerialNumberVariableValue, BoardSerialNumberType));
		defaultVariableDefs.add(generateVariableDefStructure(fastCarDriverECUBoardSerialNumberVariableName, fastCarDriverECUBoardSerialNumberVariableValue, BoardSerialNumberType));
		
		defaultVariableDefs.add(generateVariableDefStructure(slowCarCoordinatorECUNameVariableName, slowCarCoordinatorECUNameVariableValue, StringType));
		defaultVariableDefs.add(generateVariableDefStructure(slowCarCoordinatorECUBoardSerialNumberVariableName, slowCarCoordinatorECUBoardSerialNumberVariableValue, BoardSerialNumberType));
		defaultVariableDefs.add(generateVariableDefStructure(slowCarDriverECUBoardSerialNumberVariableName, slowCarDriverECUBoardSerialNumberVariableValue, BoardSerialNumberType));
		
		defaultVariableDefs.add(generateVariableDefStructure(fastCarCoordinatorECUFolderPathVariableName, fastCarCoordinatorECUFolderPathVariableValue, FolderPathType));
		defaultVariableDefs.add(generateVariableDefStructure(fastCarCoordinatorECUINOFilePathVariableName, fastCarCoordinatorECUINOFilePathVariableValue, FilePathType));
		defaultVariableDefs.add(generateVariableDefStructure(fastCarCoordinatorECUINOHEXFilePathVariableName, fastCarCoordinatorECUINOHEXFilePathVariableValue, FilePathType));

		defaultVariableDefs.add(generateVariableDefStructure(fastCarDriverECUFolderPathVariableName, fastCarDriverECUFolderPathVariableValue, FolderPathType));
		defaultVariableDefs.add(generateVariableDefStructure(fastCarDriverECUINOFilePathVariableName, fastCarDriverECUINOFilePathVariableValue, FilePathType));
		defaultVariableDefs.add(generateVariableDefStructure(fastCarDriverECUINOHEXFilePathVariableName, fastCarDriverECUINOHEXFilePathVariableValue, FilePathType));

		defaultVariableDefs.add(generateVariableDefStructure(slowCarCoordinatorECUFolderPathVariableName, slowCarCoordinatorECUFolderPathVariableValue, FolderPathType));
		defaultVariableDefs.add(generateVariableDefStructure(slowCarCoordinatorECUINOFilePathVariableName, slowCarCoordinatorECUINOFilePathVariableValue, FilePathType));
		defaultVariableDefs.add(generateVariableDefStructure(slowCarCoordinatorECUINOHEXFilePathVariableName, slowCarCoordinatorECUINOHEXFilePathVariableValue, FilePathType));

		defaultVariableDefs.add(generateVariableDefStructure(slowCarDriverECUFolderPathVariableName, slowCarDriverECUFolderPathVariableValue, FolderPathType));
		defaultVariableDefs.add(generateVariableDefStructure(slowCarDriverECUINOFilePathVariableName, slowCarDriverECUINOFilePathVariableValue, FilePathType));
		defaultVariableDefs.add(generateVariableDefStructure(slowCarDriverECUINOHEXFilePathVariableName, slowCarDriverECUINOHEXFilePathVariableValue, FilePathType));

		mapForPipelineSettings.put(variableDefsKeyword, defaultVariableDefs);

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
		//defaultStandaloneTransformationAndGenerationsDefsKeyword.put(PostProcessingStepsUntilConfig.nameFlag,
		//		PostProcessingStepsUntilConfig.generateDefaultOrExampleValues());
		mapForPipelineSettings.put(transformationAndCodeGenerationPreconfigurationsDefKeyword,
				defaultStandaloneTransformationAndGenerationsDefsKeyword);

		// For better/easier readability blank lines inbetween:
		settingsWriter.write(yaml.dump(mapForPipelineSettings));
		mapForPipelineSettings.clear();
		settingsWriter.write("\n\n");

		ArrayList<Object> defaultPostProcessingSequenceDefs = new ArrayList<Object>();

		defaultPostProcessingSequenceDefs.add(pipelineSegmentHelper(yaml,
				directValueKeyword + " " + DeleteFolder.nameFlag, deleteDirectoryDeployableFiles));

		defaultPostProcessingSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + CopyFolder.nameFlag, copyFolderGeneratedDeployable));
		defaultPostProcessingSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortCopyFolder));

		/*defaultPostProcessingSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingStepsUntilConfig.nameFlag,
						postProcessingUntilConfigSettings));
		defaultPostProcessingSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortPostProcessingUntilConfig));*/

		
		defaultPostProcessingSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingAdjustIncludes.nameFlag,
				postProcessingAdjustInlcudesComponents));

		defaultPostProcessingSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingAdjustIncludes.nameFlag,
				postProcessingAdjustInlcudesLib));

		defaultPostProcessingSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingAdjustIncludes.nameFlag,
				postProcessingAdjustInlcudesMessages));

		defaultPostProcessingSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingAdjustIncludes.nameFlag,
				postProcessingAdjustInlcudesOperations));

		defaultPostProcessingSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingAdjustIncludes.nameFlag,
				postProcessingAdjustInlcudesRTSCs));

		defaultPostProcessingSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingAdjustIncludes.nameFlag,
				postProcessingAdjustInlcudesTypes));

		defaultPostProcessingSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingMoveIncludeBefore_ifdef__cplusplus.nameFlag,
				postProcessingMoveIncludeBefore_ifdef__cplusplusClock));

		defaultPostProcessingSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingCopyFolderContentsToECUsWhitelist.nameFlag,
				postProcessingCopyFolderContentsToECUsWhitelistComponents1));

		defaultPostProcessingSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingCopyFolderContentsToECUsAndExcept.nameFlag,
				postProcessingCopyFolderContentsToECUsAndExceptComponents2));

		defaultPostProcessingSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingCopyFolderContentsToECUsAndExcept.nameFlag,
				postProcessingCopyFolderContentsToECUsAndExceptLib));

		defaultPostProcessingSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingCopyFolderContentsToECUsAndExcept.nameFlag,
				postProcessingCopyFolderContentsToECUsAndExceptMessages));

		defaultPostProcessingSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingCopyFolderContentsToECUsAndExcept.nameFlag,
				postProcessingCopyFolderContentsToECUsAndExceptOperations));

		defaultPostProcessingSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingCopyFolderContentsToECUsWhitelist.nameFlag,
				postProcessingCopyFolderContentsToECUsWhitelistRTSCs1));

		defaultPostProcessingSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingCopyFolderContentsToECUsWhitelist.nameFlag,
				postProcessingCopyFolderContentsToECUsWhitelistRTSCs2));

		defaultPostProcessingSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingCopyFolderContentsToECUsAndExcept.nameFlag,
				postProcessingCopyFolderContentsToECUsAndExceptTypes));

		defaultPostProcessingSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingAdjustAPIMappingFile.nameFlag,
				postProcessingFrontDistanceSensorFastCarAPI));

		defaultPostProcessingSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingAdjustAPIMappingFile.nameFlag,
				postProcessingRearDistanceSensorFastCarAPI));

		defaultPostProcessingSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingAdjustAPIMappingFile.nameFlag,
				postProcessingDriveControllerVelocityFastCarAPI));

		defaultPostProcessingSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingAdjustAPIMappingFile.nameFlag,
				postProcessingDriveControllerAngleFastCarAPI));

		defaultPostProcessingSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingAdjustAPIMappingFile.nameFlag,
				postProcessingFrontDistanceSensorSlowCarAPI));

		defaultPostProcessingSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingAdjustAPIMappingFile.nameFlag,
				postProcessingRearDistanceSensorSlowCarAPI));

		defaultPostProcessingSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingAdjustAPIMappingFile.nameFlag,
				postProcessingDriveControllerVelocitySlowCarAPI));

		defaultPostProcessingSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingAdjustAPIMappingFile.nameFlag,
				postProcessingDriveControllerAngleSlowCarAPI));

		defaultPostProcessingSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + CopyFiles.nameFlag,
				postProcessingCopyAPIMappingsToFastCarDriverECU));

		defaultPostProcessingSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + CopyFiles.nameFlag,
				postProcessingCopyAPIMappingsToSlowCarDriverECU));

		defaultPostProcessingSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingCopyLocalConfig_hppToCarDeriverECUs.nameFlag,
				postProcessingCopyLocalConfig_hppToCarDeriverECUs));

		defaultPostProcessingSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingAddHALPartsIntoCarDriverInoFiles.nameFlag,
				postProcessingAddHALPartsIntoCarDriverInoFiles));

		defaultPostProcessingSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingFillOutMethodStubs.nameFlag,
				postProcessingFillOutMethodStubs));
		
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

		defaultPostProcessingSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingAdjustSerialCommunicationSizes.nameFlag,
				postProcessingAdjustSerialCommunicationSizes));

		defaultPostProcessingSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingConfigureWLANSettings.nameFlag,
				postProcessingConfigureWLAN));

		defaultPostProcessingSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingConfigureMQTTSettings.nameFlag,
				postProcessingConfigureMQTTFastCoordinator));

		defaultPostProcessingSequenceDefs
		.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + PostProcessingConfigureMQTTSettings.nameFlag,
				postProcessingConfigureMQTTSlowCoordinator));
		

		defaultPostProcessingSequenceDefs.add(pipelineSegmentHelper(yaml,
				directValueKeyword + " " + DialogMessage.nameFlag, windowMessageFinishedPostProcessing));
		
		mapForPipelineSettings.put(postProcessingSequenceDefKeyword, defaultPostProcessingSequenceDefs);

		// For better/easier readability blank lines inbetween:
		settingsWriter.write(yaml.dump(mapForPipelineSettings));
		mapForPipelineSettings.clear();
		settingsWriter.write("\n\n");

		ArrayList<Object> defaultPipelineSequenceDefs = new ArrayList<Object>();

		defaultPipelineSequenceDefs.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + DeleteFolder.nameFlag,
				deleteDirectoryGeneratedFiles));
		defaultPipelineSequenceDefs.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + DeleteFolder.nameFlag,
				deleteDirectoryDeployableFiles));

		//defaultPipelineSequenceDefs.add(fromKeyword + " " + transformationAndCodeGenerationPreconfigurationsDefKeyword + ": "
		//		+ ContainerTransformation.nameFlag);
		LinkedHashMap<String, String> loadContainerTransformation = new LinkedHashMap<String, String>();
		loadContainerTransformation.put(fromKeyword + " " + transformationAndCodeGenerationPreconfigurationsDefKeyword, ContainerTransformation.nameFlag);
		defaultPipelineSequenceDefs.add(loadContainerTransformation);
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortContainerTransformation));

		//defaultPipelineSequenceDefs.add(fromKeyword + " " + transformationAndCodeGenerationPreconfigurationsDefKeyword + ": "
		//		+ ContainerCodeGeneration.nameFlag);
		LinkedHashMap<String, String> loadContainerCodeGeneration = new LinkedHashMap<String, String>();
		loadContainerCodeGeneration.put(fromKeyword + " " + transformationAndCodeGenerationPreconfigurationsDefKeyword, ContainerCodeGeneration.nameFlag);
		defaultPipelineSequenceDefs.add(loadContainerCodeGeneration);
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortContainerCodeGeneration));

		//defaultPipelineSequenceDefs.add(fromKeyword + " " + transformationAndCodeGenerationPreconfigurationsDefKeyword + ": "
		//		+ ComponentCodeGeneration.nameFlag);
		LinkedHashMap<String, String> loadComponentCodeGeneration = new LinkedHashMap<String, String>();
		loadComponentCodeGeneration.put(fromKeyword + " " + transformationAndCodeGenerationPreconfigurationsDefKeyword, ComponentCodeGeneration.nameFlag);
		defaultPipelineSequenceDefs.add(loadComponentCodeGeneration);
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortComponentCode));

		//defaultPipelineSequenceDefs.add(
		//		fromKeyword + " " + postProcessingSequenceDefKeyword + ": " + allKeyword);
		LinkedHashMap<String, String> loadPostProcessing = new LinkedHashMap<String, String>();
		loadPostProcessing.put(fromKeyword + " " + postProcessingSequenceDefKeyword, allKeyword);
		defaultPipelineSequenceDefs.add(loadPostProcessing);

		//Deep copies of maps are difficult in java, so this gets a bit redundant.
		defaultPipelineSequenceDefs.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + Compile.nameFlag,
				compileSettingsFastCoordinator));
		Map<String, Map<String, String>> compileReport = DialogMessage
				.generateDefaultOrExampleValues();
		Map<String, String> compileReportSettingsIns = compileReport.get(inKeyword);
		compileReportSettingsIns.put("condition",
				notKeyword + " " + fromKeyword + " ifSuccessful");
		compileReportSettingsIns.put("message",
				fromKeyword + " resultMessage");
		compileReport.put(inKeyword, compileReportSettingsIns);

		defaultPipelineSequenceDefs.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + DialogMessage.nameFlag,
				compileReport));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortCompileFastCoordinator));
		
		defaultPipelineSequenceDefs.add(
				pipelineSegmentHelper(yaml, directValueKeyword + " " + Compile.nameFlag, compileSettingsFastDriver));
		compileReport = DialogMessage
				.generateDefaultOrExampleValues();
		compileReportSettingsIns = compileReport.get(inKeyword);
		compileReportSettingsIns.put("condition",
				notKeyword + " " + fromKeyword + " ifSuccessful");
		compileReportSettingsIns.put("message",
				fromKeyword + " resultMessage");
		compileReport.put(inKeyword, compileReportSettingsIns);
		defaultPipelineSequenceDefs.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + DialogMessage.nameFlag,
				compileReport));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortCompileFastDriver));
		
		defaultPipelineSequenceDefs.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + Compile.nameFlag,
				compileSettingsSlowCoordinator));
		compileReport = DialogMessage
				.generateDefaultOrExampleValues();
		compileReportSettingsIns = compileReport.get(inKeyword);
		compileReportSettingsIns.put("condition",
				notKeyword + " " + fromKeyword + " ifSuccessful");
		compileReportSettingsIns.put("message",
				fromKeyword + " resultMessage");
		compileReport.put(inKeyword, compileReportSettingsIns);
		defaultPipelineSequenceDefs.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + DialogMessage.nameFlag,
				compileReport));
		defaultPipelineSequenceDefs
				.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + OnlyContinueIfFulfilledElseAbort.nameFlag,
						onlyContinueIfFulfilledElseAbortCompileSlowCoordinator));
		
		defaultPipelineSequenceDefs.add(
				pipelineSegmentHelper(yaml, directValueKeyword + " " + Compile.nameFlag, compileSettingsSlowDriver));
		compileReport = DialogMessage
				.generateDefaultOrExampleValues();
		compileReportSettingsIns = compileReport.get(inKeyword);
		compileReportSettingsIns.put("condition",
				notKeyword + " " + fromKeyword + " ifSuccessful");
		compileReportSettingsIns.put("message",
				fromKeyword + " resultMessage");
		compileReport.put(inKeyword, compileReportSettingsIns);
		defaultPipelineSequenceDefs.add(pipelineSegmentHelper(yaml, directValueKeyword + " " + DialogMessage.nameFlag,
				compileReport));
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
				directValueKeyword + " " + DialogMessage.nameFlag, windowMessageFinishedPipeline));

		mapForPipelineSettings.put(pipelineSequenceDefKeyword, defaultPipelineSequenceDefs);

		settingsWriter.write(yaml.dump(mapForPipelineSettings));
		settingsWriter.close();

		// For the # Or DDS_CONFIG
		String intermediateFileName = completeMUMLACGPPASettingsFilePath.toString() + ".editing";
		FileWriter workCopy = new FileWriter(intermediateFileName);
		Scanner currentFileReader = new Scanner(completeMUMLACGPPASettingsFilePath.toFile());
		while (currentFileReader.hasNextLine()) {
			String currentLine = currentFileReader.nextLine();
			if (currentLine.contains("MQTT_I2C_CONFIG")) {
				String intermediate = currentLine + " # Or DDS_CONFIG";
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
