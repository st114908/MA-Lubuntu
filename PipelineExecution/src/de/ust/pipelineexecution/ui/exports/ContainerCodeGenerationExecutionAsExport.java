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

import de.ust.mumlacgppa.pipeline.parts.exceptions.InOrOutKeyNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StructureException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.steps.PipelineStep;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ContainerCodeGeneration;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.LookupBoardBySerialNumber;
import de.ust.mumlacgppa.pipeline.paths.PipelineSettingsDirectoryAndFilePaths;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

/**
 * For versatility this is basically PipelineExecutionAsExport with a different targeted field.
 * @author muml
 */
public class ContainerCodeGenerationExecutionAsExport extends PipelineExecutionAsExport {

	protected ContainerCodeGeneration generation;
	
	@Override
	public String wizardGetId() {
		return "pipelineexecution.ui.exports.ContainerCodeGenerationExecutionAsExport";
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
		
		generation = (ContainerCodeGeneration) PSRInstance.getStandaloneUsageDefs().get(ContainerCodeGeneration.nameFlag);
		
		try {
			generateFolderIfNecessary(generation.getResolvedPathContentOfInput("arduinoContainersDestinationFolder").getParent());
		} catch (VariableNotDefinedException | StructureException | InOrOutKeyNotDefinedException e) {
			exceptionFeedback(e);
		}
		
		InfoWindow readyToStartPipelineIfoWindow = new InfoWindow("Container code generation execution",
				"Container code generation ready to start.",
				"The execution of the container code generation is ready to start.\n" + "Click \"Finish\" to start it.");
		addPage(readyToStartPipelineIfoWindow);

		final IFile selectedFile = (IFile) ((IStructuredSelection) selection).getFirstElement();
		try {
			refreshWorkSpace(selectedFile);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}


	@Override
	public IFujabaExportOperation wizardCreateExportOperation() {

		// Do nothing if the settings file couldn't be found.
		if (pipelineSettingsFileNotFound || pipelineSettingsErrorDetected) {
			return new AbstractFujabaExportOperation() {
				@Override
				protected IStatus doExecute(IProgressMonitor progressMonitor) {
					return Status.OK_STATUS;
				}
			};
		}

		// Here the preparations, because when doExecute(IProgressMonitor
		// progressMonitor) gets executed
		// the file handling gets a bit tricky as is evident with
		// MUML_Container2.muml_container getting generated
		// if MUML_Container.muml_container doesn't get deleted early enough.
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
		final IFile selectedFile = (IFile) ((IStructuredSelection) selection).getFirstElement();
		final IResource selectedResource = (IResource) selection.getFirstElement();
		final IProject targetProject = selectedResource.getProject();

		return new AbstractFujabaExportOperation() {
			@Override
			protected IStatus doExecute(IProgressMonitor progressMonitor) {
				// T3.6 and T3.7: Container Code Generation
				try {
					doExecuteContainerCodeGeneration(targetProject, generation, progressMonitor);
				} catch (VariableNotDefinedException | StructureException | InOrOutKeyNotDefinedException e) {
					// TODO Auto-generated catch block
					return exceptionFeedback(e);
				}
				try {
					refreshWorkSpace(selectedFile);
				} catch (CoreException e) {
					e.printStackTrace();
				}
				return Status.OK_STATUS;
			}
		};
	}
	
}
