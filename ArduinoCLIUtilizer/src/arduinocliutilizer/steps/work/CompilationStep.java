package arduinocliutilizer.steps.work;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.yaml.snakeyaml.Yaml;

import arduinocliutilizer.paths.FQBNStorageFileName;
import arduinocliutilizer.paths.SelectedFilePathAndContextFinder;
import arduinocliutilizer.steps.common.ArduinoCLICommandLineHandler;
import arduinocliutilizer.steps.common.ResponseFeedback;
import arduinocliutilizer.steps.common.SaveResponseInfoLocation;
import arduinocliutilizer.steps.common.StepSuperClass;
import arduinocliutilizer.steps.exceptions.NoArduinoCLIConfigFileException;

public class CompilationStep extends StepSuperClass implements SelectedFilePathAndContextFinder, SaveResponseInfoLocation, FQBNStorageFileName{
	
	public boolean performCompilation(String foundFqbn, String foundPortAddress, String target, boolean saveCompiledFilesNearby, String parentLocation) throws IOException, InterruptedException, NoArduinoCLIConfigFileException {
		ArduinoCLICommandLineHandler commandLineDoer = new ArduinoCLICommandLineHandler();
		String compilationCommand;
		String compiledFilesDirIfUsed = SelectedFilePathAndContextFinder.getCompiledFilesDirectory();
		if(saveCompiledFilesNearby){
			// arduino-cli compile --fqbn [BOARD] [INOFILE] --output-dir [OUT]
			compilationCommand = "arduino-cli compile --port " + foundPortAddress + " --fqbn " + foundFqbn + " " + target + " --output-dir " + compiledFilesDirIfUsed + " --format yaml";
		}
		else {
			// arduino-cli compile --fqbn [BOARD] [INOFILE]
			compilationCommand = "arduino-cli compile --port " + foundPortAddress + " --fqbn " + foundFqbn + " " + target + " --format yaml";
		}
		ResponseFeedback feedbackCompilation = commandLineDoer.doShellCommand(compilationCommand);
		//System.out.println(feedbackCompilation);
		responseLocation = SaveResponseInfoLocation.saveShellResponseInfo(
			parentLocation, "CompilationInfo.txt",
			compilationCommand, feedbackCompilation);
		
		if(feedbackCompilation.exitCode != 0){
			return false;
		}
		Yaml yamlCompileResponse = new Yaml();
		@SuppressWarnings("unchecked")
		Map<String, Object> compileResponse = (Map<String, Object>) yamlCompileResponse.load(feedbackCompilation.normalFeedback);
		if( !((boolean) compileResponse.get("success")) ){
			return false;
		}
		
		// Save fqbn as safeguard against Uploads on different board types.
		if(saveCompiledFilesNearby){
			FileWriter myWriter = new FileWriter(compiledFilesDirIfUsed + "/" + fqbnStorageFileName);
			myWriter.write(foundFqbn);
			myWriter.close();
		}
		return true;
	}
	
	public void doErrorMessage(Shell shell){
		MessageDialog.openInformation(
				shell,
				"ArduinoCLIUtilizer: Compiling step",
				"Error at the compilation!\n"+
					"For more details see\n"+
						responseLocation);
	}
}
