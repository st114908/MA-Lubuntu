package mumlacgppa.pipeline.settings;

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
import mumlacgppa.pipeline.parts.steps.functions.PostProcessingStateChartValues;
import mumlacgppa.pipeline.parts.steps.functions.PostProcessingStepsUntilConfig;
import mumlacgppa.pipeline.paths.PipelineSettingsDirectoryAndFilePaths;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

public class PipelineSettingsGenerator implements PipelineSettingsDirectoryAndFilePaths, Keywords{
	private Path completeSettingsDirectoryPath;
	private Path completeSettingsFilePath;
	
	public PipelineSettingsGenerator() throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super();
		if(ProjectFolderPathStorage.projectFolderPath == null){
			throw new ProjectFolderPathNotSetExceptionMUMLACGPPA(
					"The static field projectFolderPath in ProjectFolderPathStorage has "
					+ "to be set to a complete file system path to the project's folder!"
							);
		}
		completeSettingsDirectoryPath = ProjectFolderPathStorage.projectFolderPath.resolve(PIPELINE_SETTINGS_DIRECTORY_FOLDER);
		completeSettingsFilePath = completeSettingsDirectoryPath.resolve(PIPELINE_SETTINGS_FILE_NAME);
	}
	
	
	public Path getCompleteSettingsFilePath() {
		return completeSettingsFilePath;
	}


	private Map<String, Map<String, Map<String, String>>> pipelineSegmentHelper(Yaml yaml, String key, Map<String, Map<String, String>> settings){
		Map<String, Map<String, Map<String, String>>> exampleUsageDef = new LinkedHashMap<String, Map<String, Map<String, String>>>();
		exampleUsageDef.put(key, settings);
		return exampleUsageDef;
	}
	
	
	public boolean generatePipelineConfigFile() throws IOException{
		File directoryCheck = completeSettingsDirectoryPath.toFile();
		if (!directoryCheck.exists()){
		    directoryCheck.mkdirs();
		}
		
		/*File configExistenceCheck = completeSettingsFilePath.toFile();
		if(configExistenceCheck.exists() && !configExistenceCheck.isDirectory()) {
			return false;
		}*/
		

	    // For DumperOptions examples see 
	    // https://www.tabnine.com/code/java/methods/org.yaml.snakeyaml.DumperOptions$LineBreak/getPlatformLineBreak
	    DumperOptions options = new DumperOptions();
	    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
	    options.setPrettyFlow(true);
	    options.setLineBreak(DumperOptions.LineBreak.getPlatformLineBreak());
	    Yaml yaml = new Yaml(options);
		FileWriter settingsWriter = new FileWriter(completeSettingsFilePath.toFile());
		
	    // Settings changes of the generated default/example settings here in order to make the written entries and sequences more readable.
		Map<String, Map<String, String>> postProcessingStateChartValuesFastDriver = PostProcessingStateChartValues.generateDefaultOrExampleValues();
		Map<String, String> compileSettingsFastDriverPostProcessingStateChartValuesIns = postProcessingStateChartValuesFastDriver.get(inFlag);
		//compileSettingsFastDriverPostProcessingStateChartValuesIns.put("arduinoContainersPath", "from arduinoContainersFolderName");
		compileSettingsFastDriverPostProcessingStateChartValuesIns.put("ECUName", "from fastCarDriverECUFolderName");
		postProcessingStateChartValuesFastDriver.put(inFlag, compileSettingsFastDriverPostProcessingStateChartValuesIns);

		Map<String, Map<String, String>> postProcessingStateChartValuesSlowDriver = PostProcessingStateChartValues.generateDefaultOrExampleValues();
		Map<String, String> compileSettingsSlowDriverPostProcessingStateChartValuesIns = postProcessingStateChartValuesSlowDriver.get(inFlag);
		//compileSettingsSlowDriverPostProcessingStateChartValuesIns.put("arduinoContainersPath", "from arduinoContainersFolderName");
		compileSettingsSlowDriverPostProcessingStateChartValuesIns.put("ECUName", "from slowCarDriverECUFolderName");
		postProcessingStateChartValuesSlowDriver.put(inFlag, compileSettingsSlowDriverPostProcessingStateChartValuesIns);
		
		
		Map<String, Map<String, String>> compileSettingsFastCoordinator = Compile.generateDefaultOrExampleValues();
		Map<String, String> compileSettingsFastCoordinatorIns = compileSettingsFastCoordinator.get(inFlag);
		compileSettingsFastCoordinatorIns.put("boardTypeIdentifierFQBN", "from usedCoordinatorBoardIdentifierFQBN_Example");
		compileSettingsFastCoordinatorIns.put("targetInoFile", "from fastCarCoordinatorECUINOFilePath");
		compileSettingsFastCoordinator.put(inFlag, compileSettingsFastCoordinatorIns);
		
		Map<String, Map<String, String>> compileSettingsFastDriver = Compile.generateDefaultOrExampleValues();
		Map<String, String> compileSettingsFastDriverIns = compileSettingsFastDriver.get(inFlag);
		compileSettingsFastDriverIns.put("boardTypeIdentifierFQBN", "from usedDriverBoardIdentifierFQBN_Example");
		compileSettingsFastDriverIns.put("targetInoFile", "from fastCarDriverECUINOFilePath");
		compileSettingsFastDriver.put(inFlag, compileSettingsFastDriverIns);
		
		Map<String, Map<String, String>> compileSettingsSlowCoordinator = Compile.generateDefaultOrExampleValues();
		Map<String, String> compileSettingsSlowCoordinatorIns = compileSettingsSlowCoordinator.get(inFlag);
		compileSettingsSlowCoordinatorIns.put("boardTypeIdentifierFQBN", "from usedCoordinatorBoardIdentifierFQBN_Example");
		compileSettingsSlowCoordinatorIns.put("targetInoFile", "from slowCarCoordinatorECUINOFilePath");
		compileSettingsSlowCoordinator.put(inFlag, compileSettingsSlowCoordinatorIns);
		
		Map<String, Map<String, String>> compileSettingsSlowDriver = Compile.generateDefaultOrExampleValues();
		Map<String, String> compileSettingsSlowDriverIns = compileSettingsSlowDriver.get(inFlag);
		compileSettingsSlowDriverIns.put("boardTypeIdentifierFQBN", "from usedDriverBoardIdentifierFQBN_Example");
		compileSettingsSlowDriverIns.put("targetInoFile", "from slowCarDriverECUINOFilePath");
		compileSettingsSlowDriver.put(inFlag, compileSettingsSlowDriverIns);
		

		// Now the pipeline settings and sequence itself.
		Map<String, Object> mapForPipelineSettings = new LinkedHashMap<String, Object>();
		
		// Use default values and generate the config file with default values.
		Map<String, String> exampleVariableDefs = new LinkedHashMap<String, String>();
		exampleVariableDefs.put("ExampleNumberVariableName", "direct 12");
		exampleVariableDefs.put("ExampleStringVariableName", "direct ExampleString");
		exampleVariableDefs.put("ExampleBooleanVariableName", "direct true");
		//exampleVariableDefs.put("arduinoContainersFolderName", "arduinoContainers");
		exampleVariableDefs.put("usedDriverBoardIdentifierFQBN_Example", "direct arduino:avr:mega");
		exampleVariableDefs.put("usedCoordinatorBoardIdentifierFQBN_Example", "direct arduino:avr:nano");
		exampleVariableDefs.put("fastCarDriverECUFolderName", "direct fastCarDriverECU");
		exampleVariableDefs.put("fastCarCoordinatorECUINOFilePath", "direct arduino-containers/fastCarCoordinatorECU/fastCarDriverECU.ino");
		exampleVariableDefs.put("fastCarDriverECUINOFilePath", "direct arduino-containers/fastCarDriverECU/fastCarDriverECU.ino");
		exampleVariableDefs.put("slowCarDriverECUFolderName", "direct slowCarDriverECU");
		exampleVariableDefs.put("slowCarCoordinatorECUINOFilePath", "direct arduino-containers/slowCarCoordinatorECU/slowCarDriverECU.ino");
		exampleVariableDefs.put("slowCarDriverECUINOFilePath", "direct arduino-containers/slowCarDriverECU/slowCarDriverECU.ino");
		mapForPipelineSettings.put(variableDefsFlag, exampleVariableDefs);

		// For better/easier readability blank lines inbetween:
		settingsWriter.write(yaml.dump(mapForPipelineSettings));
		mapForPipelineSettings.clear();
		settingsWriter.write("\n\n");
		
		Map<String, Map<String, Map<String, String>>> defaultStandaloneUsageDefs = new LinkedHashMap<String, Map<String, Map<String, String>>>();
		defaultStandaloneUsageDefs.put(ContainerTransformation.nameFlag, ContainerTransformation.generateDefaultOrExampleValues());
		defaultStandaloneUsageDefs.put(ContainerCodeGeneration.nameFlag, ContainerCodeGeneration.generateDefaultOrExampleValues());
		defaultStandaloneUsageDefs.put(ComponentCodeGeneration.nameFlag, ComponentCodeGeneration.generateDefaultOrExampleValues());
		defaultStandaloneUsageDefs.put(PostProcessingStepsUntilConfig.nameFlag, PostProcessingStepsUntilConfig.generateDefaultOrExampleValues());
		mapForPipelineSettings.put(standaloneUsageDefsFlag, defaultStandaloneUsageDefs);

		// For better/easier readability blank lines inbetween:
		settingsWriter.write(yaml.dump(mapForPipelineSettings));
		mapForPipelineSettings.clear();
		settingsWriter.write("\n\n");
		
		ArrayList<Object> defaultPipelineSequenceDefs = new ArrayList<Object>();
		defaultPipelineSequenceDefs.add(fromValueFlag + " " + standaloneUsageDefsFlag + ": " + ContainerTransformation.nameFlag);
		defaultPipelineSequenceDefs.add(fromValueFlag + " " + standaloneUsageDefsFlag + ": " + ContainerCodeGeneration.nameFlag);
		defaultPipelineSequenceDefs.add(fromValueFlag + " " + standaloneUsageDefsFlag + ": " + ComponentCodeGeneration.nameFlag);
		defaultPipelineSequenceDefs.add(fromValueFlag + " " + standaloneUsageDefsFlag + ": " + PostProcessingStepsUntilConfig.nameFlag);
		defaultPipelineSequenceDefs.add( pipelineSegmentHelper(yaml, directValueFlag + " " + PostProcessingStateChartValues.nameFlag, postProcessingStateChartValuesFastDriver) );
		defaultPipelineSequenceDefs.add( pipelineSegmentHelper(yaml, directValueFlag + " " + PostProcessingStateChartValues.nameFlag, postProcessingStateChartValuesSlowDriver) );
		defaultPipelineSequenceDefs.add( pipelineSegmentHelper(yaml, directValueFlag + " " + Compile.nameFlag, compileSettingsFastCoordinator) );
		defaultPipelineSequenceDefs.add( pipelineSegmentHelper(yaml, directValueFlag + " " + Compile.nameFlag, compileSettingsFastDriver) );
		defaultPipelineSequenceDefs.add( pipelineSegmentHelper(yaml, directValueFlag + " " + Compile.nameFlag, compileSettingsSlowCoordinator) );
		defaultPipelineSequenceDefs.add( pipelineSegmentHelper(yaml, directValueFlag + " " + Compile.nameFlag, compileSettingsSlowDriver) );
		mapForPipelineSettings.put(pipelineSequenceDefFlag, defaultPipelineSequenceDefs);
	    
		settingsWriter.write(yaml.dump(mapForPipelineSettings));
		settingsWriter.close();
		
		// Cleanup of unwanted additions: ' chars around from entries in the pipeline sequence and after the "in:" flags the &id...s and *id...s.
		String inSequenceForCleanup = " " + inFlag + ":";
		
		String intermediateFileName = completeSettingsFilePath.toString() + ".editing";
		FileWriter workCopy = new FileWriter(intermediateFileName);
		Scanner currentFileReader = new Scanner(completeSettingsFilePath.toFile());
    	while (currentFileReader.hasNextLine()) {
    		String currentLine = currentFileReader.nextLine();
    		if(currentLine.contains("'")){
    			String intermediate = currentLine.replace("'", "");
    			workCopy.write(intermediate + "\n");
    		}
    		else if(currentLine.contains(" " + inFlag + ":")){
    			int index = currentLine.indexOf(inSequenceForCleanup);
    			String intermediate = currentLine.substring(0, index + inSequenceForCleanup.length());
    			workCopy.write(intermediate + "\n");
    		}
    		else{
    			workCopy.write(currentLine + "\n");
    		}
    	}
	    currentFileReader.close();
	    workCopy.close();
	    Files.move(Paths.get(intermediateFileName), completeSettingsFilePath, StandardCopyOption.REPLACE_EXISTING);

		//
		
		return true;
	}

	
	public static void main(String[] args)
			throws ProjectFolderPathNotSetExceptionMUMLACGPPA, IOException, StructureException, StepNotMatched,
			VariableNotDefinedException, FaultyDataException, ParameterMismatchException {
		ProjectFolderPathStorage.projectFolderPath = Paths.get("/home/muml/MUMLProjects/Overtaking-Cars-Baumfalk");
		PipelineSettingsGenerator PipelineSettingsGeneratorInstance = new PipelineSettingsGenerator();
		PipelineSettingsGeneratorInstance.generatePipelineConfigFile();
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(PipelineSettingsGeneratorInstance.getCompleteSettingsFilePath());
		PSRInstance.validateOrder();
	}
}
