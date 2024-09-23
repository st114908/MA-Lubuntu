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

/**
 * Handles the generation of the config file. At usage (see
 * generateArduinoCLIUtilizerConfigFile()) it detects if the Path to the
 * Arduino-CLI is known to the System by performing a test call. It also checks
 * if the Arduino-CLI is installed at the default path.
 */
public class ArduinoCLIUtilizerConfigGenerator implements DefaultConfigDirectoryAndFilePath, DefaultArduinoCLIPath {
	private Path completeConfigDirectoryPath;
	private Path completeConfigFilePath;
	private String advice;

	public ArduinoCLIUtilizerConfigGenerator() throws ProjectFolderPathNotSetException {
		if (ProjectFolderPathStorage.projectFolderPath == null) {
			throw new ProjectFolderPathNotSetException(
					"The static field projectFolderPath in ProjectFolderPathStorage has "
							+ "to be set to a complete file system path to the project's folder!");
		}
		completeConfigDirectoryPath = ProjectFolderPathStorage.projectFolderPath.resolve(CONFIG_DIRECTORY_FOLDER_NAME);
		completeConfigFilePath = completeConfigDirectoryPath.resolve(CONFIG_FILE_NAME);
		advice = "The generation is yet to be started!";
	}

	/**
	 * At usage (see generateArduinoCLIUtilizerConfigFile()) it detects if the
	 * Path to the Arduino-CLI is known to the System by performing a test call.
	 * It also checks if the Arduino-CLI is installed at the default path.
	 * Generates an advice (see  depending on the results.
	 * 
	 * @return If the generation happend.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public boolean generateArduinoCLIUtilizerConfigFile() throws IOException, InterruptedException {
		if (!(Files.exists(completeConfigDirectoryPath) && Files.isDirectory(completeConfigDirectoryPath))) {
			Files.createDirectories(completeConfigDirectoryPath);
		}

		if (checkIfArduinoCLIUtilizerConfigFileExistsAtDefaultLocation()) {
			advice = "The ArduinoCLIUtilizer config file already exists!\n"
					+ "See " + completeConfigFilePath;
			return false;
		}

		boolean arduinoCLIKnownToSystem = checkForArduinoCLIKnownToSystem();
		boolean arduinoCLIAtDefaultPath = checkForArduinoCLIAtDefaultPath();

		// Use default values and generate the config file with default values.
		Map<String, Object> data = new LinkedHashMap<String, Object>();
		if (arduinoCLIKnownToSystem) {
			data.put("arduinoCLIPathSetInPathEnvironment", true);
		} else {
			data.put("arduinoCLIPathSetInPathEnvironment", false);
		}
		data.put("arduinoCLIDirectory", DEFAULT_ARDUINO_CLI_PATH);
		data.put("fallbackBoardIdentifierFQBN", "arduino:avr:nano");

		// For DumperOptions examples see
		// https://www.tabnine.com/code/java/methods/org.yaml.snakeyaml.DumperOptions$LineBreak/getPlatformLineBreak
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		options.setPrettyFlow(true);
		options.setLineBreak(DumperOptions.LineBreak.getPlatformLineBreak());
		Yaml yaml = new Yaml(options);
		FileWriter myWriter = new FileWriter(completeConfigFilePath.toString());
		myWriter.write(yaml.dump(data));
		myWriter.write("\n");
		if (arduinoCLIKnownToSystem) {
			advice = "The config file generation has detected,\n"
					+ "that the path to Arduino-CLI is known to the system,\n"
					+ "so the arduinoCLIDirectory setting won't be read.\n"
					+ "The generated settings should directly work.";
			myWriter.write("# Info: The config file generation has detected, that the path to Arduino-CLI is known to the system, so the arduinoCLIDirectory setting won't be read.\n");
			myWriter.write("# Info: The generated settings should directly work.");
		} else {
			if (arduinoCLIAtDefaultPath) {
				advice = "The config file generation has detected,\n"
						+ "that the Arduino-CLI is already installed in the folder set by arduinoCLIDirectory.\n"
						+ "The generated settings should directly work.";
				myWriter.write("# Info: The config file generation has detected, that the Arduino-CLI is already installed in the folder set by arduinoCLIDirectory.\n");
				myWriter.write("# Info: The generated settings should directly work.");
			} else {
				advice = "The config file generation has detected,\n"
						+ "that the paths Arduino-CLI is neither known to the system nor\n"
						+ "in the directory set by arduinoCLIDirectory.\n"
						+ "Recommendation: Either install the Arduino-CLI in the directory set by arduinoCLIDirectory or\n"
						+ "adjust the settings to the installation path of the Arduino-CLI.";
				myWriter.write("# Info: The config file generation has detected, that the paths Arduino-CLI is neither known to the system nor in the directory of arduinoCLIDirectory.\n");
				myWriter.write("# Recommendation: Install the Arduino-CLI in the directory of arduinoCLIDirectory or adjust the settings to the installation path of the Arduino-CLI.");
			}
		}
		myWriter.close();

		return true;
	}

	public boolean checkIfArduinoCLIUtilizerConfigFileExistsAtDefaultLocation() {
		if (Files.exists(completeConfigFilePath) && Files.isRegularFile(completeConfigFilePath)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean checkForArduinoCLIKnownToSystem() throws IOException, InterruptedException {
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command("bash", "-c", "arduino-cli config dump");
		Process proc = processBuilder.start();
		int exitCode = proc.waitFor();
		boolean testSuccessful = (exitCode == 0);
		return testSuccessful;
	}

	public boolean checkForArduinoCLIAtDefaultPath() {
		if (checkIfArduinoCLIUtilizerConfigFileExistsAtDefaultLocation()) {
			return true;
		} else {
			try {
				ProcessBuilder processBuilder = new ProcessBuilder();
				processBuilder.command("bash", "-c", DEFAULT_ARDUINO_CLI_PATH + "/arduino-cli config dump");
				Process proc = processBuilder.start();
				int exitCode = proc.waitFor();
				boolean testSuccessful = (exitCode == 0);
				return testSuccessful;
			} catch (InterruptedException | IOException e) {
				return false;
			}
		}
	}

	public Path getCompleteConfigFilePath() {
		return completeConfigFilePath;
	}
	
	public String getAdvice(){
		return advice;
	}
}
