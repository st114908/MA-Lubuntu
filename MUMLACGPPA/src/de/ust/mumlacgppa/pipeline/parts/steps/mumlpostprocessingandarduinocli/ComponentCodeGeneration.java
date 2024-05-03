/**
 * 
 */
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

/**
 * @author muml
 *
 */
public class ComponentCodeGeneration extends PipelineStep {

	public static final String nameFlag = "ComponentCodeGeneration";

	/**
	 * @param readData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA
	 */
	public ComponentCodeGeneration(VariableHandler VariableHandlerInstance, Map<String, Map<String, String>> readData)
			throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, readData);
	}

	/**
	 * @param yamlData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA
	 */
	public ComponentCodeGeneration(VariableHandler VariableHandlerInstance, String yamlData)
			throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, yamlData);
	}

	/**
	 * @see de.ust.mumlacgppa.pipeline.parts.steps.PipelineStep#setRequiredInsAndOuts()
	 */
	@Override
	protected void setRequiredInsAndOuts() {
		requiredInsAndOuts = new LinkedHashMap<String, HashSet<String>>();

		HashSet<String> ins = new LinkedHashSet<String>();
		ins.add("roboCar_mumlSourceFile");
		ins.add("arduinoContainersDestinationFolder");
		requiredInsAndOuts.put(inKeyword, ins);

		HashSet<String> outs = new LinkedHashSet<String>();
		outs.add("ifSuccessful");
		requiredInsAndOuts.put(outKeyword, outs);
	}

	public static Map<String, Map<String, String>> generateDefaultOrExampleValues() {
		// Map<String, Map<String, String>> for
		// Map<InOrOut, Map<ParameterOrOneOutput, SourceOrSaveTarget>>
		Map<String, Map<String, String>> exampleSettings = new LinkedHashMap<String, Map<String, String>>();

		// Ins:
		Map<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("roboCar_mumlSourceFile", "direct model/roboCar.muml");
		ins.put("arduinoContainersDestinationFolder", "direct arduino-containers");
		exampleSettings.put(inKeyword, ins);

		// Out:
		Map<String, String> outs = new LinkedHashMap<String, String>();
		outs.put("ifSuccessful", "ifSuccessful");
		exampleSettings.put(outKeyword, outs);

		return exampleSettings;
	}

	/**
	 * @see mumlacga.pipeline.parts.steps.common.PipelineStep#execute()
	 */
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
