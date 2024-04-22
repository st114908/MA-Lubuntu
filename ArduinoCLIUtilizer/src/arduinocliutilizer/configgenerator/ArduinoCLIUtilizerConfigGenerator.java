package arduinocliutilizer.configgenerator;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import arduinocliutilizer.paths.DefaultArduinoCLIPath;
import arduinocliutilizer.paths.DefaultConfigDirectoryAndFilePath;
import projectfolderpathstorageplugin.ProjectFolderPathNotSetException;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

public class ArduinoCLIUtilizerConfigGenerator implements DefaultConfigDirectoryAndFilePath, DefaultArduinoCLIPath {
	//private String completeConfigDirectoryPath;
	private Path completeConfigDirectoryPath;
	//private String completeConfigFilePath;
	private Path completeConfigFilePath;
	private boolean hasCheckedForArduinoCLI;
	private boolean canAccessArduinoCLI;
	
	
	public ArduinoCLIUtilizerConfigGenerator() throws ProjectFolderPathNotSetException {
		if(ProjectFolderPathStorage.projectFolderPath == null){
			throw new ProjectFolderPathNotSetException(
					"The static field projectFolderPath in ProjectFolderPathStorage has "
					+ "to be set to a complete file system path to the project's folder!"
							);
		}
		completeConfigDirectoryPath = ProjectFolderPathStorage.projectFolderPath.resolve(CONFIG_DIRECTORY_FOLDER_NAME);
		completeConfigFilePath = completeConfigDirectoryPath.resolve(CONFIG_FILE_NAME);
		hasCheckedForArduinoCLI = false;
		canAccessArduinoCLI = false;
	}

	
	public boolean checkForArduinoCLI() throws IOException, InterruptedException{
		hasCheckedForArduinoCLI = true;
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command("bash", "-c", "export PATH=" + DEFAULT_ARDUINO_CLI_PATH + ":$PATH && arduino-cli config dump");
		Process proc = processBuilder.start();
		int exitCode = proc.waitFor();
		boolean testSuccessful = (exitCode == 0); 
		canAccessArduinoCLI = testSuccessful;
		return testSuccessful;
	}
	
	
	public boolean generateArduinoCLIUtilizerConfigFile() throws IOException{
		if (!Files.isDirectory(completeConfigDirectoryPath)){
			Files.createDirectory(completeConfigDirectoryPath);
		}
		
		if(checkIfArduinoCLIUtilizerConfigFileExistsAtDefaultLocation()){
			return false;
		}
		
		// Use default values and generate the config file with default values.
		Map<String, Object> data = new LinkedHashMap<String, Object>();
	    data.put("arduinoCLIPathSetInPathEnvironment", false);
	    data.put("arduinoCLIDirectory", DEFAULT_ARDUINO_CLI_PATH);
	    
	    // For DumperOptions examples see 
	    // https://www.tabnine.com/code/java/methods/org.yaml.snakeyaml.DumperOptions$LineBreak/getPlatformLineBreak
	    DumperOptions options = new DumperOptions();
	    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
	    options.setPrettyFlow(true);
	    options.setLineBreak(DumperOptions.LineBreak.getPlatformLineBreak());
	    Yaml yaml = new Yaml(options);
		FileWriter myWriter = new FileWriter(completeConfigFilePath.toString());
		myWriter.write(yaml.dump(data));
		myWriter.close();
		
		return true;
	}


	/**
	 * 
	 */
	public boolean checkIfArduinoCLIUtilizerConfigFileExistsAtDefaultLocation() {
		if(Files.exists(completeConfigFilePath) && Files.isRegularFile(completeConfigFilePath)) {
			return true;
		}
		else{
			return false;
		}
	}
	
	
	public boolean canAccessArduinoCLI() {
		if(hasCheckedForArduinoCLI){
			return canAccessArduinoCLI;
		}
		else if(checkIfArduinoCLIUtilizerConfigFileExistsAtDefaultLocation()){
			hasCheckedForArduinoCLI = true;
			canAccessArduinoCLI = true;
			return true;
		}
		else{
			try {
				return checkForArduinoCLI();
			} catch (InterruptedException | IOException e) {
				return false;
			} 
		}
	}


	public Path getCompleteConfigFilePath() {
		return completeConfigFilePath;
	}
}
