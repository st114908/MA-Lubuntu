package de.ust.mumlacgppa.pipeline.parts.steps;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import de.ust.mumlacgppa.pipeline.parts.exceptions.TypeMismatchException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.storage.VariableContent;
import de.ust.mumlacgppa.pipeline.parts.storage.VariableHandler;
import de.ust.mumlacgppa.pipeline.parts.storage.VariableTypes;
import projectfolderpathstorageplugin.ProjectFolderPathNotSetException;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

public abstract class PipelineStep implements Keywords, VariableTypes{
	protected VariableHandler VariableHandlerInstance;
	// The ins and outs are initially stored raw as strings since the variable content is only available at runtime. 
	protected Map<String, String> in; // <Name>: [not] {from, direct} <VariableNameOrValue>
	protected Map<String, String> out; // <Name>: <VariableName>
	
	
	/**
	 * To set the required parameters for the inputs (in) and the outputs (out).
	 * Required to set the field requiredInsAndOuts for checkForDetectableErrorsEntryCheck since
	 * "public static abstract" at once don't fit together in java.
	 * @return 
	 */
	protected abstract Map<String, Map<String, String>> getRequiredInsAndOuts();
	
	
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
				out.put(currentKey, outData.get(currentKey).trim());
			}
		}

		getRequiredInsAndOuts();
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
	
	
	private VariableContent resolveInputEntry(String entry) throws VariableNotDefinedException, StructureException, FaultyDataException{
		String directAndFollowingSpace = directValueKeyword + " ";
		String fromAndFollowingSpace = fromKeyword + " ";
		String notAndFollowingSpace = notKeyword + " ";
		if(entry.startsWith(directAndFollowingSpace)){
			String writtenInValue = entry.substring(directAndFollowingSpace.length()).trim();
			VariableContent directlyInsertedValue = new VariableContent(writtenInValue);
			return directlyInsertedValue;
		}
		else if(entry.startsWith(fromAndFollowingSpace)){
			String referencedVariable = entry.substring(fromAndFollowingSpace.length()).trim();
			return VariableHandlerInstance.getVariableValue(referencedVariable);
		}
		else if(entry.startsWith(notAndFollowingSpace)){
			String afterNot = entry.substring(notAndFollowingSpace.length()).trim();
			VariableContent gainedContent = resolveInputEntry(afterNot);
			boolean received;
			try {
				received = gainedContent.getBooleanContent();
				VariableContent invertedBooleanContent = new VariableContent( Boolean.toString(!received) );
				return invertedBooleanContent;
			} catch (FaultyDataException e) {
				throw new FaultyDataException("Value reading error at " + entry + ": " + e.getMessage());
			}
		}
		else{
			throw new StructureException("Structure error or unexpected element " + entry);
		}
	}
	
	
	protected VariableContent handleInputByKey(String inputParameterKey) throws VariableNotDefinedException, StructureException, InOrOutKeyNotDefinedException, FaultyDataException{
		if(!in.containsKey(inputParameterKey)){
			throw new InOrOutKeyNotDefinedException("Input parameter key " + inputParameterKey
					+ " can't get matched to the defined input parameter keys in "
					+ toString()
					+ ".");
		}
		return resolveInputEntry(in.get(inputParameterKey));
	}
	
	
	protected String[] handleInputByKeyAsArray(String inputParameterKey, String split)
			throws VariableNotDefinedException, StructureException, InOrOutKeyNotDefinedException, FaultyDataException{
		if(!in.containsKey(inputParameterKey)){
			throw new InOrOutKeyNotDefinedException("Input parameter key " + inputParameterKey + " can't get matched to the defined input parameter keys.");
		}
		String[] arrayToTrimAndReturn = resolveInputEntry(in.get(inputParameterKey)).getContent().split(split);
		for(int i = 0; i < arrayToTrimAndReturn.length; i++){
			arrayToTrimAndReturn[i] = arrayToTrimAndReturn[i].trim();
		}
		return arrayToTrimAndReturn;
	}
	
	
	protected void handleOutputByKey(String outputKey, VariableContent newValue) throws InOrOutKeyNotDefinedException{
		if(!out.containsKey(outputKey)){
			throw new InOrOutKeyNotDefinedException("Output key " + outputKey + " can't get matched to the defined output parameter keys.");
		}
		VariableHandlerInstance.setVariableValue(out.get(outputKey), newValue);
	}
	

	protected void handleOutputByKey(String outputKey, String newValue) throws InOrOutKeyNotDefinedException{
		if(!out.containsKey(outputKey)){
			throw new InOrOutKeyNotDefinedException("Output key " + outputKey + " can't get matched to the defined output parameter keys (" + out.keySet() + ").");
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
	 * Ensures an following space char to prevent trouble with variable names and values that start directly with them.
	 * Also checks the type unless the value was directly given.
	 * @param entry
	 * @param expectedType
	 * @param ValidationVariableHandlerInstance 
	 * @throws StructureException
	 * @throws VariableNotDefinedException
	 * @throws FaultyDataException
	 * @throws TypeMismatchException 
	 */
	private void checkForDetectableVariableOrValueErrorsEntryCheck(String entry, String expectedType, VariableHandler ValidationVariableHandlerInstance)
			throws StructureException, VariableNotDefinedException, FaultyDataException, TypeMismatchException{
		String directAndFollowingSpace = directValueKeyword + " ";
		String fromAndFollowingSpace = fromKeyword + " ";
		String notAndFollowingSpace = notKeyword + " ";
		
		if(entry.startsWith(directAndFollowingSpace)){
			String afterDirect = entry.substring(directAndFollowingSpace.length()).trim();
			if(afterDirect.equals("")){
				throw new FaultyDataException("Value in direct entry missing or not recognized!");
			}
			// Currently nothing more to do since the users are supposed to know what they are doing when they write in something directly.
		}
		
		else if(entry.startsWith(fromAndFollowingSpace)){
			String referencedVariable = entry.substring(fromAndFollowingSpace.length()).trim();
			if(referencedVariable.equals("")){
				throw new FaultyDataException("Reference in from entry missing or not recognized!");
			}

			if(referencedVariable.contains(" ")){
				throw new StructureException("Variable name error:\n"
						+ "Variable names are not allowed to contain spaces!\n"
						+ "Step info: " + toString());
			}
			
			if(ValidationVariableHandlerInstance.isVariableInitialized(referencedVariable)){
				String foundType = ValidationVariableHandlerInstance.getVariableType(referencedVariable);
				if(!( expectedType.equals(AnyType) )){
					if(!( expectedType.equals(foundType) )){
						throw new TypeMismatchException("Type error in entry " + entry + ": Expected " + expectedType + ", got " + foundType+".\n"+
								"Step info: " + toString());
					}
				}
				
			}
			else{
				ValidationVariableHandlerInstance.generateVariableNotDefinedException(referencedVariable);
			}
		}
		
		else if(entry.startsWith(notAndFollowingSpace)){
			// Boolean operation means boolean value!
			if(!( expectedType.equals("Boolean") )){
				throw new TypeMismatchException("Type error in entry " + entry + ": Expected " + expectedType + ", got Boolean"+".\n"+
						"Step info: " + toString());
			}
			
			String afterNot = entry.substring(notAndFollowingSpace.length()).trim();
			checkForDetectableVariableOrValueErrorsEntryCheck(afterNot, expectedType, ValidationVariableHandlerInstance);
		}
		else{
			throw new StructureException("Structure error or unexpected element " + entry);
		}

		for(String currentKey:out.keySet()){
			@SuppressWarnings("unused") // This is intentionally used for checking.
			String readAsTest = out.get(currentKey);
		}
	}
	
	
	/**
	 * Checks for errors such as missing parameters, referenced not existing variables and (through a helper function) type mismatches.
	 * If it finds an error, then it throws an exception, else it simply finishes its execution.
	 * It uses an external VariableHandler instance in order to allow more complex tests/checks.
	 * @param ValidationVariableHandlerInstance 
	 * @param validationVariableHandlerInstance
	 * @throws VariableNotDefinedException
	 * @throws StructureException
	 * @throws FaultyDataException
	 * @throws ParameterMismatchException
	 * @throws TypeMismatchException 
	 */
	public void checkForDetectableParameterVariableAndTypeErrors(VariableHandler ValidationVariableHandlerInstance)
			throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		Map<String, Map<String, String>> requiredInsAndOuts = getRequiredInsAndOuts();
		
		if(! (in.keySet()).equals(requiredInsAndOuts.get(inKeyword).keySet() ) ){
			throw new ParameterMismatchException("Input parameter set mismatch! Expected \n"+
					requiredInsAndOuts.get(inKeyword).keySet() + ", got \n" + in.keySet().toString()+"\n"+
					"Step info: " + toString());
		}
		if(! (out.keySet()).equals(requiredInsAndOuts.get(outKeyword).keySet()) ){
			throw new ParameterMismatchException("Output parameter set mismatch! Expected "+
					requiredInsAndOuts.get(outKeyword).keySet() + ", got \n" + out.keySet().toString()+"\n"+
					"Step info: " + toString());
		}
		
		for(String currentInParameterKey:in.keySet()){
			String input = in.get(currentInParameterKey);
			String variableEntryType = requiredInsAndOuts.get(inKeyword).get(currentInParameterKey);
			checkForDetectableVariableOrValueErrorsEntryCheck(input, variableEntryType, ValidationVariableHandlerInstance);
		}
		
		for(String currentOutParameterKey:out.keySet()){
			String variableEntryName = out.get(currentOutParameterKey);
			String variableEntryType = requiredInsAndOuts.get(outKeyword).get(currentOutParameterKey);
			
			if(variableEntryName.contains(" ")){
				throw new StructureException("Variable name error:\n"
						+ "Variable names are not allowed to contain spaces!\n"
						+ "Step info: " + toString());
			}
			
			if(variableEntryType.equals(AnyType)){
				throw new StructureException("PipelineStep subclass error: The Any type is only allowed for inputs, but not for outputs!\n"
						+ "If you are only a user then it is not your fault, but the responsible programmers is to blame.\n"
						+ "You can only stop using this step.\n"
						+ "Step info: " + toString());
			}
			
			// Prevent type changes if already initialized:
			if(ValidationVariableHandlerInstance.isVariableInitialized(variableEntryName)){
				String typeOfExistingInstance = ValidationVariableHandlerInstance.getVariableType(variableEntryName); 
				if(!( typeOfExistingInstance.equals(variableEntryType) )){
					throw new TypeMismatchException("Attempted type change detected: Already existing definition has type " + typeOfExistingInstance
							+ ", current output parameter has " + variableEntryType + ".\n"
							+ "Step info: " + toString());
				}
			}
			else{
				ValidationVariableHandlerInstance.setVariableAsInitializedForValidation(variableEntryName, variableEntryType);
			}
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
			return getClass().getSimpleName() + "\n[in=" + in + ",\nout=" + out + "]";
		}
		else if( (out.size() == 0) && (in.size() > 0) ){
			return getClass().getSimpleName() + "\n[in=" + in + "]";
		}
		else if( (out.size() > 0) && (in.size() == 0) ){
			return getClass().getSimpleName() + "\n[out=" + out + "]";
		}
		else{
			return getClass().getSimpleName() + " []";
		}
	}
	

	// For workarounds that require more access possibilities:
	
	
	public VariableContent getContentOfInput(String key) throws VariableNotDefinedException, StructureException, InOrOutKeyNotDefinedException, FaultyDataException{
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
	 * Please note: This is an helper method  that is only intended for the improvised pipeline execution (with workarounds),
	 * more specifically to be used for PipelineExecution/pipelineexecution.ui.exports/*Workaround.java helper classes.
	 * Please remove as soon as a misused export is no longer used for the pipeline execution.
	 * @param key The input parameter name the step shall look for.  
	 * @return
	 * @throws VariableNotDefinedException
	 * @throws StructureException
	 * @throws InOrOutKeyNotDefinedException 
	 * @throws FaultyDataException 
	 */
	public Path getResolvedPathContentOfInput(String key) throws VariableNotDefinedException, StructureException, InOrOutKeyNotDefinedException, FaultyDataException{
		Path result = resolveFullOrLocalPath(getContentOfInput(key).getContent());
		return result;
	}
}
