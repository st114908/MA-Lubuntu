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
public class PostProcessingConfigureWLANSettings extends PipelineStep implements PipelineSettingsDirectoryAndFilesPaths, VariableTypes {

	public static final String nameFlag = "PostProcessingConfigureWLANSettings";

	
	/**
	 * @param readData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 */
	public PostProcessingConfigureWLANSettings(VariableHandler VariableHandlerInstance, Map<String, Map<String, String>> readData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, readData);
	}

	/**
	 * @param yamlData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 */
	public PostProcessingConfigureWLANSettings(VariableHandler VariableHandlerInstance, String yamlData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
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
		ins.put("ecuEnding", StringType);
		ins.put("nameOrSSID", WLANNameType);
		ins.put("password", WLANPasswordType);
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
		ins.put("ecuEnding", directValueKeyword + " CarCoordinatorECU");
		ins.put("nameOrSSID", directValueKeyword + " DummyWLANNameOrSSID");
		ins.put("password", directValueKeyword + " DummyWLANPassword");
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
		String ecuEnding = handleInputByKey("ecuEnding").getContent();
		String wlanNameOrSSID = handleInputByKey("nameOrSSID").getContent();
		String wlanPassword = handleInputByKey("password").getContent();
		// String wlanStatus = handleInputByKey("status").getContent();
		
		DirectoryStream<Path> arduinoContainersContent = Files.newDirectoryStream(arduinoContainersPath);
		for(Path currentArduinoContainersEntryPath: arduinoContainersContent){
			if(currentArduinoContainersEntryPath.toString().endsWith(ecuEnding)){
				// The name of the folder should be the same as the wanted file with the ".ino" ending as sole exception.
				Path currentTargetECU_inoFile = currentArduinoContainersEntryPath.resolve(currentArduinoContainersEntryPath.getFileName().toString() + ".ino");
				File currentInoFileIn = currentTargetECU_inoFile.toFile();
				String intermediateFileName = currentTargetECU_inoFile.toString() + ".editing";
				FileWriter workCopy = new FileWriter(intermediateFileName);
				
				Scanner currentHFileReader = new Scanner(currentInoFileIn);
		    	while (currentHFileReader.hasNextLine()) {
		    		String currentLine = currentHFileReader.nextLine();
		    		if(currentLine.contains("struct WiFiConfig wifiConfig =")){ // I decided to use this as reference point.
		    			workCopy.write(currentLine + "\n");
		    			workCopy.write("		\"" + wlanNameOrSSID + "\"," + "\n");
		    			currentHFileReader.nextLine(); // To avoid reading and copying.
		    			workCopy.write("		\"" + wlanPassword + "\"," + "\n");
		    			currentHFileReader.nextLine();
		    			//workCopy.write("		" + wlanStatus + "" + "\n");
		    			//currentHFileReader.nextLine();
		    			continue; // To skip the if check during the following lines.
		    		}
		    		else{
		    			workCopy.write(currentLine + "\n");
		    		}
		    	}
		    	while (currentHFileReader.hasNextLine()) {
		    		String currentLine = currentHFileReader.nextLine();
		    		workCopy.write(currentLine + "\n");
		    	}
			    currentHFileReader.close();
			    workCopy.close();
				Files.move(Paths.get(intermediateFileName), currentTargetECU_inoFile, StandardCopyOption.REPLACE_EXISTING);
			}
		}
		
		handleOutputByKey("ifSuccessful", true);
	}

}
