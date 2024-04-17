package mumlacgppa.pipeline.parts.steps;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.yaml.snakeyaml.Yaml;

import arduinocliutilizer.worksteps.exceptions.FQBNErrorEception;
import arduinocliutilizer.worksteps.exceptions.NoArduinoCLIConfigFileException;
import mumlacgppa.pipeline.parts.exceptions.AbortPipelineException;
import mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import mumlacgppa.pipeline.parts.exceptions.ParameterMismatchException;
import mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import mumlacgppa.pipeline.parts.exceptions.StructureException;
import mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import mumlacgppa.pipeline.parts.storage.VariableContent;
import mumlacgppa.pipeline.parts.storage.VariableHandler;
import mumlacgppa.pipeline.settings.PipelineSettingsReader;
import projectfolderpathstorageplugin.ProjectFolderPathNotSetException;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

public abstract class PipelineStep implements Keywords{
	VariableHandler VariableHandlerInstance;
	// The ins and outs are initially stored raw as strings since the variable content is only available at runtime. 
	protected Map<String, String> in; // <Name>: [not] {from, direct} <VariableNameOrValue>
	protected Map<String, String> out; // <Name>: <VariableName>
	
	protected Map<String, HashSet<String>> requiredInsAndOuts;
	
	
	protected abstract void setRequiredInsAndOuts();

	public static Map<String, Map<String, String>> generateDefaultOrExampleValues(){
		return new LinkedHashMap<String, Map<String, String>>();
	};
	
