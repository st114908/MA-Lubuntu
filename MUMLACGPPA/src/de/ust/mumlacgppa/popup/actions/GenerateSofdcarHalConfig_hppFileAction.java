package de.ust.mumlacgppa.popup.actions;

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

import de.ust.mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import de.ust.mumlacgppa.pipeline.settingsgeneration.mumlpostprocessingandarduinocli.SofdcarHalConfig_hppFileGenerator;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

public class GenerateSofdcarHalConfig_hppFileAction implements IObjectActionDelegate, SelectedFilePathAndContextFinder {

	private Shell shell;
	
	/**
	 * Constructor for Action1.
	 */
	public GenerateSofdcarHalConfig_hppFileAction() {
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
		try {
			SofdcarHalConfig_hppFileGenerator generator = new SofdcarHalConfig_hppFileGenerator();
			try {
				boolean generated = generator.generateSofdcarHalConfig_hppFile();
				if(generated){
					MessageDialog.openInformation(
						shell,
						"MUML Arduino Code Generation and Post Processing Automatisation",
						"The file with the local settings for Sofdcar-Hal has been generated at"
						+ generator.getCompleteSofdcarHalConfigFilePath() + ".\n\n"
						+ "Please replace the values with the ones you are using.");
				}
				else{
					MessageDialog.openInformation(
						shell,
						"MUML Arduino Code Generation and Post Processing Automatisation",
						"The file with the local settings for Sofdcar-Hal has been generated at"
						+ generator.getCompleteSofdcarHalConfigFilePath() + ".\n\n"
						+ "Please replace the values with the ones you are using.");
				}
			} catch (IOException e) {
				MessageDialog.openInformation(
					shell,
					"MUML Arduino Code Generation and Post Processing Automatisation",
					"IOException occured!\n"
					+ "Please make sure that nothing blocks the generation of\n"
					+ generator.getCompleteSofdcarHalConfigFilePath() + "\n"
					+ "and then try again."
					);
			}
			
		} catch (ProjectFolderPathNotSetExceptionMUMLACGPPA e) {
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
