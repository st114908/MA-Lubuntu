/**
 * 
 */
package de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli;

import java.util.Map;

import de.ust.mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StepNotMatched;
import de.ust.mumlacgppa.pipeline.parts.steps.PipelineStep;
import de.ust.mumlacgppa.pipeline.parts.steps.PipelineStepDictionary;
import de.ust.mumlacgppa.pipeline.parts.storage.VariableHandler;

/**
 * @author muml
 *
 */
public class PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer extends PipelineStepDictionary {
	
	
	public PipelineStep lookupStepNameAndGenerateInstance(VariableHandler VariableHandlerInstance, String className, Map<String, Map<String, String>> parameters)
			throws StepNotMatched, ProjectFolderPathNotSetExceptionMUMLACGPPA{
		// The following flexible code failed, because no working solution as source attachment could be found.
		// But maybe there is a workaround for this problem.
		// https://stackoverflow.com/questions/1268817/create-new-object-from-a-string-in-java
		//Class cl = Class.forName(//className);
		//Class<?> cl = Class.forName("mumlarduinopipelineautomatisation.pipeline.parts.steps.functions." + className);
		//Constructor<?> con = cl.getConstructor(Map.class);
		//PipelineStep InterpretedStep = (PipelineStep) con.newInstance(parameters);
		
		// So this time a switch case usage is better to read than a long sequence of if(...){...}else ...
		// Class names have to be written in as Strings, because [class' name].class.getSimpleName() is for neither as field value nor as case entry allowed.
		// Attempts like case TerminalCommand.class.getName() aren't accepted either.
		
		switch(className){
			case AutoGitCommitAllAndPushCommand.nameFlag: 
				return new AutoGitCommitAllAndPushCommand(VariableHandlerInstance, parameters);
				
			case Compile.nameFlag:
				return new Compile(VariableHandlerInstance, parameters);
			
			case ComponentCodeGeneration.nameFlag:
				return new ComponentCodeGeneration(VariableHandlerInstance, parameters);
				
			case ContainerCodeGeneration.nameFlag:
				return new ContainerCodeGeneration(VariableHandlerInstance, parameters);
			
			case ContainerTransformation.nameFlag:
				return new ContainerTransformation(VariableHandlerInstance, parameters);

			case CopyFiles.nameFlag:
				return new CopyFiles(VariableHandlerInstance, parameters);
				
			case CopyFolder.nameFlag:
				return new CopyFolder(VariableHandlerInstance, parameters);

			case DeleteFile.nameFlag:
				return new DeleteFile(VariableHandlerInstance, parameters);
			
			case DeleteFolder.nameFlag:
				return new DeleteFolder(VariableHandlerInstance, parameters);
			
			case LookupBoardBySerialNumber.nameFlag:
				return new LookupBoardBySerialNumber(VariableHandlerInstance, parameters);

			case OnlyContinueIfFulfilledElseAbort.nameFlag:
				return new OnlyContinueIfFulfilledElseAbort(VariableHandlerInstance, parameters);
			
			case PopupWindowMessage.nameFlag:
				return new PopupWindowMessage(VariableHandlerInstance, parameters);
				
			case PostProcessingAddHALPartsIntoCarDriverInoFiles.nameFlag:
				return new PostProcessingAddHALPartsIntoCarDriverInoFiles(VariableHandlerInstance, parameters);

			case PostProcessingAdjustAPIMappingFile.nameFlag:
				return new PostProcessingAdjustAPIMappingFile(VariableHandlerInstance, parameters);

			case PostProcessingAdjustIncludes.nameFlag:
				return new PostProcessingAdjustIncludes(VariableHandlerInstance, parameters);

			case PostProcessingCopyFolderContentsToECUsAndExcept.nameFlag:
				return new PostProcessingCopyFolderContentsToECUsAndExcept(VariableHandlerInstance, parameters);

			case PostProcessingCopyFolderContentsToECUsWhitelist.nameFlag:
				return new PostProcessingCopyFolderContentsToECUsWhitelist(VariableHandlerInstance, parameters);

			case PostProcessingCopyLocalConfig_hppToCarDeriverECUs.nameFlag:
				return new PostProcessingCopyLocalConfig_hppToCarDeriverECUs(VariableHandlerInstance, parameters);

			case PostProcessingDownloadConfig_hpp.nameFlag:
				return new PostProcessingDownloadConfig_hpp(VariableHandlerInstance, parameters);

			case PostProcessingFillOutMethodStubs.nameFlag:
				return new PostProcessingFillOutMethodStubs(VariableHandlerInstance, parameters);

			case PostProcessingInsertAtLineIndex.nameFlag:
				return new PostProcessingInsertAtLineIndex(VariableHandlerInstance, parameters);

			case PostProcessingMoveIncludeBefore_ifdef__cplusplus.nameFlag:
				return new PostProcessingMoveIncludeBefore_ifdef__cplusplus(VariableHandlerInstance, parameters);

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
