package de.ust.pipelineexecution.ui.exports;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.muml.c.adapter.componenttype.ui.export.C99SourceCodeExport;
import org.muml.codegen.componenttype.export.ui.Activator;
import org.muml.codegen.componenttype.export.ui.ITargetPlatformGenerator;

import de.ust.mumlacgppa.pipeline.parts.exceptions.InOrOutKeyNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StructureException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ComponentCodeGeneration;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

class ComponentCodeGenerationImprovisation {
	private ComponentCodeGeneration step;

	public ComponentCodeGenerationImprovisation(ComponentCodeGeneration step) {
		this.step = step;
	}

	public void performComponentCodeGeneration(IProject targetProject, EObject[] sourceElementsComponentInstance,
			IProgressMonitor progressMonitor)
			throws VariableNotDefinedException, StructureException, InOrOutKeyNotDefinedException {
		step.setContentOfOutput("ifSuccessful", false);
		
		Path arduinoContainersDestinationFolder = step
				.getResolvedPathContentOfInput("arduino_containersDestinationFolder");
		Path subPathArduinoContainersDestinationFolder = ProjectFolderPathStorage.projectFolderPath
				.relativize(arduinoContainersDestinationFolder);
		IFolder destinationFolder = targetProject.getFolder(subPathArduinoContainersDestinationFolder.toString());
		final IContainer componentCodeDestinationIContainer = destinationFolder;

		if(! Files.isDirectory(arduinoContainersDestinationFolder) ){
			try {
				Files.createDirectories(arduinoContainersDestinationFolder);
			} catch (IOException e) {
				IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
				Activator.getDefault().getLog().log(status);
			}
		}
		
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
