package arduinocliutilizer.worksteps.functions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import arduinocliutilizer.paths.FQBNStorageFileName;
import arduinocliutilizer.worksteps.common.ACLIWorkstep;
import arduinocliutilizer.worksteps.common.ArduinoCLICommandLineHandler;
import arduinocliutilizer.worksteps.common.SaveResponseInfo;
import arduinocliutilizer.worksteps.exceptions.FQBNErrorEception;
import arduinocliutilizer.worksteps.exceptions.NoArduinoCLIConfigFileException;
import projectfolderpathstorageplugin.ProjectFolderPathNotSetException;

public class UploadCall extends ACLIWorkstep implements SaveResponseInfo, FQBNStorageFileName{
	
	public static final String messageWindowTitle = "ArduinoCLIUtilizer: Upload step";
	
	
	public UploadCall(Path targetFilePath, String foundPortAddress, String targetFqbn)
			throws IOException, InterruptedException, FQBNErrorEception, NoArduinoCLIConfigFileException, ProjectFolderPathNotSetException{
		ArduinoCLICommandLineHandler commandLineDoer = new ArduinoCLICommandLineHandler();
		String uploadCommand;
		String targetFile = targetFilePath.toString();
		if(targetFile.endsWith(".ino")){
			// arduino-cli upload --port [PORT] --fqbn [BOARD]  [INOFILE]
			uploadCommand = "arduino-cli upload --port " + foundPortAddress + " --fqbn " + targetFqbn + " " + targetFile + " --format yaml";
		}
		else{
			// arduino-cli upload --port [PORT] --fqbn [BOARD] --input-file [HEXFILE]
			uploadCommand = "arduino-cli upload --port " + foundPortAddress + " --fqbn " + targetFqbn + " --input-file " + targetFile + " --format yaml";
			Path parentPath = targetFilePath.getParent();
			Path fqbnFilePath = parentPath.resolve(FQBN_STORAGE_FILE_NAME);
			if(Files.exists(fqbnFilePath) && Files.isRegularFile(fqbnFilePath)){
				BufferedReader FQBNFileReader = new BufferedReader(new FileReader(fqbnFilePath.toFile()));
				String readFQBN = FQBNFileReader.readLine();
				FQBNFileReader.close();
				if(!targetFqbn.equals(readFQBN)){
					throw new FQBNErrorEception("FQBN (board type identifier) mismatch!\nPlease recompile using the target board!");
				}
			}
			else{
				throw new FQBNErrorEception("Textfile with the used FQBN (board type identifier) not found!");
			}
		}
		ReceivedFeedback = commandLineDoer.doShellCommand(uploadCommand);
		responseLocation = SaveResponseInfo.saveShellResponseInfo(
			targetFilePath.getParent(), "UploadInfo.txt",
			uploadCommand, ReceivedFeedback);
		
		successful = (ReceivedFeedback.exitCode == 0);
	}
	
	
	@Override
	public String generateResultMessage(){
		if(successful){
			return "Nothing wrong.";
		}
		else{
			return "Error at the upload!\n"
					+ "For more details see\n"
					+ responseLocation;
		}
	}
	
	
}
