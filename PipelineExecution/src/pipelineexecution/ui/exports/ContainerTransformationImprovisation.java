package pipelineexecution.ui.exports;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

import org.muml.codegen.componenttype.export.ui.Activator;
import org.muml.container.transformation.job.ContainerGenerationJob;
import org.muml.container.transformation.job.MiddlewareOption;
import org.muml.psm.allocation.SystemAllocation;
import org.muml.psm.muml_container.DeploymentConfiguration;

import projectfolderpathstorageplugin.ProjectFolderPathStorage;

import mumlacgppa.pipeline.parts.exceptions.StructureException;
import mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import mumlacgppa.pipeline.parts.steps.functions.ContainerTransformation;

class ContainerTransformationImprovisation{
	private EObject[] sourceElementsSystemAllocation;
	private ContainerTransformation step;
	
	public ContainerTransformationImprovisation(EObject[] sourceElementsSystemAllocation, ContainerTransformation step)
			throws VariableNotDefinedException, StructureException{
		// Because I didn't manage to get the resource selection working:
		this.sourceElementsSystemAllocation = sourceElementsSystemAllocation;
		this.step = step;
	}
	
	public void performContainerTransformation(IProgressMonitor progressMonitor, EditingDomain editingDomain)
			throws VariableNotDefinedException, StructureException{
		Path referenceFolderForURIPathGeneration = ProjectFolderPathStorage.projectFolderPath.getParent();
		
		Path muml_containerFolderPath = step.getResolvedPathContentOfInput("muml_containerFileDestination").getParent();
		Path subPathForPlatformRessourseURI = referenceFolderForURIPathGeneration.relativize(muml_containerFolderPath);
		
		final URI destinationContainerTransformationURI = URI.createPlatformResourceURI(subPathForPlatformRessourseURI.toString(), true);
		Path destinationContainerTransformationPath = Paths.get( muml_containerFolderPath.toString() );

		if(! Files.isDirectory(destinationContainerTransformationPath) ){
			try {
				Files.createDirectory(destinationContainerTransformationPath);
			} catch (IOException e) {
				IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
				Activator.getDefault().getLog().log(status);
			}
		}

		MiddlewareOption selectedMiddleware;
		if(step.getContentOfInput("middlewareOption").getContent().equals("MQTT_I2C_CONFIG")){
			selectedMiddleware = MiddlewareOption.MQTT_I2C_CONFIG;
		}
		else{
			selectedMiddleware = MiddlewareOption.DDS_CONFIG;
		}
		
		
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
		} catch (InterruptedException e) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
			Activator.getDefault().getLog().log(status);
		} 
	
		// Handling of existing old MUML_Container.muml_container via deletion of the old and moving/renaming of the new one.
		Path muml_containerPath = destinationContainerTransformationPath.resolve("MUML_Container.muml_container");
		Path muml_container2Path = destinationContainerTransformationPath.resolve("MUML_Container2.muml_container");
		if(Files.exists(muml_container2Path)){
			try {
				Files.delete(muml_containerPath);
				Files.move(muml_container2Path, muml_containerPath);
			} catch (IOException e) {
				IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
				Activator.getDefault().getLog().log(status);
			}
		}
	}
}

