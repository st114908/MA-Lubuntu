package de.ust.mumlacgppa.pipeline.parts.storage;

import de.ust.mumlacgppa.pipeline.parts.exceptions.FaultyDataException;

/**
 * This class handles the storage of variable content. The possibility of
 * generics The possibility of data types for differentiating between strings
 * for paths and strings for has been noticed during planning, but didn't get
 * implemented due to time constraints.
 */
public class VariableContent {
	private String content;
	// TODO: type

	public VariableContent(String content) {
		this.content = content;
	}

	public VariableContent(int content) {
		this.content = Integer.toString(content);
	}

	public VariableContent(boolean content) {
		this.content = Boolean.toString(content);
	}

	public VariableContent(Boolean content) {
		this.content = Boolean.toString(content);
	}

	public void setContent(String content) {
		this.content = content;
		// TODO: Type Stuff
	}

	public String getContent() {
		return content;
	}

	public boolean getBooleanContent() throws FaultyDataException {
		try{
			return Boolean.parseBoolean(content);
		}
		catch(Exception e){
			throw new FaultyDataException("Content " + content + " coulnd't be read as boolean (true or false) value! Check if you are using the correct value type or if you are accidentally using \"direct\" instead of \"from\".");
		}
	}

	public int getIntContent() throws FaultyDataException {
		try{
			return Integer.parseInt(content);
		}
		catch(Exception e){
			throw new FaultyDataException("Content " + content + " coulnd't be read as integer number value! Check if you are using the correct value type or if you are accidentally using \"direct\" instead of \"from\".");
		}
	}

}

/*
 * public class VariableContent<T> { private T content; // TODO: type
 * implementation
 * 
 * public VariableContent(T content) { this.content = content; }
 * 
 * public void setContent(T content) { this.content = content; // TODO: Type
 * implementation }
 * 
 * public T getContent() { return content; } }
 */
