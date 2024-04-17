package mumlacgppa.tests;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import mumlacgppa.pipeline.parts.exceptions.StructureException;
import mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import mumlacgppa.pipeline.parts.steps.PipelineStep;
import mumlacgppa.pipeline.parts.storage.VariableContent;
import mumlacgppa.pipeline.parts.storage.VariableHandler;

class InternalTestDummyStep extends PipelineStep {


	public static final String pipelineKeyword = "InternalTestDummyStep";
	

	protected void setRequiredInsAndOuts(){
		requiredInsAndOuts = new HashMap<String, HashSet<String>>();

		HashSet<String> ins = new HashSet<String>();
		ins.add("input1");
		ins.add("input2");
		requiredInsAndOuts.put(inFlag, ins);
		
		HashSet<String> outs = new HashSet<String>();
		outs.add("output1");
		outs.add("output2");
		requiredInsAndOuts.put(outFlag, outs);
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
	@SuppressWarnings("unchecked")
	public InternalTestDummyStep(VariableHandler VariableHandlerInstance, String yamlData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, yamlData);
	}
	
	
	@Override
	public void execute(){
		//throw new Exception("This test dummy is not supposed to be used outside of unit tests!");
	}

	
	public VariableContent handleInputTestFunction(String key) throws VariableNotDefinedException, StructureException{
		if(!in.containsKey(key)){
			throw new VariableNotDefinedException("Error: "+ key + " is unknown to the called step and should not be able to access it!");
		}
		return handleInputByKey(key);
	}
	
	
	public void handleOutputTestFunction(String key, VariableContent newValue) throws VariableNotDefinedException{
		if(!out.containsKey(key)){
			throw new VariableNotDefinedException("Error: "+ key + "is unknown to the called step and should not be able to access it!");
		}
		handleOutputByKey(key, newValue);
	}
}
