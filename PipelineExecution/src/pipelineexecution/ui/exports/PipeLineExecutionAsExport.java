package pipelineexecution.ui.exports;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import org.muml.codegen.componenttype.export.ui.Activator;
import org.muml.core.export.operation.AbstractFujabaExportOperation;
import org.muml.core.export.operation.IFujabaExportOperation;
import org.muml.core.export.pages.AbstractFujabaExportSourcePage;
import org.muml.core.export.pages.ElementSelectionMode;
import org.muml.core.export.wizard.AbstractFujabaExportWizard;
import org.muml.pim.instance.ComponentInstanceConfiguration;
import org.muml.psm.allocation.SystemAllocation;

import projectfolderpathstorageplugin.ProjectFolderPathNotSetException;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

import arduinocliutilizer.worksteps.exceptions.FQBNErrorEception;
import arduinocliutilizer.worksteps.exceptions.NoArduinoCLIConfigFileException;

import mumlacgppa.pipeline.parts.exceptions.AbortPipelineException;
import mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import mumlacgppa.pipeline.parts.exceptions.ParameterMismatchException;
import mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import mumlacgppa.pipeline.parts.exceptions.StepNotMatched;
import mumlacgppa.pipeline.parts.exceptions.StructureException;
import mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import mumlacgppa.pipeline.parts.steps.PipelineStep;
import mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ComponentCodeGeneration;
import mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ContainerCodeGeneration;
import mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ContainerTransformation;
import mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.LookupBoardBySerialNumber;
import mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer;
import mumlacgppa.pipeline.paths.PipelineSettingsDirectoryAndFilePaths;
import mumlacgppa.pipeline.reader.PipelineSettingsReader;


public class PipeLineExecutionAsExport extends AbstractFujabaExportWizard{
	
	private AbstractFujabaExportSourcePage sourceSystemAllocationPage;
	private AbstractFujabaExportSourcePage sourceComponentInstancePage;
	private Path pipelineSettingsPath;
	
	private PipelineSettingsReader PSRInstance;
	private boolean containerTransformationToBePerformed;
	private boolean containerCodeGenerationToBePerformed;
	private boolean componentCodeGenerationToBePerformed;

	@Override
	public String wizardGetId() {
		return "pipelineexecution.ui.exports.PipeLineExecutionAsExport";
	}

	@Override
	public void addPages() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
		final IResource selectedResource = (IResource) selection.getFirstElement();
		
		final IProject targetProject = selectedResource.getProject();
		Path projectPath = Paths.get(targetProject.getRawLocation().toString());
		
		ProjectFolderPathStorage.projectFolderPath = projectPath;
		pipelineSettingsPath = projectPath.resolve(PipelineSettingsDirectoryAndFilePaths.PIPELINE_SETTINGS_DIRECTORY_FOLDER)
				.resolve(PipelineSettingsDirectoryAndFilePaths.PIPELINE_SETTINGS_FILE_NAME);
		
