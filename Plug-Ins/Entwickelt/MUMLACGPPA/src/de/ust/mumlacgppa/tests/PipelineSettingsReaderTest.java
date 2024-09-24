/**
 * 
 */
package de.ust.mumlacgppa.tests;

import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.ust.mumlacgppa.pipeline.parts.exceptions.StepNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StructureException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.steps.Keywords;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ContainerCodeGeneration;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ContainerTransformation;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.LookupBoardBySerialNumber;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.DialogMessage;
import de.ust.mumlacgppa.pipeline.reader.PipelineSettingsReader;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

/**
 * @author muml
 *
 */
public class PipelineSettingsReaderTest implements Keywords{
	InternalTestVariableHandlerClearStorageAccess VariableHandlerInstance;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception{
		ProjectFolderPathStorage.projectFolderPath = Paths.get("/home/muml/MUMLProjects/Overtaking-Cars-Baumfalk");
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		VariableHandlerInstance = new InternalTestVariableHandlerClearStorageAccess();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		VariableHandlerInstance.clearVariableHandlerStorage();
	}

	
	@Test
	public void testEmptyFile() throws Exception{
		String testYamlText = 
				variableDefsKeyword + ": []\n"
				+ transformationAndCodeGenerationPreconfigurationsDefKeyword + ": {}\n"
				+ pipelineSequenceDefKeyword + ": []\n";
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());
		PSRInstance.interpretPipelineSettings(testYamlText);
		PSRInstance.checkForDetectableErrors();
	}


	@Test
	public void testEmptyFileMinimal() throws Exception{
		String testYamlText = "\n";
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());
		PSRInstance.interpretPipelineSettings(testYamlText);
		PSRInstance.checkForDetectableErrors();
	}

	
	@Test
	public void testVariablesOnly() throws Exception{
		String testYamlText = 
				variableDefsKeyword + ":\n"
				+ "- Boolean dummybool true\n"
				+ "- FolderPath dummyString AttemptedDummyPath\n"
				+ transformationAndCodeGenerationPreconfigurationsDefKeyword + ": {}\n"
				+ pipelineSequenceDefKeyword + ": []\n";
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());
		PSRInstance.interpretPipelineSettings(testYamlText);
		PSRInstance.checkForDetectableErrors();
	}

	
	@Test
	public void testStandaloneUsageDefsOnly() throws Exception{
		String testYamlText = 
				transformationAndCodeGenerationPreconfigurationsDefKeyword + ": \n  "
				+  ContainerTransformation.nameFlag + ": \n"
				+ "    " + inKeyword + ":\n"
				+ "      roboCar_mumlSourceFile: direct model/roboCar.muml \n"
				+ "      middlewareOption: direct MQTT_I2C_CONFIG \n"
				+ "      muml_containerFileDestination: direct container-models/MUML_Container.muml_container\n"
				+ "    " + outKeyword + ":\n"
				+ "      ifSuccessful: ifSuccessful \n";
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());
		PSRInstance.interpretPipelineSettings(testYamlText);
		PSRInstance.checkForDetectableErrors();
	}

	@Test
	public void testPostProcessingOnly() throws Exception{
		String testYamlText = 
				postProcessingSequenceDefKeyword + ": \n" 
				+ "- " + directValueKeyword + " " + DialogMessage.nameFlag + ": \n"
				+ "    " + inKeyword + ": \n"
				+ "      condition: direct true\n"
				+ "      message: direct HelloTest\n"
				+ "\n";
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());
		PSRInstance.interpretPipelineSettings(testYamlText);
		PSRInstance.checkForDetectableErrors();
	}

	@Test
	public void testPipelineOnly() throws Exception{
		String testYamlText = 
				pipelineSequenceDefKeyword + ": \n" 
				+ "- " + directValueKeyword + " " + DialogMessage.nameFlag + ": \n"
				+ "    " + inKeyword + ": \n"
				+ "      condition: direct true\n"
				+ "      message: direct HelloTest\n"
				+ "\n";
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());
		PSRInstance.interpretPipelineSettings(testYamlText);
		PSRInstance.checkForDetectableErrors();
	}

	@Test
	public void testPipelineOnly2() throws Exception{
		String testYamlText = 
				variableDefsKeyword + ": \n  []\n"
				+ pipelineSequenceDefKeyword + ": \n"
				+ "- " + directValueKeyword + " " + LookupBoardBySerialNumber.nameFlag + ": \n"
				+ "    " + inKeyword + ": \n"
				+ "      boardSerialNumber: direct dummy\n"
				+ "      boardTypeIdentifierFQBN: direct dummy\n"
				+ "    " + outKeyword + ": \n"
				+ "      ifSuccessful: dummyBool\n"
				+ "      foundPortAddress: dummyPort\n"
				+ "- " + directValueKeyword + " " + DialogMessage.nameFlag + ": \n"
				+ "    " + inKeyword + ": \n"
				+ "      condition: from dummyBool \n"
				+ "      message: from dummyPort\n";
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());
		PSRInstance.interpretPipelineSettings(testYamlText);
		PSRInstance.checkForDetectableErrors();
	}

	@Test
	public void testPipelineOnlyMinimalBracketsAndSoOn() throws Exception{
		String testYamlText = 
				pipelineSequenceDefKeyword + ":  \n"
				+ "  - " + directValueKeyword + " " + LookupBoardBySerialNumber.nameFlag + ": \n"
				+ "      " + inKeyword + ": \n"
				+ "        boardSerialNumber: direct dummy\n"
				+ "        boardTypeIdentifierFQBN: direct dummy\n"
				+ "      " + outKeyword + ": \n"
				+ "        ifSuccessful: dummyBool\n"
				+ "        foundPortAddress: dummyPort\n"
				+ "  - " + directValueKeyword + " " + DialogMessage.nameFlag + ": \n"
				+ "      " + inKeyword + ": \n"
				+ "        condition: from dummyBool \n"
				+ "        message: from dummyPort\n";
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());
		PSRInstance.interpretPipelineSettings(testYamlText);
		PSRInstance.checkForDetectableErrors();
	}

	@Test
	public void testAllFieldsUsedOnlyMinimalBracketsAndSoOn() throws Exception{
		String testYamlText = 
				  variableDefsKeyword + ": \n"
				+ "- BoardSerialNumber dummySerialVar dummySerial\n"
				+ transformationAndCodeGenerationPreconfigurationsDefKeyword + ": \n"
				+ "  " + ContainerTransformation.nameFlag + ": \n"
				+ "    " + inKeyword + ": \n"
				+ "      roboCar_mumlSourceFile: direct dummyPath1\n"
				+ "      middlewareOption: direct dummySetting\n"
				+ "      muml_containerFileDestination: direct dummyPath2\n"
				+ "    " + outKeyword + ": \n"
				+ "      ifSuccessful: dummyBool\n"
				+ postProcessingSequenceDefKeyword + ": \n" 
				+ "- " + directValueKeyword + " " + DialogMessage.nameFlag + ": \n"
				+ "    " + inKeyword + ": \n"
				+ "      condition: direct true\n"
				+ "      message: direct HelloTest\n"
				+ "\n"
				+ pipelineSequenceDefKeyword + ":  \n"
				+ "  - " + fromKeyword + " " + transformationAndCodeGenerationPreconfigurationsDefKeyword + ": " + ContainerTransformation.nameFlag + "\n"
				+ "  - " + fromKeyword + " " + postProcessingSequenceDefKeyword + ": " + allKeyword + "\n"
				+ "  - " + directValueKeyword + " " + LookupBoardBySerialNumber.nameFlag + ": \n"
				+ "      " + inKeyword + ": \n"
				+ "        boardSerialNumber: from dummySerialVar\n"
				+ "        boardTypeIdentifierFQBN: direct dummy\n"
				+ "      " + outKeyword + ": \n"
				+ "        ifSuccessful: dummyBool\n"
				+ "        foundPortAddress: dummyPort\n"
				+ "  - " + directValueKeyword + " " + DialogMessage.nameFlag + ": \n"
				+ "      " + inKeyword + ": \n"
				+ "        condition: from dummyBool \n"
				+ "        message: from dummyPort\n"
				+ "  - " + fromKeyword + " " + transformationAndCodeGenerationPreconfigurationsDefKeyword + ": " + ContainerTransformation.nameFlag;
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());
		PSRInstance.interpretPipelineSettings(testYamlText);
		PSRInstance.checkForDetectableErrors();
	}
	
	@Test
	public void testEntryCheckTransformationAndCodeGenerationPreconfigurations() throws Exception{
		String testYamlText = 
				  variableDefsKeyword + ": \n"
				+ "- BoardSerialNumber dummySerialVar dummySerial\n"
				+ transformationAndCodeGenerationPreconfigurationsDefKeyword + ": \n"
				+ "  " + ContainerTransformation.nameFlag + ": \n"
				+ "    " + inKeyword + ": \n"
				+ "      roboCar_mumlSourceFile: direct dummyPath1\n"
				+ "      middlewareOption: direct dummySerialVar\n"
				+ "      muml_containerFileDestination: direct dummyPath2\n"
				+ "    " + outKeyword + ": \n"
				+ "      ifSuccessful: dummyBool\n"
				+ postProcessingSequenceDefKeyword + ": \n" 
				+ "- " + directValueKeyword + " " + DialogMessage.nameFlag + ": \n"
				+ "    " + inKeyword + ": \n"
				+ "      condition: direct true\n"
				+ "      message: direct HelloTest\n"
				+ "\n"
				+ pipelineSequenceDefKeyword + ":  \n"
				+ "  - " + fromKeyword + " " + transformationAndCodeGenerationPreconfigurationsDefKeyword + ": " + ContainerTransformation.nameFlag + "\n"
				+ "  - " + fromKeyword + " " + postProcessingSequenceDefKeyword + ": " + allKeyword + "\n"
				+ "  - " + directValueKeyword + " " + LookupBoardBySerialNumber.nameFlag + ": \n"
				+ "      " + inKeyword + ": \n"
				+ "        boardSerialNumber: from dummySerialVar\n"
				+ "        boardTypeIdentifierFQBN: direct dummy\n"
				+ "      " + outKeyword + ": \n"
				+ "        ifSuccessful: dummyBool\n"
				+ "        foundPortAddress: dummyPort\n"
				+ "  - " + directValueKeyword + " " + DialogMessage.nameFlag + ": \n"
				+ "      " + inKeyword + ": \n"
				+ "        condition: from dummyBool \n"
				+ "        message: from dummyPort\n"
				+ "  - " + fromKeyword + " " + transformationAndCodeGenerationPreconfigurationsDefKeyword + ": " + ContainerTransformation.nameFlag;
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());
		PSRInstance.interpretPipelineSettings(testYamlText);
		PSRInstance.checkForDetectableErrors();
		
		assertTrue(PSRInstance.IsEntryInTransformationAndCodeGenerationPreconfigurations(ContainerTransformation.nameFlag));
		assertFalse(PSRInstance.IsEntryInTransformationAndCodeGenerationPreconfigurations(ContainerCodeGeneration.nameFlag));
	}
	

	@Test//(expected = VariableNotDefinedException)
	public void testCheckForMissingVariable() throws Exception{
		String testYamlText = 
				pipelineSequenceDefKeyword + ":  \n"
				+ "  - " + directValueKeyword + " " + LookupBoardBySerialNumber.nameFlag + ": \n"
				+ "      " + inKeyword + ": \n"
				+ "        boardSerialNumber: direct dummy\n"
				+ "        boardTypeIdentifierFQBN: direct dummy\n"
				+ "      " + outKeyword + ": \n"
				+ "        ifSuccessful: dummyBool\n"
				+ "        foundPortAddress: dummyPort\n"
				+ "  - " + directValueKeyword + " " + DialogMessage.nameFlag + ": \n"
				+ "      " + inKeyword + ": \n"
				+ "        condition: from missingBool \n"
				+ "        message: from dummyPort\n";
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());
		PSRInstance.interpretPipelineSettings(testYamlText);
		//VariableNotDefinedException expected.
		try{
			PSRInstance.checkForDetectableErrors(); 
		}
		catch(VariableNotDefinedException e){
			assertTrue(e.getMessage().contains("Variable missingBool not found!"));
			return;
		}
		assertTrue("Missing Variable not detected!", false);
	}

	@Test
	public void testCheckForMissingVariableFromTransformationAndCodeGenerationPreconfigurations() throws Exception{
		String testYamlText = 
				  variableDefsKeyword + ": \n"
				+ "- BoardSerialNumber dummySerialVar dummySerial\n"
				+ transformationAndCodeGenerationPreconfigurationsDefKeyword + ": \n"
				+ "  " + ContainerTransformation.nameFlag + ": \n"
				+ "    " + inKeyword + ": \n"
				+ "      roboCar_mumlSourceFile: direct dummyPath1\n"
				+ "      middlewareOption: direct dummySerialVar\n"
				+ "      muml_containerFileDestination: direct dummyPath2\n"
				+ "    " + outKeyword + ": \n"
				+ "      ifSuccessful: outBoolContainerTransformation\n"
				+ postProcessingSequenceDefKeyword + ": \n" 
				+ "- " + directValueKeyword + " " + DialogMessage.nameFlag + ": \n"
				+ "    " + inKeyword + ": \n"
				+ "      condition: direct true\n"
				+ "      message: direct HelloTest\n"
				+ "\n"
				+ pipelineSequenceDefKeyword + ":  \n"
				// + "  - " + fromKeyword + " " + transformationAndCodeGenerationPreconfigurationsDefKeyword + ": " + ContainerTransformation.nameFlag + "\n"
				+ "  - " + directValueKeyword + " " + DialogMessage.nameFlag + ": \n"
				+ "      " + inKeyword + ": \n"
				+ "        condition: from outBoolContainerTransformation \n"
				+ "        message: from dummyPort\n";
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());
		PSRInstance.interpretPipelineSettings(testYamlText);
		
		//VariableNotDefinedException expected.
		try{
			PSRInstance.checkForDetectableErrors(); 
		}
		catch(VariableNotDefinedException e){
			assertTrue(e.getMessage().contains("Variable outBoolContainerTransformation not found!"));
			return;
		}
		assertTrue("Missing Variable not detected!", false);
	}
	
	@Test
	public void testCheckForMissingVariableFromPostProcessing() throws Exception{
		String testYamlText = 
				  variableDefsKeyword + ": \n"
				+ "- BoardSerialNumber dummySerialVar dummySerial\n"
				+ transformationAndCodeGenerationPreconfigurationsDefKeyword + ": {}\n"
				+ postProcessingSequenceDefKeyword + ": \n" 
				+ "  - " + directValueKeyword + " " + LookupBoardBySerialNumber.nameFlag + ": \n"
				+ "      " + inKeyword + ": \n"
				+ "        boardSerialNumber: from dummySerialVar\n"
				+ "        boardTypeIdentifierFQBN: direct dummy\n"
				+ "      " + outKeyword + ": \n"
				+ "        ifSuccessful: dummyBoolLookupBoardBySerialNumber\n"
				+ "        foundPortAddress: dummyPortLookupBoardBySerialNumber\n"
				+ pipelineSequenceDefKeyword + ":  \n"
				// + "  - " + fromKeyword + " " + postProcessingSequenceDefKeyword + ": " + allKeyword + "\n"
				+ "  - " + directValueKeyword + " " + DialogMessage.nameFlag + ": \n"
				+ "      " + inKeyword + ": \n"
				+ "        condition: from dummyBoolLookupBoardBySerialNumber \n"
				+ "        message: direct dummyPort\n";
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());
		PSRInstance.interpretPipelineSettings(testYamlText);
		
		//VariableNotDefinedException expected.
		try{
			PSRInstance.checkForDetectableErrors(); 
		}
		catch(VariableNotDefinedException e){
			assertTrue(e.getMessage().contains("Variable dummyBoolLookupBoardBySerialNumber not found!"));
			return;
		}
		assertTrue("Missing Variable not detected!", false);
	}
	

	@Test
	public void testCheckForMissingVariableEvenForInputOfTypeAny() throws Exception{
		String testYamlText = 
				  variableDefsKeyword + ": \n"
				+ "- BoardSerialNumber dummySerialVar dummySerial\n"
				+ transformationAndCodeGenerationPreconfigurationsDefKeyword + ": {}\n"
				+ postProcessingSequenceDefKeyword + ": \n" 
				+ "  - " + directValueKeyword + " " + LookupBoardBySerialNumber.nameFlag + ": \n"
				+ "      " + inKeyword + ": \n"
				+ "        boardSerialNumber: from dummySerialVar\n"
				+ "        boardTypeIdentifierFQBN: direct dummy\n"
				+ "      " + outKeyword + ": \n"
				+ "        ifSuccessful: dummyBoolLookupBoardBySerialNumber\n"
				+ "        foundPortAddress: dummyPortLookupBoardBySerialNumber\n"
				+ pipelineSequenceDefKeyword + ":  \n"
				// + "  - " + fromKeyword + " " + postProcessingSequenceDefKeyword + ": " + allKeyword + "\n"
				+ "  - " + directValueKeyword + " " + DialogMessage.nameFlag + ": \n"
				+ "      " + inKeyword + ": \n"
				+ "        condition: direct True \n"
				+ "        message: from dummyPortLookupBoardBySerialNumber\n";
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());
		PSRInstance.interpretPipelineSettings(testYamlText);
		
		//VariableNotDefinedException expected.
		try{
			PSRInstance.checkForDetectableErrors(); 
		}
		catch(VariableNotDefinedException e){
			assertTrue(e.getMessage().contains("Variable dummyPortLookupBoardBySerialNumber not found!"));
			return;
		}
		assertTrue("Missing Variable not detected!", false);
	}
	
	// Loading Steps from TransformationAndCodeGenerationPreconfigurations:
	
	@Test
	public void testCheckForMissingStepFromTransformationAndCodeGenerationPreconfigurations() throws Exception{
		String testYamlText = 
				  variableDefsKeyword + ": []\n"
				+ transformationAndCodeGenerationPreconfigurationsDefKeyword + ": {}\n"
				+ postProcessingSequenceDefKeyword + ": []\n"
				+ pipelineSequenceDefKeyword + ":  \n"
				+ "  - " + fromKeyword + " " + transformationAndCodeGenerationPreconfigurationsDefKeyword + ": " + ContainerTransformation.nameFlag + "\n";
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());
		
		
		try{
			PSRInstance.interpretPipelineSettings(testYamlText);
		}
		catch(StepNotDefinedException e){
			assertTrue(e.getMessage().contains("Missing step from TransformationAndCodeGenerationPreconfigurations: ContainerTransformation"));
			return;
		}
		assertTrue("Missing Step defintion in TransformationAndCodeGenerationPreconfigurations not detected!", false);
	}
	

	@Test
	public void testReportSpaceInInputParameterFrom() throws Exception{
		String testYamlText = 
				  variableDefsKeyword + ": \n"
				+ "- BoardSerialNumber dummySerialVar dummySerial\n"
				+ transformationAndCodeGenerationPreconfigurationsDefKeyword + ": \n"
				+ "  " + ContainerTransformation.nameFlag + ": \n"
				+ "    " + inKeyword + ": \n"
				+ "      roboCar_mumlSourceFile: direct dummyPath1\n"
				+ "      middlewareOption: direct dummySetting\n"
				+ "      muml_containerFileDestination: direct dummyPath2\n"
				+ "    " + outKeyword + ": \n"
				+ "      ifSuccessful: dummyBool\n"
				+ postProcessingSequenceDefKeyword + ": \n" 
				+ "- " + directValueKeyword + " " + DialogMessage.nameFlag + ": \n"
				+ "    " + inKeyword + ": \n"
				+ "      condition: direct true\n"
				+ "      message: direct HelloTest\n"
				+ "\n"
				+ pipelineSequenceDefKeyword + ":  \n"
				+ "  - " + fromKeyword + " " + transformationAndCodeGenerationPreconfigurationsDefKeyword + ": " + ContainerTransformation.nameFlag + "\n"
				+ "  - " + fromKeyword + " " + postProcessingSequenceDefKeyword + ": " + allKeyword + "\n"
				+ "  - " + directValueKeyword + " " + LookupBoardBySerialNumber.nameFlag + ": \n"
				+ "      " + inKeyword + ": \n"
				+ "        boardSerialNumber: from dummy SerialVar\n"
				+ "        boardTypeIdentifierFQBN: direct dummy\n"
				+ "      " + outKeyword + ": \n"
				+ "        ifSuccessful: dummyBool\n"
				+ "        foundPortAddress: dummyPort\n"
				+ "  - " + directValueKeyword + " " + DialogMessage.nameFlag + ": \n"
				+ "      " + inKeyword + ": \n"
				+ "        condition: from dummyBool \n"
				+ "        message: from dummyPort\n"
				+ "  - " + fromKeyword + " " + transformationAndCodeGenerationPreconfigurationsDefKeyword + ": " + ContainerTransformation.nameFlag;
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());

		try{
			PSRInstance.interpretPipelineSettings(testYamlText);
			PSRInstance.checkForDetectableErrors();
		}
		catch(StructureException e){
			assertTrue(e.getMessage().contains("Variable names are not allowed to contain spaces!"));
			return;
		}
		assertTrue("Space in input parameter variable name not detected!", false);
	}
	
	@Test
	public void testReportSpaceInOutpotParameterFrom() throws Exception{
		String testYamlText = 
				  variableDefsKeyword + ": \n"
				+ "- BoardSerialNumber dummySerialVar dummySerial\n"
				+ transformationAndCodeGenerationPreconfigurationsDefKeyword + ": \n"
				+ "  " + ContainerTransformation.nameFlag + ": \n"
				+ "    " + inKeyword + ": \n"
				+ "      roboCar_mumlSourceFile: direct dummyPath1\n"
				+ "      middlewareOption: direct dummySetting\n"
				+ "      muml_containerFileDestination: direct dummyPath2\n"
				+ "    " + outKeyword + ": \n"
				+ "      ifSuccessful: dummyBool\n"
				+ postProcessingSequenceDefKeyword + ": \n" 
				+ "- " + directValueKeyword + " " + DialogMessage.nameFlag + ": \n"
				+ "    " + inKeyword + ": \n"
				+ "      condition: direct true\n"
				+ "      message: direct HelloTest\n"
				+ "\n"
				+ pipelineSequenceDefKeyword + ":  \n"
				+ "  - " + fromKeyword + " " + transformationAndCodeGenerationPreconfigurationsDefKeyword + ": " + ContainerTransformation.nameFlag + "\n"
				+ "  - " + fromKeyword + " " + postProcessingSequenceDefKeyword + ": " + allKeyword + "\n"
				+ "  - " + directValueKeyword + " " + LookupBoardBySerialNumber.nameFlag + ": \n"
				+ "      " + inKeyword + ": \n"
				+ "        boardSerialNumber: from dummySerialVar\n"
				+ "        boardTypeIdentifierFQBN: direct dummy\n"
				+ "      " + outKeyword + ": \n"
				+ "        ifSuccessful: dummyBool\n"
				+ "        foundPortAddress: dummy Port\n"
				+ "  - " + directValueKeyword + " " + DialogMessage.nameFlag + ": \n"
				+ "      " + inKeyword + ": \n"
				+ "        condition: from dummyBool \n"
				+ "        message: from dummyPort\n"
				+ "  - " + fromKeyword + " " + transformationAndCodeGenerationPreconfigurationsDefKeyword + ": " + ContainerTransformation.nameFlag;
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());

		try{
			PSRInstance.interpretPipelineSettings(testYamlText);
			PSRInstance.checkForDetectableErrors();
		}
		catch(StructureException e){
			assertTrue(e.getMessage().contains("Variable names are not allowed to contain spaces!"));
			return;
		}
		assertTrue("Space in input parameter variable name not detected!", false);
	}
	

	@Test
	public void testReportHeavyStructureError() throws Exception{
		String testYamlText = 
				  variableDefsKeyword + ": \n"
				+ "- BoardSerialNumber dummySerialVar dummySerial\n"
				+ transformationAndCodeGenerationPreconfigurationsDefKeyword + ": \n"
				+ "  " + ContainerTransformation.nameFlag + ": \n"
				+ "    " + inKeyword + ": \n"
				+ "      roboCar_mumlSourceFile: direct dummyPath1\n"
				+ "      middlewareOption: direct dummySetting\n"
				+ "      muml_containerFileDestination: direct dummyPath2\n"
				+ "    " + outKeyword + ": \n"
				+ "      ifSuccessful: dummyBool\n"
				+ postProcessingSequenceDefKeyword + ": \n" 
				+ "- " + directValueKeyword + " " + DialogMessage.nameFlag + ": \n"
				+ "    " + inKeyword + ": \n"
				+ "      condition: direct true\n"
				+ "      message: direct HelloTest\n"
				+ "\n"
				+ pipelineSequenceDefKeyword + ":  \n"
				+ "  - " + fromKeyword + " " + transformationAndCodeGenerationPreconfigurationsDefKeyword + ": " + ContainerTransformation.nameFlag + "\n"
				+ "  - '" + fromKeyword + " " + postProcessingSequenceDefKeyword + ": " + allKeyword + "'\n";
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());

		try{
			PSRInstance.interpretPipelineSettings(testYamlText);
			PSRInstance.checkForDetectableErrors();
		}
		catch(StructureException e){
			assertTrue(e.getMessage().contains("Apparently there was a heavy structure error while \n"
					+ "trying to read the PipelineSequence."));
			return;
		}
		assertTrue("Space in input parameter variable name not detected!", false);
	}
}
