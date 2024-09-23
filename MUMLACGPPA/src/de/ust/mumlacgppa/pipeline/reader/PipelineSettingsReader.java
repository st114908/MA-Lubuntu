package de.ust.mumlacgppa.pipeline.reader;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.yaml.snakeyaml.Yaml;

import de.ust.mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ParameterMismatchException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StepNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StepNotMatched;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StructureException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.TypeMismatchException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.steps.Keywords;
import de.ust.mumlacgppa.pipeline.parts.steps.PipelineStep;
import de.ust.mumlacgppa.pipeline.parts.steps.PipelineStepDictionary;
import de.ust.mumlacgppa.pipeline.parts.storage.VariableContent;
import de.ust.mumlacgppa.pipeline.parts.storage.VariableHandler;
import de.ust.mumlacgppa.pipeline.parts.storage.VariableTypes;

public class PipelineSettingsReader implements Keywords {
	// private Map<String, String> VariableDefs; //Variables are stored static
	// in the class VariableHandler.
	private Map<String, PipelineStep> transformationAndCodeGenerationPreconfigurationsDefs;
	private ArrayList<PipelineStep> postProcessingSequence;
	private ArrayList<PipelineStep> pipelineSequence;
	private VariableHandler VariableHandlerInstance;
	private PipelineStepDictionary stepDictionaryToUse;
	private boolean interpretPipelineSettingsCalled;
	private int postProcessingListExecutionIndex;
	private int pipelineSequenceExecutionIndex;

	@SuppressWarnings("unchecked")
	protected ArrayList<PipelineStep> interpretSequenceDef(ArrayList<Map<String, Object>> rawCurrentSequenceDef,
			boolean fromPostProcessingSequenceAllowed)
			throws StructureException, StepNotMatched, ProjectFolderPathNotSetExceptionMUMLACGPPA, StepNotDefinedException {
		// ArrayList<Map<String, Object>> for
		// either
		//
		// ArrayList<Map<"direct:", DataForUsedStepAsObject>> with
		// DataForUsedStepAsObject as
		// Map<String, Map<String, Map<String, String>>> for
		// Map<Step, Map<InOrOut, Map<ParameterOrOneOutput,
		// SourceOrSaveTarget>>>
		//
		// or
		//
		// ArrayList<Map<"from TransformationAndCodeGenerationPreconfigurations",
		// NameOfReferencedStepFromStandaloneUsageDefsAsObject>>
		//
		// or
		//
		// ArrayList<Map<"from TransformationAndCodeGenerationPreconfigurations", "all">>
		
		ArrayList<PipelineStep> interpretedSequence = new ArrayList<PipelineStep>();
		for (Map<String, Object> currentSequenceStepDef : rawCurrentSequenceDef) {
			// There should be only one key: [direct/from] [stepName] ...
			String currentPipelineStepKey = (String) currentSequenceStepDef.keySet().toArray()[0];
			boolean loadFromStandaloneUsage = currentPipelineStepKey.contains(fromKeyword); // from
			boolean directDefinitionUsage = currentPipelineStepKey.contains(directValueKeyword); // direct

			if (loadFromStandaloneUsage && !directDefinitionUsage) { // from
				String transformationAndCodeGenerationPreconfigurationsUsageBeginning = fromKeyword + " " + transformationAndCodeGenerationPreconfigurationsDefKeyword;
				String postProcessingSequenceUsageBeginning = fromKeyword + " " + postProcessingSequenceDefKeyword;
				boolean transformationAndCodeGenerationPreconfigurationsUsage = currentPipelineStepKey.contains(transformationAndCodeGenerationPreconfigurationsUsageBeginning);
				boolean postProcessingSequenceUsage = currentPipelineStepKey.contains(postProcessingSequenceUsageBeginning);
				

				if( !fromPostProcessingSequenceAllowed && postProcessingSequenceUsage){
					throw new StructureException("Structure error: Loading \"" + postProcessingSequenceDefKeyword + "\" isn't allowed at the location of "
							+ currentSequenceStepDef);
				}
				
				if(transformationAndCodeGenerationPreconfigurationsUsage){
					String wantedStep = (String) currentSequenceStepDef.get(transformationAndCodeGenerationPreconfigurationsUsageBeginning);
					if(!transformationAndCodeGenerationPreconfigurationsDefs.containsKey(wantedStep)){
						throw new StepNotDefinedException("Missing step from TransformationAndCodeGenerationPreconfigurations: " + wantedStep);
					}
					
					interpretedSequence.add(transformationAndCodeGenerationPreconfigurationsDefs.get(wantedStep));
				}
				else if(postProcessingSequenceUsage){
					if(currentSequenceStepDef.get(postProcessingSequenceUsageBeginning).equals(allKeyword)){
						interpretedSequence.addAll(postProcessingSequence);
					}
					else{
						throw new StructureException("Structure error: Only the entire sequence of " + postProcessingSequenceDefKeyword + " can be copied."
								+ "Alternatively a spelling mistake happend to what should be " + allKeyword);
					}
				}
				else{
					throw new StructureException("Structure error: No source could be identified in " + currentPipelineStepKey + " in "
							+ currentSequenceStepDef);
				}
			}
			else if (!loadFromStandaloneUsage && directDefinitionUsage) { // direct
				Map<String, Map<String, String>> rawStepDef = (Map<String, Map<String, String>>) currentSequenceStepDef
						.get(currentPipelineStepKey);
				String className = currentPipelineStepKey.replace(directValueKeyword, "").trim();
				PipelineStep InterpretedStep = stepDictionaryToUse
						.lookupStepNameAndGenerateInstance(VariableHandlerInstance, className, rawStepDef);
				interpretedSequence.add(InterpretedStep);
			}
			else if (loadFromStandaloneUsage && directDefinitionUsage) {
				throw new StructureException("Structure error: Both \"from\" and \"direct\" have been found in "
						+ currentSequenceStepDef);
			}
			else {
				throw new StructureException("Structure error: Neither \"from\" nor \"direct\" have been found in "
						+ currentSequenceStepDef);
			}
		}
		return interpretedSequence;
	}


