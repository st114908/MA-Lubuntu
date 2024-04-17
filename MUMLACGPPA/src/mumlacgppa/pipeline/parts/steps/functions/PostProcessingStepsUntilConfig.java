/**
 * 
 */
package mumlacgppa.pipeline.parts.steps.functions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
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
public class PostProcessingStepsUntilConfig extends PipelineStep{

	public static final String nameFlag = "PostProcessingStepsUntilConfig";

	
	/**
	 * @param readData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 */
	public PostProcessingStepsUntilConfig(VariableHandler VariableHandlerInstance, Map<String, Map<String, String>> readData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, readData);
	}

	/**
	 * @param yamlData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 */
	public PostProcessingStepsUntilConfig(VariableHandler VariableHandlerInstance, String yamlData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, yamlData);
	}

	/* (non-Javadoc)
	 * @see mumlacga.pipeline.parts.steps.common.PipelineStep#setRequiredInsAndOuts()
	 */
	@Override
	protected void setRequiredInsAndOuts() {
		requiredInsAndOuts = new LinkedHashMap<String, HashSet<String>>();

		HashSet<String> ins = new LinkedHashSet<String>();
		ins.add("arduinoContainersPath");
		ins.add("componentCodePath");
		requiredInsAndOuts.put(inFlag, ins);
		
		HashSet<String> outs = new LinkedHashSet<String>();
		outs.add("ifSuccessful");
		requiredInsAndOuts.put(outFlag, outs);
	}

	// Map<String, Map<String, String>> for
	// Map<InOrOut, Map<ParameterOrOneOutput, SourceOrSaveTarget>>
	public static Map<String, Map<String, String>> generateDefaultOrExampleValues(){
		Map<String, Map<String, String>> exampleSettings = new LinkedHashMap<String, Map<String,String>>();
		
		// Ins:
		Map<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("arduinoContainersPath", "direct arduino-containers");
		ins.put("componentCodePath", "direct arduino-containers/fastAndSlowCar_v2");
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
		
		Path dummy = ProjectFolderPathStorage.projectFolderPath;
		
		// Only load once.
		Path arduinoContainersPath = resolvePath( handleInputByKey("arduinoContainersPath").getContent() );
		Path componentCodePath = resolvePath( handleInputByKey("componentCodePath").getContent() );
		
		// From https://github.com/SQA-Robo-Lab/Overtaking-Cars/blob/hal_demo/arduino-containers_demo_hal/deployable-files-hal-test/README.md:
		// The comments are a rewritten summary to have the instructions/actions easier to read as comments in the code.  
		
		adjustIncludes(componentCodePath);
		copyFilesFromComponentsFolderItself(arduinoContainersPath, componentCodePath);
		copyFilesFromLibFolder(arduinoContainersPath, componentCodePath);
		copyFilesFromMessagesFolder(arduinoContainersPath, componentCodePath);
		copyFilesFromOperationsFolder(arduinoContainersPath, componentCodePath);
		copyFilesFromRTSCsFolder(arduinoContainersPath, componentCodePath);
		copyFilesFromTypesFolder(arduinoContainersPath, componentCodePath);
		
		// fastAndSlowCar_v2: Files „CmakeLists.txt“ und „Doxyfile“ will not be used.
		// So nothing to do.
		
		copyAPImappingsAndAdjust(arduinoContainersPath);
		fetchAndNotYetAdjustConfig_hpp(arduinoContainersPath); // TODO: Werte!!!
		addHALPartsIntoCarDriverInoFiles(arduinoContainersPath);
		fillOutMethodStubs(arduinoContainersPath);
		// Moved to PerformPostProcessingStateChartValues and PerformPostProcessingStateChartValueFlexible.
		//setVelocitiesAndDistancesForStates(arduinoContainersPathString); 

        /*
        18. Compile and upload for/to the respective desired Arduino micro controller via Arduino IDE.
        This is done separately.
		*/
		
		handleOutputByKey("ifSuccessful", true);
	}

	
	/**
	 * @param componentCodePath
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void adjustIncludes(Path componentCodePath) throws IOException, FileNotFoundException {
		
		// For all files in "fastAndSlowCar_v2":
		// 1: Adjust #include calls for all header files.
		// 2: Move up "# include clock.h" from C++ check.
		DirectoryStream<Path> componentCodeFolder = Files.newDirectoryStream(componentCodePath);
		for(Path currentSubdirectoryPath: componentCodeFolder){
			if(Files.isRegularFile(currentSubdirectoryPath)){ // Just becasue of Doxyfile and CMakeLists.txt, which are not folders/directories.
				continue;
			}
			boolean isInComponentsSubdirectory = currentSubdirectoryPath.getFileName().toString().equals("components");
			
			DirectoryStream<Path> currentFolder = Files.newDirectoryStream(currentSubdirectoryPath);
			for(Path currentEntryPath: currentFolder){
				if( (currentEntryPath.getFileName().toString().endsWith(".h")) || (currentEntryPath.getFileName().toString().endsWith(".c")) ){
					File currentFileIn = currentEntryPath.toFile();
					String intermediateFileName = currentEntryPath.toString() + ".editing";
					FileWriter workCopy = new FileWriter(intermediateFileName);
					Scanner currentFileReader = new Scanner(currentFileIn);
			    	while (currentFileReader.hasNextLine()) {
			    		String currentLine = currentFileReader.nextLine();
			    		
			    		// If in components sub directory: Pull up the include for the clock.h file outside of the "extern "C" {  " part.
			    		if(isInComponentsSubdirectory && currentLine.contains("#ifdef __cplusplus")){
			    			workCopy.write("#include \"clock.h\"\n");
			    			workCopy.write(currentLine + "\n");
			    		}
			    		else if(isInComponentsSubdirectory && currentLine.contains("#include \"../lib/clock.h\"")){
			    			// Don't copy!
			    		}
			    		
		    			// The following part could have been done more flexible using a detection of found folders,
			    		// but that would have increased the complexity and reduced readability for practically no gain.  
			    		else if(currentLine.contains("#include \"../components/")){
			    			String intermediate = currentLine.replace("#include \"../components/", "#include \"");
			    			workCopy.write(intermediate + "\n");
			    		}
			    		else if(currentLine.contains("#include \"../lib/")){
			    			String intermediate = currentLine.replace("#include \"../lib/", "#include \"");
			    			workCopy.write(intermediate + "\n");
			    		}
			    		else if(currentLine.contains("#include \"../messages/")){
			    			String intermediate = currentLine.replace("#include \"../messages/", "#include \"");
			    			workCopy.write(intermediate + "\n");
			    		}
			    		else if(currentLine.contains("#include \"../operations/")){
			    			String intermediate = currentLine.replace("#include \"../operations/", "#include \"");
			    			workCopy.write(intermediate + "\n");
			    		}
			    		else if(currentLine.contains("#include \"../RTSCs/")){
			    			String intermediate = currentLine.replace("#include \"../RTSCs/", "#include \"");
			    			workCopy.write(intermediate + "\n");
			    		}
			    		else if(currentLine.contains("#include \"../types/")){
			    			String intermediate = currentLine.replace("#include \"../types/", "#include \"");
			    			workCopy.write(intermediate + "\n");
			    		}
			    		
			    		else{
			    			workCopy.write(currentLine + "\n");
			    		}
			    	}
				    currentFileReader.close();
				    workCopy.close();
				    Files.move(Paths.get(intermediateFileName), currentEntryPath, StandardCopyOption.REPLACE_EXISTING);
				}
			}
		}
	}
	
	/**
	 * @param arduinoContainersPath
	 * @param componentCodePath
	 * @throws IOException
	 */
	private void copyFilesFromComponentsFolderItself(Path arduinoContainersPath, Path componentCodePath) throws IOException {
		
		// "fastAndSlowCar_v2/components":
		// Copy „coordinatorComponent_Interface.h“ and "coordinatorComponent.c" files into each  *CarCoordinatorECU folder.
		// Copy other files into each *CarDriverECU folder.
		String coordinatorComponent_Interface_h_fileNameAlone = "coordinatorComponent_Interface.h";
		String coordinatorComponent_c_fileNameAlone = "coordinatorComponent.c";
		Path coordinatorComponent_Interface_h = componentCodePath.resolve("components").resolve(coordinatorComponent_Interface_h_fileNameAlone);
		Path coordinatorComponent_c = componentCodePath.resolve("components").resolve(coordinatorComponent_c_fileNameAlone);
		
		DirectoryStream<Path> arduinoContainersContent = Files.newDirectoryStream(arduinoContainersPath);
		for(Path currentArduinoContainersEntryPath: arduinoContainersContent){
			if(currentArduinoContainersEntryPath.getFileName().toString().endsWith("CarCoordinatorECU")){
				Files.copy(coordinatorComponent_Interface_h, currentArduinoContainersEntryPath.resolve(coordinatorComponent_Interface_h_fileNameAlone),
						StandardCopyOption.REPLACE_EXISTING);
				Files.copy(coordinatorComponent_c, currentArduinoContainersEntryPath.resolve(coordinatorComponent_c_fileNameAlone), StandardCopyOption.REPLACE_EXISTING);
			}
			
			if(currentArduinoContainersEntryPath.getFileName().toString().endsWith("CarDriverECU")){
				DirectoryStream<Path> componentsFolderItselfContent = Files.newDirectoryStream(componentCodePath.resolve("components"));
				for(Path currentComponentsFolderItselfEntryPath: componentsFolderItselfContent){
					String currentFileName = currentComponentsFolderItselfEntryPath.getFileName().toString();
					if( currentFileName.equals(coordinatorComponent_Interface_h_fileNameAlone) || currentFileName.equals(coordinatorComponent_c_fileNameAlone) ){
						// Do not copy
					}
					else{
						Files.copy(currentComponentsFolderItselfEntryPath, currentArduinoContainersEntryPath.resolve(currentFileName),
								StandardCopyOption.REPLACE_EXISTING); 
					}
				}
			}
		}
	}

	/**
	 * @param arduinoContainersPath
	 * @param componentCodePath
	 * @throws IOException
	 */
	private void copyFilesFromLibFolder(Path arduinoContainersPath, Path componentCodePath) throws IOException {
		
		// fastAndSlowCar_v2/lib: Copy all files except „clock.h“ and „standardTypes.h“ into the *ECU folders.
		DirectoryStream<Path> arduinoContainersContent = Files.newDirectoryStream(arduinoContainersPath);
		for(Path currentArduinoContainersEntryPath: arduinoContainersContent){
			if(currentArduinoContainersEntryPath.getFileName().toString().endsWith("ECU")){
				DirectoryStream<Path> libFolderContent = Files.newDirectoryStream(componentCodePath.resolve("lib"));
				for(Path currentlibFolderEntryPath: libFolderContent){
					String currentFileName = currentlibFolderEntryPath.getFileName().toString();
					if( currentFileName.equals("clock.h") || currentFileName.equals("standardTypes.h") ){
						// Do not copy
					}
					else{
						Files.copy(currentlibFolderEntryPath, currentArduinoContainersEntryPath.resolve(currentFileName),
								StandardCopyOption.REPLACE_EXISTING);
					}
				}
			}
		}
	}

	/**
	 * @param arduinoContainersPathString
	 * @param componentCodePathString
	 * @throws IOException
	 */
	private void copyFilesFromMessagesFolder(Path arduinoContainersPath, Path componentCodePath)
			throws IOException {
		// fastAndSlowCar_v2/messages: Copy "messages_types.h" into the *ECU folders.
		// (Only "messages_types.h" is in the messages folder, but for more flexibility a copy everything search is still done.)
		DirectoryStream<Path> arduinoContainersContent = Files.newDirectoryStream(arduinoContainersPath);
		for(Path currentArduinoContainersEntryPath: arduinoContainersContent){
			if(currentArduinoContainersEntryPath.getFileName().toString().endsWith("ECU")){
				DirectoryStream<Path> libFolderContent = Files.newDirectoryStream(componentCodePath.resolve("messages"));
				for(Path currentlibFolderEntryPath: libFolderContent){
					String currentFileName = currentlibFolderEntryPath.getFileName().toString();
					Files.copy(currentlibFolderEntryPath, currentArduinoContainersEntryPath.resolve(currentFileName),
							StandardCopyOption.REPLACE_EXISTING);
				}
			}
		}
	}

	/**
	 * @param arduinoContainersPathString
	 * @param componentCodePathString
	 * @throws IOException
	 */
	private void copyFilesFromOperationsFolder(Path arduinoContainersPath, Path componentCodePath)
			throws IOException {
		// fastAndSlowCar_v2/operations: Copy all files into the *CarDriverECU folders.
		DirectoryStream<Path> arduinoContainersContent = Files.newDirectoryStream(arduinoContainersPath);
		for(Path currentArduinoContainersEntryPath: arduinoContainersContent){
			if(currentArduinoContainersEntryPath.getFileName().toString().endsWith("CarDriverECU")){
				DirectoryStream<Path> libFolderContent = Files.newDirectoryStream(componentCodePath.resolve("operations"));
				for(Path currentlibFolderEntryPath: libFolderContent){
					String currentFileName = currentlibFolderEntryPath.getFileName().toString();
					Files.copy(currentlibFolderEntryPath, currentArduinoContainersEntryPath.resolve(currentFileName),
							StandardCopyOption.REPLACE_EXISTING);
				}
			}
		}
	}

	/**
	 * @param arduinoContainersPathString
	 * @param componentCodePathString
	 * @throws IOException
	 */
	private void copyFilesFromRTSCsFolder(Path arduinoContainersPath, Path componentCodePath)
			throws IOException {
		// fastAndSlowCar_v2/RTSCs:
	    //1. Copy „coordinatorCoordinatorComponentStateChart.c“  into the *CarCoordinatorECU folders.
	    //2. Copy „driveControlDriveControlComponentStateChart.c“ into the *CarDriverECU folders.
		String coordinatorCoordinatorComponentStateChart_c_fileNameAlone = "coordinatorCoordinatorComponentStateChart.c";
		String courseControlCourseControlComponentStateChart_c_fileNameAlone = "courseControlCourseControlComponentStateChart.c";
		Path coordinatorCoordinatorComponentStateChart_c = componentCodePath.resolve("RTSCs").resolve(coordinatorCoordinatorComponentStateChart_c_fileNameAlone);
		Path driveControlDriveControlComponentStateChart_c = componentCodePath.resolve("RTSCs").resolve(courseControlCourseControlComponentStateChart_c_fileNameAlone);
		DirectoryStream<Path> arduinoContainersContent = Files.newDirectoryStream(arduinoContainersPath);
		for(Path currentArduinoContainersEntryPath: arduinoContainersContent){
			if(currentArduinoContainersEntryPath.getFileName().toString().endsWith("CarCoordinatorECU")){
				Files.copy(coordinatorCoordinatorComponentStateChart_c, currentArduinoContainersEntryPath.resolve(coordinatorCoordinatorComponentStateChart_c_fileNameAlone),
						StandardCopyOption.REPLACE_EXISTING);
			}

			if(currentArduinoContainersEntryPath.getFileName().toString().endsWith("CarDriverECU")){
				Files.copy(driveControlDriveControlComponentStateChart_c, currentArduinoContainersEntryPath.resolve(courseControlCourseControlComponentStateChart_c_fileNameAlone),
						StandardCopyOption.REPLACE_EXISTING);
			}
		}
	}

	/**
	 * @param arduinoContainersPathString
	 * @param componentCodePathString
	 * @throws IOException
	 */
	private void copyFilesFromTypesFolder(Path arduinoContainersPath, Path componentCodePath)
			throws IOException {
		// fastAndSlowCar_v2/types: Copy all files into the *ECU folders.
		DirectoryStream<Path> arduinoContainersContent = Files.newDirectoryStream(arduinoContainersPath);
		for(Path currentArduinoContainersEntryPath: arduinoContainersContent){
			if(currentArduinoContainersEntryPath.getFileName().toString().endsWith("ECU")){
				DirectoryStream<Path> libFolderContent = Files.newDirectoryStream(componentCodePath.resolve("types"));
				for(Path currentlibFolderEntryPath: libFolderContent){
					String currentFileName = currentlibFolderEntryPath.getFileName().toString();
					Files.copy(currentlibFolderEntryPath, currentArduinoContainersEntryPath.resolve(currentFileName),
							StandardCopyOption.REPLACE_EXISTING);
				}
			}
		}
	}

	/**
	 * @param arduinoContainersPathString
	 * @throws IOException
	 */
	private void copyAPImappingsAndAdjust(Path arduinoContainersPath) throws IOException {
		
		/*
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
            
         For our modifications:
            1. Copy all files with „*F*“ into the folder fastCarDriverECU.
            CI_FRONTDISTANCESENSORFDISTANCESENSORdistancePortaccessCommand.c
			CI_FRONTDISTANCESENSORFDISTANCESENSORdistancePortaccessCommand.h
			CI_DRIVECONTROLLERFDRIVECONTROLLERvelocityPortaccessCommand.c
			CI_DRIVECONTROLLERFDRIVECONTROLLERvelocityPortaccessCommand.h
			CI_DRIVECONTROLLERFDRIVECONTROLLERanglePortaccessCommand.c
			CI_DRIVECONTROLLERFDRIVECONTROLLERanglePortaccessCommand.h
			CI_REARDISTANCESENSORFDISTANCESENSORdistancePortaccessCommand.c
			CI_REARDISTANCESENSORFDISTANCESENSORdistancePortaccessCommand.h

            2. Copy all files with „*S*“ into the folder fastCarDriverECU.
            CI_FRONTDISTANCESENSORSDISTANCESENSORdistancePortaccessCommand.c
			CI_FRONTDISTANCESENSORSDISTANCESENSORdistancePortaccessCommand.h
			CI_DRIVECONTROLLERSDRIVECONTROLLERvelocityPortaccessCommand.c
			CI_DRIVECONTROLLERSDRIVECONTROLLERvelocityPortaccessCommand.h
			CI_DRIVECONTROLLERSDRIVECONTROLLERanglePortaccessCommand.c
			CI_DRIVECONTROLLERSDRIVECONTROLLERanglePortaccessCommand.h
			CI_REARDISTANCESENSORSDISTANCESENSORdistancePortaccessCommand.c
			CI_REARDISTANCESENSORSDISTANCESENSORdistancePortaccessCommand.h
        
        From later:
        Fill the method stubs in the API Mapping files
            1. In every API-Mapping include: (recommended in the `.c` file, not the `.h` file to only have the import localy) `SimpleHardwareController_Connector.h`:
            2. frontDistance: ```*distance = SimpleHardwareController_DistanceSensor_GetDistanceToClosestMm(0);```
            3. rearDistance: ```*distance = SimpleHardwareController_DistanceSensor_GetDistanceToClosestMm(1);```
            4. velocity: ```*velocity = SimpleHardwareController_DriveController_GetSpeed();```
            [For our modifications: 5. angle: "*angle = SimpleHardwareController_DriveController_GetAngle();"]
        */
		// No automatic  *CarDriverECU folders search this time due to the Name format used for the Mappings.
		
		// HashMap<CarDriverECUFolderName, HashSet<MappingsFileName>
		HashMap<String, HashSet<String>> destinationMappingCopying = new HashMap<String, HashSet<String>>();
		
		HashSet<String> fastCarDriverECUMappingsFileNames = new HashSet<String>();
		fastCarDriverECUMappingsFileNames.add("CI_FRONTDISTANCESENSORFDISTANCESENSORdistancePortaccessCommand.c");
		fastCarDriverECUMappingsFileNames.add("CI_FRONTDISTANCESENSORFDISTANCESENSORdistancePortaccessCommand.h");
		fastCarDriverECUMappingsFileNames.add("CI_DRIVECONTROLLERFDRIVECONTROLLERvelocityPortaccessCommand.c");
		fastCarDriverECUMappingsFileNames.add("CI_DRIVECONTROLLERFDRIVECONTROLLERvelocityPortaccessCommand.h");
		fastCarDriverECUMappingsFileNames.add("CI_DRIVECONTROLLERFDRIVECONTROLLERanglePortaccessCommand.c");
		fastCarDriverECUMappingsFileNames.add("CI_DRIVECONTROLLERFDRIVECONTROLLERanglePortaccessCommand.h");
		fastCarDriverECUMappingsFileNames.add("CI_REARDISTANCESENSORFDISTANCESENSORdistancePortaccessCommand.c");
		fastCarDriverECUMappingsFileNames.add("CI_REARDISTANCESENSORFDISTANCESENSORdistancePortaccessCommand.h");
		destinationMappingCopying.put("fastCarDriverECU", fastCarDriverECUMappingsFileNames);

		HashSet<String> slowCarDriverECUMappingsFileNames = new HashSet<String>();
		slowCarDriverECUMappingsFileNames.add("CI_FRONTDISTANCESENSORSDISTANCESENSORdistancePortaccessCommand.c");
		slowCarDriverECUMappingsFileNames.add("CI_FRONTDISTANCESENSORSDISTANCESENSORdistancePortaccessCommand.h");
		slowCarDriverECUMappingsFileNames.add("CI_DRIVECONTROLLERSDRIVECONTROLLERvelocityPortaccessCommand.c");
		slowCarDriverECUMappingsFileNames.add("CI_DRIVECONTROLLERSDRIVECONTROLLERvelocityPortaccessCommand.h");
		slowCarDriverECUMappingsFileNames.add("CI_DRIVECONTROLLERSDRIVECONTROLLERanglePortaccessCommand.c");
		slowCarDriverECUMappingsFileNames.add("CI_DRIVECONTROLLERSDRIVECONTROLLERanglePortaccessCommand.h");
		slowCarDriverECUMappingsFileNames.add("CI_REARDISTANCESENSORSDISTANCESENSORdistancePortaccessCommand.c");
		slowCarDriverECUMappingsFileNames.add("CI_REARDISTANCESENSORSDISTANCESENSORdistancePortaccessCommand.h");
		destinationMappingCopying.put("slowCarDriverECU", slowCarDriverECUMappingsFileNames);
		
		/*
		From later:
        Fill the method stubs in the API Mapping files
            1. In every API-Mapping include: (recommended in the `.c` file, not the `.h` file to only have the import localy) `SimpleHardwareController_Connector.h`:
            2. frontDistance: ```*distance = SimpleHardwareController_DistanceSensor_GetDistanceToClosestMm(0);```
            3. rearDistance: ```*distance = SimpleHardwareController_DistanceSensor_GetDistanceToClosestMm(1);```
            4. velocity: ```*velocity = SimpleHardwareController_DriveController_GetSpeed();```
            [For our modifications: 5. angle: "*angle = SimpleHardwareController_DriveController_GetAngle();" // TODO: In die Dokumentation eintragen!!!]
		*/
		
		for(String currentCarDriverECUFolderNameKey: destinationMappingCopying.keySet()){
			Path currentCarDriverECUFolderPath = arduinoContainersPath.resolve(currentCarDriverECUFolderNameKey);
			HashSet<String> currentMappingsFileNames = destinationMappingCopying.get(currentCarDriverECUFolderNameKey);
			for(String currentMappingsFileName: currentMappingsFileNames){
				Path currentMappingsFilePath = arduinoContainersPath.resolve("APImappings").resolve(currentMappingsFileName);
				Path targetPath = currentCarDriverECUFolderPath.resolve(currentMappingsFileName);
				
				Files.copy(currentMappingsFilePath, targetPath, StandardCopyOption.REPLACE_EXISTING); // In all cases copy
				
				// If necessary: Adjust: 
				boolean frontDistance = currentMappingsFileName.toLowerCase().contains("frontdistance");
				boolean rearDistance = currentMappingsFileName.toLowerCase().contains("reardistance");
				boolean velocity = currentMappingsFileName.toLowerCase().contains("velocity");
				boolean angle = currentMappingsFileName.toLowerCase().contains("angle");
				
				if(currentMappingsFileName.endsWith(".c")){
					File currentFileIn = targetPath.toFile();
					String intermediateFileName = targetPath.toString() + ".editing";
					FileWriter workCopy = new FileWriter(intermediateFileName);

		    		workCopy.write("#include <SimpleHardwareController_Connector.h>\n");
					Scanner currentCFileReader = new Scanner(currentFileIn);
			    	while (currentCFileReader.hasNextLine()) {
			    		String currentLine = currentCFileReader.nextLine();
			    		String currentLineTrimmed = currentLine.trim();

			    		if(frontDistance && currentLine.contains("// Start of user code API") ){
			    			workCopy.write(currentLine + "\n");
			    			workCopy.write("	*distance = SimpleHardwareController_DistanceSensor_GetDistanceToClosestMm(0);");
			    		}
			    		else if(rearDistance && currentLine.contains("// Start of user code API") ){
			    			workCopy.write(currentLine + "\n");
			    			workCopy.write("	*distance = SimpleHardwareController_DistanceSensor_GetDistanceToClosestMm(1);");
			    		}
			    		else if(velocity && currentLine.contains("// Start of user code API") ){
			    			workCopy.write(currentLine + "\n");
			    			workCopy.write("	*velocity = SimpleHardwareController_DriveController_GetSpeed();");
			    		}
			    		else if(angle && currentLine.contains("// Start of user code API") ){
			    			workCopy.write(currentLine + "\n");
			    			workCopy.write("	*angle = SimpleHardwareController_DriveController_GetAngle();");
			    		}
			    		else{
			    			workCopy.write(currentLine + "\n");
			    		}
			    	}
				    currentCFileReader.close();
				    workCopy.close();
					Files.move(Paths.get(intermediateFileName), currentCarDriverECUFolderPath.resolve(currentMappingsFileName), StandardCopyOption.REPLACE_EXISTING);
				}
			}
		}
	}

	/**
	 * @param arduinoContainersPathString
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	private void fetchAndNotYetAdjustConfig_hpp(Path arduinoContainersPath)
			throws IOException, MalformedURLException {
		
        // Download https://github.com/SQA-Robo-Lab/Sofdcar-HAL/blob/main/examples/SimpleHardwareController/Config.hpp into the *CarDriverECU folders.
		// Raw URL: https://raw.githubusercontent.com/SQA-Robo-Lab/Sofdcar-HAL/main/examples/SimpleHardwareController/Config.hpp
        // And adjust configurations.
		InputStream Config_hpp = new URL("https://raw.githubusercontent.com/SQA-Robo-Lab/Sofdcar-HAL/main/examples/SimpleHardwareController/Config.hpp").openStream();
		Path downloadStoragePath = arduinoContainersPath.resolve("Config.hpp");
		Files.copy(Config_hpp, downloadStoragePath, StandardCopyOption.REPLACE_EXISTING);
		DirectoryStream<Path> arduinoContainersContent = Files.newDirectoryStream(arduinoContainersPath);
		for(Path currentArduinoContainersEntryPath: arduinoContainersContent){
			if(currentArduinoContainersEntryPath.getFileName().toString().endsWith("CarDriverECU")){
				Files.copy(downloadStoragePath, currentArduinoContainersEntryPath.resolve("Config.hpp"), StandardCopyOption.REPLACE_EXISTING);
				System.out.println("TODO: Adjust config values in " + currentArduinoContainersEntryPath.resolve("Config.hpp").toString() + "!!!");
				//TODO: Anpassungen vornehmen
			}
		}
		Files.deleteIfExists(downloadStoragePath);
	}

	/**
	 * @param arduinoContainersPathString
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void addHALPartsIntoCarDriverInoFiles(Path arduinoContainersPath) throws IOException, FileNotFoundException {

        /*14. Add the iInitialisation of the HAL parts in the [*CarDriverECU/]*CarDriverECU.ino files.
            1. Include the following library files: `SimpleHardwareController.hpp` and `SimpleHardwareController_Connector.h`
            2. Add the global Variable `SimpleHardwareController fastCarDriverController;` in the `.ino`-File (name the var to taste)
            3. In the `setup()` function user code section of the `.ino` file add the following lines to initialize the HAL and provide it to the C-Connector:
            4. 	* `initSofdcarHalConnectorFor(&fastCarDriverController);`
            5.  * `fastCarDriverController.initializeCar(config, lineConfig);`
            6. Call the loop funciton of the HAL in the `loop()` function of the sketch: `fastCarDriverController.loop();`
            [Apparently for fastCarDriverECU. For slowCarDriverECU e.g. for sub step 2 "SimpleHardwareController slowCarDriverController;" and so on..]*/
		DirectoryStream<Path> arduinoContainersContent = Files.newDirectoryStream(arduinoContainersPath);
		for(Path currentArduinoContainersEntryPath: arduinoContainersContent){
			if(currentArduinoContainersEntryPath.toString().endsWith("CarDriverECU")){
				// The name of the folder should be the same es the wanted file with the ".ino" ending as sole exception.
				String namePartString = currentArduinoContainersEntryPath.getFileName().toString().replace("ECU", "");
				
				Path currentCarDriverECU_inoFile = currentArduinoContainersEntryPath.resolve(currentArduinoContainersEntryPath.getFileName().toString() + ".ino");
				File currentHFileIn = currentCarDriverECU_inoFile.toFile();
				String intermediateFileName = currentCarDriverECU_inoFile.toString() + ".editing";
				FileWriter workCopy = new FileWriter(intermediateFileName);
				
				Scanner currentHFileReader = new Scanner(currentHFileIn);
		    	while (currentHFileReader.hasNextLine()) {
		    		String currentLine = currentHFileReader.nextLine();
		    		if(currentLine.contains("#include \"Debug.h\"")){ // I decided to use this as reference point for inserting the includes.
		    			workCopy.write(currentLine + "\n");
		    			workCopy.write("#include <SimpleHardwareController.hpp>\n");
		    			workCopy.write("#include <SimpleHardwareController_Connector.h>\n");
		    			workCopy.write("#include \"Config.hpp\"\n");
		    		}
		    		else if( currentLine.contains("/* TODO: if devices or libraries are used which need an initialization, include the headers here */") ){
	    				workCopy.write("SimpleHardwareController " + namePartString + "Controller;\n");
		    		}
		    		else if( currentLine.contains("/* TODO: if devices are used which need an initialization, call the functionse here */") ){
	    				workCopy.write("	initSofdcarHalConnectorFor(&" + namePartString + "Controller);\n");
						workCopy.write("	" + namePartString + "Controller.initializeCar(config, lineConfig);\n");
		    		}
		    		else if(currentLine.contains("void loop(){")){ // I decided to use this as reference point for inserting the includes.
		    			workCopy.write(currentLine + "\n");
		    			workCopy.write("	" + namePartString + "Controller.loop();\n");
		    		}
		    		else{
		    			workCopy.write(currentLine + "\n");
		    		}
		    	}
			    currentHFileReader.close();
			    workCopy.close();
				Files.move(Paths.get(intermediateFileName), currentCarDriverECU_inoFile, StandardCopyOption.REPLACE_EXISTING);
			}
		}
	}

	/**
	 * @param arduinoContainersPathString
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void fillOutMethodStubs(Path arduinoContainersPath) throws IOException, FileNotFoundException {
		
        /*16. Fill out method stubs in „[*CarDriverECU/]robotCarPowerTrainOpRep.c“:
            1. Include `SimpleHardwareController_Connector.h` at the top of the file (also recommended in the `.c`, not the `.h`)
            2. for changing to the left lane use ```SimpleHardwareController_LineFollower_SetLineToFollow(0);```
            3. for changing to the right lane use ```SimpleHardwareController_LineFollower_SetLineToFollow(1);```
            4. for start driving along the line use ```SimpleHardwareController_DriveController_SetSpeed(velocity);```
		
		For our modifications:
		Fill out method stubs in „[*CarDriverECU/]robotCarDriveControllerOpRep.c“:
            1. Include `SimpleHardwareController_Connector.h` at the top of the file (also recommended in the `.c`, not the `.h`)
            2. for changing to the left lane use ```SimpleHardwareController_LineFollower_SetLineToFollow(0);```
            3. for changing to the right lane use ```SimpleHardwareController_LineFollower_SetLineToFollow(1);```
            4. for start driving along the line use ```SimpleHardwareController_DriveController_SetSpeed(velocity);```
		*/

		DirectoryStream<Path> arduinoContainersContent = Files.newDirectoryStream(arduinoContainersPath);
		for(Path currentArduinoContainersEntryPath: arduinoContainersContent){
			if(currentArduinoContainersEntryPath.toString().endsWith("CarDriverECU")){
				// The name of the folder should be the same as the wanted file with the ".ino" ending as sole exception.
				
				Path currentCarDriverECU_inoFile = currentArduinoContainersEntryPath.resolve("robotCarDriveControllerOpRep.c");
				File currentHFileIn = currentCarDriverECU_inoFile.toFile();
				String intermediateFileName = currentCarDriverECU_inoFile.toString() + ".editing";
				FileWriter workCopy = new FileWriter(intermediateFileName);
				
				Scanner currentHFileReader = new Scanner(currentHFileIn);
		    	while (currentHFileReader.hasNextLine()) {
		    		String currentLine = currentHFileReader.nextLine();
		    		if(currentLine.contains("/** Start of user code User includes **/")){ // I decided to use this as reference point for inserting the includes.
		    			workCopy.write(currentLine + "\n");
		    			workCopy.write("#include <SimpleHardwareController_Connector.h>\n");
		    		}
		    		else if( currentLine.contains("#warning Missing implemenation of repository operation 'RobotCarDriveController_robotCarDriveControllerChangeLaneLeft'") ){
	    				workCopy.write("SimpleHardwareController_LineFollower_SetLineToFollow(0);\n");
		    		}
		    		else if( currentLine.contains("#warning Missing implemenation of repository operation 'RobotCarDriveController_robotCarDriveControllerChangeLaneRight'") ){
	    				workCopy.write("SimpleHardwareController_LineFollower_SetLineToFollow(1);\n");
		    		}
		    		else if(currentLine.contains("#warning Missing implemenation of repository operation 'RobotCarDriveController_robotCarDriveControllerFollowLine'")){
		    			workCopy.write("SimpleHardwareController_DriveController_SetSpeed(velocity);\n");
		    		}
		    		else{
		    			workCopy.write(currentLine + "\n");
		    		}
		    	}
			    currentHFileReader.close();
			    workCopy.close();
				Files.move(Paths.get(intermediateFileName), currentCarDriverECU_inoFile, StandardCopyOption.REPLACE_EXISTING);
			}
		}
	}


	public static void main(String[] args) throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, IOException, InterruptedException, NoArduinoCLIConfigFileException, FQBNErrorEception, ProjectFolderPathNotSetExceptionMUMLACGPPA {
		ProjectFolderPathStorage.projectFolderPath = Paths.get("/home/muml/MUMLProjects/Overtaking-Cars-Baumfalk");
		String testYaml =
				inFlag + ":\n"
				+ "  arduinoContainersPath: direct /home/muml/MUMLProjects/Overtaking-Cars-Baumfalk/arduino-containers\n"
				+ "  componentCodePath: direct /home/muml/MUMLProjects/Overtaking-Cars-Baumfalk/arduino-containers/fastAndSlowCar_v2\n"
				+ outFlag + ":\n"
				+ "  ifSuccessful: ifSuccessful\n";
		Yaml yaml = new Yaml(); // For yaml validity check.
		Map <String, Map <String, String>> testMap = yaml.load(testYaml);
		VariableHandler VariableHandlerInstance = new VariableHandler();
		PostProcessingStepsUntilConfig debugTest = new PostProcessingStepsUntilConfig(VariableHandlerInstance, testYaml);
		debugTest.execute();
	}
}
