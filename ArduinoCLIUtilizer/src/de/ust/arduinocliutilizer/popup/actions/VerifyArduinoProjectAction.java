package de.ust.arduinocliutilizer.popup.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class VerifyArduinoProjectAction implements IObjectActionDelegate {

	private Shell shell;
	
	/**
	 * Constructor for Action1.
	 */
	public VerifyArduinoProjectAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		MessageDialog.openInformation(
			shell,
			"ArduinoCLIUtilizer: Verify",
			"Info: the ArduinoIDE and CLI use the compilation step for the verification of the code.\n"+
			"So use (Right click on a .zip, .ino or .hex file)/\"ArduinoCLIUtilizer\"/\n"+
			"\"Compile Arduino Project\" or \"Compile and upload Arduino Project\" and\n"+
			"check (.ino file location)/SavedResponses/CompilationInfo.txt .");
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