	@SuppressWarnings("unchecked")
	protected void interpretPipelineSettings(Map<String, Object> rawSettings)
			throws Exception {
		if(interpretPipelineSettingsCalled){
			throw new Exception("The interpretaton has already been called!");
		}
		
		if (rawSettings == null) {
			return;
		}

		// Variable definitions are directly given to the VariableHandler.
		if (rawSettings.keySet().contains(variableDefsKeyword)) {
			ArrayList<String> variableDefsList = 
					(ArrayList<String>) rawSettings.get(variableDefsKeyword);
			// Map<String, Map<String, String>> for Map<VariableDef, Map<NameOrValueKeys, NameOrValue>>
			for (String rawCurrentVariableDef : variableDefsList){
				/*if(!rawCurrentVariableDef.containsKey(variableDefKeyword)){
					throw new StructureException(
							"Error at reading the pipeline: The variable defintion entry " + rawCurrentVariableDef + " did not get acknowledged as variable defintion.");
				}*/
				
				try{
					String[] currentVariableDef = rawCurrentVariableDef.trim().split(" ", 3);
					
					if(currentVariableDef.length != 3){
						throw new StructureException(
								"Error at reading the pipeline: The entry for variable definition " + rawCurrentVariableDef
								+ " couldn't get split up properly during interpretation.");
					}
					
					String currentName = currentVariableDef[1];
					
					if(VariableHandlerInstance.isVariableInitialized(currentName)){
						throw new StructureException(
								"Error at reading the pipeline: The variable name " + currentName + " is already known.\n"
								+ "The overwriting of Variable contents has no purpose at the beginning of the pipeline configurations, so it is seen as an error.\n"
								+ "So either remove the earlier declaration or assign this definition a different name.");
					}
					
					String currentType = currentVariableDef[0];
					if(!VariableTypes.checkIfAllowedType(currentType)){
						throw new StructureException(
								"Error at reading the pipeline: The entry for variable definition " + rawCurrentVariableDef
								+ " contains the unknown or not recognizable type " + currentType + ".");
					}
					
					String currentContent = currentVariableDef[2].trim();
					// The Variables defined at the beginning also need to "announced" as initialized for the checks. 
					VariableHandlerInstance.setVariableAsInitializedForValidation(currentName, currentType); 
					VariableHandlerInstance.setVariableValue(currentName, new VariableContent(currentContent));
				}
				catch(ClassCastException e){
					throw new StructureException(
							"Error at reading the pipeline: The entry for variable definition " + rawCurrentVariableDef
							+ " couldn't get interpreted.");
				}
			}
		}

		// Here the transformation and code generation configurations:

		// Map<String, Map<String, Map<String, String>>> for
		// Map<Step, Map<InOrOut, Map<ParameterOrOneOutput,
		// SourceOrSaveTarget>>>
		Set<String> allowedTransformationAndCodeGenerationPreconfigurationsStepTypes = stepDictionaryToUse.getAllowedTransformationAndCodeGenerationPreconfigurations();
		if (rawSettings.keySet().contains(transformationAndCodeGenerationPreconfigurationsDefKeyword)) {
			try{
				Map<String, Map<String, Map<String, String>>> rawStandaloneUsageDefs = (Map<String, Map<String, Map<String, String>>>) rawSettings
						.get(transformationAndCodeGenerationPreconfigurationsDefKeyword);
				
				for (String currentStepKey : rawStandaloneUsageDefs.keySet()) {
					String className = currentStepKey;
		
					if(!allowedTransformationAndCodeGenerationPreconfigurationsStepTypes.contains(className)){
						throw new StructureException("Error at reading the pipeline: The step type " + className + " is not allwed for "
								+ transformationAndCodeGenerationPreconfigurationsDefKeyword + " !\n");
					}
					
					
					PipelineStep InterpretedStep = stepDictionaryToUse.lookupStepNameAndGenerateInstance(
							VariableHandlerInstance, className, rawStandaloneUsageDefs.get(currentStepKey));
					transformationAndCodeGenerationPreconfigurationsDefs.put(currentStepKey, InterpretedStep);
				}
			}
			catch(ClassCastException e){
				throw new StructureException(e.getMessage() + "\n"
						+ "Apparently there was a heavy structure error while \n"
						+ "trying to read the TransformationAndCodeGenerationPreconfigurations.\n"
						+ "Example: For the interpreter entirely unreadable entries can cause this.");
			}
		}
		
		// Here the post-processing:
		
		if (rawSettings.keySet().contains(postProcessingSequenceDefKeyword)) {
			try{
				ArrayList<Map<String, Object>> rawCurrentSequenceDef = (ArrayList<Map<String, Object>>) rawSettings
					.get(postProcessingSequenceDefKeyword);
				// Here loading/copying the results of the interpretation of PostProcessingSequence is not possible since it is in the process of being generated.
				postProcessingSequence = interpretSequenceDef(rawCurrentSequenceDef, false);
			}
			catch(ClassCastException e){
				throw new StructureException(e.getMessage() + "\n"
						+ "Apparently there was a heavy structure error while \n"
						+ "trying to read the PostProcessingSequence.\n"
						+ "Example: For the interpreter entirely unreadable entries can cause this.");
			}
		}
		else{
			// For simplicity at copying this sequence or executing the pipeline.
			postProcessingSequence = new ArrayList<PipelineStep>();
		}
		
		// Here the pipeline sequence:
		
		if (rawSettings.keySet().contains(pipelineSequenceDefKeyword)) {
			try{
				ArrayList<Map<String, Object>> rawCurrentSequenceDef = (ArrayList<Map<String, Object>>) rawSettings
						.get(pipelineSequenceDefKeyword);
				// Here loading/copying the results of the interpretation of PostProcessingSequence is possible
				pipelineSequence = interpretSequenceDef(rawCurrentSequenceDef, true);
			}
			catch(ClassCastException e){
				throw new StructureException(e.getMessage() + "\n"
						+ "Apparently there was a heavy structure error while \n"
						+ "trying to read the PipelineSequence.\n"
						+ "Example: For the interpreter entirely unreadable entries can cause this.");
			}
		}
		else{
			// For simplicity at executing the pipeline.
			pipelineSequence = new ArrayList<PipelineStep>();
		}
		
		pipelineSequenceExecutionIndex = 0;
		interpretPipelineSettingsCalled = true;
	}

