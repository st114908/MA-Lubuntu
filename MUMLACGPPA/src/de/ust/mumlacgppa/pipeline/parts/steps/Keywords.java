package de.ust.mumlacgppa.pipeline.parts.steps;

public interface Keywords {
	public final String directValueKeyword = "direct";
	public final String fromKeyword = "from";
	public final String notKeyword = "not";
	public final String inKeyword = "in";
	public final String outKeyword = "out";
	public final String allKeyword = "all";
	
	public final String variableDefsKeyword = "VariableDefs";
	public final String standaloneTransformationAndCodeGenerationsDefsKeyword = "StandaloneTransformationAndCodeGenerationsDefs";
	public final String standalonePostProcessingSequenceDefKeyword = "StandalonePostProcessingSequence";
	public final String pipelineSequenceDefKeyword = "PipelineSequence";
}
