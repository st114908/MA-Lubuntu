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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.muml.arduino.adapter.container.ui.common.GenerateAll;
import org.muml.codegen.componenttype.export.ui.Activator;

class ContainerCodeGenerationHandler{
	private final IFile muml_containerFile;
	private final IFolder destinationFolder;
	
	public ContainerCodeGenerationHandler(IFile muml_containerFile, IFolder destinationFolder){
		this.muml_containerFile = muml_containerFile;
		this.destinationFolder = destinationFolder;
	}
	
	public void performContainerCodeGeneration(IProgressMonitor progressMonitor){
		final URI muml_containerURI = URI.createPlatformResourceURI(muml_containerFile.getFullPath().toString(), true);
		final IContainer arduinoCodeDestination = destinationFolder;
		final Path arduinoCodeDestinationPath = Paths.get( arduinoCodeDestination.getRawLocation().toString() );
		if(! Files.isDirectory(arduinoCodeDestinationPath) ){
			try {
				Files.createDirectory(arduinoCodeDestinationPath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
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
		
	}
}
