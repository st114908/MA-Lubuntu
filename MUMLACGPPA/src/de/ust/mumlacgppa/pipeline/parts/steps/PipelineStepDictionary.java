/**
 * 
 */
package de.ust.mumlacgppa.pipeline.parts.steps;

import java.util.Map;

import de.ust.mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StepNotMatched;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.Compile;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ComponentCodeGeneration;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ContainerCodeGeneration;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ContainerTransformation;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.LookupBoardBySerialNumber;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.OnlyContinueIfFulfilledElseAbort;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PopupWindowMessage;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingStateChartValues;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingStateChartValuesFlexible;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingStepsUntilConfig;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ReplaceLineContent;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.SaveToTextFile;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.SelectableTextWindow;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.TerminalCommand;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.Upload;
import de.ust.mumlacgppa.pipeline.parts.storage.VariableHandler;

/**
 * @author muml
 * 
 */
public abstract class PipelineStepDictionary {
	
	public abstract PipelineStep lookupStepNameAndGenerateInstance(VariableHandler VariableHandlerInstance, String className, Map<String, Map<String, String>> parameters)
			throws StepNotMatched, ProjectFolderPathNotSetExceptionMUMLACGPPA;
}
