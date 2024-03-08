package arduinocliutilizer.steps.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import arduinocliutilizer.paths.ConfigDirectoryAndFilePaths;
import arduinocliutilizer.paths.SelectedFilePathAndContextFinder;
import arduinocliutilizer.steps.exceptions.NoArduinoCLIConfigFileException;


public class ArduinoCLICommandLineHandler implements ConfigDirectoryAndFilePaths, SelectedFilePathAndContextFinder {
	private boolean arduinoCLIPathSetInPathEnvironment;
	private String arduinoCLIDirectory; // By default /home/muml/ArduinoCLI
	private String potentialArduinoCLIPathCommand;
	//private boolean isWindows;
	
	public ArduinoCLICommandLineHandler() throws IOException, NoArduinoCLIConfigFileException{
		String projectPath = SelectedFilePathAndContextFinder.getProjectOfSelectedFile();
		String completeConfigFilePath = projectPath + "/" + configFilePath;
		
		File fileInputOutput = new File(completeConfigFilePath);
		if(fileInputOutput.exists() && !fileInputOutput.isDirectory()) {
		}
		else{
			throw new NoArduinoCLIConfigFileException(
				"The ArduinoCLIUtilizer config file is missing!\n"
					+ "Generate one this way:\n" 
						+ "(Right click on a .zip, .ino or .ino.hex file)/\n"
							+ "\"ArduinoCLIUtilizer\"/ \"GenerateArduinoCLIUtilizer config file\"");
		}
		
		// Read the config file.		
		InputStream inputStream = new FileInputStream(new File(completeConfigFilePath));
		Yaml yaml = new Yaml();
		Map<String, Object> loadedData = yaml.load(inputStream);
		arduinoCLIPathSetInPathEnvironment = (boolean) loadedData.get("arduinoCLIPathSetInPathEnvironment");
		arduinoCLIDirectory = (String) loadedData.get("arduinoCLIDirectory");
		inputStream.close();
		
		//System.out.println("ArduinoCLICommandLineHandler: Config read!");
		if(arduinoCLIPathSetInPathEnvironment){
			potentialArduinoCLIPathCommand = "";
		}
		else{
			potentialArduinoCLIPathCommand = "export PATH=" + arduinoCLIDirectory + ":$PATH && ";
		}
		
		//isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
	}
	
	public ResponseFeedback doShellCommand(String commandSequence) throws IOException, InterruptedException{
		
		// Processbuilder is more intuitive to use than Runtime.
		ProcessBuilder processBuilder = new ProcessBuilder();
		//if(isWindows){
		//	processBuilder.command("cmd.exe", "/c", potentialArduinoCLIPathCommand + commandSequence);
		//}
		//else{
			processBuilder.command("bash", "-c", potentialArduinoCLIPathCommand + commandSequence);
		//}
		Process proc = processBuilder.start();
		
		// https://stackoverflow.com/questions/5711084/java-runtime-getruntime-getting-output-from-executing-a-command-line-program
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
		int exitCode = proc.waitFor();
		
		// Read the output from the command
		String currentNormalFeedback = null;
		String normalFeedback = "";
		while ((currentNormalFeedback = stdInput.readLine()) != null) {
			normalFeedback += currentNormalFeedback + "\n";
		}
		// Read any errors from the attempted command
		String currentErrorFeedback = null;
		String errorFeedback = "";
		while ((currentErrorFeedback = stdError.readLine()) != null) {
			errorFeedback += currentErrorFeedback + "\n";
		}
		
		return new ResponseFeedback(exitCode, normalFeedback, errorFeedback);
	}
	
}
