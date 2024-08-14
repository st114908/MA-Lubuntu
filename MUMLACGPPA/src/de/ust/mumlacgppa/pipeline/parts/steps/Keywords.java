package de.ust.mumlacgppa.pipeline.parts.steps;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ComponentCodeGeneration;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ContainerCodeGeneration;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ContainerTransformation;

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
	
	public static Set<String> allowedTransformationAndCodeGenerationPreconfigurations = new HashSet<>(
			Arrays.asList(ContainerTransformation.nameFlag, ContainerCodeGeneration.nameFlag, ComponentCodeGeneration.nameFlag));
}
