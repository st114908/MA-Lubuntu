package pipelineexecution.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import mumlacgppa.pipeline.parts.exceptions.ParameterMismatchException;
import mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import mumlacgppa.pipeline.parts.exceptions.StructureException;
import mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import mumlacgppa.pipeline.parts.steps.Keywords;
import mumlacgppa.pipeline.parts.steps.functions.*;
import mumlacgppa.pipeline.parts.storage.VariableHandler;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;


public class AutoDefaultGenerateAndPostProcess extends AbstractFujabaExportWizard{
	
	private AbstractFujabaExportSourcePage sourceSystemAllocationPage;
	private AbstractFujabaExportSourcePage sourceComponentInstancePage;

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
		
		
		// T3.2: Deployment Configuration aka Container Transformation:
		// Source page
		sourceSystemAllocationPage = generateSourceSystemAllocationPage();
		addPage(sourceSystemAllocationPage);
		
		// T3.3: Component Code Generation
		sourceComponentInstancePage = generateSourceComponentInstancePage();
		addPage(sourceComponentInstancePage);

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
		Path projectPath = Paths.get(targetProject.getRawLocation().toString());
		ProjectFolderPathStorage.projectFolderPath = projectPath;
		VariableHandler VariableHandlerInstance = new VariableHandler();
				
		String containerModelsFolderName = "container-models";
		IFolder containerModelsFolder = selectedResource.getProject().getFolder(containerModelsFolderName);
		IFile muml_containerFile = containerModelsFolder.getFile("MUML_Container.muml_container");
		
		// The Folders have to be generated here, because else Eclipse won't acknowledge them.
		
		// T3.2: Deployment Configuration aka Container Transformation
		final EObject[] sourceElementsSystemAllocation = sourceSystemAllocationPage.getSourceElements();
		Path destinationContainerTransformationPath = Paths.get( containerModelsFolder.getRawLocation().toString() );
		if(! Files.isDirectory(destinationContainerTransformationPath) ){
			try {
				Files.createDirectory(destinationContainerTransformationPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// T3.6 and T3.7: Container Code Generation
		String arduinoContainersFolderName = "arduino-containers";
		IFolder arduinoContainersFolder = selectedResource.getProject().getFolder(arduinoContainersFolderName);
		final Path arduinoCodeDestinationPath = Paths.get( arduinoContainersFolder.getRawLocation().toString() );
		if(! Files.isDirectory(arduinoCodeDestinationPath) ){
			try {
				Files.createDirectory(arduinoCodeDestinationPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// T3.3: Component Code Generation
		final EObject[] sourceElementsComponentInstance = sourceComponentInstancePage.getSourceElements();
		
		// Deleting of old "MUML_container.muml_container" file, if it exists:
		// Won't work here, because MUML_Container2.muml_container would be generated anyway if MUML_Container.muml_container already exists.
		// So done further below with renaming.
		/*Path muml_containerPath = Paths.get(muml_containerFile.getRawLocation().toString());
		if(Files.exists(muml_containerPath)){
			System.out.println("Deleting existing MUML_Container.muml_container");
			try {
				Files.delete(muml_containerPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		try {
			selectedFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new AbstractFujabaExportOperation() {
			@Override
			protected IStatus doExecute(IProgressMonitor progressMonitor) {
				// T3.2: Deployment Configuration aka Container Transformation
				final MiddlewareOption selectedMiddleware = MiddlewareOption.MQTT_I2C_CONFIG;
				ContainerTransformationHandler ContainerTransformationHandlerInstance = new ContainerTransformationHandler(
						selectedResource, sourceElementsSystemAllocation, containerModelsFolder, selectedMiddleware);
				ContainerTransformationHandlerInstance.performContainerTransformation(progressMonitor, editingDomain);
				
				try {
					selectedFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// T3.6 and T3.7: Container Code Generation
				String arduinoContainersFolderName = "arduino-containers";
				IFolder arduinoContainersFolder = selectedResource.getProject().getFolder(arduinoContainersFolderName);
				ContainerCodeGenerationHandler ContainerCodeGenerationHandlerInstance = new ContainerCodeGenerationHandler(muml_containerFile, arduinoContainersFolder);
				ContainerCodeGenerationHandlerInstance.performContainerCodeGeneration(progressMonitor);
				
				try {
					selectedFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// T3.3: Component Code Generation
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

				// Post-Processing:
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
				
				
				try {
					selectedFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return Status.CANCEL_STATUS;
				}
				return Status.OK_STATUS;
			}
		};
	}

}
