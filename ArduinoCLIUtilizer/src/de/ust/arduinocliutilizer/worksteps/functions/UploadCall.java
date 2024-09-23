package de.ust.arduinocliutilizer.worksteps.functions;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import de.ust.arduinocliutilizer.paths.FQBNStorageFileName;
import de.ust.arduinocliutilizer.worksteps.common.ACLIWorkstep;
import de.ust.arduinocliutilizer.worksteps.common.ArduinoCLICommandLineHandler;
import de.ust.arduinocliutilizer.worksteps.exceptions.FQBNErrorEception;
import de.ust.arduinocliutilizer.worksteps.exceptions.NoArduinoCLIConfigFileException;
import projectfolderpathstorageplugin.ProjectFolderPathNotSetException;

public class UploadCall extends ACLIWorkstep implements FQBNStorageFileName {
	
	public static final String messageWindowTitle = "ArduinoCLIUtilizer: Upload step";
	
	private boolean notInSyncProblem;
	
	public UploadCall(Path targetFilePath, String foundPortAddress, String targetFqbn)
			throws IOException, InterruptedException, FQBNErrorEception, NoArduinoCLIConfigFileException, ProjectFolderPathNotSetException{
		ArduinoCLICommandLineHandler commandLineDoer = new ArduinoCLICommandLineHandler();
		String uploadCommand;
		String targetFile = targetFilePath.toString();
		if(targetFile.endsWith(".hex")){
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
				throw new FQBNErrorEception("Textfile with the used FQBN (board type identifier) not found at " + fqbnFilePath.toString() + "!");
			}
		}
		else{
			// arduino-cli upload --port [PORT] --fqbn [BOARD]  [INOFILE]
			uploadCommand = "arduino-cli upload --port " + foundPortAddress + " --fqbn " + targetFqbn + " " + targetFile + " --format yaml";
		}
		ReceivedFeedback = commandLineDoer.doShellCommand(uploadCommand);
		responseLocation = saveShellResponseInfo(
			targetFilePath.getParent(), "UploadInfo.txt", ReceivedFeedback);
		
		successful = (ReceivedFeedback.getExitCode() == 0);
		if(ReceivedFeedback.getErrorFeedback().contains("not in sync")){
			notInSyncProblem = true;
		}
		else{
			notInSyncProblem = false;
		}
	}
	
	
	@Override
	public String generateResultMessage(){
		if(successful){
			return "Nothing wrong.";
		}
		else{
			if(notInSyncProblem){
				return "Error at the upload!\n"
						+ "The programmer is out of sync or the board at the given address is of a different type/FQBN than given type/FQBN.\n"
						+ "For more details see\n"
						+ responseLocation;
			}
			else{
				return "Error at the upload!\n"
						+ "For more details see\n"
						+ responseLocation;
			}
		}
	}
	
	public boolean hadNotInSyncProblem(){
		return notInSyncProblem;
	}
}