	/**
	 * @param in
	 * @param out
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 * @throws PipelineSettingsReaderHasNoProjectLocationException 
	 */
	public PipelineStep(VariableHandler VariableHandlerInstance, Map<String, Map<String, String>> readData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA{
		if(ProjectFolderPathStorage.projectFolderPath == null){
			throw new ProjectFolderPathNotSetExceptionMUMLACGPPA(
					"The static field projectFolderPath in ProjectFolderPathStorage has "
					+ "to be set to a complete file system path to the project's folder!"
							);
		}
		
		this.VariableHandlerInstance = VariableHandlerInstance;
		
		in = new LinkedHashMap<String, String>();
		out = new LinkedHashMap<String, String>();
		
		if(readData.containsKey(inFlag)){
			Map<String, String> inData = (Map<String, String>) readData.get(inFlag); 
			for(String currentKey: inData.keySet()){
				in.put(currentKey, inData.get(currentKey));
			}
		}
		
		if(readData.containsKey(outFlag)){
			Map<String, String> outData = (Map<String, String>) readData.get(outFlag); 
			for(String currentKey: outData.keySet()){
				out.put(currentKey, outData.get(currentKey));
			}
		}

		setRequiredInsAndOuts();
	}
	
	/**
	 * @param in
	 * @param out
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 * @throws PipelineSettingsReaderHasNoProjectLocationException 
	 */
	@SuppressWarnings("unchecked")
	public PipelineStep(VariableHandler VariableHandlerInstance, String yamlData) throws ProjectFolderPathNotSetExceptionMUMLACGPPA{
		// Not allowed, because for the this(...) constructor call has to be in the first executed line.
		//Yaml yaml = new Yaml();
		//Map<String, Map<String, String>> readData = (Map<String, Map<String, String>>) yaml.load(yamlData);
		//this(readData);
		
		this(VariableHandlerInstance, (Map<String, Map<String, String>>) new Yaml().load(yamlData));
	}
	
	
	public Map<String, String> getIn() {
		return in;
	}


	public Map<String, String> getOut() {
		return out;
	}


	public void setIn(Map<String, String> in) {
		this.in = in;
	}


	public void setOut(Map<String, String> out) {
		this.out = out;
	}
	

	protected VariableContent handleInputEntry(String entry) throws VariableNotDefinedException, StructureException{
		if(entry.startsWith(directValueFlag)){
			String writtenInValue = entry.substring(directValueFlag.length()).trim();
			VariableContent directlyInsertedValue = new VariableContent(writtenInValue);
			return directlyInsertedValue;
		}
		else if(entry.startsWith(fromValueFlag)){
			String referencedVariable = entry.substring(fromValueFlag.length()).trim();
			return VariableHandlerInstance.getVariableValue(referencedVariable);
		}
		else if(entry.startsWith(notValueFlag)){
			String afterNot = entry.substring(notValueFlag.length()).trim();
			VariableContent gainedContent = handleInputEntry(afterNot);
			boolean received = gainedContent.getBooleanContent();
			VariableContent invertedBooleanContent = new VariableContent( Boolean.toString(!received) );
			return invertedBooleanContent;
		}
		else{
			throw new StructureException("Structure error or unexpected element " + entry);
		}
	}
	
	
	protected VariableContent handleInputByKey(String key) throws VariableNotDefinedException, StructureException{
		return handleInputEntry(in.get(key));
	}
	
	
	protected void handleOutputByKey(String keyToTargetVariableName, VariableContent newValue){
		VariableHandlerInstance.setVariableValue(out.get(keyToTargetVariableName), newValue);
	}
	

	protected void handleOutputByKey(String keyToTargetVariableName, String newValue){
		VariableHandlerInstance.setVariableValue(out.get(keyToTargetVariableName), newValue);
	}
	

	protected void handleOutputByKey(String keyToTargetVariableName, int newValue){
		VariableHandlerInstance.setVariableValue(out.get(keyToTargetVariableName), newValue);
	}
	

	protected void handleOutputByKey(String keyToTargetVariableName, boolean newValue){
		VariableHandlerInstance.setVariableValue(out.get(keyToTargetVariableName), newValue);
	}
	
	
	private void validateEntryCheck(String entry) throws StructureException, VariableNotDefinedException, FaultyDataException{
		if(entry.startsWith(directValueFlag)){
			String afterDirect = entry.substring(directValueFlag.length()).trim();
			if(afterDirect.equals("")){
				throw new FaultyDataException("Value in direct entry missing or not recognized!");
			}
			// Currently nothing more to do.
		}
		else if(entry.startsWith(fromValueFlag)){
			String referencedVariable = entry.substring(fromValueFlag.length()).trim();
			if(VariableHandlerInstance.isVariableInitialized(referencedVariable)){
				String afterFrom = entry.substring(fromValueFlag.length()).trim();
				if(afterFrom.equals("")){
					throw new FaultyDataException("Reference in from entry missing or not recognized!");
				}
				// Currently nothing more to do.
			}
			else{
				VariableHandlerInstance.generateVariableNotDefinedException(referencedVariable);
			}
		}
		else if(entry.startsWith(notValueFlag)){
			String afterNot = entry.substring(notValueFlag.length()).trim();
			validateEntryCheck(afterNot);
		}
		else{
			throw new StructureException("Structure error or unexpected element " + entry);
		}

		for(String currentKey:out.keySet()){
			@SuppressWarnings("unused")
			String readAsTest = out.get(currentKey);
		}
	}
	
	public void validate() throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException{
		if(! (in.keySet()).equals(requiredInsAndOuts.get(inFlag)) ){
			throw new ParameterMismatchException("Input parameter mismatch! Expected "+
					requiredInsAndOuts.get(inFlag).toString() + ", got " + in.keySet().toString());
		}
		if(! (out.keySet()).equals(requiredInsAndOuts.get(outFlag)) ){
			throw new ParameterMismatchException("Output parameter mismatch! Expected "+
					requiredInsAndOuts.get(outFlag).toString() + ", got " + out.keySet().toString());
		}
		
		for(String currentKey:in.keySet()){
			String entry = in.get(currentKey);
			validateEntryCheck(entry);
		}
		for(String currentKey:out.keySet()){
			String entry = out.get(currentKey);
			VariableHandlerInstance.setVariableInitialized(entry);
		}
	}
	
	
	protected Path resolvePath(String givenPath){
		if(givenPath.startsWith("/")){
			return Paths.get(givenPath);
		}
		else{
			//String completePath = projectFolderPath + "/" + givenPath;
			Path completePath = ProjectFolderPathStorage.projectFolderPath.resolve(givenPath);
			return completePath;
		}
	}
	
	
	public abstract void execute() throws VariableNotDefinedException, StructureException, FaultyDataException,
		ParameterMismatchException, IOException, InterruptedException, NoArduinoCLIConfigFileException,
		FQBNErrorEception, ProjectFolderPathNotSetException, AbortPipelineException;
	//public abstract ArrayList<String> executeTestVariation();

	
	public VariableContent getContentOfInput(String key) throws VariableNotDefinedException, StructureException{
		return handleInputByKey(key);
	}
	
	
	@Override
	public String toString() {
		if( (out.size() > 0) && (in.size() > 0) ){
			return "PipelineStep [in=" + in + ", out=" + out + "]";
		}
		else if( (out.size() == 0) && (in.size() > 0) ){
			return "PipelineStep [in=" + in + "]";
		}
		else if( (out.size() > 0) && (in.size() == 0) ){
			return "PipelineStep [out=" + out + "]";
		}
		else{
			return "PipelineStep []";
		}
	}
}