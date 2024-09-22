package de.ust.mumlacgppa.pipeline.parts.storage;

import java.util.HashSet;
import java.util.LinkedHashMap;

import de.ust.mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;

public class VariableHandler {
	// A set and a map for more detailed error responses and explanations at verification or execution.
	private HashSet<String> variableInitialized;
	private LinkedHashMap<String, VariableContent> variableValues;
	private LinkedHashMap<String, String> variableTypes;
	
	public VariableHandler(){
		variableInitialized = new HashSet<String>();
		variableValues = new LinkedHashMap<String, VariableContent>();
		variableTypes = new LinkedHashMap<String, String>();
	}
	
	
	// For the tests:
	protected void clearStorage(){
		variableInitialized.clear();
		variableValues.clear();
		variableTypes.clear();
	}
	
	// For validation:
	
	public VariableHandler getCopyForValidations(){
		VariableHandler copyinstance = new VariableHandler();
		copyinstance.variableInitialized = new HashSet<String>(variableInitialized);
		copyinstance.variableValues = new LinkedHashMap<String, VariableContent>(variableValues);
		copyinstance.variableTypes = new LinkedHashMap<String, String>(variableTypes);
		return copyinstance;
	}
	
	public void setVariableAsInitializedForValidation(String key, String type){
		if(!variableInitialized.contains(key)){
			variableInitialized.add(key);
			variableTypes.put(key, type);
		}
	}
	

	public boolean isVariableInitialized(String key){
		boolean result = ( (variableInitialized.contains(key)) || (variableValues.containsKey(key)) );
		return result;
	}
	
	public String getVariableType(String key) throws VariableNotDefinedException{
		if(variableTypes.containsKey(key)){
			return variableTypes.get(key);
		}
		else{
			throw new VariableNotDefinedException("Variable " + key + " is not initialized at the usage point!");
		}
	}
	
	
	// For execution:
	
	public void setVariableValue(String key, VariableContent value){
		if(variableValues.containsKey(key)){
			variableValues.replace(key, value);
		}
		else{
			variableValues.put(key, value);
		}
	}


	public void setVariableValue(String key, String value){
		VariableContent newContent = new VariableContent(value);
		if(variableValues.containsKey(key)){
			variableValues.replace(key, newContent);
		}
		else{
			variableValues.put(key, newContent);
		}
	}
	

	public void setVariableValue(String key, int value){
		VariableContent newContent = new VariableContent(value);
		if(variableValues.containsKey(key)){
			variableValues.replace(key, newContent);
		}
		else{
			variableValues.put(key, newContent);
		}
	}
	
	
	public void setVariableValue(String key, boolean value){
		VariableContent newContent = new VariableContent(value);
		if(variableValues.containsKey(key)){
			variableValues.replace(key, newContent);
		}
		else{
			variableValues.put(key, newContent);
		}
	}
	
	
	public VariableContent getVariableValue(String key) throws VariableNotDefinedException{
		if(variableValues.containsKey(key)){
			return variableValues.get(key);
		}
		else{
			throw new VariableNotDefinedException("Variable " + key + " not found!");
		}
	}
	
	
	public void generateVariableNotDefinedException(String key) throws VariableNotDefinedException{
		throw new VariableNotDefinedException("Variable " + key + " not found!");
	}
}
