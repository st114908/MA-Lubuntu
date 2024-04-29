package de.ust.arduinocliutilizer.configgenerator;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import de.ust.arduinocliutilizer.paths.DefaultArduinoCLIPath;
import de.ust.arduinocliutilizer.paths.DefaultConfigDirectoryAndFilePath;
import projectfolderpathstorageplugin.ProjectFolderPathNotSetException;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

public class ArduinoCLIUtilizerConfigGenerator implements DefaultConfigDirectoryAndFilePath, DefaultArduinoCLIPath {
	private Path completeConfigDirectoryPath;
	private Path completeConfigFilePath;
	
	
	public ArduinoCLIUtilizerConfigGenerator() throws ProjectFolderPathNotSetException {
		if(ProjectFolderPathStorage.projectFolderPath == null){
			throw new ProjectFolderPathNotSetException(
					"The static field projectFolderPath in ProjectFolderPathStorage has "
					+ "to be set to a complete file system path to the project's folder!"
							);
		}
		completeConfigDirectoryPath = ProjectFolderPathStorage.projectFolderPath.resolve(CONFIG_DIRECTORY_FOLDER_NAME);
		completeConfigFilePath = completeConfigDirectoryPath.resolve(CONFIG_FILE_NAME);
	}

	
	public boolean generateArduinoCLIUtilizerConfigFile() throws IOException{
		if (! (Files.exists(completeConfigDirectoryPath) && Files.isDirectory(completeConfigDirectoryPath)) ){
			Files.createDirectories(completeConfigDirectoryPath);
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

	
	public boolean checkIfArduinoCLIUtilizerConfigFileExistsAtDefaultLocation() {
		if(Files.exists(completeConfigFilePath) && Files.isRegularFile(completeConfigFilePath)) {
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean checkForArduinoCLIByCall() throws IOException, InterruptedException{
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command("bash", "-c", "export PATH=" + DEFAULT_ARDUINO_CLI_PATH + ":$PATH && arduino-cli config dump");
		Process proc = processBuilder.start();
		int exitCode = proc.waitFor();
		boolean testSuccessful = (exitCode == 0); 
		return testSuccessful;
	}
	
	
	/**
	 * Checks if the ArduinoCLI can be accessed.
	 * First it checks if it can find the file at the default location (this is just the config generator) and then if it can be called.
	 * @return If the ArduinoCLI can be accessed or not.
	 */
	public boolean canAccessArduinoCLI() {
		if(checkIfArduinoCLIUtilizerConfigFileExistsAtDefaultLocation()){
			return true;
		}
		else{
			try {
				return checkForArduinoCLIByCall();
			} catch (InterruptedException | IOException e) {
				return false;
			} 
		}
	}


	public Path getCompleteConfigFilePath() {
		return completeConfigFilePath;
	}
}