		try {
			PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer(), pipelineSettingsPath);
			PSRInstance.validateOrder();
		} catch (FileNotFoundException | StructureException | StepNotMatched
				| ProjectFolderPathNotSetExceptionMUMLACGPPA | VariableNotDefinedException
				| FaultyDataException | ParameterMismatchException e) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
			Activator.getDefault().getLog().log(status);
		}
		//Search for some steps that require some preparations but
		// neither allow that they get done right before they are needed
		// nor that their values are regardless of the execution logic just maybe initialized
		// due to doExecute requiring the requested values to be "final or effecively final" (or with other words not maybe given values).
		// .getClass().getSimpleName()
		containerTransformationToBePerformed = false;
		containerCodeGenerationToBePerformed = false;
		componentCodeGenerationToBePerformed = false;
		for(PipelineStep currentStep: PSRInstance.getPipelineSequence()){
			if(currentStep.getClass().getSimpleName().equals(ContainerTransformation.nameFlag)){
				// T3.2: Deployment Configuration aka Container Transformation:
				containerTransformationToBePerformed = true;
				sourceSystemAllocationPage = generateSourceSystemAllocationPage();
				addPage(sourceSystemAllocationPage);
				try {
					generateFolderIfNecessary(targetProject, currentStep.getContentOfInput("muml_containerFileDestination").getContent().split("/")[0]);
				} catch (VariableNotDefinedException | StructureException e) {
					IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
					Activator.getDefault().getLog().log(status);
				}
			}
			else if( currentStep.getClass().getSimpleName().equals(ContainerCodeGeneration.nameFlag)){
				// T3.6 and T3.7: Container Code Generation
				containerCodeGenerationToBePerformed = true;
				try {
					generateFolderIfNecessary(targetProject, currentStep.getContentOfInput("arduino_containersDestinationFolder").getContent());
				} catch (VariableNotDefinedException | StructureException e) {
					IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
					Activator.getDefault().getLog().log(status);
				}
    		}
			else if( currentStep.getClass().getSimpleName().equals(ComponentCodeGeneration.nameFlag)){
				// T3.3: Component Code Generation
    			componentCodeGenerationToBePerformed = true;
				sourceComponentInstancePage = generateSourceComponentInstancePage();
				addPage(sourceComponentInstancePage);
				try {
					generateFolderIfNecessary(targetProject, currentStep.getContentOfInput("arduino_containersDestinationFolder").getContent());
				} catch (VariableNotDefinedException | StructureException e) {
					IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
					Activator.getDefault().getLog().log(status);
				}
    		}
		}
		final IFile selectedFile = (IFile) ((IStructuredSelection) selection).getFirstElement();
		refreshWorkSpace(selectedFile);
	}

	/**
	 * @return
	 */
	protected AbstractFujabaExportSourcePage generateSourceComponentInstancePage() {
		return new AbstractFujabaExportSourcePage("source", toolkit, getResourceSet(), initialSelection) {
			@Override
			public String wizardPageGetSourceFileExtension() {
				return "muml";
			}

			@Override
			public boolean wizardPageSupportsSourceModelElement(EObject element) {
				return element instanceof ComponentInstanceConfiguration;
			}

			@Override
			public ElementSelectionMode wizardPageGetSupportedSelectionMode() {
				return ElementSelectionMode.ELEMENT_SELECTION_MODE_MULTI;
			}
		};
	}

	/**
	 * @return
	 */
	protected AbstractFujabaExportSourcePage generateSourceSystemAllocationPage() {
		return new AbstractFujabaExportSourcePage("source", toolkit, getResourceSet(), initialSelection) {

			@Override
			public String wizardPageGetSourceFileExtension() {
				return "muml";
			}

			@Override
			public boolean wizardPageSupportsSourceModelElement(EObject element) {
				return element instanceof SystemAllocation;
			}

			@Override
			public ElementSelectionMode wizardPageGetSupportedSelectionMode() {
				return ElementSelectionMode.ELEMENT_SELECTION_MODE_SINGLE;
			}

		};
	}
	
	

	@Override
	public IFujabaExportOperation wizardCreateExportOperation() {
		// Here the preparations, because when doExecute(IProgressMonitor progressMonitor) gets executed
		// the file handling gets a bit tricky as is evident with MUML_Container2.muml_container getting generated
		// if MUML_Container.muml_container doesn't get deleted early enough.
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
		final IFile selectedFile = (IFile) ((IStructuredSelection) selection).getFirstElement();
		final IResource selectedResource = (IResource) selection.getFirstElement();
		
		final IProject targetProject = selectedResource.getProject();
		
		String containerModelsFolderName = "container-models";
		IFolder containerModelsFolder = targetProject.getFolder(containerModelsFolderName);
		IFile muml_containerFile = containerModelsFolder.getFile("MUML_Container.muml_container");
		
		LookupBoardBySerialNumber.resetSearchState();
		refreshWorkSpace(selectedFile);
		
		// Due to doExecute requiring the requested values to be "final or effecively final" (or with other words not maybe given values),
		// duplication can't be avoided with my knowledge of eclipse.
		if( (containerTransformationToBePerformed == false) && (componentCodeGenerationToBePerformed == false) ){
			return new AbstractFujabaExportOperation() {
				@Override
				protected IStatus doExecute(IProgressMonitor progressMonitor) {
					for(PipelineStep currentStep: PSRInstance.getPipelineSequence()){
						if( currentStep.getClass().getSimpleName().equals(ContainerCodeGeneration.nameFlag)){
							// T3.6 and T3.7: Container Code Generation
							if(containerCodeGenerationToBePerformed){
								doExecuteContainerCodeGeneration(targetProject, muml_containerFile, (ContainerCodeGeneration) currentStep, progressMonitor);
							}
			    		}
						else{
							try {
								currentStep.execute();
							} catch (VariableNotDefinedException | StructureException | FaultyDataException
									| ParameterMismatchException | IOException | InterruptedException
									| NoArduinoCLIConfigFileException | FQBNErrorEception
									| ProjectFolderPathNotSetException | AbortPipelineException e) {
								IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
								Activator.getDefault().getLog().log(status);
							}
						}
						refreshWorkSpace(selectedFile);
					}
					return Status.OK_STATUS;
				}
			};
		}
		else if( (containerTransformationToBePerformed == true) && (componentCodeGenerationToBePerformed == false) ){
			// T3.2: Deployment Configuration aka Container Transformation
			final EObject[] sourceElementsSystemAllocation = sourceSystemAllocationPage.getSourceElements();
			generateContainerTransformationFolder(containerModelsFolder);
			
			return new AbstractFujabaExportOperation() {
				@Override
				protected IStatus doExecute(IProgressMonitor progressMonitor) {
					for(PipelineStep currentStep: PSRInstance.getPipelineSequence()){
						if(currentStep.getClass().getSimpleName().equals(ContainerTransformation.nameFlag)){
							// T3.2: Deployment Configuration aka Container Transformation
							doExecuteContainerTransformationPart(sourceElementsSystemAllocation, (ContainerTransformation) currentStep, progressMonitor);
						}
						else if( currentStep.getClass().getSimpleName().equals(ContainerCodeGeneration.nameFlag)){
							// T3.6 and T3.7: Container Code Generation
							if(containerCodeGenerationToBePerformed){
								doExecuteContainerCodeGeneration(targetProject, muml_containerFile, (ContainerCodeGeneration) currentStep, progressMonitor);
							}
			    		}
						else{
							try {
								currentStep.execute();
							} catch (VariableNotDefinedException | StructureException | FaultyDataException
									| ParameterMismatchException | IOException | InterruptedException
									| NoArduinoCLIConfigFileException | FQBNErrorEception
									| ProjectFolderPathNotSetException | AbortPipelineException e) {
								IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
								Activator.getDefault().getLog().log(status);								
							}
						}
						refreshWorkSpace(selectedFile);
					}
					refreshWorkSpace(selectedFile);
					return Status.OK_STATUS;
				}
			};
		}
		else if( (containerTransformationToBePerformed == false) && (componentCodeGenerationToBePerformed == true) ){
			// T3.3: Component Code Generation
			final EObject[] sourceElementsComponentInstance = sourceComponentInstancePage.getSourceElements();
			refreshWorkSpace(selectedFile);
			
			return new AbstractFujabaExportOperation() {
				@Override
				protected IStatus doExecute(IProgressMonitor progressMonitor) {
					for(PipelineStep currentStep: PSRInstance.getPipelineSequence()){
						if( currentStep.getClass().getSimpleName().equals(ContainerCodeGeneration.nameFlag)){
							// T3.6 and T3.7: Container Code Generation
							if(containerCodeGenerationToBePerformed){
								doExecuteContainerCodeGeneration(targetProject, muml_containerFile, (ContainerCodeGeneration) currentStep, progressMonitor);
							}
			    		}
						else if( currentStep.getClass().getSimpleName().equals(ComponentCodeGeneration.nameFlag)){
							// T3.3: Component Code Generation
							doExecuteComponentCodeGeneration(targetProject, sourceElementsComponentInstance, (ComponentCodeGeneration) currentStep, progressMonitor);
			    		}
						else{
							try {
								currentStep.execute();
							} catch (VariableNotDefinedException | StructureException | FaultyDataException
									| ParameterMismatchException | IOException | InterruptedException
									| NoArduinoCLIConfigFileException | FQBNErrorEception
									| ProjectFolderPathNotSetException | AbortPipelineException e) {
								IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
								Activator.getDefault().getLog().log(status);
							}
						}
						refreshWorkSpace(selectedFile);
					}
					refreshWorkSpace(selectedFile);
					return Status.OK_STATUS;
				}
			};
		}
		else{ // else assume both containerTransformationToBePerformed and componentCodeGenerationToBePerformed to be set to true.
			// T3.2: Deployment Configuration aka Container Transformation
			final EObject[] sourceElementsSystemAllocation = sourceSystemAllocationPage.getSourceElements();
			generateContainerTransformationFolder(containerModelsFolder);
			// T3.3: Component Code Generation
			final EObject[] sourceElementsComponentInstance = sourceComponentInstancePage.getSourceElements();
			refreshWorkSpace(selectedFile);
			
			return new AbstractFujabaExportOperation() {
				@Override
				protected IStatus doExecute(IProgressMonitor progressMonitor) {
					for(PipelineStep currentStep: PSRInstance.getPipelineSequence()){
						if(currentStep.getClass().getSimpleName().equals(ContainerTransformation.nameFlag)){
							// T3.2: Deployment Configuration aka Container Transformation
							doExecuteContainerTransformationPart(sourceElementsSystemAllocation, (ContainerTransformation) currentStep, progressMonitor);
						}
						else if( currentStep.getClass().getSimpleName().equals(ContainerCodeGeneration.nameFlag)){
							// T3.6 and T3.7: Container Code Generation
							if(containerCodeGenerationToBePerformed){
								doExecuteContainerCodeGeneration(targetProject, muml_containerFile, (ContainerCodeGeneration) currentStep, progressMonitor);
							}
			    		}
						else if( currentStep.getClass().getSimpleName().equals(ComponentCodeGeneration.nameFlag)){
							// T3.3: Component Code Generation
							doExecuteComponentCodeGeneration(targetProject, sourceElementsComponentInstance, (ComponentCodeGeneration) currentStep, progressMonitor);
			    		}
						else{
							try {
								currentStep.execute();
							} catch (VariableNotDefinedException | StructureException | FaultyDataException
									| ParameterMismatchException | IOException | InterruptedException
									| NoArduinoCLIConfigFileException | FQBNErrorEception
									| ProjectFolderPathNotSetException | AbortPipelineException e) {
								IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
								Activator.getDefault().getLog().log(status);
							}
						}
						refreshWorkSpace(selectedFile);
					}
					return Status.OK_STATUS;
				}
			};
		}
	
	}

	
	
	/**
	 * @param targetProject
	 */
	private void generateFolderIfNecessary(final IProject targetProject, String folderName) {
		String arduinoContainersFolderName = folderName;
		IFolder arduinoContainersFolder = targetProject.getFolder(arduinoContainersFolderName);
		final Path arduinoCodeDestinationPath = Paths.get( arduinoContainersFolder.getRawLocation().toString() );
		if(! Files.isDirectory(arduinoCodeDestinationPath) ){
			try {
				Files.createDirectories(arduinoCodeDestinationPath);
			} catch (IOException e) {
				IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
				Activator.getDefault().getLog().log(status);
			}
		}
	}

	/**
	 * @param containerModelsFolder
	 */
	private void generateContainerTransformationFolder(IFolder containerModelsFolder) {
		Path destinationContainerTransformationPath = Paths.get( containerModelsFolder.getRawLocation().toString() );
		if(! Files.isDirectory(destinationContainerTransformationPath) ){
			try {
				Files.createDirectories(destinationContainerTransformationPath);
			} catch (IOException e) {
				IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
				Activator.getDefault().getLog().log(status);
			}
		}
	}

	/**
	 * @param selectedFile
	 * @param selectedResource
	 * @param containerModelsFolder
	 * @param sourceElementsSystemAllocation
	 * @param progressMonitor
	 */
	private void doExecuteContainerTransformationPart(final EObject[] sourceElementsSystemAllocation,
			ContainerTransformation step, IProgressMonitor progressMonitor) {
		try {
			ContainerTransformationImprovisation ContainerTransformationHandlerInstance = new ContainerTransformationImprovisation(
					sourceElementsSystemAllocation, step);
			ContainerTransformationHandlerInstance.performContainerTransformation(progressMonitor, editingDomain);
		} catch (VariableNotDefinedException | StructureException e) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
			Activator.getDefault().getLog().log(status);
		}
	}

	/**
	 * @param targetProject
	 * @param muml_containerFile
	 * @param progressMonitor
	 */
	private void doExecuteContainerCodeGeneration(final IProject targetProject, IFile muml_containerFile,
			ContainerCodeGeneration step, IProgressMonitor progressMonitor) {
		try {
			ContainerCodeGenerationImprovisation ContainerCodeGenerationHandlerInstance = new ContainerCodeGenerationImprovisation(step);
			ContainerCodeGenerationHandlerInstance.performContainerCodeGeneration(targetProject, muml_containerFile, progressMonitor);
		} catch (VariableNotDefinedException | StructureException e) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
			Activator.getDefault().getLog().log(status);
		}
	}
	
	/**
	 * @param targetProject
	 * @param sourceElementsComponentInstance
	 * @param progressMonitor
	 */
	private void doExecuteComponentCodeGeneration(final IProject targetProject, final EObject[] sourceElementsComponentInstance, 
			ComponentCodeGeneration step, IProgressMonitor progressMonitor) {
		try {
			ComponentCodeGenerationImprovisation componentCodeGenerationHandlerInstance = new ComponentCodeGenerationImprovisation(step);
			componentCodeGenerationHandlerInstance.performComponentCodeGeneration(targetProject, sourceElementsComponentInstance, progressMonitor);
		} catch (VariableNotDefinedException | StructureException e) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
			Activator.getDefault().getLog().log(status);
		}
	}

	/**
	 * @param selectedFile
	 */
	private void refreshWorkSpace(final IFile selectedFile) {
		try {
			selectedFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		} catch (CoreException e) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
			Activator.getDefault().getLog().log(status);
		}
	}

}
