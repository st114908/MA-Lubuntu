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
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PopupWindowMessage;
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
	public void testEmptyFile() throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, StepNotMatched, ProjectFolderPathNotSetExceptionMUMLACGPPA{
		String testYamlText = 
				variableDefsFlag + ": {}\n"
				+ standaloneUsageDefsFlag + ": {}\n"
				+ pipelineSequenceDefFlag + ": []\n";
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());
		PSRInstance.interpretPipelineSettings(testYamlText);
		PSRInstance.validateOrder();
	}


	@Test
	public void testEmptyFileMinimal() throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, StepNotMatched, ProjectFolderPathNotSetExceptionMUMLACGPPA{
		String testYamlText = "\n";
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());
		PSRInstance.interpretPipelineSettings(testYamlText);
		PSRInstance.validateOrder();
	}

	
	@Test
	public void testVariablesOnly() throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, StepNotMatched, ProjectFolderPathNotSetExceptionMUMLACGPPA{
		String testYamlText = 
				variableDefsFlag + ": { "
				+ "dummybool: direct true, "
				+ "dummypathString: direct AttemptedDummyPath"
				+ "}\n"
				//+ standaloneUsageDefsFlag + ": {}\n"
				//+ pipelineSequenceDefFlag + ": []\n"
				;
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());
		PSRInstance.interpretPipelineSettings(testYamlText);
		PSRInstance.validateOrder();
	}

	@Test
	public void testStandaloneUsageDefsOnly() throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, StepNotMatched, ProjectFolderPathNotSetExceptionMUMLACGPPA{
		String testYamlText = 
				//variableDefsFlag + ": {}\n"
				//+ 
				standaloneUsageDefsFlag + ": { \n"
				+   PopupWindowMessage.nameFlag + ": { \n"
				+     inFlag + ": {\n"
				+ "     condition: direct true, \n"
				+ "     message: direct HelloTest \n"
				+ "   } \n"
				+ " }\n"
				+ "}\n"
				//+ pipelineSequenceDefFlag + ": []\n"
				;
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());
		PSRInstance.interpretPipelineSettings(testYamlText);
		PSRInstance.validateOrder();
	}
	
	@Test
	public void testPipelineOnly() throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, StepNotMatched, ProjectFolderPathNotSetExceptionMUMLACGPPA{
		String testYamlText = 
				//variableDefsFlag + ": {}\n"
				//+ standaloneUsageDefsFlag + ": {}\n"
				//+ 
				pipelineSequenceDefFlag + ": \n" 
				+ "- " + directValueFlag + " " + PopupWindowMessage.nameFlag + ": \n"
				+ "    " + inFlag + ": \n"
				+ "      condition: direct true\n"
				+ "      message: direct HelloTest\n"
				+ "\n";
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());
		PSRInstance.interpretPipelineSettings(testYamlText);
		PSRInstance.validateOrder();
	}

	@Test
	public void testPipelineOnly2() throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, StepNotMatched, ProjectFolderPathNotSetExceptionMUMLACGPPA{
		String testYamlText = 
				//variableDefsFlag + ": {}\n"
				//+ standaloneUsageDefsFlag + ": {}\n"
				//+
				pipelineSequenceDefFlag + ": \n"
				+ "- " + directValueFlag + " " + LookupBoardBySerialNumber.nameFlag + ": \n"
				+ "    " + inFlag + ": \n"
				+ "      boardSerialNumber: direct dummy\n"
				+ "    " + outFlag + ": \n"
				+ "      ifSuccessful: dummyBool\n"
				+ "      portAddress: dummyPort\n"
				+ "- " + directValueFlag + " " + PopupWindowMessage.nameFlag + ": \n"
				+ "    " + inFlag + ": \n"
				+ "      condition: from dummyBool \n"
				+ "      message: from dummyPort\n";
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());
		PSRInstance.interpretPipelineSettings(testYamlText);
		PSRInstance.validateOrder();
	}

	@Test
	public void testPipelineOnlyMinimalBracketsAndSoOn() throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, StepNotMatched, ProjectFolderPathNotSetExceptionMUMLACGPPA{
		String testYamlText = 
				pipelineSequenceDefFlag + ":  \n"
				+ "  - " + directValueFlag + " " + LookupBoardBySerialNumber.nameFlag + ": \n"
				+ "      " + inFlag + ": \n"
				+ "        boardSerialNumber: direct dummy\n"
				+ "      " + outFlag + ": \n"
				+ "        ifSuccessful: dummyBool\n"
				+ "        portAddress: dummyPort\n"
				+ "  - " + directValueFlag + " " + PopupWindowMessage.nameFlag + ": \n"
				+ "      " + inFlag + ": \n"
				+ "        condition: from dummyBool \n"
				+ "        message: from dummyPort\n";
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());
		PSRInstance.interpretPipelineSettings(testYamlText);
		PSRInstance.validateOrder();
	}

	@Test
	public void testAllFieldsUsedOnlyMinimalBracketsAndSoOn() throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, StepNotMatched, ProjectFolderPathNotSetExceptionMUMLACGPPA{
		String testYamlText = 
				  variableDefsFlag + ": \n"
				+ "  dummySerialVar: direct dummySerial\n"
				+ standaloneUsageDefsFlag + ": \n"
				+ "  " + ContainerTransformation.nameFlag + ": \n"
				+ "    " + inFlag + ": \n"
				+ "      roboCar_mumlSourceFile: direct dummyPath1\n"
				+ "      middlewareOption: direct dummySerialVar\n"
				+ "      muml_containerFileDestination: direct dummyPath2\n"
				+ "    " + outFlag + ": \n"
				+ "      ifSuccessful: dummyBool\n"
				+ pipelineSequenceDefFlag + ":  \n"
				+ "  - " + directValueFlag + " " + LookupBoardBySerialNumber.nameFlag + ": \n"
				+ "      " + inFlag + ": \n"
				+ "        boardSerialNumber: from dummySerialVar\n"
				+ "      " + outFlag + ": \n"
				+ "        ifSuccessful: dummyBool\n"
				+ "        portAddress: dummyPort\n"
				+ "  - " + directValueFlag + " " + PopupWindowMessage.nameFlag + ": \n"
				+ "      " + inFlag + ": \n"
				+ "        condition: from dummyBool \n"
				+ "        message: from dummyPort\n"
				+ "  - " + fromValueFlag + " " + standaloneUsageDefsFlag + ": " + ContainerTransformation.nameFlag;
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());
		PSRInstance.interpretPipelineSettings(testYamlText);
		PSRInstance.validateOrder();
	}
	
}