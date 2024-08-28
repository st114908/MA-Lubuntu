package de.ust.mumlacgppa.tests;

import java.util.HashMap;
import java.util.Map;

import de.ust.mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.InOrOutKeyNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StructureException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.steps.PipelineStep;
import de.ust.mumlacgppa.pipeline.parts.storage.VariableContent;
import de.ust.mumlacgppa.pipeline.parts.storage.VariableHandler;
import de.ust.mumlacgppa.pipeline.parts.storage.VariableTypes;

class InternalTestDummyStep extends PipelineStep implements VariableTypes{


	public static final String pipelineKeyword = "InternalTestDummyStep";
	

	protected Map<String, Map<String, String>> getRequiredInsAndOuts(){
		Map<String, Map<String, String>> requiredInsAndOuts = new HashMap<String, Map<String, String>>();

		HashMap<String, String> ins = new HashMap<String, String>();
		ins.put("input1", AnyType);
		ins.put("input2", AnyType);
		requiredInsAndOuts.put(inKeyword, ins);
		
		HashMap<String, String> outs = new HashMap<String, String>();
		outs.put("output1", StringType);
		outs.put("output2", StringType);
		requiredInsAndOuts.put(outKeyword, outs);
		
		return requiredInsAndOuts;
	}
	

	/**
	 * @param projectFolderPath
	 * @param readData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 */
	public InternalTestDummyStep(VariableHandler VariableHandlerInstance, Map<String, Map<String, String>> readData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, readData);
	}

	
	/**
	 * @param projectFolderPath
	 * @param yamlData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 */
	public InternalTestDummyStep(VariableHandler VariableHandlerInstance, String yamlData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, yamlData);
	}
	

	public Map<String, String> getIn() {
		return in;
	}


	public Map<String, String> getOut() {
		return out;
	}


	@Override
	public void execute(){
		//throw new Exception("This test dummy is not supposed to be used outside of unit tests!");
		System.out.println("This test dummy is not supposed to be used outside of unit tests!");
	}

	
	public VariableContent handleInputTestFunction(String key) throws VariableNotDefinedException, StructureException, InOrOutKeyNotDefinedException, FaultyDataException{
		if(!in.containsKey(key)){
			throw new VariableNotDefinedException("Error: "+ key + " is unknown to the called step and should not be able to access it!");
		}
		return handleInputByKey(key);
	}
	
	
	public void handleOutputTestFunction(String key, VariableContent newValue) throws VariableNotDefinedException, InOrOutKeyNotDefinedException{
		if(!out.containsKey(key)){
			throw new VariableNotDefinedException("Error: "+ key + "is unknown to the called step and should not be able to access it!");
		}
		handleOutputByKey(key, newValue);
	}
}
