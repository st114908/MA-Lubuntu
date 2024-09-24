package de.ust.mumlacgppa.popup.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class RunPipelineAction implements IObjectActionDelegate {

	private Shell shell;
	
	/**
	 * Constructor for Action1.
	 */
	public RunPipelineAction() {
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
			"MUMLArduinoAutomatisation",
			"The pipeline execution via right click menu couldn't be within the given time.\n"
			+ "Currently it has been improvisized as export wizard (as workaround). So please use\n"
			+ "(Right click on a .muml file)/\"Export\"/\n"
			+ "\"MechatronicUML\"/ \"Execute pipeline sequence\"");
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
