package de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.ust.mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.InOrOutKeyNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ParameterMismatchException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StructureException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.steps.Keywords;
import de.ust.mumlacgppa.pipeline.parts.steps.PipelineStep;
import de.ust.mumlacgppa.pipeline.parts.storage.VariableHandler;
import de.ust.mumlacgppa.pipeline.parts.storage.VariableTypes;

public class DialogMessage extends PipelineStep implements Keywords, VariableTypes{

	public static final String nameFlag = "DialogMessage";

	/**
	 * @see de.ust.mumlacgppa.pipeline.parts.steps.PipelineStep#getRequiredInsAndOuts()
	 */
	@Override
	protected Map<String, Map<String, String>> getRequiredInsAndOuts() {
		LinkedHashMap<String, Map<String, String>> requiredInsAndOuts = new LinkedHashMap<String, Map<String, String>>();
		
		LinkedHashMap<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("condition", BooleanType);
		ins.put("message", AnyType);
		requiredInsAndOuts.put(inKeyword, ins);
		
		LinkedHashMap<String, String> outs = new LinkedHashMap<String, String>();
		
		requiredInsAndOuts.put(outKeyword, outs);
		
		return requiredInsAndOuts;
	}

	public static Map<String, Map<String, String>> generateDefaultOrExampleValues(){
		// Map<String, Map<String, String>> for
		// Map<InOrOut, Map<ParameterOrOneOutput, SourceOrSaveTarget>>
		Map<String, Map<String, String>> exampleSettings = new LinkedHashMap<String, Map<String,String>>();

		// Ins:
		Map<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("condition", "direct true");
		ins.put("message", "direct The condition is true, so this will be shown.");
		exampleSettings.put(inKeyword, ins);
		
		return exampleSettings;
	}
	
	
	public DialogMessage(VariableHandler VariableHandlerInstance, Map<String, Map<String, String>> readData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, readData);
	}
	

	/**
	 * @param yamlData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 */
	public DialogMessage(VariableHandler VariableHandlerInstance, String yamlData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, yamlData);
	}

	@Override
	public void execute() throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, InOrOutKeyNotDefinedException {
		boolean condition = handleInputByKey("condition").getBooleanContent();
		if(!condition){
			return;
		}
		String message = handleInputByKey("message").getContent();
		
		Display display = Display.getCurrent();
		final Shell shellWindowMessage = new Shell(display);
		MessageDialog.openInformation(
				shellWindowMessage,
				"Pipeline message",
				message);
	}
}
