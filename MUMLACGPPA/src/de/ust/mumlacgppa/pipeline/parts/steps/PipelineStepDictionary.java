/**
 * 
 */
package de.ust.mumlacgppa.pipeline.parts.steps;

import java.util.Map;
import java.util.Set;

import de.ust.mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StepNotMatched;
import de.ust.mumlacgppa.pipeline.parts.storage.VariableHandler;

/**
 * @author muml
 * 
 */
public abstract class PipelineStepDictionary {
	
	public abstract PipelineStep lookupStepNameAndGenerateInstance(VariableHandler VariableHandlerInstance, String className, Map<String, Map<String, String>> parameters)
			throws StepNotMatched, ProjectFolderPathNotSetExceptionMUMLACGPPA;

	public abstract Set<String> getAllowedTransformationAndCodeGenerationPreconfigurations();
}
