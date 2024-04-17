package pipelineexecution.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.workspace.WorkspaceEditingDomainFactory;
import org.muml.container.transformation.job.ContainerGenerationJob;
import org.muml.container.transformation.job.MiddlewareOption;
import org.muml.psm.allocation.SystemAllocation;
import org.muml.psm.muml_container.DeploymentConfiguration;

class ContainerTransformationHandler{
	private IResource selectedResource;
	private EObject[] sourceElementsSystemAllocation;
	private final URI destinationContainerTransformationURI;
	private Path destinationContainerTransformationPath;
	final MiddlewareOption selectedMiddleware;
	
	public ContainerTransformationHandler(IResource selectedResource, EObject[] sourceElementsSystemAllocation, IFolder destinationFolder,
			final MiddlewareOption selectedMiddleware){
		this.selectedResource = selectedResource;
		this.sourceElementsSystemAllocation = sourceElementsSystemAllocation;
		destinationContainerTransformationURI = URI.createPlatformResourceURI( destinationFolder.getFullPath().toString() , true);
		destinationContainerTransformationPath = Paths.get( destinationFolder.getRawLocation().toString() );
		
		if(! Files.isDirectory(destinationContainerTransformationPath) ){
			try {
				Files.createDirectory(destinationContainerTransformationPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.selectedMiddleware = selectedMiddleware;
	}
	
	public void performContainerTransformation(IProgressMonitor progressMonitor, EditingDomain editingDomain){
		final SystemAllocation systemAllocation = (SystemAllocation) sourceElementsSystemAllocation[0];
		AdapterFactoryEditingDomain.getEditingDomainFor(systemAllocation);

		Job containerJob = new ContainerGenerationJob(systemAllocation, destinationContainerTransformationURI, editingDomain, selectedMiddleware);
		containerJob.schedule();

		try {
			containerJob.join();
			URI newContinerFile = ((ContainerGenerationJob) containerJob).getCreatedFile();
			ResourceSet resSet = new ResourceSetImpl();
			Resource res = resSet.getResource(newContinerFile, true);

			final DeploymentConfiguration systemConfig = (DeploymentConfiguration) res.getContents().get(0);

			if (selectedMiddleware == MiddlewareOption.DDS_CONFIG){ // Only call the ddsJob if DDS was selected
				EditingDomain editingDomain2 = AdapterFactoryEditingDomain.getEditingDomainFor(systemConfig);
				if (editingDomain2 == null) {
					editingDomain2 = WorkspaceEditingDomainFactory.INSTANCE.createEditingDomain();
				}
				//Job ddsJob = new DDSContainerGenerationJob(systemConfig, destinationURIContainerGeneration, editingDomain);
				//ddsJob.schedule();
			}
			//selectedFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, progressMonitor);
			selectedResource.getProject().refreshLocal(IResource.DEPTH_INFINITE, progressMonitor);
		} catch (InterruptedException | CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	
		// Handling of existing old MUML_Container.muml_container via deletion of the old and moving/renaming of the new one.
		Path muml_containerPath = destinationContainerTransformationPath.resolve("MUML_Container.muml_container");
		Path muml_container2Path = destinationContainerTransformationPath.resolve("MUML_Container2.muml_container");
		if(Files.exists(muml_container2Path)){
			try {
				Files.delete(muml_containerPath);
				Files.move(muml_container2Path, muml_containerPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
