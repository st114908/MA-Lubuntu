package de.ust.mumlacgppa.pipeline.parts.storage;

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

	public boolean getBooleanContent() {
		return Boolean.parseBoolean(content);
	}

	public int getIntContent() {
		return Integer.parseInt(content);
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
