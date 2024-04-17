package arduinocliutilizer.configgenerator;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import arduinocliutilizer.paths.DefaultConfigDirectoryAndFilePath;
import projectfolderpathstorageplugin.ProjectFolderPathNotSetException;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;
import arduinocliutilizer.paths.DefaultArduinoCLIPath;

public class ArduinoCLIUtilizerConfigGenerator implements DefaultConfigDirectoryAndFilePath, DefaultArduinoCLIPath {
	//private String completeConfigDirectoryPath;
	private Path completeConfigDirectoryPath;
	//private String completeConfigFilePath;
	private Path completeConfigFilePath;
	private boolean checkedForArduinoCLIFile;
	private boolean arduinoCLIFileFound;
	
	
	public ArduinoCLIUtilizerConfigGenerator() throws ProjectFolderPathNotSetException {
		if(ProjectFolderPathStorage.projectFolderPath == null){
			throw new ProjectFolderPathNotSetException(
					"The static field projectFolderPath in ProjectFolderPathStorage has "
					+ "to be set to a complete file system path to the project's folder!"
							);
		}
		completeConfigDirectoryPath = ProjectFolderPathStorage.projectFolderPath;
		completeConfigFilePath = completeConfigDirectoryPath.resolve(configFileName);
		checkedForArduinoCLIFile = false;
		arduinoCLIFileFound = false;
	}

	
	public boolean checkForArduinoCLIFile() throws IOException{
		checkedForArduinoCLIFile = true;
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command("bash", "-c", "export PATH=" + defaultArduinoCLIPath + ":$PATH && arduino-cli config dump");
		Process proc = processBuilder.start();
		try {
			int exitCode = proc.waitFor();
			boolean testSuccessful = (exitCode == 0); 
			arduinoCLIFileFound = testSuccessful;
			return testSuccessful;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	
	public boolean generateArduinoCLIUtilizerConfigFile() throws IOException{
		if (!Files.isDirectory(completeConfigDirectoryPath)){
			Files.createDirectory(completeConfigDirectoryPath);
		}
		
		if(Files.exists(completeConfigFilePath) && Files.isRegularFile(completeConfigFilePath)) {
			return false;
		}
		
		// Use default values and generate the config file with default values.
		Map<String, Object> data = new LinkedHashMap<String, Object>();
	    data.put("arduinoCLIPathSetInPathEnvironment", false);
	    data.put("arduinoCLIDirectory", defaultArduinoCLIPath);
	    // For DumperOptions examples see 
	    // https://www.tabnine.com/code/java/methods/org.yaml.snakeyaml.DumperOptions$LineBreak/getPlatformLineBreak
	    DumperOptions options = new DumperOptions();
	    //options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
	    options.setPrettyFlow(true);
	    options.setLineBreak(DumperOptions.LineBreak.getPlatformLineBreak());
	    Yaml yaml = new Yaml(options);
		FileWriter myWriter = new FileWriter(completeConfigFilePath.toString());
		myWriter.write(yaml.dump(data));
		myWriter.close();
		
		return true;
	}
	
	
	public String generateConfigFileAndHandleMessageTexts() throws IOException{
		boolean configFileCreated = generateArduinoCLIUtilizerConfigFile();
		if(!configFileCreated){
			return "The ArduinoCLIUtilizer config file already exists!";
		}
		
		if(!checkedForArduinoCLIFile){
			checkForArduinoCLIFile();
		}
		
		if(arduinoCLIFileFound){
			return "Successfully generated the ArduinoCLIUtilizer config file!\n"
				+ "You can find it at " + completeConfigFilePath;
		}
		else{
			return "Successfully generated the ArduinoCLIUtilizer config file!\n"
				+ "You can find it at " + completeConfigFilePath + "\n"
					+ "But the ArduinoCLI file (arduino-cli) hasn't been found.\n"
						+ "Either install the ArduinoCLI there or\n"
							+ "adjust the setting arduinoCLIDirectory!";
		}
	}

	
	public boolean isCheckedForArduinoCLIFile() {
		return checkedForArduinoCLIFile;
	}

	
	public boolean isArduinoCLIFileFound() {
		return arduinoCLIFileFound;
	}
}
