package de.ust.mumlacgppa.tests;

import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ust.mumlacgppa.pipeline.parts.exceptions.TypeMismatchException;
import de.ust.mumlacgppa.pipeline.parts.steps.Keywords;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.DialogMessage;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.LookupBoardBySerialNumber;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer;
import de.ust.mumlacgppa.pipeline.reader.PipelineSettingsReader;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

public class TypeCheckTest implements Keywords{
	InternalTestVariableHandlerClearStorageAccess VariableHandlerInstance;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ProjectFolderPathStorage.projectFolderPath = Paths.get("/home/muml/MUMLProjects/Overtaking-Cars-Baumfalk");
	}

	@Before
	public void setUp() throws Exception {
		VariableHandlerInstance = new InternalTestVariableHandlerClearStorageAccess();
	}

	@After
	public void tearDown() throws Exception {
		VariableHandlerInstance.clearVariableHandlerStorage();
	}

	
	@Test
	public void testTypeCheckNoAnyType() throws Exception{
		String testYamlText = 
				  variableDefsKeyword + ": \n"
				+ "- BoardSerialNumber dummySerialVar dummySerial\n"
				+ transformationAndCodeGenerationPreconfigurationsDefKeyword + ": {}\n"
				+ pipelineSequenceDefKeyword + ":  \n"
				+ "  - " + directValueKeyword + " " + LookupBoardBySerialNumber.nameFlag + ": \n"
				+ "      " + inKeyword + ": \n"
				+ "        boardSerialNumber: from dummySerialVar\n"
				+ "        boardTypeIdentifierFQBN: direct dummy\n"
				+ "      " + outKeyword + ": \n"
				+ "        ifSuccessful: dummyBool\n"
				+ "        foundPortAddress: dummyPort\n";
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());
		PSRInstance.interpretPipelineSettings(testYamlText);
		PSRInstance.checkForDetectableErrors();
	}

	@Test
	public void testTypeCheckAnyTypeReceivesManualDefinedVariable() throws Exception{
		String testYamlText = 
				  variableDefsKeyword + ": \n"
				+ "- BoardSerialNumber dummySerialVar dummySerial\n"
				+ transformationAndCodeGenerationPreconfigurationsDefKeyword + ": {}\n"
				+ pipelineSequenceDefKeyword + ":  \n"
				+ "  - " + directValueKeyword + " " + DialogMessage.nameFlag + ": \n"
				+ "      " + inKeyword + ": \n"
				+ "        condition: direct True\n"
				+ "        message: from dummySerialVar\n";
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());
		PSRInstance.interpretPipelineSettings(testYamlText);
		PSRInstance.checkForDetectableErrors();
	}


	@Test
	public void testCheckTypeChangePrevention() throws Exception{
		String testYamlText = 
				  variableDefsKeyword + ": \n"
				+ "- BoardSerialNumber dummySerialVar dummySerial\n"
				+ transformationAndCodeGenerationPreconfigurationsDefKeyword + ": {}\n"
				+ pipelineSequenceDefKeyword + ":  \n"
				+ "  - " + directValueKeyword + " " + LookupBoardBySerialNumber.nameFlag + ": \n"
				+ "      " + inKeyword + ": \n"
				+ "        boardSerialNumber: from dummySerialVar\n"
				+ "        boardTypeIdentifierFQBN: direct dummy\n"
				+ "      " + outKeyword + ": \n"
				+ "        ifSuccessful: dummyOut\n"
				+ "        foundPortAddress: dummyOut\n";
		
		PipelineSettingsReader PSRInstance = new PipelineSettingsReader(new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer());
		PSRInstance.interpretPipelineSettings(testYamlText);
		
		try{
			PSRInstance.checkForDetectableErrors();
		}
		catch(TypeMismatchException e){
			assertTrue(e.getMessage().contains("Attempted type change detected: Already existing definition has type Boolean, current output parameter has ConnectionPort."));
			return;
		}
		assertTrue("Missing Step defintion in TransformationAndCodeGenerationPreconfigurations not detected!", false);
	}
}
