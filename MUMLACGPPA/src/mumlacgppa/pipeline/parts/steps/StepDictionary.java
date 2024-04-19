/**
 * 
 */
package mumlacgppa.pipeline.parts.steps;

import java.util.Map;

import mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import mumlacgppa.pipeline.parts.exceptions.StepNotMatched;
import mumlacgppa.pipeline.parts.steps.functions.Compile;
import mumlacgppa.pipeline.parts.steps.functions.ComponentCodeGeneration;
import mumlacgppa.pipeline.parts.steps.functions.ContainerCodeGeneration;
import mumlacgppa.pipeline.parts.steps.functions.ContainerTransformation;
import mumlacgppa.pipeline.parts.steps.functions.LookupBoardBySerialNumber;
import mumlacgppa.pipeline.parts.steps.functions.OnlyContinueIfFullfilledElseAbort;
import mumlacgppa.pipeline.parts.steps.functions.PopupWindowMessage;
import mumlacgppa.pipeline.parts.steps.functions.PostProcessingStateChartValues;
import mumlacgppa.pipeline.parts.steps.functions.PostProcessingStateChartValuesFlexible;
import mumlacgppa.pipeline.parts.steps.functions.PostProcessingStepsUntilConfig;
import mumlacgppa.pipeline.parts.steps.functions.ReplaceLineContent;
import mumlacgppa.pipeline.parts.steps.functions.SaveToTextFile;
import mumlacgppa.pipeline.parts.steps.functions.SelectableTextWindow;
import mumlacgppa.pipeline.parts.steps.functions.TerminalCommand;
import mumlacgppa.pipeline.parts.steps.functions.Upload;
import mumlacgppa.pipeline.parts.storage.VariableHandler;

/**
 * @author muml
 *
 */
public interface StepDictionary {
	// Failed, because no working solution as source attachment could be found..
	// https://stackoverflow.com/questions/1268817/create-new-object-from-a-string-in-java
	//Class cl = Class.forName(//className);
	//Class<?> cl = Class.forName("mumlarduinopipelineautomatisation.pipeline.parts.steps.functions." + className);
	//Constructor<?> con = cl.getConstructor(Map.class);
	//PipelineStep InterpretedStep = (PipelineStep) con.newInstance(parameters);
	
	
	public static PipelineStep lookupStepNameAndGenerateInstance(VariableHandler VariableHandlerInstance, String className, Map<String, Map<String, String>> parameters)
			throws StepNotMatched, ProjectFolderPathNotSetExceptionMUMLACGPPA{
		// This time a switch case usage is better to read than a long sequence of if(...){...}else ...
		// Class names have to be written in as Strings, because [class' name].class.getSimpleName() is for neither as field value nor as case entry allowed.
		// Attempts like case TerminalCommand.class.getName() aren't accepted either.
		
		switch(className){
			case Compile.nameFlag:
				return new Compile(VariableHandlerInstance, parameters);
			
			case ComponentCodeGeneration.nameFlag:
				return new ComponentCodeGeneration(VariableHandlerInstance, parameters);
				
			case ContainerCodeGeneration.nameFlag:
				return new ContainerCodeGeneration(VariableHandlerInstance, parameters);
			
			case ContainerTransformation.nameFlag:
				return new ContainerTransformation(VariableHandlerInstance, parameters);
				
			case LookupBoardBySerialNumber.nameFlag:
				return new LookupBoardBySerialNumber(VariableHandlerInstance, parameters);

			case OnlyContinueIfFullfilledElseAbort.nameFlag:
				return new OnlyContinueIfFullfilledElseAbort(VariableHandlerInstance, parameters);
			
			case PopupWindowMessage.nameFlag:
				return new PopupWindowMessage(VariableHandlerInstance, parameters);
				
			case PostProcessingStateChartValues.nameFlag:
				return new PostProcessingStateChartValues(VariableHandlerInstance, parameters);

			case PostProcessingStateChartValuesFlexible.nameFlag:
				return new PostProcessingStateChartValuesFlexible(VariableHandlerInstance, parameters);

			case PostProcessingStepsUntilConfig.nameFlag:
				return new PostProcessingStepsUntilConfig(VariableHandlerInstance, parameters);

			case ReplaceLineContent.nameFlag:
				return new ReplaceLineContent(VariableHandlerInstance, parameters);
			
			case SaveToTextFile.nameFlag:
				return new SaveToTextFile(VariableHandlerInstance, parameters);

			case SelectableTextWindow.nameFlag:
				return new SelectableTextWindow(VariableHandlerInstance, parameters);
			
			case TerminalCommand.nameFlag:
				return new TerminalCommand(VariableHandlerInstance, parameters);
				
			case Upload.nameFlag:
				return new Upload(VariableHandlerInstance, parameters);
				
			default:
				throw new StepNotMatched("String " + className + " could not get matched to a PipelineStep class!");
			
		}
	}
}
