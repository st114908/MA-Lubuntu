package de.ust.pipelineexecution.ui.exports;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.muml.core.export.operation.AbstractFujabaExportOperation;
import org.muml.core.export.operation.IFujabaExportOperation;

import de.ust.mumlacgppa.pipeline.parts.steps.PipelineStep;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.LookupBoardBySerialNumber;
import de.ust.mumlacgppa.pipeline.paths.PipelineSettingsDirectoryAndFilePaths;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

/**
 * For versatility this is basically PipelineExecutionAsExport with a different targeted field.
 * @author muml
 */
public class PostProcessingExecutionAsExport extends PipelineExecutionAsExport {

	@Override
	public String wizardGetId() {
		return "pipelineexecution.ui.exports.PostProcessingExecutionAsExport";
	}
	

	@Override
	protected PipelineStep getNextStepRespectiveSequence() {
		return PSRInstance.getNextPostProcessingStep();
	}

	@Override
	protected boolean hasNextStepRespectiveSequence() {
		return PSRInstance.hasNextPostProcessingStep();
	}

	@Override
	protected void resetRespectiveSequence() {
		PSRInstance.resetPostProcessingProgress();;
	}


	@Override
	public void addPages() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
		final IResource selectedResource = (IResource) selection.getFirstElement();

		final IProject targetProject = selectedResource.getProject();
		Path projectPath = Paths.get(targetProject.getRawLocation().toString());

		ProjectFolderPathStorage.projectFolderPath = projectPath;
		completePipelineSettingsFilePath = projectPath
				.resolve(PipelineSettingsDirectoryAndFilePaths.PIPELINE_SETTINGS_DIRECTORY_FOLDER)
				.resolve(PipelineSettingsDirectoryAndFilePaths.PIPELINE_SETTINGS_FILE_NAME);

		boolean problemsWithPipelineSettingsFile = handlePipelineSettingsFile();
		if(problemsWithPipelineSettingsFile){
			return;
		}

		// Search for steps that require the ArduinoCLIUtilizer to be able to
		// work.
		// (It is assumed to be loaded, so this will just check for the
		// ArduinoCLI settings file.
		boolean problemsWithArduinoCLIUtilizer = handleArduinoCLIUtilizerCheck(projectPath);
		if(problemsWithArduinoCLIUtilizer){
			return;
		}
		
		// Information about steps with windows that appear.
		handleStepsWithWindowCheck();

		InfoWindow readyToStartPipelineInfoWindow = new InfoWindow("Postprocessing execution",
				"PostProcessing execution ready to start.",
				"The execution of the postprocessing is ready to start.\n" + "Click \"Finish\" to start it.");
		addPage(readyToStartPipelineInfoWindow);

		final IFile selectedFile = (IFile) ((IStructuredSelection) selection).getFirstElement();
		try {
			refreshWorkSpace(selectedFile);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

}