	/**
	 * Public for testing. No other usage intended! 
	 * @param rawSettingsString
	 * @throws Exception 
	 */
	public void interpretPipelineSettings(String rawSettingsString)
			throws Exception {
		Yaml yaml = new Yaml();
		Map<String, Object> rawSettingsMap = yaml.load(rawSettingsString);
		interpretPipelineSettings(rawSettingsMap);
	}
	
	/**
	 * Mainly for the tests.
	 * @param stepDictionaryToUse
	 */
	public PipelineSettingsReader(PipelineStepDictionary stepDictionaryToUse) {
		transformationAndCodeGenerationPreconfigurationsDefs = new HashMap<String, PipelineStep>();
		this.stepDictionaryToUse = stepDictionaryToUse;
		postProcessingSequence = new ArrayList<PipelineStep>();
		pipelineSequence = new ArrayList<PipelineStep>();
		interpretPipelineSettingsCalled = false;
		VariableHandlerInstance = new VariableHandler();
	}

	public PipelineSettingsReader(PipelineStepDictionary stepDictionaryToUse, Path settingsFilePath)
			throws Exception {
		transformationAndCodeGenerationPreconfigurationsDefs = new HashMap<String, PipelineStep>();
		this.stepDictionaryToUse = stepDictionaryToUse;
		interpretPipelineSettingsCalled = false;
		VariableHandlerInstance = new VariableHandler();

		Yaml yaml = new Yaml();
		/*
		 * InputStream inputStream = this.getClass() .getClassLoader().
		 * getResourceAsStream(settingsFilePath);
		 */
		InputStream inputStream = new FileInputStream(settingsFilePath.toFile());
		Map<String, Object> rawSettings = yaml.load(inputStream);
		interpretPipelineSettings(rawSettings);
	}

