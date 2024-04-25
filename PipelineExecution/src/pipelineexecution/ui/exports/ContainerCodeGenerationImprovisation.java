package pipelineexecution.ui.exports;

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
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.muml.arduino.adapter.container.ui.common.GenerateAll;
import org.muml.codegen.componenttype.export.ui.Activator;

import mumlacgppa.pipeline.parts.exceptions.StructureException;
import mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ContainerCodeGeneration;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

class ContainerCodeGenerationImprovisation{
	private ContainerCodeGeneration step;
	
	public ContainerCodeGenerationImprovisation(ContainerCodeGeneration step){
		this.step = step;
	}
	
	public void performContainerCodeGeneration(IProject targetProject, IFile muml_containerFile, IProgressMonitor progressMonitor) throws VariableNotDefinedException, StructureException{
		Path referenceFolderForURIPathGeneration = ProjectFolderPathStorage.projectFolderPath.getParent();
		Path sourceContainerFilePath = step.getResolvedPathContentOfInput("muml_containerSourceFile");
		Path subPathForSourceContainerPlatformRessourseURI = referenceFolderForURIPathGeneration.relativize(sourceContainerFilePath);
		final URI sourceContainerURI = URI.createPlatformResourceURI(subPathForSourceContainerPlatformRessourseURI.toString(), true);
		
		Path arduinoContainersDestinationFolder = step.getResolvedPathContentOfInput("arduino_containersDestinationFolder");
		Path subPathArduinoContainersDestinationFolder = ProjectFolderPathStorage.projectFolderPath.relativize(arduinoContainersDestinationFolder);
		IFolder destinationFolder = targetProject.getFolder(subPathArduinoContainersDestinationFolder.toString());
		final IContainer arduinoCodeDestination = destinationFolder;
		final Path arduinoCodeDestinationPath = Paths.get( arduinoCodeDestination.getRawLocation().toString() );
		
		if(! Files.isDirectory(arduinoCodeDestinationPath) ){
			try {
				Files.createDirectories(arduinoCodeDestinationPath);
			} catch (IOException e) {
				IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
				Activator.getDefault().getLog().log(status);
			}
		}
		
		try {
			GenerateAll generator = new GenerateAll(sourceContainerURI, arduinoCodeDestination, ((List<? extends Object>) new ArrayList<String>()) );
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
		
	}
}
