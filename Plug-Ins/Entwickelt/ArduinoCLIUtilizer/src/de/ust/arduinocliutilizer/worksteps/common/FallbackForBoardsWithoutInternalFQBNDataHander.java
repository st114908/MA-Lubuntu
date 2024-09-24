package de.ust.arduinocliutilizer.worksteps.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import de.ust.arduinocliutilizer.paths.DefaultConfigDirectoryAndFilePath;
import de.ust.arduinocliutilizer.worksteps.exceptions.NoArduinoCLIConfigFileException;
import projectfolderpathstorageplugin.ProjectFolderPathNotSetException;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

public abstract class FallbackForBoardsWithoutInternalFQBNDataHander implements DefaultConfigDirectoryAndFilePath {
	private static String FQBNToUse;

	public static String getFallbackFQBN() throws IOException, ProjectFolderPathNotSetException, NoArduinoCLIConfigFileException{
		Path projectPathOfSelectedFile = ProjectFolderPathStorage.projectFolderPath;
		if (ProjectFolderPathStorage.projectFolderPath == null) {
			throw new ProjectFolderPathNotSetException(
					"The static field projectFolderPath in ProjectFolderPathStorageArduinoCLIUtilizer has "
							+ "to be set to a complete file system path to the project's folder!");
		}

		Path completeConfigFilePath = projectPathOfSelectedFile.resolve(CONFIG_DIRECTORY_FOLDER_NAME)
				.resolve(CONFIG_FILE_NAME);
		if (Files.exists(completeConfigFilePath) && Files.isRegularFile(completeConfigFilePath)) {

		} else {
			throw new NoArduinoCLIConfigFileException("The ArduinoCLIUtilizer config file is missing!\n"
					+ "Generate one this way:\n" + "(Right click on a .zip, .ino or .hex file)/\n"
					+ "\"ArduinoCLIUtilizer\"/ \"GenerateArduinoCLIUtilizer config file\"");
		}

		// Read the config file.
		InputStream inputStream = new FileInputStream(completeConfigFilePath.toFile());
		Yaml yaml = new Yaml();
		Map<String, Object> loadedData = yaml.load(inputStream);
		FQBNToUse = (String) loadedData.get("fallbackBoardIdentifierFQBN");
		inputStream.close();

		return FQBNToUse;
	}

}
