package mumlacgppa.pipeline.parts.steps;

public interface Keywords {
	public String directValueFlag = "direct";
	public String fromValueFlag = "from";
	public String notValueFlag = "not";
	public String inFlag = "in";
	public String outFlag = "out";
	
	public String variableDefsFlag = "VariableDefs";
	public String standaloneUsageDefsFlag = "StandaloneUsageDefs";
	public String pipelineSequenceDefFlag = "PipelineSequence";
	
	public String typeFlag = "type";
	public String insAndOutsFlag = "insAndOuts";
}