	public void checkForDetectableErrors()
			throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException	{
		String lastStep = "None";
		String lastPart = postProcessingSequenceDefKeyword;
		try {
			// TransformationAndCodeGenerationPreconfigurations
			for (String currentKey : transformationAndCodeGenerationPreconfigurationsDefs.keySet()) {
				lastStep = currentKey;
				VariableHandler ValidationVariableHandlerInstance = VariableHandlerInstance.getCopyForValidations();
				transformationAndCodeGenerationPreconfigurationsDefs.get(currentKey).checkForDetectableParameterVariableAndTypeErrors(ValidationVariableHandlerInstance);
			}
		
			// PostProcessingSequence
			lastPart = postProcessingSequenceDefKeyword;
			VariableHandler ValidationVariableHandlerInstancePostProcessing = VariableHandlerInstance.getCopyForValidations();
			for (PipelineStep currentStep : postProcessingSequence) {
				lastStep = currentStep.toString();
				currentStep.checkForDetectableParameterVariableAndTypeErrors(ValidationVariableHandlerInstancePostProcessing);
			}
	
			// PipelineSequence:
			lastPart = pipelineSequenceDefKeyword;
			VariableHandler ValidationVariableHandlerInstancePipelineSequence = VariableHandlerInstance.getCopyForValidations();
			for (PipelineStep currentStep : pipelineSequence) {
				lastStep = currentStep.toString();
				currentStep.checkForDetectableParameterVariableAndTypeErrors(ValidationVariableHandlerInstancePipelineSequence);
			}
		
		} catch (ParameterMismatchException e) {
			throw new ParameterMismatchException(e.getMessage() + " at \n" + lastStep + " in \n"
					+ lastPart + ".");
		} catch (VariableNotDefinedException e) {
			throw new VariableNotDefinedException(e.getMessage() + " at \n" + lastStep + " in \n"
					+ lastPart + ".");
		} catch (StructureException e) {
			throw new StructureException(e.getMessage() + " at \n" + lastStep + " in \n"
					+ lastPart + ".");
		} catch (FaultyDataException e) {
			throw new FaultyDataException(e.getMessage() + " at \n" + lastStep + " in \n"
					+ lastPart + ".");
		}
	}

	public PipelineStep getTransformationAndCodeGenerationPreconfigurationsDef(String stepName) {
		return transformationAndCodeGenerationPreconfigurationsDefs.get(stepName);
	}
	
	
	public boolean hasNextPostProcessingStep() {
		return ( postProcessingListExecutionIndex < postProcessingSequence.size() );
	}

	public PipelineStep getNextPostProcessingStep() {
		PipelineStep currentStep = postProcessingSequence.get(postProcessingListExecutionIndex);
		postProcessingListExecutionIndex++;
		return currentStep;
	}
	

	public boolean hasNextPipelineSequenceStep() {
		return ( pipelineSequenceExecutionIndex < pipelineSequence.size() );
	}

	public PipelineStep getNextPipelineSequenceStep() {
		PipelineStep currentStep = pipelineSequence.get(pipelineSequenceExecutionIndex);
		pipelineSequenceExecutionIndex++;
		return currentStep;
	}

	
	public void resetPostProcessingProgress(){
		postProcessingListExecutionIndex = 0;
	}
	
	public void resetPipelineSequenceProgress(){
		pipelineSequenceExecutionIndex = 0;
	}
	
	public boolean IsEntryInTransformationAndCodeGenerationPreconfigurations(String name){
		return transformationAndCodeGenerationPreconfigurationsDefs.containsKey(name);
	}
}
