package de.ust.mumlacgppa.pipeline.parts.steps;

public interface Keywords {
	public final String directValueKeyword = "direct";
	public final String fromKeyword = "from";
	public final String notKeyword = "not";
	public final String inKeyword = "in";
	public final String outKeyword = "out";
	public final String allKeyword = "all";
	
	public final String variableDefsKeyword = "VariableDefs";
	public final String transformationAndCodeGenerationPreconfigurationsDefKeyword = "TransformationAndCodeGenerationPreconfigurations";
	public final String postProcessingSequenceDefKeyword = "PostProcessingSequence";
	public final String pipelineSequenceDefKeyword = "PipelineSequence";
	
	public final String variableDefKeyword = "VariableDef";
	public final String variableDefNameKeyword = "name";
	public final String variableDefTypeKeyword = "type";
	public final String variableDefValueKeyword = "value";
}
