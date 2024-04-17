package mumlacgppa.pipeline.parts.storage;

public class VariableContent {
	private String content;
	// TODO: type
	
	public VariableContent(String content){
		this.content = content;
	}
	

	public VariableContent(int content){
		this.content = Integer.toString(content);
	}
	
	
	public VariableContent(boolean content){
		this.content = Boolean.toString(content);
	}

	
	public VariableContent(Boolean content){
		this.content = Boolean.toString(content);
	}
	
	
	public void setContent(String content){
		this.content = content;
		// TODO: Type Stuff
	}
	

	public String getContent(){
		return content;
	}
	
	
	public boolean getBooleanContent(){
		return Boolean.parseBoolean(content);
	}
	
	
	public int getIntContent(){
		return Integer.parseInt(content);
	}
	
	
}
