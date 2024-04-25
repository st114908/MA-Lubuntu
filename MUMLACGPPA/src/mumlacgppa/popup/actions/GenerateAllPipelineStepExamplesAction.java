package mumlacgppa.popup.actions;

import java.io.IOException;
import java.nio.file.Paths;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import mumlacgppa.pipeline.mumlpostprocessingandarduinocli.settingsgeneration.StepsExamplesGenerator;
import mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import mumlacgppa.pipeline.parts.exceptions.ParameterMismatchException;
import mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import mumlacgppa.pipeline.parts.exceptions.StepNotMatched;
import mumlacgppa.pipeline.parts.exceptions.StructureException;
import mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

public class GenerateAllPipelineStepExamplesAction implements IObjectActionDelegate {

	private Shell shell;
	
	/**
	 * Constructor for Action1.
	 */
	public GenerateAllPipelineStepExamplesAction() {
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
		ProjectFolderPathStorage.projectFolderPath = SelectedFilePathAndContextFinder.getProjectPathOfSelectedFileByRessource();
		try {
			StepsExamplesGenerator generator = new StepsExamplesGenerator();
			try {
				boolean generated = generator.generateExampleListFile();
				if(generated){
					MessageDialog.openInformation(
						shell,
						"MUML Arduino Code Generation and Post Processing Automatisation",
						"The file with the pipeline step examples has been generated at"
						+ generator.getCompleteExamplesFilePath() + "."
						);
				}
				else{
					MessageDialog.openInformation(
						shell,
						"MUML Arduino Code Generation and Post Processing Automatisation",
						"The file with the pipeline step examples already exists at"
						+ generator.getCompleteExamplesFilePath() + "."
						);
				}
			} catch (IOException e) {
				MessageDialog.openInformation(
					shell,
					"MUML Arduino Code Generation and Post Processing Automatisation",
					"IOException occured!\n"
					+ "Please make sure that nothing blocks the generation of\n"
					+ generator.getCompleteExamplesFilePath() + "\n"
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
				SelectedFilePathAndContextFinder.getProjectOfSelectedFileByRessource().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
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
