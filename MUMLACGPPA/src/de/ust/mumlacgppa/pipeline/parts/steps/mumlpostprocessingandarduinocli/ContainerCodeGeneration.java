package de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import de.ust.arduinocliutilizer.worksteps.exceptions.FQBNErrorEception;
import de.ust.arduinocliutilizer.worksteps.exceptions.NoArduinoCLIConfigFileException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ParameterMismatchException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StructureException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.steps.PipelineStep;
import de.ust.mumlacgppa.pipeline.parts.storage.VariableHandler;
import de.ust.mumlacgppa.pipeline.parts.storage.VariableTypes;

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
			IOException, InterruptedException, NoArduinoCLIConfigFileException, FQBNErrorEception {
		/*
		 * Due to a lack of detailed knowledge about Eclipse plug ins and a lack
		 * of time this is currently done by a copied and adjusted call from
		 * PipeLineExecutionAsExport. Its presence in the pipeline simply gets
		 * detected by PipeLineExecutionAsExport, which adjusts its behaviour
		 * and calls in order to trigger the desired generation/transformation.
		 */
	}

}
