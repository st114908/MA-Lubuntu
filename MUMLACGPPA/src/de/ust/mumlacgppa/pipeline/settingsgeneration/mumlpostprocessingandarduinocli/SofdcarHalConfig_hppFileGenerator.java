package de.ust.mumlacgppa.pipeline.settingsgeneration.mumlpostprocessingandarduinocli;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import de.ust.mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import de.ust.mumlacgppa.pipeline.parts.steps.Keywords;
import de.ust.mumlacgppa.pipeline.paths.PipelineSettingsDirectoryAndFilesPaths;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

public class SofdcarHalConfig_hppFileGenerator implements PipelineSettingsDirectoryAndFilesPaths, Keywords {
	private Path completeMUMLACGPPASettingsDirectoryPath;
	private Path completeSofdcarHalConfigFilePath;

	public Path getCompleteSofdcarHalConfigFilePath() {
		return completeSofdcarHalConfigFilePath;
	}

	public SofdcarHalConfig_hppFileGenerator() throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super();
		if (ProjectFolderPathStorage.projectFolderPath == null) {
			throw new ProjectFolderPathNotSetExceptionMUMLACGPPA(
					"The static field projectFolderPath in ProjectFolderPathStorage has "
							+ "to be set to a complete file system path to the project's folder!");
		}
		completeMUMLACGPPASettingsDirectoryPath = ProjectFolderPathStorage.projectFolderPath
				.resolve(PIPELINE_SETTINGS_DIRECTORY_FOLDER);
		completeSofdcarHalConfigFilePath = completeMUMLACGPPASettingsDirectoryPath.resolve(SOFDCAR_HAL_LOCAL_CONFIG_FILE_NAME);
	}

	public boolean generateSofdcarHalConfig_hppFile() throws IOException {
		File directoryCheck = completeMUMLACGPPASettingsDirectoryPath.toFile();
		if (!directoryCheck.exists()) {
			directoryCheck.mkdirs();
		}

		File configExistenceCheck = completeSofdcarHalConfigFilePath.toFile();
		if (configExistenceCheck.exists() && configExistenceCheck.isFile()) {
			return false;
		}

		FileWriter localSofdcarHalConfig_hppFile = new FileWriter(completeSofdcarHalConfigFilePath.toFile());
		localSofdcarHalConfig_hppFile.write("#ifndef SIMPLE_HARDWARE_COINTROLLER_EXAMPLE_CONFIG_HPP\n");
		localSofdcarHalConfig_hppFile.write("#define SIMPLE_HARDWARE_COINTROLLER_EXAMPLE_CONFIG_HPP\n");
		localSofdcarHalConfig_hppFile.write("\n");
		localSofdcarHalConfig_hppFile.write("#include <SimpleHardwareController.hpp>\n");
		localSofdcarHalConfig_hppFile.write("\n");
		localSofdcarHalConfig_hppFile.write("TurnSteeringCarConfig config = {\n");
		localSofdcarHalConfig_hppFile.write("    {13, 12, 11},              // rearLeft\n");
		localSofdcarHalConfig_hppFile.write("    {8, 9, 10},                // rearRight\n");
		localSofdcarHalConfig_hppFile.write("    {52, 40, 39, 19, 66, 109}, // steering\n");
		localSofdcarHalConfig_hppFile.write("    120,\n");
		localSofdcarHalConfig_hppFile.write("    100,      // width, length\n");
		localSofdcarHalConfig_hppFile.write("    204,      // wheel circumfrence\n");
		localSofdcarHalConfig_hppFile.write("    {48, 49}, // frontDistance\n");
		localSofdcarHalConfig_hppFile.write("    {46, 47}  // rearDistance\n");
		localSofdcarHalConfig_hppFile.write("};\n");
		localSofdcarHalConfig_hppFile.write("\n");
		localSofdcarHalConfig_hppFile.write("uint8_t brightnessPins[3] = {A0, A1, A2};\n");
		localSofdcarHalConfig_hppFile.write("BrightnessThresholds thresholds[3] = {{41, 114},\n");
		localSofdcarHalConfig_hppFile.write("                                      {61, 140},\n");
		localSofdcarHalConfig_hppFile.write("                                      {31, 81}};\n");
		localSofdcarHalConfig_hppFile.write("\n");
		localSofdcarHalConfig_hppFile.write("LineSensorConfig lineConfig = {\n");
		localSofdcarHalConfig_hppFile.write("    16,             // sensor distance\n");
		localSofdcarHalConfig_hppFile.write("    3,              // number of sesnor\n");
		localSofdcarHalConfig_hppFile.write("    brightnessPins, // sensor pins\n");
		localSofdcarHalConfig_hppFile.write("    thresholds      // sensor thretholds\n");
		localSofdcarHalConfig_hppFile.write("};\n");
		localSofdcarHalConfig_hppFile.write("\n");
		localSofdcarHalConfig_hppFile.write("#endif\n");
		localSofdcarHalConfig_hppFile.close();
		
		return true;
	}

}
