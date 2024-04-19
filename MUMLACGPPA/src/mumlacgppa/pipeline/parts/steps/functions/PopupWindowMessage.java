package mumlacgppa.pipeline.parts.steps.functions;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import mumlacgppa.pipeline.parts.exceptions.ParameterMismatchException;
import mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import mumlacgppa.pipeline.parts.exceptions.StructureException;
import mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import mumlacgppa.pipeline.parts.steps.Keywords;
import mumlacgppa.pipeline.parts.steps.PipelineStep;
import mumlacgppa.pipeline.parts.storage.VariableHandler;

public class PopupWindowMessage extends PipelineStep implements Keywords{

	public static final String nameFlag = "PopupWindowMessage";

	/**
	 * @see mumlacgppa.pipeline.parts.steps.PipelineStep#setRequiredInsAndOuts()
	 */
	@Override
	protected void setRequiredInsAndOuts(){
		requiredInsAndOuts = new LinkedHashMap<String,HashSet<String>>();
		
		HashSet<String> ins = new LinkedHashSet<String>();
		ins.add("condition");
		ins.add("message");
		requiredInsAndOuts.put(inFlag, ins);
		
		HashSet<String> outs = new LinkedHashSet<String>();
		
		requiredInsAndOuts.put(outFlag, outs);
	}

	public static Map<String, Map<String, String>> generateDefaultOrExampleValues(){
		// Map<String, Map<String, String>> for
		// Map<InOrOut, Map<ParameterOrOneOutput, SourceOrSaveTarget>>
		Map<String, Map<String, String>> exampleSettings = new LinkedHashMap<String, Map<String,String>>();

		// Ins:
		Map<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("condition", "direct true");
		ins.put("message", "direct The condition is true, so this will be shown.");
		exampleSettings.put(inFlag, ins);
		
		return exampleSettings;
	}
	
	
	public PopupWindowMessage(VariableHandler VariableHandlerInstance, Map<String, Map<String, String>> readData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, readData);
	}
	

	/**
	 * @param yamlData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 */
	@SuppressWarnings("unchecked")
	public PopupWindowMessage(VariableHandler VariableHandlerInstance, String yamlData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, yamlData);
	}

	@Override
	public void execute() throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException {
		boolean condition = handleInputByKey("condition").getBooleanContent();
		if(!condition){
			return;
		}
		String message = handleInputByKey("message").getContent();
		
		Display display = Display.getCurrent();
		final Shell shellPopupWindowMessage = new Shell(display);
		/*GridLayout gridLayout = new GridLayout(1, true);
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
		*/
		MessageDialog.openInformation(
				shellPopupWindowMessage,
				"Pipeline message",
				message);
	}
}
