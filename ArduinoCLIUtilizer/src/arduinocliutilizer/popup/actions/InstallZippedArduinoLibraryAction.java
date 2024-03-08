package arduinocliutilizer.popup.actions;



import java.io.IOException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import arduinocliutilizer.paths.SelectedFilePathAndContextFinder;
import arduinocliutilizer.steps.exceptions.NoArduinoCLIConfigFileException;
import arduinocliutilizer.steps.installation.ZippedArduinoLibraryInstaller;

public class InstallZippedArduinoLibraryAction implements IObjectActionDelegate, SelectedFilePathAndContextFinder {

	private Shell shell;
	
	/**
	 * Constructor for Action1.
	 */
	public InstallZippedArduinoLibraryAction() {
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
		try {
			ZippedArduinoLibraryInstaller ZippedArduinoLibraryInstallerInstance = new ZippedArduinoLibraryInstaller();
			boolean success = ZippedArduinoLibraryInstallerInstance.installZippedArduinoLibrary(
					SelectedFilePathAndContextFinder.getLocationOfSelectedFile(), SelectedFilePathAndContextFinder.getParentOfSelectedFile());
			if(success){
				MessageDialog.openInformation(
						shell,
						"ArduinoCLIUtilizer",
						"Library installed");
			}
			else{
				ZippedArduinoLibraryInstallerInstance.doErrorMessage(shell);
				return;
			}
		} catch (IOException e) {
			MessageDialog.openInformation(
					shell,
					"ArduinoCLIUtilizer",
					"IOException occured!\n"+
					"The stack trace has beeen printed in the console of the starting eclipse window");
			e.printStackTrace();
		} catch (InterruptedException e) {
			MessageDialog.openInformation(
					shell,
					"ArduinoCLIUtilizer",
					"The installation has been interrupted!\n"+
					"The stack trace has beeen printed in the console of the starting eclipse window");
			e.printStackTrace();
		} catch (NoArduinoCLIConfigFileException e) {
			MessageDialog.openInformation(
					shell,
					"ArduinoCLIUtilizer",
					e.getMessage());
					e.printStackTrace();
		}
		finally {
			// Refresh Project
			try {
				SelectedFilePathAndContextFinder.findSelectedFile().getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
