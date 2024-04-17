package mumlacgppa.pipeline.parts.steps.functions;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.eclipse.emf.common.util.URI;

import arduinocliutilizer.worksteps.exceptions.FQBNErrorEception;
import arduinocliutilizer.worksteps.exceptions.NoArduinoCLIConfigFileException;
import mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import mumlacgppa.pipeline.parts.exceptions.ParameterMismatchException;
import mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import mumlacgppa.pipeline.parts.exceptions.StructureException;
import mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import mumlacgppa.pipeline.parts.steps.PipelineStep;
import mumlacgppa.pipeline.parts.storage.VariableHandler;

public class ContainerCodeGeneration extends PipelineStep{

	public static final String nameFlag = "ContainerCodeGeneration";
	
	public ContainerCodeGeneration(VariableHandler VariableHandlerInstance, Map<String, Map<String, String>> readData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, readData);
	}

	public ContainerCodeGeneration(VariableHandler VariableHandlerInstance, String yamlData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, yamlData);
	}

	@Override
	protected void setRequiredInsAndOuts() {
		requiredInsAndOuts = new LinkedHashMap<String, HashSet<String>>();

		HashSet<String> ins = new LinkedHashSet<String>();
		// Source/ MUML domain element:
		// platform:/resource/[Project]/model/roboCar.muml
		// There "root node"/"Model Element Category manual_allocation"/"System Allocation"
		
		ins.add("muml_containerSourceFile");
		ins.add("arduino_containersDestinationFolder");
    	// As per tutorial: platform:/resource/[Project]/container-models,
		// so only the folder within the project will set and
		// internally platform:/resource/[Project]/ will be appended in front of it. 
		
		// The Overwrite option doesn't work. 
		
		requiredInsAndOuts.put(inFlag, ins);
		
		HashSet<String> outs = new LinkedHashSet<String>();
		outs.add("ifSuccessful");
		requiredInsAndOuts.put(outFlag, outs);
	}

	// Map<String, Map<String, String>> for
	// Map<InOrOut, Map<ParameterOrOneOutput, SourceOrSaveTarget>>
	public static Map<String, Map<String, String>> generateDefaultOrExampleValues(){
		Map<String, Map<String, String>> exampleSettings = new LinkedHashMap<String, Map<String,String>>();
		
		// Ins:
		Map<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("muml_containerSourceFile", "direct container-models/MUML_Container.muml_container");
		ins.put("arduino_containersDestinationFolder", "direct arduino-containers");
		exampleSettings.put(inFlag, ins);
		
		// Out:
		Map<String, String> outs = new LinkedHashMap<String, String>();
		outs.put("ifSuccessful", "ifSuccessful");
		exampleSettings.put(outFlag, outs);
		
		return exampleSettings;
	}
	
	
	@Override
	public void execute()
			throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException,
			IOException, InterruptedException, NoArduinoCLIConfigFileException, FQBNErrorEception {
		// Currently done by export structures in the project PipelineExecution.  
		// Its presence in the pipeline gets detected.
	}

}
