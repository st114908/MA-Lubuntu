package de.ust.pipelineexecution.ui.exports;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.muml.core.export.operation.AbstractFujabaExportOperation;
import org.muml.core.export.operation.IFujabaExportOperation;

import de.ust.arduinocliutilizer.worksteps.exceptions.FQBNErrorEception;
import de.ust.arduinocliutilizer.worksteps.exceptions.NoArduinoCLIConfigFileException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.InOrOutKeyNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ParameterMismatchException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StructureException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ContainerCodeGeneration;
import de.ust.mumlacgppa.pipeline.paths.PipelineSettingsDirectoryAndFilesPaths;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

/**
 * For versatility this is basically PipelineExecutionAsExport with a different targeted field.
 * @author muml
 */
public class ContainerCodeGenerationExecutionAsExport extends PipelineExecutionAsExport {

	protected ContainerCodeGeneration generation;
	protected boolean settingsMissing;
	
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
				.resolve(PipelineSettingsDirectoryAndFilesPaths.PIPELINE_SETTINGS_DIRECTORY_FOLDER)
				.resolve(PipelineSettingsDirectoryAndFilesPaths.PIPELINE_SETTINGS_FILE_NAME);

		boolean problemsWithPipelineSettingsFile = handlePipelineSettingsFile();
		if(problemsWithPipelineSettingsFile){
			return;
		}
		
		if(!PSRInstance.IsEntryInTransformationAndCodeGenerationPreconfigurations(ContainerCodeGeneration.nameFlag)){
			InfoWindow errorInfoWindow = new InfoWindow("Container code generation execution",
					"Container code generation can't start.",
					"The container code generation can't be executed because the required configurations are missing or can't be found.");
			addPage(errorInfoWindow);
			settingsMissing = true;
			return;
		}
		settingsMissing = false;
		
		generation = (ContainerCodeGeneration) PSRInstance.getTransformationAndCodeGenerationPreconfigurationsDef(ContainerCodeGeneration.nameFlag);
		
		try {
			generateFolderIfNecessary(generation.getResolvedPathContentOfInput("arduinoContainersDestinationFolder").getParent());
		} catch (VariableNotDefinedException | StructureException | InOrOutKeyNotDefinedException | FaultyDataException e) {
			exceptionFeedback(e);
		}
		
		final IFile selectedFile = (IFile) ((IStructuredSelection) selection).getFirstElement();
		try {
			refreshWorkSpace(selectedFile);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		InfoWindow readyToStartPipelineInfoWindow = new InfoWindow("Container code generation execution",
				"Container code generation ready to start.",
				"The execution of the container code generation is ready to start.\n" + "Click \"Finish\" to start it.");
		addPage(readyToStartPipelineInfoWindow);

	}


	@Override
	public IFujabaExportOperation wizardCreateExportOperation() {

		// Do nothing if the settings file couldn't be found.
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

		return new AbstractFujabaExportOperation() {
			@Override
			protected IStatus doExecute(IProgressMonitor progressMonitor) {
				// T3.6 and T3.7: Container Code Generation
				try {
					generation.execute();
				} catch (VariableNotDefinedException | StructureException | InOrOutKeyNotDefinedException |
						FaultyDataException | ParameterMismatchException | IOException | InterruptedException |
						NoArduinoCLIConfigFileException | FQBNErrorEception e) {
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
