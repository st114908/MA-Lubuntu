package arduinocliutilizer.worksteps.functions;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import arduinocliutilizer.paths.CompiledFilesFolderNameInterface;
import arduinocliutilizer.paths.FQBNStorageFileName;
import arduinocliutilizer.worksteps.common.ACLIWorkstep;
import arduinocliutilizer.worksteps.common.ArduinoCLICommandLineHandler;
import arduinocliutilizer.worksteps.common.SaveResponseInfo;
import arduinocliutilizer.worksteps.exceptions.NoArduinoCLIConfigFileException;
import projectfolderpathstorageplugin.ProjectFolderPathNotSetException;

public class CompilationCall extends ACLIWorkstep implements SaveResponseInfo, CompiledFilesFolderNameInterface, FQBNStorageFileName{

	public static final String messageWindowTitle = "ArduinoCLIUtilizer: Compiling step";
	
	
	public CompilationCall(Path targetINOFilePath, String foundFqbn, boolean saveCompiledFilesNearby)
			throws IOException, InterruptedException, NoArduinoCLIConfigFileException, ProjectFolderPathNotSetException {
		
		ArduinoCLICommandLineHandler commandLineDoer = new ArduinoCLICommandLineHandler();
		String compilationCommand;
		
		String targetINOFilePathString = targetINOFilePath.toString();
		Path parentPath =  targetINOFilePath.getParent();
		String compiledFilesDirIfUsed = parentPath.resolve(COMPILED_FILES_FOLDER_NAME).toString();
		if(saveCompiledFilesNearby){
			// arduino-cli compile --fqbn [BOARD] [INOFILE] --output-dir [OUT]
			compilationCommand = "arduino-cli compile --fqbn " + foundFqbn + " " + targetINOFilePathString + " --output-dir " + compiledFilesDirIfUsed + " --format yaml";
		}
		else {
			// arduino-cli compile --fqbn [BOARD] [INOFILE]
			compilationCommand = "arduino-cli compile --fqbn " + foundFqbn + " " + targetINOFilePathString + " --format yaml";
		}
		ReceivedFeedback = commandLineDoer.doShellCommand(compilationCommand);
		
		responseLocation = SaveResponseInfo.saveShellResponseInfo(
				parentPath, "CompilationInfo.txt",
			compilationCommand, ReceivedFeedback);
		
		if(ReceivedFeedback.exitCode != 0){
			successful = false;
			return;
		}
		Yaml yamlCompileResponse = new Yaml();
		@SuppressWarnings("unchecked")
		Map<String, Object> compileResponse = (Map<String, Object>) yamlCompileResponse.load(ReceivedFeedback.normalFeedback);
		if( !((boolean) compileResponse.get("success")) ){
			successful = false;
			return;
		}
		
		if(saveCompiledFilesNearby){
			// Save fqbn as additional safeguard against Uploads on different board types and for looking it up.
			FileWriter myWriter = new FileWriter(compiledFilesDirIfUsed + "/" + FQBN_STORAGE_FILE_NAME);
			myWriter.write(foundFqbn);
			myWriter.close();
		}
		
		successful = true;
	}
	
	
	@Override
	public String generateResultMessage(){
		if(successful){
			return "Nothing wrong.";
		}
		else{
			return "Error at the compilation!\n"
					+ "For more details see\n"
					+ responseLocation;
		}
	}
	
}
