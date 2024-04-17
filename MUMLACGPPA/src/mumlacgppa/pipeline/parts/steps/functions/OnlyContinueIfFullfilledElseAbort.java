package mumlacgppa.pipeline.parts.steps.functions;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.yaml.snakeyaml.Yaml;

import mumlacgppa.pipeline.parts.exceptions.AbortPipelineException;
import mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import mumlacgppa.pipeline.parts.exceptions.ParameterMismatchException;
import mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import mumlacgppa.pipeline.parts.exceptions.StructureException;
import mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import mumlacgppa.pipeline.parts.steps.Keywords;
import mumlacgppa.pipeline.parts.steps.PipelineStep;
import mumlacgppa.pipeline.parts.storage.VariableHandler;

public class OnlyContinueIfFullfilledElseAbort extends PipelineStep implements Keywords{

	public static final String nameFlag = "OnlyContinueIfFullfilledElseAbort";
	
	protected void setRequiredInsAndOuts(){
		requiredInsAndOuts = new LinkedHashMap<String,HashSet<String>>();
		
		HashSet<String> ins = new LinkedHashSet<String>();
		ins.add("condition");
		requiredInsAndOuts.put(inFlag, ins);
		
		HashSet<String> outs = new LinkedHashSet<String>();
		
		requiredInsAndOuts.put(outFlag, outs);
	}

	// Map<String, Map<String, String>> for
	// Map<InOrOut, Map<ParameterOrOneOutput, SourceOrSaveTarget>>
	public static Map<String, Map<String, String>> generateDefaultOrExampleValues(){
		Map<String, Map<String, String>> exampleSettings = new LinkedHashMap<String, Map<String,String>>();

		// Ins:
		Map<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("condition", "direct true");
		exampleSettings.put(inFlag, ins);
		
		// Out:
		Map<String, String> outs = new LinkedHashMap<String, String>();
		exampleSettings.put(outFlag, outs);
		
		return exampleSettings;
	}
	
	
	public OnlyContinueIfFullfilledElseAbort(VariableHandler VariableHandlerInstance, Map<String, Map<String, String>> readData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, readData);
	}
	

	/**
	 * @param yamlData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 */
	@SuppressWarnings("unchecked")
	public OnlyContinueIfFullfilledElseAbort(VariableHandler VariableHandlerInstance, String yamlData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, yamlData);
	}

	@Override
	public void execute() throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, AbortPipelineException {
		boolean condition = handleInputByKey("condition").getBooleanContent();
		if(!condition){
		
			Display display = Display.getCurrent();
			final Shell shellPopupWindowMessage = new Shell(display);
			MessageDialog.openInformation(
				shellPopupWindowMessage,
				"Pipeline message",
				"Pipeline aborted!\n"
				+ "Input " + in.get("condition") + " has been evaluated to false.");
			throw new AbortPipelineException("The pipeline has been aborted.");
		}
	}
}
