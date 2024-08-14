package de.ust.mumlacgppa.pipeline.parts.storage;

import java.util.HashSet;
import java.util.LinkedHashMap;

import de.ust.mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;

public class VariableHandler {
	// A set and a map for more detailed error responses and explanations at verification or execution.
	private HashSet<String> variableInitialized;
	private LinkedHashMap<String, VariableContent> variableValue;
	
	public VariableHandler(){
		variableInitialized = new HashSet<String>();
		variableValue = new LinkedHashMap<String, VariableContent>();
	}
	
	
	// For the tests:
	protected void clearStorage(){
		variableInitialized.clear();
		variableValue.clear();
	}
	
	// For validation:
	
	public VariableHandler getCopyForValidations(){
		VariableHandler copyinstance = new VariableHandler();
		copyinstance.variableInitialized = new HashSet<String>(variableInitialized);
		copyinstance.variableValue = new LinkedHashMap<String, VariableContent>(variableValue);
		return copyinstance;
	}
	
	public void setVariableAsInitialized(String key){
		if(!variableInitialized.contains(key)){
			variableInitialized.add(key);
		}
	}
	

	public boolean isVariableInitialized(String key){
		boolean result = ( (variableInitialized.contains(key)) || (variableValue.containsKey(key)) );
		return result;
	}
	
	
	// For execution:
	
	public void setVariableValue(String key, VariableContent value){
		if(variableValue.containsKey(key)){
			variableValue.replace(key, value);
		}
		else{
			variableValue.put(key, value);
		}
	}


	public void setVariableValue(String key, String value){
		VariableContent newContent = new VariableContent(value);
		if(variableValue.containsKey(key)){
			variableValue.replace(key, newContent);
		}
		else{
			variableValue.put(key, newContent);
		}
	}
	

	public void setVariableValue(String key, int value){
		VariableContent newContent = new VariableContent(value);
		if(variableValue.containsKey(key)){
			variableValue.replace(key, newContent);
		}
		else{
			variableValue.put(key, newContent);
		}
	}
	
	
	public void setVariableValue(String key, boolean value){
		VariableContent newContent = new VariableContent(value);
		if(variableValue.containsKey(key)){
			variableValue.replace(key, newContent);
		}
		else{
			variableValue.put(key, newContent);
		}
	}
	
	
	public VariableContent getVariableValue(String key) throws VariableNotDefinedException{
		if(variableValue.containsKey(key)){
			return variableValue.get(key);
		}
		else{
			throw new VariableNotDefinedException("Variable " + key + " does not exist (at least the usage point)!");
		}
	}
	
	
	public void generateVariableNotDefinedException(String key) throws VariableNotDefinedException{
		throw new VariableNotDefinedException("Variable " + key + " does not exist (at least the usage point)!");
	}
}
