package mumlacgppa.pipeline.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import mumlacgppa.pipeline.parts.exceptions.ParameterMismatchException;
import mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import mumlacgppa.pipeline.parts.exceptions.StepNotMatched;
import mumlacgppa.pipeline.parts.exceptions.StructureException;
import mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import mumlacgppa.pipeline.parts.steps.Keywords;
import mumlacgppa.pipeline.parts.steps.functions.Compile;
import mumlacgppa.pipeline.parts.steps.functions.ComponentCodeGeneration;
import mumlacgppa.pipeline.parts.steps.functions.ContainerCodeGeneration;
import mumlacgppa.pipeline.parts.steps.functions.ContainerTransformation;
import mumlacgppa.pipeline.parts.steps.functions.LookupBoardBySerialNumber;
import mumlacgppa.pipeline.parts.steps.functions.OnlyContinueIfFullfilledElseAbort;
import mumlacgppa.pipeline.parts.steps.functions.PopupWindowMessage;
import mumlacgppa.pipeline.parts.steps.functions.PostProcessingStateChartValues;
import mumlacgppa.pipeline.parts.steps.functions.PostProcessingStateChartValuesFlexible;
import mumlacgppa.pipeline.parts.steps.functions.PostProcessingStepsUntilConfig;
import mumlacgppa.pipeline.parts.steps.functions.ReplaceLineContent;
import mumlacgppa.pipeline.parts.steps.functions.SaveToTextFile;
import mumlacgppa.pipeline.parts.steps.functions.SelectableTextWindow;
import mumlacgppa.pipeline.parts.steps.functions.TerminalCommand;
import mumlacgppa.pipeline.parts.steps.functions.Upload;
import mumlacgppa.pipeline.paths.PipelineSettingsDirectoryAndFilePaths;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

public class StepsExamplesGenerator implements PipelineSettingsDirectoryAndFilePaths, Keywords{
	private Path completeSettingsDirectoryPath;
	private Path completeSettingsFilePath;
	
	
	public StepsExamplesGenerator() throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super();
		if(ProjectFolderPathStorage.projectFolderPath == null){
			throw new ProjectFolderPathNotSetExceptionMUMLACGPPA(
					"The static field projectFolderPath in ProjectFolderPathStorage has "
					+ "to be set to a complete file system path to the project's folder!"
							);
		}
		completeSettingsDirectoryPath = ProjectFolderPathStorage.projectFolderPath.resolve(pipelineSettingsDirectoryFolder);
		completeSettingsFilePath = completeSettingsDirectoryPath.resolve(StepExamplesFileName);
	}
	
	
	private String exampleSegment(Yaml yaml, String name, Map<String, Map<String, String>> exampleSettings){
		Map<String, Map<String, Map<String, String>>> exampleUsageDef = new LinkedHashMap<String, Map<String, Map<String, String>>>();
		exampleUsageDef.put(name, exampleSettings);
		return yaml.dump(exampleUsageDef);
	}
	
	
	public boolean generateExampleListFile()
			throws IOException, StructureException, StepNotMatched, ProjectFolderPathNotSetExceptionMUMLACGPPA,
			VariableNotDefinedException, FaultyDataException, ParameterMismatchException{
		File directoryCheck = completeSettingsDirectoryPath.toFile();
		if (!directoryCheck.exists()){
		    directoryCheck.mkdirs();
		}
		
		File configExistenceCheck = completeSettingsFilePath.toFile();
		if(configExistenceCheck.exists() && !configExistenceCheck.isDirectory()) {
			return false;
		}
		
		
	    
	    // For DumperOptions examples see 
	    // https://www.tabnine.com/code/java/methods/org.yaml.snakeyaml.DumperOptions$LineBreak/getPlatformLineBreak
	    DumperOptions options = new DumperOptions();
	    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
	    options.setPrettyFlow(true);
	    options.setLineBreak(DumperOptions.LineBreak.getPlatformLineBreak());
	    Yaml yaml = new Yaml(options);
	    
	    
	    
		FileWriter myWriter = new FileWriter(completeSettingsFilePath.toFile());
		myWriter.write("These are all the steps, each represented by one example.\n\n\n");
		
		myWriter.write(exampleSegment(yaml, ContainerTransformation.nameFlag, ContainerTransformation.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, ContainerCodeGeneration.nameFlag, ContainerCodeGeneration.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, ComponentCodeGeneration.nameFlag, ComponentCodeGeneration.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, PostProcessingStepsUntilConfig.nameFlag, PostProcessingStepsUntilConfig.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, PostProcessingStateChartValues.nameFlag, PostProcessingStateChartValues.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, PostProcessingStateChartValuesFlexible.nameFlag, PostProcessingStateChartValuesFlexible.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, ReplaceLineContent.nameFlag, ReplaceLineContent.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, Compile.nameFlag, Compile.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, LookupBoardBySerialNumber.nameFlag, LookupBoardBySerialNumber.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, Upload.nameFlag, Upload.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, OnlyContinueIfFullfilledElseAbort.nameFlag, OnlyContinueIfFullfilledElseAbort.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, PopupWindowMessage.nameFlag, PopupWindowMessage.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, SelectableTextWindow.nameFlag, SelectableTextWindow.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, SaveToTextFile.nameFlag, SaveToTextFile.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		myWriter.write(exampleSegment(yaml, TerminalCommand.nameFlag, TerminalCommand.generateDefaultOrExampleValues()));
		myWriter.write("\n\n\n");
		
		myWriter.close();

		return true;
	}
	
	
	
	
	public static void main(String[] args)
			throws ProjectFolderPathNotSetExceptionMUMLACGPPA, IOException, StructureException, StepNotMatched,
			VariableNotDefinedException, FaultyDataException, ParameterMismatchException {
		ProjectFolderPathStorage.projectFolderPath = Paths.get("/home/muml/MUMLProjects/Overtaking-Cars-Baumfalk");
		StepsExamplesGenerator PipelineSettingsGeneratorInstance = new StepsExamplesGenerator();
		PipelineSettingsGeneratorInstance.generateExampleListFile();
		
	}
}