package de.ust.pipelineexecution.ui.exports;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.muml.arduino.adapter.container.ui.common.GenerateAll;
import org.muml.codegen.componenttype.export.ui.Activator;

import de.ust.mumlacgppa.pipeline.parts.exceptions.InOrOutKeyNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StructureException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ContainerCodeGeneration;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

class ContainerCodeGenerationImprovisation{
	private ContainerCodeGeneration step;
	
	public ContainerCodeGenerationImprovisation(ContainerCodeGeneration step){
		this.step = step;
	}
	
	public void performContainerCodeGeneration(IProject targetProject, IProgressMonitor progressMonitor)
			throws VariableNotDefinedException, StructureException, InOrOutKeyNotDefinedException{
		step.setContentOfOutput("ifSuccessful", false);
		
		Path referenceFolderForURIPathGeneration = ProjectFolderPathStorage.projectFolderPath.getParent();
		Path sourceContainerFilePath = step.getResolvedPathContentOfInput("muml_containerSourceFile");
		Path subPathForSourceContainerPlatformRessourseURI = referenceFolderForURIPathGeneration.relativize(sourceContainerFilePath);
		final URI sourceContainerURI = URI.createPlatformResourceURI(subPathForSourceContainerPlatformRessourseURI.toString(), true);
		
		Path arduinoContainersDestinationFolderPath = step.getResolvedPathContentOfInput("arduinoContainersDestinationFolder");
		
		if(! Files.isDirectory(arduinoContainersDestinationFolderPath) ){
			try {
				Files.createDirectories(arduinoContainersDestinationFolderPath);
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
		
		Path subPathArduinoContainersDestinationFolder = ProjectFolderPathStorage.projectFolderPath.relativize(arduinoContainersDestinationFolderPath);
		IFolder destinationFolder = targetProject.getFolder(subPathArduinoContainersDestinationFolder.toString());
		final IContainer arduinoCodeDestination = destinationFolder;
		
		try {
			GenerateAll generator = new GenerateAll(sourceContainerURI, arduinoCodeDestination, ((List<? extends Object>) new ArrayList<String>()) );
			generator.doGenerate(progressMonitor);
		} catch (IOException e) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
			Activator.getDefault().getLog().log(status);
		} finally {
			try {
				targetProject.getProject().refreshLocal(IResource.DEPTH_INFINITE, progressMonitor);
			} catch (CoreException e) {
				IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
				Activator.getDefault().getLog().log(status);
			}
		}

		step.setContentOfOutput("ifSuccessful", true);
	}
}
