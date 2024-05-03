package de.ust.arduinocliutilizer.popup.actions;

import java.io.IOException;
import java.nio.file.Path;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import de.ust.arduinocliutilizer.worksteps.exceptions.FQBNErrorEception;
import de.ust.arduinocliutilizer.worksteps.exceptions.NoArduinoCLIConfigFileException;
import de.ust.arduinocliutilizer.worksteps.functions.UploadCall;
import projectfolderpathstorageplugin.ProjectFolderPathNotSetException;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

public class UploadArduinoProjectAction implements IObjectActionDelegate, SelectedFilePathAndContextFinder {

	private Shell shell;
	
	/**
	 * Constructor for Action1.
	 */
	public UploadArduinoProjectAction() {
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
		ProjectFolderPathStorage.projectFolderPath = getProjectPathOfSelectedFileByRessource();
		Path targetFilePath = getPathOfSelectedFile();
		try {
			BoardAutoSelectionAndInstallation BoardAutoSelectionAndInstallationInstance = new BoardAutoSelectionAndInstallation(); 
			boolean autoSelectionAndInstallationSuccessful = BoardAutoSelectionAndInstallationInstance.autoSelectAndInstal(shell, targetFilePath);
			if(!autoSelectionAndInstallationSuccessful){
				return;
			}
			
			String foundFqbn = BoardAutoSelectionAndInstallationInstance.getFoundFqbn();
			String foundPortAddress = BoardAutoSelectionAndInstallationInstance.getFoundPortAddress();
			
			// Upload:
			UploadCall UploadStepInstance = new UploadCall(targetFilePath, foundPortAddress, foundFqbn);
			if(!UploadStepInstance.isSuccessful()){
				UploadStepInstance.doErrorMessage(shell);
				return;
			}
			
			MessageDialog.openInformation(
					shell,
					"ArduinoCLIUtilizer: Upload step",
					"Code uploaded successfully!");
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
		} catch (FQBNErrorEception | NoArduinoCLIConfigFileException | ProjectFolderPathNotSetException e) {
			MessageDialog.openInformation(
				shell,
				"ArduinoCLIUtilizer",
				e.getMessage());
				e.printStackTrace();
		}
		finally {
			// Refresh Project
			try {
				getRessourceOfSelectedFile().getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
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
