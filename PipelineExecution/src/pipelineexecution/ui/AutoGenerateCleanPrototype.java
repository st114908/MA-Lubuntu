package pipelineexecution.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
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

public class AutoGenerateCleanPrototype extends AbstractFujabaExportWizard {
	
	private AbstractFujabaExportSourcePage sourcePageSystemAllocationConfiguration;
	//private MiddlewareOptionsPage middlewareOptionsPage;
	//private AbstractFujabaExportTargetPage targetPage;
	

	private AbstractFujabaExportSourcePage sourcePageComponentInstanceConfiguration;

	@Override
	public String wizardGetId() {
		// TODO Auto-generated method stub
		return "org.muml.container.transformation.ContainerWizard";
	}

	@Override
	public void addPages() {
		// T3.2: Deployment Configuration aka Container Transformation:
		// Source page
		sourcePageSystemAllocationConfiguration = new AbstractFujabaExportSourcePage("source", toolkit, getResourceSet(), initialSelection) {

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
		addPage(sourcePageSystemAllocationConfiguration);
		
		/*middlewareOptionsPage = new MiddlewareOptionsPage();
		addPage(middlewareOptionsPage);

		targetPage = new AbstractFujabaExportTargetPage("target", toolkit) {

			@Override
			public boolean wizardPageSupportsOverwriteOption() {
				return true;
			}

			@Override
			public boolean wizardPageDirectoryDestination() {
				return true;
			}

		};
		addPage(targetPage);*/
		
		// T3.3: Component Code Generation
		sourcePageComponentInstanceConfiguration = new AbstractFujabaExportSourcePage("source", toolkit, getResourceSet(), initialSelection) {
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
		addPage(sourcePageComponentInstanceConfiguration);

	}

	
	/*public static IFile findSelectedFile() {
		// From VonC's answer on https://stackoverflow.com/questions/6892294/eclipse-plugin-how-to-get-the-path-to-the-currently-selected-project
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
		
		// See mechatronicuml-cadapter-component-container/org.muml.arduino.adapter.container.ui/src/org/muml/arduino/adapter/container/ui/popupMenus/AcceleoGenerateArduinoContainerCodeAction.java
		IFile selectedFile = (IFile) ((IStructuredSelection) selection).toList().get(0);
		return selectedFile;
	}

	public static String getProjectOfSelectedFile(){
		IFile selectedFile = findSelectedFile();
		String result = selectedFile.getProject().getLocation().toString(); 
		return result;
	}*/
	
	/*protected List<? extends Object> getArguments() {
		return new ArrayList<String>();
	}*/
	
	@Override
	public IFujabaExportOperation wizardCreateExportOperation() {
		// T3.2: Deployment Configuration aka Container Transformation
		final EObject[] sourceElementsSystemAllocation = sourcePageSystemAllocationConfiguration.getSourceElements();
		final URI sourceURISystemAllocation = sourcePageSystemAllocationConfiguration.getURI();

		//final URI destinationURI = targetPage.getDestinationURI();
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();

		final IFile selectedFile = (IFile) ((IStructuredSelection) selection).getFirstElement();
		
		final IResource selectedResource = (IResource) selection.getFirstElement();
		final String projectName = selectedResource.getProject().getName();
		final String destinationFolderURIString = "platform:/resource/" + projectName + "/"+"container-models";
		final URI destinationURIContainerGeneration = URI.createURI(destinationFolderURIString , true);
		
		//final MiddlewareOption selectedMiddleware = middlewareOptionsPage.getSelectedMiddleware();
		final MiddlewareOption selectedMiddleware = MiddlewareOption.MQTT_I2C_CONFIG;
		
		// T3.6 and T3.7: Container Code Generation
		//final String muml_containerFilePath = destinationFolderURIString + "/MUML_Container.muml_container";
		final IFile muml_containerFile = selectedResource.getProject().getFile("container-models" + "/MUML_Container.muml_container");
		final URI muml_containerURI = URI.createPlatformResourceURI(muml_containerFile.getFullPath().toString(), true);
		final IContainer arduinoCodeDestination = selectedResource.getProject().getFolder("arduino-containers");
		Path arduinoCodeDestinationPath = Paths.get( arduinoCodeDestination.getRawLocation().toString() );
		if(! Files.isDirectory(arduinoCodeDestinationPath) ){
			try {
				Files.createDirectory(arduinoCodeDestinationPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		// T3.3: Component Code Generation
		final EObject[] sourceElementsComponentInstance = sourcePageComponentInstanceConfiguration.getSourceElements();
		//final URI componentCodeDestinationURI = URI.createURI(destinationFolderURIString , true);
		//final IContainer target = (IContainer) ResourcesPlugin.getWorkspace().getRoot().findMember(targetURI.toPlatformString(true));
		final IContainer componentCodeDestinationIContainer = selectedResource.getProject().getFolder("arduino-containers");

		final ITargetPlatformGenerator targetPlatform = new C99SourceCodeExport();
		
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
		
		
		return new AbstractFujabaExportOperation() {
			@Override
			protected IStatus doExecute(IProgressMonitor progressMonitor) {

				final SystemAllocation systemAllocation = (SystemAllocation) sourceElementsSystemAllocation[0];
				AdapterFactoryEditingDomain.getEditingDomainFor(systemAllocation);

				Job containerJob = new ContainerGenerationJob(systemAllocation, destinationURIContainerGeneration, editingDomain, selectedMiddleware);
				containerJob.schedule();

				// T3.2: Deployment Configuration aka Container Transformation
				try {
					containerJob.join();
					URI newContinerFile = ((ContainerGenerationJob) containerJob).getCreatedFile();
					ResourceSet resSet = new ResourceSetImpl();
					Resource res = resSet.getResource(newContinerFile, true);

					final DeploymentConfiguration systemConfig = (DeploymentConfiguration) res.getContents().get(0);

					if (selectedMiddleware == MiddlewareOption.DDS_CONFIG){ // Only call the ddsJob if DDS was selected
						EditingDomain editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(systemConfig);
						if (editingDomain == null) {
							editingDomain = WorkspaceEditingDomainFactory.INSTANCE.createEditingDomain();
						}
						//Job ddsJob = new DDSContainerGenerationJob(systemConfig, destinationURIContainerGeneration, editingDomain);
						//ddsJob.schedule();
					}
					selectedFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, progressMonitor);
				} catch (InterruptedException | CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 

				// Handling of existing old MUML_Container.muml_container via deletion of the old and moving/renaming of the new one.
				Path muml_containerPath = Paths.get(muml_containerFile.getRawLocation().toString());
				final IFile muml_container2File = selectedResource.getProject().getFile("container-models" + "/MUML_Container2.muml_container");
				Path muml_container2Path = Paths.get(muml_container2File.getRawLocation().toString());
				if(Files.exists(muml_container2Path)){
					System.out.println("Replacing old MUML_Container.muml_container");
					try {
						Files.delete(muml_containerPath);
						Files.move(muml_container2Path, muml_containerPath);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				// T3.6 and T3.7: Container Code Generation
				
				try {
					//GenerateAllHelper.doGenerateAll(modelURI, arduinoCodeDestination, progressMonitor);
					GenerateAll generator = new GenerateAll(muml_containerURI, arduinoCodeDestination, ((List<? extends Object>) new ArrayList<String>()) );
					generator.doGenerate(progressMonitor);
					
				} catch (IOException e) {
					IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
					Activator.getDefault().getLog().log(status);
				} finally {
					try {
						muml_containerFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, progressMonitor);
					} catch (CoreException e) {
						IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
						Activator.getDefault().getLog().log(status);
					}
				}
				
				
				// T3.3: Component Code Generation
				try {
					for (EObject element : sourceElementsComponentInstance){
						targetPlatform.generateSourceCode(element, componentCodeDestinationIContainer, progressMonitor);
					}
				} catch (Exception e) {
					IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
					Activator.getDefault().getLog().log(status);
				} finally {
					try {
						componentCodeDestinationIContainer.getProject().refreshLocal(IResource.DEPTH_INFINITE, progressMonitor);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}

				// Post-Processing:
				
				// for all header files in "fastAndSlowCar_v2/components":
				// https://stackoverflow.com/questions/23088286/reading-filenames-from-a-folder-in-a-project-in-eclipse				
				final IFolder fastAndSlowCar_v2_components = selectedResource.getProject().getFolder("arduino-containers/fastAndSlowCar_v2/components");
				fastAndSlowCar_v2_components.getLocation();

				// 
				
				return Status.OK_STATUS;
			}
		};
	}

}
