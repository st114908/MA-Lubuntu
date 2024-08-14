package de.ust.mumlacgppa.pipeline.reader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import de.ust.mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ParameterMismatchException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StepNotMatched;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StructureException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.steps.Keywords;
import de.ust.mumlacgppa.pipeline.parts.steps.PipelineStep;
import de.ust.mumlacgppa.pipeline.parts.steps.PipelineStepDictionary;
import de.ust.mumlacgppa.pipeline.parts.storage.VariableContent;
import de.ust.mumlacgppa.pipeline.parts.storage.VariableHandler;

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
			throws StructureException, StepNotMatched, ProjectFolderPathNotSetExceptionMUMLACGPPA {
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
					interpretedSequence.add(transformationAndCodeGenerationPreconfigurationsDefs.get(currentSequenceStepDef
							.get(transformationAndCodeGenerationPreconfigurationsUsageBeginning)));
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
		VariableHandlerInstance = new VariableHandler();

		// Variable definitions are directly given to the VariableHandler.
		if (rawSettings.keySet().contains(variableDefsKeyword)) {
			// Map<String, String> for Map<Variable, Entry>.
			Map<String, String> rawVariableDefs = (Map<String, String>) rawSettings.get(variableDefsKeyword);
			for (String currentVariableKey : rawVariableDefs.keySet()) {
				try{
					String currentContentDeclaration = rawVariableDefs.get(currentVariableKey).trim();
					if (!currentContentDeclaration.startsWith(directValueKeyword + " ")) {
						throw new StructureException(
								"Structure error: \"direct \" before the content itself is missing in the content declaration for "
										+ currentVariableKey);
					}
					String currentContent = currentContentDeclaration.replaceFirst((directValueKeyword + " "), "");
					VariableHandlerInstance.setVariableValue(currentVariableKey, new VariableContent(currentContent));
				}
				catch(ClassCastException e){
					throw new StructureException(
							"Error at reading the pipeline: The value entry for variable " + currentVariableKey + " couldn't get interpreted. This can happen if a number values is written without \"direct \" before the content itself");
				}
			}
		}

		// Here the transformation and code generation configurations:

		// Map<String, Map<String, Map<String, String>>> for
		// Map<Step, Map<InOrOut, Map<ParameterOrOneOutput,
		// SourceOrSaveTarget>>>
		if (rawSettings.keySet().contains(transformationAndCodeGenerationPreconfigurationsDefKeyword)) {
			Map<String, Map<String, Map<String, String>>> rawStandaloneUsageDefs = (Map<String, Map<String, Map<String, String>>>) rawSettings
					.get(transformationAndCodeGenerationPreconfigurationsDefKeyword);
			
			if (!(rawStandaloneUsageDefs.keySet().equals(allowedTransformationAndCodeGenerationPreconfigurations))) {
				throw new StructureException("Error at reading the pipeline: The entries found in "
						+ transformationAndCodeGenerationPreconfigurationsDefKeyword + " don't match the expected set of entries!\n"
								+ "Found: " + rawStandaloneUsageDefs.keySet() + "\n"
										+ "Expected: " + allowedTransformationAndCodeGenerationPreconfigurations);
			}
			
			for (String currentStepKey : rawStandaloneUsageDefs.keySet()) {
				String className = currentStepKey;
				PipelineStep InterpretedStep = stepDictionaryToUse.lookupStepNameAndGenerateInstance(
						VariableHandlerInstance, className, rawStandaloneUsageDefs.get(currentStepKey));
				transformationAndCodeGenerationPreconfigurationsDefs.put(currentStepKey, InterpretedStep);
			}
		}
		
		// Here the post-processing:
		
		if (rawSettings.keySet().contains(postProcessingSequenceDefKeyword)) {
			ArrayList<Map<String, Object>> rawCurrentSequenceDef = (ArrayList<Map<String, Object>>) rawSettings
					.get(postProcessingSequenceDefKeyword);
			// Here loading/copying the results of the interpretation of PostProcessingSequence is not possible since it is in the process of being generated.
			postProcessingSequence = interpretSequenceDef(rawCurrentSequenceDef, false);
		}
		else{
			// For simplicity at copying this sequence or executing the pipeline.
			postProcessingSequence = new ArrayList<PipelineStep>();
		}
		
		// Here the pipeline sequence:
		
		if (rawSettings.keySet().contains(pipelineSequenceDefKeyword)) {
			ArrayList<Map<String, Object>> rawCurrentSequenceDef = (ArrayList<Map<String, Object>>) rawSettings
					.get(pipelineSequenceDefKeyword);
			// Here loading/copying the results of the interpretation of PostProcessingSequence is possible
			pipelineSequence = interpretSequenceDef(rawCurrentSequenceDef, true);
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
	}

	public PipelineSettingsReader(PipelineStepDictionary stepDictionaryToUse, Path settingsFilePath)
			throws Exception {
		transformationAndCodeGenerationPreconfigurationsDefs = new HashMap<String, PipelineStep>();
		this.stepDictionaryToUse = stepDictionaryToUse;
		interpretPipelineSettingsCalled = false;

		Yaml yaml = new Yaml();
		/*
		 * InputStream inputStream = this.getClass() .getClassLoader().
		 * getResourceAsStream(settingsFilePath);
		 */
		InputStream inputStream = new FileInputStream(settingsFilePath.toFile());
		Map<String, Object> rawSettings = yaml.load(inputStream);
		interpretPipelineSettings(rawSettings);
	}

	public void validateOrder()
			throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException	{
		String lastStep = "None";
		String lastPart = postProcessingSequenceDefKeyword;
		try {
			// TransformationAndCodeGenerationPreconfigurations
			for (String currentKey : transformationAndCodeGenerationPreconfigurationsDefs.keySet()) {
				lastStep = currentKey;
				VariableHandler ValidationVariableHandlerInstance = VariableHandlerInstance.getCopyForValidations();
				transformationAndCodeGenerationPreconfigurationsDefs.get(currentKey).checkForDetectableErrors(ValidationVariableHandlerInstance);
			}
		
			// PostProcessingSequence
			lastPart = postProcessingSequenceDefKeyword;
			VariableHandler ValidationVariableHandlerInstancePostProcessing = VariableHandlerInstance.getCopyForValidations();
			for (PipelineStep currentStep : postProcessingSequence) {
				lastStep = currentStep.toString();
				currentStep.checkForDetectableErrors(ValidationVariableHandlerInstancePostProcessing);
			}
	
			// PipelineSequence:
			lastPart = pipelineSequenceDefKeyword;
			VariableHandler ValidationVariableHandlerInstancePipelineSequence = VariableHandlerInstance.getCopyForValidations();
			for (PipelineStep currentStep : pipelineSequence) {
				lastStep = currentStep.toString();
				currentStep.checkForDetectableErrors(ValidationVariableHandlerInstancePipelineSequence);
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

	public PipelineStep getStandaloneUsageDef(String stepName) {
		return transformationAndCodeGenerationPreconfigurationsDefs.get(stepName);
	}
	
	
	public void resetPostProcessingProgress(){
		postProcessingListExecutionIndex = 0;
	}
	
	public boolean hasNextPostProcessingStep() {
		return ( postProcessingListExecutionIndex < pipelineSequence.size() );
	}

	public PipelineStep getNextPostProcessingStep() {
		PipelineStep currentStep = postProcessingSequence.get(postProcessingListExecutionIndex);
		postProcessingListExecutionIndex++;
		return currentStep;
	}
	

	public void resetPipelineSequenceProgress(){
		pipelineSequenceExecutionIndex = 0;
	}
	
	public boolean hasNextPipelineSequenceStep() {
		return ( pipelineSequenceExecutionIndex < pipelineSequence.size() );
	}

	public PipelineStep getNextPipelineSequenceStep() {
		PipelineStep currentStep = pipelineSequence.get(pipelineSequenceExecutionIndex);
		pipelineSequenceExecutionIndex++;
		return currentStep;
	}

}
