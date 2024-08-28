package de.ust.pipelineexecution.ui.exports;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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

import org.muml.codegen.componenttype.export.ui.Activator;
import org.muml.container.transformation.job.ContainerGenerationJob;
import org.muml.container.transformation.job.MiddlewareOption;
import org.muml.psm.allocation.SystemAllocation;
import org.muml.psm.muml_container.DeploymentConfiguration;

import de.ust.mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.InOrOutKeyNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StructureException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ContainerTransformation;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

class ContainerTransformationWorkaround{
	private ContainerTransformation step;
	
	public ContainerTransformationWorkaround(ContainerTransformation step)
			throws VariableNotDefinedException, StructureException{
		this.step = step;
	}
	
	public void performContainerTransformation(EObject[] sourceElementsSystemAllocation, IProgressMonitor progressMonitor, EditingDomain editingDomain)
			throws VariableNotDefinedException, StructureException, InOrOutKeyNotDefinedException, FaultyDataException{
		step.setContentOfOutput("ifSuccessful", false);

		IProject targetProject = ProjectFolderPathStorage.project;
		
		final SystemAllocation systemAllocation = (SystemAllocation) sourceElementsSystemAllocation[0];
		AdapterFactoryEditingDomain.getEditingDomainFor(systemAllocation);

		Path destinationContainerTransformationPath = step.getResolvedPathContentOfInput("muml_containerFileDestination").getParent();

		if(! Files.isDirectory(destinationContainerTransformationPath) ){
			try {
				Files.createDirectories(destinationContainerTransformationPath);
			} catch (IOException e) {
				IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
				Activator.getDefault().getLog().log(status);
			}
		}
		
		// Necessary, because else there would be 
		// org.eclipse.emf.ecore.resource.Resource$IOWrappedException: A resource already exists on disk
		// error if the folder got deleted in a previous step. Eclipse has misleading error messages in such cases.
		try {
			targetProject.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		} catch (CoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Path referenceFolderForURIPathGeneration = ProjectFolderPathStorage.projectFolderPath.getParent();
		Path subPathForPlatformRessourseURI = referenceFolderForURIPathGeneration.relativize(destinationContainerTransformationPath);
		//final URI destinationContainerTransformationURI = URI.createPlatformResourceURI(subPathForPlatformRessourseURI.toString(), true);
		final URI destinationContainerTransformationURI = URI.createPlatformResourceURI("/" + subPathForPlatformRessourseURI.toString(), true);
		
		MiddlewareOption selectedMiddleware;
		if(step.getContentOfInput("middlewareOption").getContent().equals("MQTT_I2C_CONFIG")){
			selectedMiddleware = MiddlewareOption.MQTT_I2C_CONFIG;
		}
		else{
			selectedMiddleware = MiddlewareOption.DDS_CONFIG;
		}
		
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
	
		System.out.println("Container transformation done.");
		
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
		step.setContentOfOutput("ifSuccessful", true);
	}
}

