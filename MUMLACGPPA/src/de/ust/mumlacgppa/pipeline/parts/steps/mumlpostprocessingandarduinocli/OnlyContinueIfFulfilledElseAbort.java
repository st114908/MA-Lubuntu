package de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.ust.mumlacgppa.pipeline.parts.exceptions.AbortPipelineException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.InOrOutKeyNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ParameterMismatchException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StructureException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.steps.Keywords;
import de.ust.mumlacgppa.pipeline.parts.steps.PipelineStep;
import de.ust.mumlacgppa.pipeline.parts.storage.VariableHandler;

public class OnlyContinueIfFulfilledElseAbort extends PipelineStep implements Keywords {

	public static final String nameFlag = "OnlyContinueIfFulfilledElseAbort";

	/**
	 * @see de.ust.mumlacgppa.pipeline.parts.steps.PipelineStep#setRequiredInsAndOuts()
	 */
	@Override
	protected void setRequiredInsAndOuts() {
		requiredInsAndOuts = new LinkedHashMap<String, HashSet<String>>();

		HashSet<String> ins = new LinkedHashSet<String>();
		ins.add("condition");
		ins.add("message");
		requiredInsAndOuts.put(inKeyword, ins);

		HashSet<String> outs = new LinkedHashSet<String>();

		requiredInsAndOuts.put(outKeyword, outs);
	}

	public static Map<String, Map<String, String>> generateDefaultOrExampleValues() {
		// Map<String, Map<String, String>> for
		// Map<InOrOut, Map<ParameterOrOneOutput, SourceOrSaveTarget>>
		Map<String, Map<String, String>> exampleSettings = new LinkedHashMap<String, Map<String, String>>();

		// Ins:
		Map<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("condition", "direct false");
		ins.put("message", "direct Pipeline aborted!\nThe condition has been evaluated to false.");
		exampleSettings.put(inKeyword, ins);

		// Out:
		//Map<String, String> outs = new LinkedHashMap<String, String>();
		//exampleSettings.put(outFlag, outs);

		return exampleSettings;
	}

	public OnlyContinueIfFulfilledElseAbort(VariableHandler VariableHandlerInstance,
			Map<String, Map<String, String>> readData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, readData);
	}

	/**
	 * @param yamlData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA
	 */
	public OnlyContinueIfFulfilledElseAbort(VariableHandler VariableHandlerInstance, String yamlData)
			throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, yamlData);
	}

	@Override
	public void execute() throws VariableNotDefinedException, StructureException, FaultyDataException,
			ParameterMismatchException, AbortPipelineException, InOrOutKeyNotDefinedException {
		boolean condition = handleInputByKey("condition").getBooleanContent();
		if (condition) {
			Display display = Display.getCurrent();
			final Shell shellPopupWindowMessage = new Shell(display);
			MessageDialog.openInformation(shellPopupWindowMessage, "Pipeline message",
					handleInputByKey("message").getContent());
			throw new AbortPipelineException("The pipeline has been aborted.");
		}
	}
}
