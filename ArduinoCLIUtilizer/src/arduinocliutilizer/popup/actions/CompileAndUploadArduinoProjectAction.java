package arduinocliutilizer.popup.actions;

import java.io.IOException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import arduinocliutilizer.paths.SelectedFilePathAndContextFinder;
import arduinocliutilizer.steps.exceptions.FQBNErrorEception;
import arduinocliutilizer.steps.exceptions.NoArduinoCLIConfigFileException;
import arduinocliutilizer.steps.work.CompilationStep;
import arduinocliutilizer.steps.work.UploadStep;

public class CompileAndUploadArduinoProjectAction implements IObjectActionDelegate, SelectedFilePathAndContextFinder {

	private Shell shell;
	
	/**
	 * Constructor for Action1.
	 */
	public CompileAndUploadArduinoProjectAction() {
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
		String parentLocation = SelectedFilePathAndContextFinder.getParentOfSelectedFile();
		try {
			BoardAutoSelectionAndInstallation BoardAutoSelectionAndInstallationInstance = new BoardAutoSelectionAndInstallation();
			boolean autoSelectionAndInstallationSuccessful = BoardAutoSelectionAndInstallationInstance.autoSelectAndInstal(shell, parentLocation);
			if(!autoSelectionAndInstallationSuccessful){
				return;
			}
			
			String foundFqbn = BoardAutoSelectionAndInstallationInstance.getFoundFqbn();
			String foundPortAddress = BoardAutoSelectionAndInstallationInstance.getFoundPortAddress();
			
			String target = SelectedFilePathAndContextFinder.getLocationOfSelectedFile();
			
			// Compilation:
			CompilationStep CompilationStepInstance = new CompilationStep();
			boolean compilationOK = CompilationStepInstance.performCompilation(foundFqbn, foundPortAddress, target, false, parentLocation);
			if(!compilationOK){
				CompilationStepInstance.doErrorMessage(shell);
				return;
			}
			
			// Upload:
			UploadStep UploadStepInstance = new UploadStep();
			boolean uploadOK = UploadStepInstance.performUpload(foundPortAddress, foundFqbn, target, parentLocation);
			if(!uploadOK){
				UploadStepInstance.doErrorMessage(shell);
				return;
			}
			
			MessageDialog.openInformation(
					shell,
					"ArduinoCLIUtilizer: After all steps",
					"Code compiled and uploaded successfully!");
		}
		
		catch (IOException e) {
			MessageDialog.openInformation(
				shell,
				"ArduinoCLIUtilizer",
				"IOException occured!\n"+
				"Please make sure that none of the infolved files are opened by a program and then try again.");
			e.printStackTrace();
		} catch (InterruptedException e) {
			MessageDialog.openInformation(
				shell,
				"ArduinoCLIUtilizer",
				"The process has been interrupted!");
			e.printStackTrace();
		} catch (FQBNErrorEception e) {
			MessageDialog.openInformation(
				shell,
				"ArduinoCLIUtilizer",
				e.getMessage());
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
