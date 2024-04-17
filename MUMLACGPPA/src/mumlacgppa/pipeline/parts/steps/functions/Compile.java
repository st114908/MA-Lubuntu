/**
 * 
 */
package mumlacgppa.pipeline.parts.steps.functions;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import arduinocliutilizer.worksteps.exceptions.NoArduinoCLIConfigFileException;
import arduinocliutilizer.worksteps.functions.CompilationCall;
import mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import mumlacgppa.pipeline.parts.exceptions.StructureException;
import mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import mumlacgppa.pipeline.parts.steps.PipelineStep;
import mumlacgppa.pipeline.parts.storage.VariableHandler;
import projectfolderpathstorageplugin.ProjectFolderPathNotSetException;

/**
 * @author muml
 *
 */
public class Compile extends PipelineStep {

	public static final String nameFlag = "Compile";

	
	public Compile(VariableHandler VariableHandlerInstance, Map<String, Map<String, String>> readData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, readData);
	}
	
	protected void setRequiredInsAndOuts(){
		requiredInsAndOuts = new LinkedHashMap<String, HashSet<String>>();

		HashSet<String> ins = new LinkedHashSet<String>();
		ins.add("boardTypeIdentifierFQBN");
		ins.add("targetInoFile");
    	ins.add("saveCompiledFilesNearby");
    	//ins.add("directoryContainingInoFile");
		requiredInsAndOuts.put(inFlag, ins);
		
		HashSet<String> outs = new LinkedHashSet<String>();
		outs.add("ifSuccessful");
    	outs.add("resultMessage");
		requiredInsAndOuts.put(outFlag, outs);
	}

	// Map<String, Map<String, String>> for
	// Map<InOrOut, Map<ParameterOrOneOutput, SourceOrSaveTarget>>
	public static Map<String, Map<String, String>> generateDefaultOrExampleValues(){
		Map<String, Map<String, String>> exampleSettings = new LinkedHashMap<String, Map<String,String>>();

		// Ins:
		Map<String, String> ins = new LinkedHashMap<String, String>();
		ins.put("boardTypeIdentifierFQBN", "direct arduino:avr:uno");
		ins.put("targetInoFile", "direct arduino-containers/fastCarDriverECU/fastCarDriverECU.ino");
		ins.put("saveCompiledFilesNearby", "direct true");
		exampleSettings.put(inFlag, ins);
		
		// Out:
		Map<String, String> outs = new LinkedHashMap<String, String>();
		outs.put("ifSuccessful", "ifSuccessful");
		outs.put("resultMessage", "resultMessage");
		exampleSettings.put(outFlag, outs);
		
		return exampleSettings;
	}
	

	/**
	 * @param yamlData
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 */
	public Compile(VariableHandler VariableHandlerInstance, String yamlData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA {
		super(VariableHandlerInstance, yamlData);
	}

	
	/* (non-Javadoc)
	 * @see mumlarduinopipelineautomatisation.pipeline.parts.steps.common.PipelineStep#execute()
	 */
	@Override
	public void execute() 
			throws IOException, InterruptedException, NoArduinoCLIConfigFileException, VariableNotDefinedException,
			StructureException, ProjectFolderPathNotSetException{
		handleOutputByKey("ifSuccessful", false); // For Exceptions 
		String foundFqbn = handleInputByKey("boardTypeIdentifierFQBN").getContent();
		Path targetINOFilePath = resolvePath( handleInputByKey("targetInoFile").getContent() );
		boolean saveCompiledFilesNearby = handleInputByKey("saveCompiledFilesNearby").getBooleanContent();
		CompilationCall CompilationStepInstance = new CompilationCall(targetINOFilePath, foundFqbn, saveCompiledFilesNearby);
		
		if(CompilationStepInstance.isSuccessful()){
			handleOutputByKey("ifSuccessful", true);
		}
		handleOutputByKey("resultMessage", CompilationStepInstance.generateResultMessage());
	}

}
