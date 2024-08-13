/**
 * 
 */
package de.ust.mumlacgppa.tests;

import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ust.mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ParameterMismatchException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StepNotMatched;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StructureException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.steps.Keywords;
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
				variableDefsKeyword + ": {}\n"
				+ transformationAndCodeGenerationPreconfigurationsDefKeyword + ": {}\n"
				+ pipelineSequenceDefKeyword + ": []\n";
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());
		PSRInstance.interpretPipelineSettings(testYamlText);
		PSRInstance.validateOrder();
	}


	@Test
	public void testEmptyFileMinimal() throws Exception{
		String testYamlText = "\n";
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());
		PSRInstance.interpretPipelineSettings(testYamlText);
		PSRInstance.validateOrder();
	}

	
	@Test
	public void testVariablesOnly() throws Exception{
		String testYamlText = 
				variableDefsKeyword + ": { "
				+ "dummybool: direct true, "
				+ "dummypathString: direct AttemptedDummyPath"
				+ "}\n";
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());
		PSRInstance.interpretPipelineSettings(testYamlText);
		PSRInstance.validateOrder();
	}

	@Test
	public void testStandaloneUsageDefsOnly() throws Exception{
		String testYamlText = 
				transformationAndCodeGenerationPreconfigurationsDefKeyword + ": { \n"
				+   DialogMessage.nameFlag + ": { \n"
				+     inKeyword + ": {\n"
				+ "     condition: direct true, \n"
				+ "     message: direct HelloTest \n"
				+ "   } \n"
				+ " }\n"
				+ "}\n"
				;
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());
		PSRInstance.interpretPipelineSettings(testYamlText);
		PSRInstance.validateOrder();
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
		PSRInstance.validateOrder();
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
		PSRInstance.validateOrder();
	}

	@Test
	public void testPipelineOnly2() throws Exception{
		String testYamlText = 
				variableDefsKeyword + ": \n  {}\n"
				+ pipelineSequenceDefKeyword + ": \n"
				+ "- " + directValueKeyword + " " + LookupBoardBySerialNumber.nameFlag + ": \n"
				+ "    " + inKeyword + ": \n"
				+ "      boardSerialNumber: direct dummy\n"
				+ "    " + outKeyword + ": \n"
				+ "      ifSuccessful: dummyBool\n"
				+ "      foundPortAddress: dummyPort\n"
				+ "- " + directValueKeyword + " " + DialogMessage.nameFlag + ": \n"
				+ "    " + inKeyword + ": \n"
				+ "      condition: from dummyBool \n"
				+ "      message: from dummyPort\n";
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());
		PSRInstance.interpretPipelineSettings(testYamlText);
		PSRInstance.validateOrder();
	}

	@Test
	public void testPipelineOnlyMinimalBracketsAndSoOn() throws Exception{
		String testYamlText = 
				pipelineSequenceDefKeyword + ":  \n"
				+ "  - " + directValueKeyword + " " + LookupBoardBySerialNumber.nameFlag + ": \n"
				+ "      " + inKeyword + ": \n"
				+ "        boardSerialNumber: direct dummy\n"
				+ "      " + outKeyword + ": \n"
				+ "        ifSuccessful: dummyBool\n"
				+ "        foundPortAddress: dummyPort\n"
				+ "  - " + directValueKeyword + " " + DialogMessage.nameFlag + ": \n"
				+ "      " + inKeyword + ": \n"
				+ "        condition: from dummyBool \n"
				+ "        message: from dummyPort\n";
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());
		PSRInstance.interpretPipelineSettings(testYamlText);
		PSRInstance.validateOrder();
	}

	@Test
	public void testAllFieldsUsedOnlyMinimalBracketsAndSoOn() throws Exception{
		String testYamlText = 
				  variableDefsKeyword + ": \n"
				+ "  dummySerialVar: direct dummySerial\n"
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
		PSRInstance.validateOrder();
	}
	
}
