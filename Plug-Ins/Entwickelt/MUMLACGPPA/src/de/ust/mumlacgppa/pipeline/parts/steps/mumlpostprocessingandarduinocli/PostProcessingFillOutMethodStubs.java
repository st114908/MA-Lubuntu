/**
 * 
 */
package de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

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
import de.ust.mumlacgppa.pipeline.parts.storage.VariableTypes;
import de.ust.mumlacgppa.pipeline.paths.PipelineSettingsDirectoryAndFilesPaths;

/**
 * @author muml
 *
 */
public class PostProcessingFillOutMethodStubs extends PipelineStep implements PipelineSettingsDirectoryAndFilesPaths, VariableTypes {

	public static final String nameFlag = "PostProcessingFillOutMethodStubs";

	
	/**
	 * @param readData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 */
	public PostProcessingFillOutMethodStubs(VariableHandler VariableHandlerInstance, Map<String, Map<String, String>> readData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, readData);
	}

	/**
	 * @param yamlData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 */
	public PostProcessingFillOutMethodStubs(VariableHandler VariableHandlerInstance, String yamlData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, yamlData);
	}

	/**
	 * @see mumlacga.pipeline.parts.steps.common.PipelineStep#getRequiredInsAndOuts()
	 */
	@Override
	protected Map<String, Map<String, String>> getRequiredInsAndOuts() {
		LinkedHashMap<String, Map<String, String>> requiredInsAndOuts = new LinkedHashMap<String, Map<String, String>>();

		LinkedHashMap<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("arduinoContainersPath", FolderPathType);
		requiredInsAndOuts.put(inKeyword, ins);
		
		LinkedHashMap<String, String> outs = new LinkedHashMap<String, String>();
		outs.put("ifSuccessful", BooleanType);
		requiredInsAndOuts.put(outKeyword, outs);
		
		return requiredInsAndOuts;
	}

	public static Map<String, Map<String, String>> generateDefaultOrExampleValues(){
		// Map<String, Map<String, String>> for
		// Map<InOrOut, Map<ParameterOrOneOutput, SourceOrSaveTarget>>
		Map<String, Map<String, String>> exampleSettings = new LinkedHashMap<String, Map<String,String>>();
		
		// Ins:
		Map<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("arduinoContainersPath", directValueKeyword + " arduino-containers");
		exampleSettings.put(inKeyword, ins);
		
		// Out:
		Map<String, String> outs = new LinkedHashMap<String, String>();
		outs.put("ifSuccessful", "ifSuccessful");
		exampleSettings.put(outKeyword, outs);
		
		return exampleSettings;
	}



	/**
	 * @see mumlacga.pipeline.parts.steps.common.PipelineStep#execute()
	 */
	@Override
	public void execute()
			throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException,
			IOException, InterruptedException, NoArduinoCLIConfigFileException, FQBNErrorEception, InOrOutKeyNotDefinedException {

		// Only load once.
		Path arduinoContainersPath = resolveFullOrLocalPath( handleInputByKey("arduinoContainersPath").getContent() );
		
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
		
		handleOutputByKey("ifSuccessful", true);
	}

}
