package de.ust.arduinocliutilizer.popup.actions;

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

import de.ust.arduinocliutilizer.configgenerator.ArduinoCLIUtilizerConfigGenerator;
import projectfolderpathstorageplugin.ProjectFolderPathNotSetException;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

public class GenerateArduinoCLIUtilizerConfigAction implements IObjectActionDelegate, SelectedFilePathAndContextFinder{

	private Shell shell;
	
	/**
	 * Constructor for Action1.
	 */
	public GenerateArduinoCLIUtilizerConfigAction() {
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
			ProjectFolderPathStorage.projectFolderPath = getProjectPathOfSelectedFileByRessource();
			ArduinoCLIUtilizerConfigGenerator generator = new ArduinoCLIUtilizerConfigGenerator();
			String completeConfigFilePath = generator.getCompleteConfigFilePath().toString();
			if(generator.checkIfArduinoCLIUtilizerConfigFileExistsAtDefaultLocation()){
				getProjectOfSelectedFileByRessource().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
				MessageDialog.openInformation(
						shell,
						"ArduinoCLIUtilizer",
						"The ArduinoCLIUtilizer config file already exists!\n"
						+ "See " + completeConfigFilePath);
				return;
			}
			
			try{
				boolean generated = generator.generateArduinoCLIUtilizerConfigFile();
				if(generated){
					MessageDialog.openInformation(
						shell,
						"ArduinoCLIUtilizer",
						"Successfully generated the ArduinoCLIUtilizer config file!\n"
							+ "You can find it at " + completeConfigFilePath + "\n\n"
							+ "Advice by the generator:\n" + generator.getAdvice());
					}
				else{
					MessageDialog.openInformation(
						shell,
						"ArduinoCLIUtilizer",
						"Generation of the ArduinoCLIUtilizer config file failed!\n\n"
							+ "Advice by the generator:\n" + generator.getAdvice());
				}
			}
			catch (IOException e) {
				MessageDialog.openInformation(
					shell,
					"ArduinoCLIUtilizer",
					"IOException occured!\n"
					+ "Please make sure that nothing blocks the generation of\n\n"
					+ completeConfigFilePath + "\n"
					+ "and then try again.");
				e.printStackTrace();
			} catch (InterruptedException e) {
				MessageDialog.openInformation(
						shell,
						"ArduinoCLIUtilizer",
						"InterruptedException occured!\n"
						+ "Please make sure that nothing blocks the terminal calls on the system\n");
					e.printStackTrace();
			}
		} catch (ProjectFolderPathNotSetException e) {
			MessageDialog.openInformation(
					shell,
					"ArduinoCLIUtilizer",
					e.getMessage());
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			// Refresh Project
			try {
				getProjectOfSelectedFileByRessource().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
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
