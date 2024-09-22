/**
 * 
 */
package de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

/**
 * @author muml
 *
 */
public class TerminalCommand extends PipelineStep implements VariableTypes {

	public static final String nameFlag = "TerminalCommand";

	/**
	 * @see de.ust.mumlacgppa.pipeline.parts.steps.PipelineStep#getRequiredInsAndOuts()
	 */
	@Override
	protected Map<String, Map<String, String>> getRequiredInsAndOuts() {
		LinkedHashMap<String, Map<String, String>> requiredInsAndOuts = new LinkedHashMap<String, Map<String, String>>();

		LinkedHashMap<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("terminalCommand", StringType);
		ins.put("exitCodeNumberForSuccessfulExecution", NumberType);
		requiredInsAndOuts.put(inKeyword, ins);

		LinkedHashMap<String, String> outs = new LinkedHashMap<String, String>();
		outs.put("ifSuccessful", BooleanType);
		outs.put("exitCode", NumberType);
		outs.put("normalFeedback", StringType);
		outs.put("errorFeedback", StringType);
		requiredInsAndOuts.put(outKeyword, outs);
		
		return requiredInsAndOuts;
	}

	public static Map<String, Map<String, String>> generateDefaultOrExampleValues() {
		// Map<String, Map<String, String>> for
		// Map<InOrOut, Map<ParameterOrOneOutput, SourceOrSaveTarget>>
		Map<String, Map<String, String>> exampleSettings = new LinkedHashMap<String, Map<String, String>>();

		// Ins:
		Map<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("terminalCommand", "direct echo example");
		ins.put("exitCodeNumberForSuccessfulExecution", "direct 0");
		exampleSettings.put(inKeyword, ins);

		// Out:
		Map<String, String> outs = new LinkedHashMap<String, String>();
		outs.put("ifSuccessful", "ifSuccessful");
		outs.put("exitCode", "exitCode");
		outs.put("normalFeedback", "normalFeedback");
		outs.put("errorFeedback", "errorFeedback");
		exampleSettings.put(outKeyword, outs);

		return exampleSettings;
	}

	/**
	 * @param readData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA
	 */
	public TerminalCommand(VariableHandler VariableHandlerInstance, Map<String, Map<String, String>> readData)
			throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, readData);
	}

	/**
	 * @param yamlData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA
	 */
	public TerminalCommand(VariableHandler VariableHandlerInstance, String yamlData)
			throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, yamlData);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mumlacga.pipeline.parts.steps.common.PipelineStep#execute()
	 */
	@Override
	public void execute()
			throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException,
			IOException, InterruptedException, NoArduinoCLIConfigFileException, FQBNErrorEception, InOrOutKeyNotDefinedException {
		handleOutputByKey("ifSuccessful", false); // In case of exception.
		String command = handleInputByKey("terminalCommand").getContent();
		int desiredExitCode = handleInputByKey("exitCodeNumberForSuccessfulExecution").getIntContent();

		// Processbuilder is more intuitive to use than Runtime.
		ProcessBuilder processBuilder = new ProcessBuilder();
		// if(isWindows){
		// processBuilder.command("cmd.exe", "/c",
		// potentialArduinoCLIPathCommand + commandSequence);
		// }
		// else{
		processBuilder.command("bash", "-c", command);
		// }
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

		handleOutputByKey("ifSuccessful", (desiredExitCode == exitCode));
		handleOutputByKey("exitCode", exitCode);

		handleOutputByKey("normalFeedback", normalFeedback);
		handleOutputByKey("errorFeedback", errorFeedback);

	}

}
