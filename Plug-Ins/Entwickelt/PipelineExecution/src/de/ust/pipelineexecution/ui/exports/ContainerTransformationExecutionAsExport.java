package de.ust.pipelineexecution.ui.exports;

import java.nio.file.Path;
import java.nio.file.Paths;
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

import de.ust.mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.InOrOutKeyNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StructureException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ContainerTransformation;
import de.ust.mumlacgppa.pipeline.paths.PipelineSettingsDirectoryAndFilesPaths;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

/**
 * For versatility this is basically PipelineExecutionAsExport with a different
 * targeted field.
 * 
 * @author muml
 */
public class ContainerTransformationExecutionAsExport extends PipelineExecutionAsExport {

	protected ContainerTransformation transformation;
	protected boolean settingsMissing;

	@Override
	public String wizardGetId() {
		return "pipelineexecution.ui.exports.ContainerTransformationExecutionAsExport";
	}

	@Override
	public void addPages() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
		final IResource selectedResource = (IResource) selection.getFirstElement();

		final IProject targetProject = selectedResource.getProject();
		Path projectPath = Paths.get(targetProject.getRawLocation().toString());

		ProjectFolderPathStorage.project = targetProject;
		ProjectFolderPathStorage.projectFolderPath = projectPath;
		completePipelineSettingsFilePath = projectPath
				.resolve(PipelineSettingsDirectoryAndFilesPaths.PIPELINE_SETTINGS_DIRECTORY_FOLDER)
				.resolve(PipelineSettingsDirectoryAndFilesPaths.PIPELINE_SETTINGS_FILE_NAME);

		boolean problemsWithPipelineSettingsFile = handlePipelineSettingsFile();
		if (problemsWithPipelineSettingsFile) {
			return;
		}

		if(!PSRInstance.IsEntryInTransformationAndCodeGenerationPreconfigurations(ContainerTransformation.nameFlag)){
			InfoWindow errorInfoWindow = new InfoWindow("Container transformation execution",
					"Container transformation can't start.",
					"The container transformation can't be executed because the required configurations are missing or can't be found.");
			addPage(errorInfoWindow);
			settingsMissing = true;
			return;
		}
		settingsMissing = false;
		
		transformation = (ContainerTransformation) PSRInstance.getTransformationAndCodeGenerationPreconfigurationsDef(ContainerTransformation.nameFlag);

		sourceSystemAllocationPage = generateSourceSystemAllocationPage();
		addPage(sourceSystemAllocationPage);
		try {
			generateFolderIfNecessary(
					transformation.getResolvedPathContentOfInput("muml_containerFileDestination").getParent());
		} catch (VariableNotDefinedException | StructureException | InOrOutKeyNotDefinedException | FaultyDataException e) {
			exceptionFeedback(e);
		}

		InfoWindow readyToStartPipelineInfoWindow = new InfoWindow("Container transformation execution",
				"Container transformation ready to start.",
				"The execution of the container transformation is ready to start.\n" + "Click \"Finish\" to start it.");
		addPage(readyToStartPipelineInfoWindow);

		final IFile selectedFile = (IFile) ((IStructuredSelection) selection).getFirstElement();
		try {
			refreshWorkSpace(selectedFile);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public IFujabaExportOperation wizardCreateExportOperation() {

		// Do nothing if the settings couldn't be found.
		if (settingsMissing || pipelineSettingsFileNotFound || pipelineSettingsErrorDetected) {
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

		// T3.2: Deployment Configuration aka Container Transformation
		final EObject[] sourceElementsSystemAllocation = sourceSystemAllocationPage.getSourceElements();
		// generateContainerTransformationFolder(containerModelsFolder);

		return new AbstractFujabaExportOperation() {
			@Override
			protected IStatus doExecute(IProgressMonitor progressMonitor) {
				try {
					// T3.2: Deployment Configuration aka Container
					// Transformation
					doExecuteContainerTransformationPart(sourceElementsSystemAllocation, transformation,
							progressMonitor);
				} catch (VariableNotDefinedException | StructureException | InOrOutKeyNotDefinedException | FaultyDataException e) {
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
