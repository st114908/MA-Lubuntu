package pipelineexecution.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.workspace.WorkspaceEditingDomainFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.muml.arduino.adapter.container.ui.common.GenerateAll;
import org.muml.c.adapter.componenttype.ui.export.C99SourceCodeExport;
import org.muml.codegen.componenttype.export.ui.Activator;
import org.muml.codegen.componenttype.export.ui.ITargetPlatformGenerator;
import org.muml.container.transformation.job.ContainerGenerationJob;
import org.muml.container.transformation.job.MiddlewareOption;
import org.muml.core.export.operation.AbstractFujabaExportOperation;
import org.muml.core.export.operation.IFujabaExportOperation;
import org.muml.core.export.pages.AbstractFujabaExportSourcePage;
import org.muml.core.export.pages.ElementSelectionMode;
import org.muml.core.export.wizard.AbstractFujabaExportWizard;
import org.muml.pim.instance.ComponentInstanceConfiguration;
import org.muml.psm.allocation.SystemAllocation;
import org.muml.psm.muml_container.DeploymentConfiguration;

import arduinocliutilizer.worksteps.exceptions.FQBNErrorEception;
import arduinocliutilizer.worksteps.exceptions.NoArduinoCLIConfigFileException;
import mumlacgppa.pipeline.parts.exceptions.AbortPipelineException;
import mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import mumlacgppa.pipeline.parts.exceptions.ParameterMismatchException;
import mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import mumlacgppa.pipeline.parts.exceptions.StepNotMatched;
import mumlacgppa.pipeline.parts.exceptions.StructureException;
import mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import mumlacgppa.pipeline.parts.steps.Keywords;
import mumlacgppa.pipeline.parts.steps.PipelineStep;
import mumlacgppa.pipeline.parts.steps.functions.*;
import mumlacgppa.pipeline.parts.storage.VariableHandler;
import mumlacgppa.pipeline.paths.PipelineSettingsDirectoryAndFilePaths;
import mumlacgppa.pipeline.settings.PipelineSettingsReader;
import projectfolderpathstorageplugin.ProjectFolderPathNotSetException;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;


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
		// TODO Auto-generated method stub
		return "pipelineexecution.ui.AutoGenerateAndPostProcessPrototype";
	}

	@Override
	public void addPages() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
		final IFile selectedFile = (IFile) ((IStructuredSelection) selection).getFirstElement();
		final IResource selectedResource = (IResource) selection.getFirstElement();
		
		final IProject targetProject = selectedResource.getProject();
		Path projectPath = Paths.get(targetProject.getRawLocation().toString());
		
		ProjectFolderPathStorage.projectFolderPath = projectPath;
		pipelineSettingsPath = projectPath.resolve(PipelineSettingsDirectoryAndFilePaths.pipelineSettingsDirectoryFolder)
				.resolve(PipelineSettingsDirectoryAndFilePaths.pipelineSettingsFileName);
		
		try {
			PSRInstance = new PipelineSettingsReader(pipelineSettingsPath);
			PSRInstance.validateOrder();
		} catch (FileNotFoundException | StructureException | StepNotMatched
				| ProjectFolderPathNotSetExceptionMUMLACGPPA e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			//TODO: Fehlermeldung.
			return;
		} catch (VariableNotDefinedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FaultyDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParameterMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			System.out.println(currentStep.toString());
			if(currentStep.getClass().getSimpleName().equals(ContainerTransformation.nameFlag)){
				// T3.2: Deployment Configuration aka Container Transformation:
				containerTransformationToBePerformed = true;
				sourceSystemAllocationPage = generateSourceSystemAllocationPage();
				addPage(sourceSystemAllocationPage);
				try {
					generateFolderIfNecessary(targetProject, currentStep.getContentOfInput("muml_containerFileDestination").getContent().split("/")[0]);
				} catch (VariableNotDefinedException | StructureException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if( currentStep.getClass().getSimpleName().equals(ContainerCodeGeneration.nameFlag)){
				// T3.6 and T3.7: Container Code Generation
				containerCodeGenerationToBePerformed = true;
				try {
					generateFolderIfNecessary(targetProject, currentStep.getContentOfInput("arduino_containersDestinationFolder").getContent());
				} catch (VariableNotDefinedException | StructureException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
		}
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
		// the file handling gets a bit tricky as is evident with the MUML_Container2.muml_container if MUML_Container.muml_container doesn't get deleted early enough.
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
		final IFile selectedFile = (IFile) ((IStructuredSelection) selection).getFirstElement();
		final IResource selectedResource = (IResource) selection.getFirstElement();
		
		final IProject targetProject = selectedResource.getProject();
		
		String containerModelsFolderName = "container-models";
		IFolder containerModelsFolder = targetProject.getFolder(containerModelsFolderName);
		IFile muml_containerFile = containerModelsFolder.getFile("MUML_Container.muml_container");
		
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
								doExecuteContanerCodeGeneration(targetProject, muml_containerFile, progressMonitor);
							}
			    		}
						else{
							try {
								currentStep.execute();
							} catch (VariableNotDefinedException | StructureException | FaultyDataException
									| ParameterMismatchException | IOException | InterruptedException
									| NoArduinoCLIConfigFileException | FQBNErrorEception
									| ProjectFolderPathNotSetException | AbortPipelineException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								
							}
						}
						refreshWorkSpace(selectedFile);
					}
					// Post-Processing:
					//doExecutePostProcessingUntilConfigPart(VariableHandlerInstance);
					refreshWorkSpace(selectedFile);
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
							doExecuteContainerTransformationPart(selectedResource, containerModelsFolder, sourceElementsSystemAllocation, progressMonitor);
						}
						else if( currentStep.getClass().getSimpleName().equals(ContainerCodeGeneration.nameFlag)){
							// T3.6 and T3.7: Container Code Generation
							if(containerCodeGenerationToBePerformed){
								doExecuteContanerCodeGeneration(targetProject, muml_containerFile, progressMonitor);
							}
			    		}
						else{
							try {
								currentStep.execute();
							} catch (VariableNotDefinedException | StructureException | FaultyDataException
									| ParameterMismatchException | IOException | InterruptedException
									| NoArduinoCLIConfigFileException | FQBNErrorEception
									| ProjectFolderPathNotSetException | AbortPipelineException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								
							}
						}
						refreshWorkSpace(selectedFile);
					}
					// Post-Processing:
					//doExecutePostProcessingUntilConfigPart(VariableHandlerInstance);
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
								doExecuteContanerCodeGeneration(targetProject, muml_containerFile, progressMonitor);
							}
			    		}
						else if( currentStep.getClass().getSimpleName().equals(ComponentCodeGeneration.nameFlag)){
							// T3.3: Component Code Generation
							doExecuteComponentCodeGeneration(targetProject, sourceElementsComponentInstance, progressMonitor);
			    		}
						else{
							try {
								currentStep.execute();
							} catch (VariableNotDefinedException | StructureException | FaultyDataException
									| ParameterMismatchException | IOException | InterruptedException
									| NoArduinoCLIConfigFileException | FQBNErrorEception
									| ProjectFolderPathNotSetException | AbortPipelineException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								
							}
						}
						refreshWorkSpace(selectedFile);
					}
					// Post-Processing:
					//doExecutePostProcessingUntilConfigPart(VariableHandlerInstance);
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
							doExecuteContainerTransformationPart(selectedResource, containerModelsFolder, sourceElementsSystemAllocation, progressMonitor);
						}
						else if( currentStep.getClass().getSimpleName().equals(ContainerCodeGeneration.nameFlag)){
							// T3.6 and T3.7: Container Code Generation
							if(containerCodeGenerationToBePerformed){
								doExecuteContanerCodeGeneration(targetProject, muml_containerFile, progressMonitor);
							}
			    		}
						else if( currentStep.getClass().getSimpleName().equals(ComponentCodeGeneration.nameFlag)){
							// T3.3: Component Code Generation
							doExecuteComponentCodeGeneration(targetProject, sourceElementsComponentInstance, progressMonitor);
			    		}
						else{
							try {
								currentStep.execute();
							} catch (VariableNotDefinedException | StructureException | FaultyDataException
									| ParameterMismatchException | IOException | InterruptedException
									| NoArduinoCLIConfigFileException | FQBNErrorEception
									| ProjectFolderPathNotSetException | AbortPipelineException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								
							}
						}
						refreshWorkSpace(selectedFile);
					}
					// Post-Processing:
					//doExecutePostProcessingUntilConfigPart(VariableHandlerInstance);
					refreshWorkSpace(selectedFile);
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
				Files.createDirectory(arduinoCodeDestinationPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
				Files.createDirectory(destinationContainerTransformationPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
	private void doExecuteContainerTransformationPart(final IResource selectedResource, IFolder containerModelsFolder,
			final EObject[] sourceElementsSystemAllocation, IProgressMonitor progressMonitor) {
		final MiddlewareOption selectedMiddleware = MiddlewareOption.MQTT_I2C_CONFIG;
		ContainerTransformationHandler ContainerTransformationHandlerInstance = new ContainerTransformationHandler(
				selectedResource, sourceElementsSystemAllocation, containerModelsFolder, selectedMiddleware);
		ContainerTransformationHandlerInstance.performContainerTransformation(progressMonitor, editingDomain);
	}

	/**
	 * @param targetProject
	 * @param muml_containerFile
	 * @param progressMonitor
	 */
	private void doExecuteContanerCodeGeneration(final IProject targetProject, IFile muml_containerFile, IProgressMonitor progressMonitor) {
		String arduinoContainersFolderName = "arduino-containers";
		IFolder arduinoContainersFolder = targetProject.getFolder(arduinoContainersFolderName);
		ContainerCodeGenerationHandler ContainerCodeGenerationHandlerInstance = new ContainerCodeGenerationHandler(muml_containerFile, arduinoContainersFolder);
		ContainerCodeGenerationHandlerInstance.performContainerCodeGeneration(progressMonitor);
	}
	
	/**
	 * @param targetProject
	 * @param sourceElementsComponentInstance
	 * @param progressMonitor
	 */
	private void doExecuteComponentCodeGeneration(final IProject targetProject, final EObject[] sourceElementsComponentInstance, IProgressMonitor progressMonitor) {
		String arduinoContainersFolderName = "arduino-containers";
		IFolder arduinoContainersFolder = targetProject.getFolder(arduinoContainersFolderName);
		final IContainer componentCodeDestinationIContainer = arduinoContainersFolder;
		final ITargetPlatformGenerator targetPlatform = new C99SourceCodeExport();
		try {
			for (EObject element : sourceElementsComponentInstance){
				targetPlatform.generateSourceCode(element, componentCodeDestinationIContainer, progressMonitor);
			}
		} catch (Exception e) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
			Activator.getDefault().getLog().log(status);
		}
	}
	
	/**
	 * @param VariableHandlerInstance
	 */
	private void doExecutePostProcessingUntilConfigPart(VariableHandler VariableHandlerInstance) {
		String yamlForPostProcessingStepsUntilConfig =
				"in:\n"
				+ "  arduinoContainersPath: direct arduino-containers\n"
				+ "  componentCodePath: direct arduino-containers/fastAndSlowCar_v2\n"
				+ "out:\n"
				+ "  ifSuccessful: ifSuccessful\n";
		try {
		PostProcessingStepsUntilConfig PerformPostProcessingInstance =
				new PostProcessingStepsUntilConfig(VariableHandlerInstance, yamlForPostProcessingStepsUntilConfig);
			PerformPostProcessingInstance.execute();
		}
		catch(Exception e){
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
			Activator.getDefault().getLog().log(status);
		}
		
		try {
			String yamlForFastCarDriverECUStateChartValues =
					"in:\n"
					+ "  arduinoContainersPath: direct /home/muml/MUMLProjects/Overtaking-Cars-Baumfalk/arduino-containers\n"
					+ "  ECUName: direct fastCarDriverECU\n"
					+ "  distanceLimit: direct 1\n"
					+ "  desiredVelocity: direct 2\n"
					+ "  slowVelocity: direct 3\n"
					+ "  laneDistance: direct 4\n"
					+ "out:\n"
					+ "  ifSuccessful: ifSuccessful\n";
			PostProcessingStateChartValues PerformPostProcessingInstanceFastCarDriverECU =
					new PostProcessingStateChartValues(VariableHandlerInstance, yamlForFastCarDriverECUStateChartValues);
			PerformPostProcessingInstanceFastCarDriverECU.execute();
		}
		catch(Exception e){
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
			Activator.getDefault().getLog().log(status);
		}
		try {
			String yamlForSlowCarDriverECUStateChartValues =
					"in:\n"
					+ "  arduinoContainersPath: direct /home/muml/MUMLProjects/Overtaking-Cars-Baumfalk/arduino-containers\n"
					+ "  ECUName: direct slowCarDriverECU\n"
					+ "  distanceLimit: direct 1\n"
					+ "  desiredVelocity: direct 2\n"
					+ "  slowVelocity: direct 3\n"
					+ "  laneDistance: direct 4\n"
					+ "out:\n"
					+ "  ifSuccessful: ifSuccessful\n";
			PostProcessingStateChartValues PerformPostProcessingInstanceSlowCarDriverECU =
					new PostProcessingStateChartValues(VariableHandlerInstance, yamlForSlowCarDriverECUStateChartValues);
			PerformPostProcessingInstanceSlowCarDriverECU.execute();
		}
		catch(Exception e){
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
