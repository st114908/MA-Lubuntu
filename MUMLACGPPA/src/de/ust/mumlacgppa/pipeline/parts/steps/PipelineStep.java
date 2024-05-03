package de.ust.mumlacgppa.pipeline.parts.steps;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import de.ust.arduinocliutilizer.worksteps.exceptions.FQBNErrorEception;
import de.ust.arduinocliutilizer.worksteps.exceptions.NoArduinoCLIConfigFileException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.AbortPipelineException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.InOrOutKeyNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ParameterMismatchException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StructureException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.storage.VariableContent;
import de.ust.mumlacgppa.pipeline.parts.storage.VariableHandler;
import projectfolderpathstorageplugin.ProjectFolderPathNotSetException;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

public abstract class PipelineStep implements Keywords{
	protected VariableHandler VariableHandlerInstance;
	// The ins and outs are initially stored raw as strings since the variable content is only available at runtime. 
	protected Map<String, String> in; // <Name>: [not] {from, direct} <VariableNameOrValue>
	protected Map<String, String> out; // <Name>: <VariableName>
	
	protected Map<String, HashSet<String>> requiredInsAndOuts;
	
	
	/**
	 * To set the required parameters for the inputs (in) and the outputs (out).
	 * Required to set the field requiredInsAndOuts for checkForDetectableErrorsEntryCheck.
	 */
	protected abstract void setRequiredInsAndOuts();
	
	
	//public static abstract Map<String, Map<String, String>> generateDefaultOrExampleValues(); // public static abstract at once don't fit together.
	
	
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
		
		if(readData.containsKey(inKeyword)){
			Map<String, String> inData = (Map<String, String>) readData.get(inKeyword); 
			for(String currentKey: inData.keySet()){
				in.put(currentKey, inData.get(currentKey));
			}
		}
		
