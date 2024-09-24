/**
 * 
 */
package de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

import de.ust.arduinocliutilizer.worksteps.exceptions.FQBNErrorEception;
import de.ust.arduinocliutilizer.worksteps.exceptions.NoArduinoCLIConfigFileException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.InOrOutKeyNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ParameterMismatchException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StructureException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.steps.PipelineStep;
import de.ust.mumlacgppa.pipeline.parts.storage.VariableHandler;
import de.ust.mumlacgppa.pipeline.parts.storage.VariableTypes;
import projectfolderpathstorageplugin.ProjectFolderPathNotSetException;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

/**
 * Depending on the security measures and the used system for storing the code
 * states you might have to customize this class or to use TerminalCommand or
 * TerminalCommand with a program/script/etc. for automatic upload.
 */
public class AutoGitCommitAllAndPushCommand extends PipelineStep implements VariableTypes {

	public static final String nameFlag = "AutoGitCommitAllAndPushCommand";

	/**
	 * @see de.ust.mumlacgppa.pipeline.parts.steps.PipelineStep#getRequiredInsAndOuts()
	 */
	@Override
	protected Map<String, Map<String, String>> getRequiredInsAndOuts() {
		LinkedHashMap<String, Map<String, String>> requiredInsAndOuts = new LinkedHashMap<String, Map<String, String>>();

		LinkedHashMap<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("comment", StringType);
		ins.put("remoteName", StringType);
		ins.put("branchName", StringType);
		requiredInsAndOuts.put(inKeyword, ins);

		LinkedHashMap<String, String> outs = new LinkedHashMap<String, String>();
		outs.put("ifSuccessful", BooleanType);
		requiredInsAndOuts.put(outKeyword, outs);
		
		return requiredInsAndOuts;
	}

	public static Map<String, Map<String, String>> generateDefaultOrExampleValues() {
		// Map<String, Map<String, String>> for
		// Map<InOrOut, Map<ParameterOrOneOutput, SourceOrSaveTarget>>
		Map<String, Map<String, String>> exampleSettings = new LinkedHashMap<String, Map<String, String>>();

		// Ins:
		Map<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("comment", "Triggered automatic adding of all files, committing and uploading.");
		ins.put("remoteName", "origin");
		ins.put("branchName", "master");
		exampleSettings.put(inKeyword, ins);
		
		/*
		// Fixed version, not active in order to stay matching with the submitted thesis.
		Map<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("comment", "direct Triggered automatic adding of all files, committing and uploading.");
		ins.put("remoteName", "direct origin");
		ins.put("branchName", "direct master");
		exampleSettings.put(inKeyword, ins);
		*/

		// Out:
		Map<String, String> outs = new LinkedHashMap<String, String>();
		outs.put("ifSuccessful", "ifSuccessful");
		exampleSettings.put(outKeyword, outs);

		return exampleSettings;
	}

	/**
	 * @param readData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA
	 */
	public AutoGitCommitAllAndPushCommand(VariableHandler VariableHandlerInstance,
			Map<String, Map<String, String>> readData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, readData);
	}

	/**
	 * @param yamlData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA
	 */
	public AutoGitCommitAllAndPushCommand(VariableHandler VariableHandlerInstance, String yamlData)
			throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, yamlData);
	}

	private boolean performCommandLineCall(boolean isWindows, String checkIfFolderIsLocalRepositoryCommand,
			int expectedExitCode) throws IOException, InterruptedException {
		// Processbuilder is more intuitive to use than Runtime.
		ProcessBuilder checkIfRepositoryFolderProcess = new ProcessBuilder();
		if (isWindows) {
			checkIfRepositoryFolderProcess.command("cmd.exe", "/c", checkIfFolderIsLocalRepositoryCommand);
		} else {
			checkIfRepositoryFolderProcess.command("bash", "-c", checkIfFolderIsLocalRepositoryCommand);
		}
		Process proc = checkIfRepositoryFolderProcess.start();

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

		System.out.println(exitCode);
		System.out.println(normalFeedback);
		System.out.println(errorFeedback);

		boolean result = (expectedExitCode == exitCode);
		return result;
	}

	/**
	 * @throws ProjectFolderPathNotSetException
	 * @see mumlacga.pipeline.parts.steps.common.PipelineStep#execute()
	 */
	@Override
	public void execute() throws VariableNotDefinedException, StructureException, FaultyDataException,
			ParameterMismatchException, IOException, InterruptedException, NoArduinoCLIConfigFileException,
			FQBNErrorEception, InOrOutKeyNotDefinedException, ProjectFolderPathNotSetException {
		handleOutputByKey("ifSuccessful", false); // In case of exception.

		Path projectPathOfSelectedFile = ProjectFolderPathStorage.projectFolderPath;
		if (ProjectFolderPathStorage.projectFolderPath == null) {
			throw new ProjectFolderPathNotSetException(
					"The static field projectFolderPath in ProjectFolderPathStorageArduinoCLIUtilizer has "
							+ "to be set to a complete file system path to the project's folder!");
		}
		boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

		String goIntoProjectFolderCommandPart = "cd " + projectPathOfSelectedFile;

		String checkItselfCommandPartPart = "git rev-parse --is-inside-work-tree";
		String checkIfFolderIsLocalRepositoryCommand = goIntoProjectFolderCommandPart + " && "
				+ checkItselfCommandPartPart;
		boolean isLocalRepository = performCommandLineCall(isWindows, checkIfFolderIsLocalRepositoryCommand, 0);
		if (!isLocalRepository) {
			return;
		}

		String commentItselfPart = handleInputByKey("comment").getContent();
		String dateAndTimePart = LocalDateTime.now().toString() + " " + LocalTime.now().toString();
		String allInfosPart = dateAndTimePart + ": " + commentItselfPart;
		String addAndCommitThemselfesPart = "git add -A && git commit -m \"" + allInfosPart + "\"";
		String fullNavigateAddAndCommitCommand = goIntoProjectFolderCommandPart + " && " + addAndCommitThemselfesPart;
		boolean commitSuccessful = performCommandLineCall(isWindows, fullNavigateAddAndCommitCommand, 0);
		if (!commitSuccessful) {
			return;
		}

		String pushItselfPart = "git push -u " + handleInputByKey("remoteName").getContent() + " "
				+ handleInputByKey("branchName").getContent();
		String fullPushCommand = goIntoProjectFolderCommandPart + " && " + pushItselfPart;
		boolean pushSuccessful = performCommandLineCall(isWindows, fullPushCommand, 0);

		handleOutputByKey("ifSuccessful", pushSuccessful);
	}
}
