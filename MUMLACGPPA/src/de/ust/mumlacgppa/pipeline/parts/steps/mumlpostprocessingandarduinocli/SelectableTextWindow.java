package de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

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

public class SelectableTextWindow extends PipelineStep implements Keywords, VariableTypes{

	public static final String nameFlag = "SelectableTextWindow";

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

	// Map<String, Map<String, String>> for
	// Map<InOrOut, Map<ParameterOrOneOutput, SourceOrSaveTarget>>
	public static Map<String, Map<String, String>> generateDefaultOrExampleValues(){
		Map<String, Map<String, String>> exampleSettings = new LinkedHashMap<String, Map<String,String>>();

		// Ins:
		Map<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("condition", "direct true");
		ins.put("message", "direct The condition is true, so this will be shown.");
		exampleSettings.put(inKeyword, ins);
		
		return exampleSettings;
	}
	
	
	public SelectableTextWindow(VariableHandler VariableHandlerInstance, Map<String, Map<String, String>> readData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, readData);
	}
	

	public SelectableTextWindow(VariableHandler VariableHandlerInstance, String yamlData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
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
		final Shell shellListWindow = new Shell(display);
		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.marginWidth = 5;
		gridLayout.marginHeight = 5;
		shellListWindow.setLayout(gridLayout);

		final Text text = new Text(shellListWindow, SWT.MULTI | SWT.BORDER | SWT.WRAP
		| SWT.READ_ONLY | SWT.V_SCROLL);
		text.setText(message);
		GridData gridData = new GridData(GridData.FILL, GridData.FILL, true,
		true);
		text.setLayoutData(gridData);

		shellListWindow.setText("Pipeline message"); // Sets the title!
		shellListWindow.setBounds(200, 200, 550, 500);
		shellListWindow.open();
		while (!shellListWindow.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		//display.dispose(); // Causes crash, but if commented out no problems.
	}
}
