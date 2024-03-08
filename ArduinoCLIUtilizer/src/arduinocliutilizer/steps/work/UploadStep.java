package arduinocliutilizer.steps.work;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import arduinocliutilizer.paths.FQBNStorageFileName;
import arduinocliutilizer.steps.common.ArduinoCLICommandLineHandler;
import arduinocliutilizer.steps.common.ResponseFeedback;
import arduinocliutilizer.steps.common.SaveResponseInfoLocation;
import arduinocliutilizer.steps.common.StepSuperClass;
import arduinocliutilizer.steps.exceptions.FQBNErrorEception;
import arduinocliutilizer.steps.exceptions.NoArduinoCLIConfigFileException;

public class UploadStep extends StepSuperClass implements SaveResponseInfoLocation, FQBNStorageFileName{
	
	public boolean performUpload(String foundPortAddress, String targetFqbn, String targetFile, String parentLocation) throws IOException, InterruptedException, FQBNErrorEception, NoArduinoCLIConfigFileException{
		ArduinoCLICommandLineHandler commandLineDoer = new ArduinoCLICommandLineHandler();
		String uploadCommand;
		if(targetFile.endsWith(".ino")){
			// arduino-cli upload --port [PORT] --fqbn [BOARD]  [INOFILE]
			uploadCommand = "arduino-cli upload --port " + foundPortAddress + " --fqbn " + targetFqbn + " " + targetFile + " --format yaml";
		}
		else{
			// arduino-cli upload --port [PORT] --fqbn [BOARD] --input-file [HEXFILE]
			uploadCommand = "arduino-cli upload --port " + foundPortAddress + " --fqbn " + targetFqbn + " --input-file " + targetFile + " --format yaml";
			String fqbnFileLocation = parentLocation + "/" + fqbnStorageFileName;
			File arduinoCLICheck = new File(fqbnFileLocation);
			if(!arduinoCLICheck.exists() && !arduinoCLICheck.isDirectory()){
				throw new FQBNErrorEception("Textfile with the used FQBN (board type identifier) not found!");
			}
			else if(arduinoCLICheck.exists() && arduinoCLICheck.isDirectory()){
				throw new FQBNErrorEception("Textfile with the used FQBN (board type identifier) not found!");
			}
			else{
				BufferedReader FQBNFileReader = new BufferedReader(new FileReader(new File(fqbnFileLocation)));
				String readFQBN = FQBNFileReader.readLine();
				FQBNFileReader.close();
				if(!targetFqbn.equals(readFQBN)){
					throw new FQBNErrorEception("FQBN (board type identifier) mismatch!\nPlease recompile using the target board!");
				}
			}
		}
		ResponseFeedback feedbackUpload = commandLineDoer.doShellCommand(uploadCommand);
		responseLocation = SaveResponseInfoLocation.saveShellResponseInfo(
			parentLocation, "UploadInfo.txt",
			uploadCommand, feedbackUpload);
		
		if(feedbackUpload.exitCode == 0){
			return true;
		}
		else{
			return false;
		}
	}
	
	public void doErrorMessage(Shell shell){
		MessageDialog.openInformation(
			shell,
			"ArduinoCLIUtilizer: Upload step",
			"Error at the upload!\n"
				+ "For more details see\n"
					+ responseLocation);
	}
}
