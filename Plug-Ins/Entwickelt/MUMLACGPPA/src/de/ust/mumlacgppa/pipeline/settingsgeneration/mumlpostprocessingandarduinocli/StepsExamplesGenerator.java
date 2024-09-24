package de.ust.mumlacgppa.pipeline.settingsgeneration.mumlpostprocessingandarduinocli;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import de.ust.mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import de.ust.mumlacgppa.pipeline.parts.steps.Keywords;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.AutoGitCommitAllAndPushCommand;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.Compile;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ComponentCodeGeneration;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ContainerCodeGeneration;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ContainerTransformation;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.CopyFiles;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.CopyFolder;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.CreateFolder;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.DeleteFile;
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
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingDownloadConfig_hpp;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingFillOutMethodStubs;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingInsertAtLineIndex;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingMoveIncludeBefore_ifdef__cplusplus;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingStateChartValues;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingStateChartValuesFlexible;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ReplaceLineContent;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.SaveToTextFile;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.SelectableTextWindow;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.TerminalCommand;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.Upload;
import de.ust.mumlacgppa.pipeline.paths.PipelineSettingsDirectoryAndFilesPaths;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

public class StepsExamplesGenerator implements PipelineSettingsDirectoryAndFilesPaths, Keywords{
	private Path completeSettingsDirectoryPath;
	private Path completeExamplesFilePath;
	
	public StepsExamplesGenerator() throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super();
		if(ProjectFolderPathStorage.projectFolderPath == null){
			throw new ProjectFolderPathNotSetExceptionMUMLACGPPA(
					"The static field projectFolderPath in ProjectFolderPathStorage has "
					+ "to be set to a complete file system path to the project's folder!"
							);
		}
		completeSettingsDirectoryPath = ProjectFolderPathStorage.projectFolderPath.resolve(PIPELINE_SETTINGS_DIRECTORY_FOLDER);
		completeExamplesFilePath = completeSettingsDirectoryPath.resolve(STEP_EXAMPLES_FILE_NAME);
	}
	
	
	private String exampleSegment(Yaml yaml, String name, Map<String, Map<String, String>> exampleSettings){
		Map<String, Map<String, Map<String, String>>> exampleUsageDef = new LinkedHashMap<String, Map<String, Map<String, String>>>();
		exampleUsageDef.put(name, exampleSettings);
		return yaml.dump(exampleUsageDef);
	}
	
	
	public boolean generateExampleListFile() throws IOException{
		File directoryCheck = completeSettingsDirectoryPath.toFile();
		if (!directoryCheck.exists()){
		    directoryCheck.mkdirs();
		}
		
		File configExistenceCheck = completeExamplesFilePath.toFile();
		if(configExistenceCheck.exists() && configExistenceCheck.isFile()) {
			return false;
		}
		
		
	    // For DumperOptions examples see 
	    // https://www.tabnine.com/code/java/methods/org.yaml.snakeyaml.DumperOptions$LineBreak/getPlatformLineBreak
	    DumperOptions options = new DumperOptions();
	    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
	    options.setPrettyFlow(true);
	    options.setLineBreak(DumperOptions.LineBreak.getPlatformLineBreak());
	    Yaml yaml = new Yaml(options);
	    
		FileWriter myWriter = new FileWriter(completeExamplesFilePath.toFile());
		myWriter.write("These are all the steps, each represented by one example.\n\n\n");

		myWriter.write(exampleSegment(yaml, AutoGitCommitAllAndPushCommand.nameFlag, AutoGitCommitAllAndPushCommand.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, Compile.nameFlag, Compile.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, ComponentCodeGeneration.nameFlag, ComponentCodeGeneration.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, ContainerTransformation.nameFlag, ContainerTransformation.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, ContainerCodeGeneration.nameFlag, ContainerCodeGeneration.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, CopyFolder.nameFlag, CopyFolder.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, CopyFiles.nameFlag, CopyFiles.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, CreateFolder.nameFlag, CreateFolder.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, DeleteFile.nameFlag, DeleteFile.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, DeleteFolder.nameFlag, DeleteFolder.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, DialogMessage.nameFlag, DialogMessage.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, LookupBoardBySerialNumber.nameFlag, LookupBoardBySerialNumber.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, OnlyContinueIfFulfilledElseAbort.nameFlag, OnlyContinueIfFulfilledElseAbort.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, PostProcessingAddHALPartsIntoCarDriverInoFiles.nameFlag, PostProcessingAddHALPartsIntoCarDriverInoFiles.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, PostProcessingAdjustAPIMappingFile.nameFlag, PostProcessingAdjustAPIMappingFile.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, PostProcessingAdjustIncludes.nameFlag, PostProcessingAdjustIncludes.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, PostProcessingAdjustSerialCommunicationSizes.nameFlag, PostProcessingAdjustSerialCommunicationSizes.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, PostProcessingConfigureMQTTSettings.nameFlag, PostProcessingConfigureMQTTSettings.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, PostProcessingConfigureWLANSettings.nameFlag, PostProcessingConfigureWLANSettings.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, PostProcessingCopyFolderContentsToECUsAndExcept.nameFlag, PostProcessingCopyFolderContentsToECUsAndExcept.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, PostProcessingCopyFolderContentsToECUsWhitelist.nameFlag, PostProcessingCopyFolderContentsToECUsWhitelist.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, PostProcessingCopyLocalConfig_hppToCarDeriverECUs.nameFlag, PostProcessingCopyLocalConfig_hppToCarDeriverECUs.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, PostProcessingDownloadConfig_hpp.nameFlag, PostProcessingDownloadConfig_hpp.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, PostProcessingFillOutMethodStubs.nameFlag, PostProcessingFillOutMethodStubs.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, PostProcessingInsertAtLineIndex.nameFlag, PostProcessingInsertAtLineIndex.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, PostProcessingMoveIncludeBefore_ifdef__cplusplus.nameFlag, PostProcessingMoveIncludeBefore_ifdef__cplusplus.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, PostProcessingStateChartValues.nameFlag, PostProcessingStateChartValues.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, PostProcessingStateChartValuesFlexible.nameFlag, PostProcessingStateChartValuesFlexible.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, ReplaceLineContent.nameFlag, ReplaceLineContent.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, SaveToTextFile.nameFlag, SaveToTextFile.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, SelectableTextWindow.nameFlag, SelectableTextWindow.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, TerminalCommand.nameFlag, TerminalCommand.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, Upload.nameFlag, Upload.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.close();

		// For the # Or DDS_CONFIG
		String intermediateFileName = completeExamplesFilePath.toString() + ".editing";
		FileWriter workCopy = new FileWriter(intermediateFileName);
		Scanner currentFileReader = new Scanner(completeExamplesFilePath.toFile());
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
		Files.move(Paths.get(intermediateFileName), completeExamplesFilePath,
				StandardCopyOption.REPLACE_EXISTING);
		
		return true;
	}
	
	
	public Path getCompleteExamplesFilePath() {
		return completeExamplesFilePath;
	}
}