		if(readData.containsKey(outKeyword)){
			Map<String, String> outData = (Map<String, String>) readData.get(outKeyword); 
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
	
	
	private VariableContent resolveInputEntry(String entry) throws VariableNotDefinedException, StructureException{
		if(entry.startsWith(directValueKeyword)){
			String writtenInValue = entry.substring(directValueKeyword.length()).trim();
			VariableContent directlyInsertedValue = new VariableContent(writtenInValue);
			return directlyInsertedValue;
		}
		else if(entry.startsWith(fromKeyword)){
			String referencedVariable = entry.substring(fromKeyword.length()).trim();
			return VariableHandlerInstance.getVariableValue(referencedVariable);
		}
		else if(entry.startsWith(notKeyword)){
			String afterNot = entry.substring(notKeyword.length()).trim();
			VariableContent gainedContent = resolveInputEntry(afterNot);
			boolean received = gainedContent.getBooleanContent();
			VariableContent invertedBooleanContent = new VariableContent( Boolean.toString(!received) );
			return invertedBooleanContent;
		}
		else{
			throw new StructureException("Structure error or unexpected element " + entry);
		}
	}
	
	
	protected VariableContent handleInputByKey(String inputParameterKey) throws VariableNotDefinedException, StructureException, InOrOutKeyNotDefinedException{
		if(!in.containsKey(inputParameterKey)){
			throw new InOrOutKeyNotDefinedException("Input parameter key " + inputParameterKey + " can't get matched to the defined input parameter keys.");
		}
		return resolveInputEntry(in.get(inputParameterKey));
	}
	
	
	protected void handleOutputByKey(String outputKey, VariableContent newValue) throws InOrOutKeyNotDefinedException{
		if(!out.containsKey(outputKey)){
			throw new InOrOutKeyNotDefinedException("Output key " + outputKey + " can't get matched to the defined output parameter keys.");
		}
		VariableHandlerInstance.setVariableValue(out.get(outputKey), newValue);
	}
	

	protected void handleOutputByKey(String outputKey, String newValue) throws InOrOutKeyNotDefinedException{
		if(!out.containsKey(outputKey)){
			throw new InOrOutKeyNotDefinedException("Output key " + outputKey + " can't get matched to the defined output parameter keys.");
		}
		VariableHandlerInstance.setVariableValue(out.get(outputKey), newValue);
	}
	

	protected void handleOutputByKey(String outputKey, int newValue) throws InOrOutKeyNotDefinedException{
		if(!out.containsKey(outputKey)){
			throw new InOrOutKeyNotDefinedException("Output key " + outputKey + " can't get matched to the defined output parameter keys.");
		}
		VariableHandlerInstance.setVariableValue(out.get(outputKey), newValue);
	}
	

	protected void handleOutputByKey(String outputKey, boolean newValue) throws InOrOutKeyNotDefinedException{
		if(!out.containsKey(outputKey)){
			throw new InOrOutKeyNotDefinedException("Output key " + outputKey + " can't get matched to the defined output parameter keys.");
		}
		VariableHandlerInstance.setVariableValue(out.get(outputKey), newValue);
	}
	
	
	/**
	 * Helper function for checkForDetectableErrors.
	 * Handles direct, from and not keywords in the input entries. 
	 * @param entry
	 * @throws StructureException
	 * @throws VariableNotDefinedException
	 * @throws FaultyDataException
	 */
	private void checkForDetectableErrorsEntryCheck(String entry) throws StructureException, VariableNotDefinedException, FaultyDataException{
		if(entry.startsWith(directValueKeyword)){
			String afterDirect = entry.substring(directValueKeyword.length()).trim();
			if(afterDirect.equals("")){
				throw new FaultyDataException("Value in direct entry missing or not recognized!");
			}
			// Currently nothing more to do.
		}
		else if(entry.startsWith(fromKeyword)){
			String referencedVariable = entry.substring(fromKeyword.length()).trim();
			if(VariableHandlerInstance.isVariableInitialized(referencedVariable)){
				String afterFrom = entry.substring(fromKeyword.length()).trim();
				if(afterFrom.equals("")){
					throw new FaultyDataException("Reference in from entry missing or not recognized!");
				}
				// Currently nothing more to do.
			}
			else{
				VariableHandlerInstance.generateVariableNotDefinedException(referencedVariable);
			}
		}
		else if(entry.startsWith(notKeyword)){
			String afterNot = entry.substring(notKeyword.length()).trim();
			checkForDetectableErrorsEntryCheck(afterNot);
		}
		else{
			throw new StructureException("Structure error or unexpected element " + entry);
		}

		for(String currentKey:out.keySet()){
			@SuppressWarnings("unused")
			String readAsTest = out.get(currentKey);
		}
	}
	
	
	/**
	 * Checks for errors such as missing parameters or referenced not existing variables.
	 * If it finds an error, then it throws an exception, else it simply finishes its execution. 
	 * @throws VariableNotDefinedException
	 * @throws StructureException
	 * @throws FaultyDataException
	 * @throws ParameterMismatchException
	 */
	public void checkForDetectableErrors() throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException{
		if(! (in.keySet()).equals(requiredInsAndOuts.get(inKeyword)) ){
			throw new ParameterMismatchException("Input parameter mismatch! Expected \n"+
					requiredInsAndOuts.get(inKeyword).toString() + ", got \n" + in.keySet().toString());
		}
		if(! (out.keySet()).equals(requiredInsAndOuts.get(outKeyword)) ){
			throw new ParameterMismatchException("Output parameter mismatch! Expected "+
					requiredInsAndOuts.get(outKeyword).toString() + ", got \n" + out.keySet().toString());
		}
		
		for(String currentKey:in.keySet()){
			String entry = in.get(currentKey);
			checkForDetectableErrorsEntryCheck(entry);
		}
		for(String currentKey:out.keySet()){
			String entry = out.get(currentKey);
			VariableHandlerInstance.setVariableInitialized(entry);
		}
	}
	
	
	/**
	 * Resolves the potentially relative path givenPathString to a full/absolute path.
	 * @param givenPathString
	 * @return A full/absolute path to the intended directory or file.
	 */
	protected Path resolveFullOrLocalPath(String givenPathString){
		if(givenPathString.startsWith("/")){ // Full path.
			return Paths.get(givenPathString);
		}
		else{ // Local path
			//String completePath = projectFolderPath + "/" + givenPath;
			Path completePath = ProjectFolderPathStorage.projectFolderPath.resolve(givenPathString);
			return completePath;
		}
	}
	
	
	public abstract void execute() throws VariableNotDefinedException, StructureException, FaultyDataException,
		ParameterMismatchException, IOException, InterruptedException, NoArduinoCLIConfigFileException,
		FQBNErrorEception, ProjectFolderPathNotSetException, AbortPipelineException, InOrOutKeyNotDefinedException;
	
	
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
	

	// For improvisations that require more access possibilities:
	

	public VariableContent getContentOfInput(String key) throws VariableNotDefinedException, StructureException, InOrOutKeyNotDefinedException{
		return handleInputByKey(key);
	}

	
	public void setContentOfOutput(String key, VariableContent newValue) throws InOrOutKeyNotDefinedException {
		handleOutputByKey(key, newValue);
	}

	public void setContentOfOutput(String key, String newValue) throws InOrOutKeyNotDefinedException {
		handleOutputByKey(key, newValue);
	}

	public void setContentOfOutput(String key, int newValue) throws InOrOutKeyNotDefinedException {
		handleOutputByKey(key, newValue);
	}

	public void setContentOfOutput(String key, boolean newValue) throws InOrOutKeyNotDefinedException {
		handleOutputByKey(key, newValue);
	}
	
	
	/**
	 * Makes the called step interpret the entry under the parameter key as path and return the resolved result path. 
	 * Please note: This is an helper method  that is only intended for the improvised pipeline execution, more specifically to be used for PipelineExecution/pipelineexecution.ui.exports/*Improvisation.java helper classes.
	 * Please remove as soon as a misused export is no longer used for the pipeline execution.
	 * @param key The input parameter name the step shall look for.  
	 * @return
	 * @throws VariableNotDefinedException
	 * @throws StructureException
	 * @throws InOrOutKeyNotDefinedException 
	 */
	public Path getResolvedPathContentOfInput(String key) throws VariableNotDefinedException, StructureException, InOrOutKeyNotDefinedException{
		Path result = resolveFullOrLocalPath(getContentOfInput(key).getContent());
		return result;
	}
}
