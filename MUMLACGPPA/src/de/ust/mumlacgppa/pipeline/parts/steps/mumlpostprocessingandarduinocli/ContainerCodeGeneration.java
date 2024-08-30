package de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;

import org.muml.arduino.adapter.container.ui.common.GenerateAll;

import de.ust.arduinocliutilizer.worksteps.exceptions.FQBNErrorEception;
import de.ust.arduinocliutilizer.worksteps.exceptions.NoArduinoCLIConfigFileException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.InOrOutKeyNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ParameterMismatchException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StructureException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.steps.PipelineStep;
import de.ust.mumlacgppa.pipeline.parts.storage.VariableHandler;
import de.ust.mumlacgppa.pipeline.parts.storage.VariableTypes;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;



public class ContainerCodeGeneration extends PipelineStep implements VariableTypes {

	public static final String nameFlag = "ContainerCodeGeneration";
	
	public ContainerCodeGeneration(VariableHandler VariableHandlerInstance, Map<String, Map<String, String>> readData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, readData);
	}

	public ContainerCodeGeneration(VariableHandler VariableHandlerInstance, String yamlData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, yamlData);
	}


	/**
	 * @see de.ust.mumlacgppa.pipeline.parts.steps.PipelineStep#getRequiredInsAndOuts()
	 */
	@Override
	protected Map<String, Map<String, String>> getRequiredInsAndOuts() {
		LinkedHashMap<String, Map<String, String>> requiredInsAndOuts = new LinkedHashMap<String, Map<String, String>>();

		LinkedHashMap<String, String> ins = new LinkedHashMap<String, String>();
		
		// Source/ MUML domain element:
		// platform:/resource/[Project]/model/roboCar.muml
		// There "root node"/"Model Element Category manual_allocation"/"System Allocation"
		ins.put("muml_containerSourceFile", FilePathType);
		
    	// As per tutorial: platform:/resource/[Project]/container-models,
		// so only the folder within the project will set and
		// internally platform:/resource/[Project]/ will be appended in front of it. 
		ins.put("arduinoContainersDestinationFolder", FolderPathType);
		
		// The Overwrite option shown by the export wizard doesn't work. 
		
		requiredInsAndOuts.put(inKeyword, ins);
		
		LinkedHashMap<String, String> outs = new LinkedHashMap<String, String>();
		outs.put("ifSuccessful", BooleanType);
		requiredInsAndOuts.put(outKeyword, outs);
		
		return requiredInsAndOuts;
	}

	public static Map<String, Map<String, String>> generateDefaultOrExampleValues(){
		// Map<String, Map<String, String>> for
		// Map<InOrOut, Map<ParameterOrOneOutput, SourceOrSaveTarget>>
		Map<String, Map<String, String>> exampleSettings = new LinkedHashMap<String, Map<String,String>>();
		
		// Ins:
		Map<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("muml_containerSourceFile", "direct container-models/MUML_Container.muml_container");
		ins.put("arduinoContainersDestinationFolder", "direct arduino-containers");
		exampleSettings.put(inKeyword, ins);
		
		// Out:
		Map<String, String> outs = new LinkedHashMap<String, String>();
		outs.put("ifSuccessful", "ifSuccessful");
		exampleSettings.put(outKeyword, outs);
		
		return exampleSettings;
	}
	
	
	@Override
	public void execute()
			throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException,
			IOException, InterruptedException, NoArduinoCLIConfigFileException, FQBNErrorEception, InOrOutKeyNotDefinedException  {
		handleOutputByKey("ifSuccessful", false);
		
		IProject targetProject = ProjectFolderPathStorage.project;
		
		Path referenceFolderForURIPathGeneration = ProjectFolderPathStorage.projectFolderPath.getParent();
		Path sourceContainerFilePath = resolveFullOrLocalPath( handleInputByKey("muml_containerSourceFile").getContent() );
		Path subPathForSourceContainerPlatformRessourseURI = referenceFolderForURIPathGeneration.relativize(sourceContainerFilePath);
		final URI sourceContainerURI = URI.createPlatformResourceURI(subPathForSourceContainerPlatformRessourseURI.toString(), true);
		
		Path arduinoContainersDestinationFolderPath = resolveFullOrLocalPath( handleInputByKey("arduinoContainersDestinationFolder").getContent() );
		
		if(! Files.isDirectory(arduinoContainersDestinationFolderPath) ){
			throw new IOException("ContainerCodeGeneration: The destination folder path " + arduinoContainersDestinationFolderPath.toString() + " doesn't exist!");
		}
		
		Path subPathArduinoContainersDestinationFolder = ProjectFolderPathStorage.projectFolderPath.relativize(arduinoContainersDestinationFolderPath);
		IFolder destinationFolder = targetProject.getFolder(subPathArduinoContainersDestinationFolder.toString());
		final IContainer arduinoCodeDestination = destinationFolder;
		
		GenerateAll generator = new GenerateAll(sourceContainerURI, arduinoCodeDestination, ((List<? extends Object>) new ArrayList<String>()) );
		//generator.doGenerate(progressMonitor);
		generator.doGenerate(new NullProgressMonitor());

		handleOutputByKey("ifSuccessful", true);
	}

}
