package mumlacgppa.pipeline.settings;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import mumlacgppa.pipeline.parts.exceptions.ParameterMismatchException;
import mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import mumlacgppa.pipeline.parts.exceptions.StepNotMatched;
import mumlacgppa.pipeline.parts.exceptions.StructureException;
import mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import mumlacgppa.pipeline.parts.steps.Keywords;
import mumlacgppa.pipeline.parts.steps.PipelineStep;
import mumlacgppa.pipeline.parts.steps.StepDictionary;
import mumlacgppa.pipeline.parts.storage.VariableContent;
import mumlacgppa.pipeline.parts.storage.VariableHandler;

public class PipelineSettingsReader implements Keywords, StepDictionary{
	// private Map<String, String> VariableDefs; //Variables are stored static in the class VariableHandler.
	private Map<String, PipelineStep> standaloneUsageDefs;
	private ArrayList<PipelineStep> pipelineSequence;
	private VariableHandler VariableHandlerInstance;
	
	/**
	 * @param rawSettings
	 * @throws StructureException 
	 * @throws StepNotMatched 
	 * @throws ProjectFolderPathNotSetExceptionMUMLACGPPA 
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings("unchecked")
	public void interpretPipelineSettings(Map<String, Object> rawSettings) throws StructureException, StepNotMatched, ProjectFolderPathNotSetExceptionMUMLACGPPA{
		if(rawSettings == null){
			return;
		}
		VariableHandlerInstance = new VariableHandler();
		
		// Variable definitions are directly given to the VariableHandler.
		if( rawSettings.keySet().contains(variableDefsFlag) ){
			// Map<String, String>  for Map<Variable, Entry>.
			Map<String, String> rawVariableDefs = (Map<String, String>) rawSettings.get(variableDefsFlag);
			for(String currentVariableKey:rawVariableDefs.keySet()){
				String currentContentDeclaration = rawVariableDefs.get(currentVariableKey).trim();
				if(!currentContentDeclaration.startsWith(directValueFlag + " ")){
					throw new StructureException("Structure error: \"direct \" before the content itself is missing in the content declaration for " + currentVariableKey);
				}
				String currentContent = currentContentDeclaration.replaceFirst( (directValueFlag + " "), "");
				VariableHandlerInstance.setVariableValue(currentVariableKey, new VariableContent(currentContent));
			}
		}
		
		// The standalone usage and additional actions definitions as well as the pipeline are prepared to be validated or executed.
		
		// Map<String, Map<String, Map<String, String>>> for
		// Map<Step, Map<InOrOut, Map<ParameterOrOneOutput, SourceOrSaveTarget>>>
		if( rawSettings.keySet().contains(standaloneUsageDefsFlag) ){
			Map<String, Map<String, Map<String, String>>> rawStandaloneUsageDefs = 
					(Map<String, Map<String, Map<String, String>>>) rawSettings.get(standaloneUsageDefsFlag);
			for(String currentStepKey:rawStandaloneUsageDefs.keySet()){
				String className = currentStepKey;
				PipelineStep InterpretedStep = StepDictionary.lookupStepNameAndGenerateInstance(
						VariableHandlerInstance, className, rawStandaloneUsageDefs.get(currentStepKey));
				standaloneUsageDefs.put(currentStepKey, InterpretedStep);
			}
		}
		
		// ArrayList<Map<String, Object>> for
		// either
		//
		// ArrayList<Map<"direct:", DataForUsedStepAsObject>> with DataForUsedStepAsObject as
		// Map<String, Map<String, Map<String, String>>> for
		// Map<Step, Map<InOrOut, Map<ParameterOrOneOutput, SourceOrSaveTarget>>>
		//
		// or
		//
		// ArrayList<Map<"from:", NameOfReferencedStepFromStandaloneUsageDefsAsObject>>
		if( rawSettings.keySet().contains(pipelineSequenceDefFlag) ){
			ArrayList<Map<String, Object>> rawPipelineDef = 
					(ArrayList<Map<String, Object>>) rawSettings.get(pipelineSequenceDefFlag);
			for(Map<String, Object> currentPipelineStepDef:rawPipelineDef){
				// There should be only one key: [direct/from] [stepName] ...
				String currentPipelineStepKey = (String) currentPipelineStepDef.keySet().toArray()[0];
				boolean loadFromStandaloneUsage = currentPipelineStepKey.contains(fromValueFlag); // from: ...
				boolean directDefinitionUsage = currentPipelineStepKey.contains(directValueFlag); // direct: ...
				
				if( loadFromStandaloneUsage && !directDefinitionUsage ){ // from: ...
					pipelineSequence.add( standaloneUsageDefs.get( currentPipelineStepDef.get(fromValueFlag + " " + standaloneUsageDefsFlag) ) );
				}
				else if(directDefinitionUsage && !loadFromStandaloneUsage){ // direct: ...
					Map<String, Map<String, String>> rawStepDef = 
							(Map<String, Map<String, String>>) currentPipelineStepDef.get(currentPipelineStepKey);
					String className = currentPipelineStepKey.replace(directValueFlag, "").trim();
					PipelineStep InterpretedStep = StepDictionary.lookupStepNameAndGenerateInstance(
							VariableHandlerInstance, className, rawStepDef);
					pipelineSequence.add(InterpretedStep);
				}
				else if(loadFromStandaloneUsage && directDefinitionUsage){
					throw new StructureException("Structure error: Both \"from\" and \"direct\" have been found in " + currentPipelineStepDef);
				}
				else{
					throw new StructureException("Structure error: Neither \"from\" nor \"direct\" have been found in " + currentPipelineStepDef);
				}
			}
		}
		
	}
	

	public void interpretPipelineSettings(String rawSettingsString) throws StructureException, StepNotMatched, ProjectFolderPathNotSetExceptionMUMLACGPPA{
		Yaml yaml = new Yaml();
		Map<String, Object> rawSettingsMap = yaml.load(rawSettingsString);
		interpretPipelineSettings(rawSettingsMap);
	}
	
	
	public PipelineSettingsReader(){ // Mainly for the tests.
		standaloneUsageDefs = new HashMap<String, PipelineStep>();
		pipelineSequence = new ArrayList<PipelineStep>();
	}

	
	public PipelineSettingsReader(Path settingsFilePath)
			throws FileNotFoundException, StructureException, StepNotMatched, ProjectFolderPathNotSetExceptionMUMLACGPPA{
		standaloneUsageDefs = new HashMap<String, PipelineStep>();
		pipelineSequence = new ArrayList<PipelineStep>();
		
		Yaml yaml = new Yaml();
		/*InputStream inputStream = this.getClass()
				.getClassLoader().
				getResourceAsStream(settingsFilePath);*/
		InputStream inputStream = new FileInputStream(settingsFilePath.toFile());
		Map<String, Object> rawSettings = yaml.load(inputStream);
		interpretPipelineSettings(rawSettings);
	}
	
	
	public void validateOrder() throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException{
		// private Map<String, PipelineStep> standaloneUsageDefs;
		// private ArrayList<PipelineStep> pipelineSequence;
		
		// standaloneUsageDefs:
		for(String currentKey: standaloneUsageDefs.keySet()){
			try {
				standaloneUsageDefs.get(currentKey).checkForDetectableErrors();
			} catch (ParameterMismatchException e) {
				throw new ParameterMismatchException(e.getMessage() + " at " + currentKey + " in " + standaloneUsageDefsFlag + ".");
			}
		}
		
		// Pipeline:
		for(PipelineStep currentStep: pipelineSequence){
			try{
				currentStep.checkForDetectableErrors();
			} catch (ParameterMismatchException e) {
				throw new ParameterMismatchException(e.getMessage() + " at " + currentStep + " in " + pipelineSequenceDefFlag + ".");
			}
		}
	}


	public Map<String, PipelineStep> getStandaloneUsageDefs() {
		return standaloneUsageDefs;
	}


	public ArrayList<PipelineStep> getPipelineSequence() {
		return pipelineSequence;
	}
	
	
}
