package arduinocliutilizer.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import arduinocliutilizer.paths.ConfigDirectoryAndFilePaths;
import arduinocliutilizer.paths.DefaultArduinoCLIPath;
import arduinocliutilizer.paths.SelectedFilePathAndContextFinder;

public class ArduinoCLIUtilizerConfigGenerator implements ConfigDirectoryAndFilePaths, DefaultArduinoCLIPath, SelectedFilePathAndContextFinder {
	private boolean checkedForArduinoCLIFile;
	private boolean arduinoCLIFileFound;
	
	public ArduinoCLIUtilizerConfigGenerator() {
		super();
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
	
	public boolean generateConfigFile(String completeConfigDirectoryPath, String completeConfigFilePath) throws IOException{
		File directoryCheck = new File(completeConfigDirectoryPath);
		if (!directoryCheck.exists()){
		    directoryCheck.mkdirs();
		}
		
		File configExistenceCheck = new File(completeConfigFilePath);
		if(configExistenceCheck.exists() && !configExistenceCheck.isDirectory()) {
			return false;
		}
		
		// Use default values and generate the config file with default values.
		Map<String, Object> data = new LinkedHashMap<String, Object>();
	    data.put("arduinoCLIPathSetInPathEnvironment", false);
	    data.put("arduinoCLIDirectory", defaultArduinoCLIPath);
	    Yaml yaml = new Yaml();
		FileWriter myWriter = new FileWriter(completeConfigFilePath);
		myWriter.write(yaml.dump(data));
		myWriter.close();
		
		return true;
	}
	
	public String generateConfigFileAndHandleMessageTexts() throws IOException{
		String projectPath = SelectedFilePathAndContextFinder.getProjectOfSelectedFile();
		String completeConfigDirectoryPath = projectPath + "/" + configDirectoryFolder;
		String completeConfigFilePath = projectPath + "/" + configFilePath;
		
		boolean configFileCreated = generateConfigFile(completeConfigDirectoryPath, completeConfigFilePath);
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
