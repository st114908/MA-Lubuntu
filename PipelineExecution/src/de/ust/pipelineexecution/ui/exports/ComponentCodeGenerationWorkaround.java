package de.ust.pipelineexecution.ui.exports;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;

import org.muml.c.adapter.componenttype.ui.export.C99SourceCodeExport;
import org.muml.codegen.componenttype.export.ui.Activator;
import org.muml.codegen.componenttype.export.ui.ITargetPlatformGenerator;

import de.ust.mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.InOrOutKeyNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StructureException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ComponentCodeGeneration;

import projectfolderpathstorageplugin.ProjectFolderPathStorage;

class ComponentCodeGenerationWorkaround {
	private ComponentCodeGeneration step;

	public ComponentCodeGenerationWorkaround(ComponentCodeGeneration step) {
		this.step = step;
	}

	public void performComponentCodeGeneration(EObject[] sourceElementsComponentInstance, IProgressMonitor progressMonitor)
			throws VariableNotDefinedException, StructureException, InOrOutKeyNotDefinedException, FaultyDataException {
		step.setContentOfOutput("ifSuccessful", false);
		
		IProject targetProject = ProjectFolderPathStorage.project;
		
		Path arduinoContainersDestinationFolder = step
				.getResolvedPathContentOfInput("arduinoContainersDestinationFolder");
		Path subPathArduinoContainersDestinationFolder = ProjectFolderPathStorage.projectFolderPath
				.relativize(arduinoContainersDestinationFolder);

		if(! Files.isDirectory(arduinoContainersDestinationFolder) ){
			try {
				Files.createDirectories(arduinoContainersDestinationFolder);
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
		
		IFolder destinationFolder = targetProject.getFolder(subPathArduinoContainersDestinationFolder.toString());
		final IContainer componentCodeDestinationIContainer = destinationFolder;
		
		// Hard coded due to a lack of selectable alternatives. (To me only the
		// C source code option appeared.)
		final ITargetPlatformGenerator targetPlatform = new C99SourceCodeExport();

		try {
			for (EObject element : sourceElementsComponentInstance) {
				targetPlatform.generateSourceCode(element, componentCodeDestinationIContainer, progressMonitor);
			}
		} catch (Exception e) {
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
			Activator.getDefault().getLog().log(status);
		}

		step.setContentOfOutput("ifSuccessful", true);
	}
}
