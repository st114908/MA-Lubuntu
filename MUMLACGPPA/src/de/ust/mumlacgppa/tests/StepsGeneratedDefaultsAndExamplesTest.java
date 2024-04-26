package de.ust.mumlacgppa.tests;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.ust.arduinocliutilizer.worksteps.exceptions.FQBNErrorEception;
import de.ust.arduinocliutilizer.worksteps.exceptions.NoArduinoCLIConfigFileException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.AbortPipelineException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ParameterMismatchException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StructureException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.steps.Keywords;
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
import projectfolderpathstorageplugin.ProjectFolderPathNotSetException;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

public class StepsGeneratedDefaultsAndExamplesTest implements Keywords{
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
	public void testCompile() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException{
		Compile Instance = new Compile(VariableHandlerInstance, Compile.generateDefaultOrExampleValues());
		Instance.checkForDetectableErrors();
	}

	@Test
	public void testComponentCodeGeneration()
			throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException,
			IOException, InterruptedException, NoArduinoCLIConfigFileException, FQBNErrorEception,
			ProjectFolderPathNotSetExceptionMUMLACGPPA,ProjectFolderPathNotSetException, AbortPipelineException {
		ComponentCodeGeneration Instance = new ComponentCodeGeneration(VariableHandlerInstance, ComponentCodeGeneration.generateDefaultOrExampleValues());
		Instance.checkForDetectableErrors();
	}

	@Test
	public void testContainerCodeGeneration() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException{
		ContainerCodeGeneration Instance = new ContainerCodeGeneration(VariableHandlerInstance, ContainerCodeGeneration.generateDefaultOrExampleValues());
		Instance.checkForDetectableErrors();
	}

	@Test
	public void testContainerTransformation() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException{
		ContainerTransformation Instance = new ContainerTransformation(VariableHandlerInstance, ContainerTransformation.generateDefaultOrExampleValues());
		Instance.checkForDetectableErrors();
	}

	@Test
	public void testLookupBoardBySerialNumber() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException{
		LookupBoardBySerialNumber Instance = new LookupBoardBySerialNumber(VariableHandlerInstance, LookupBoardBySerialNumber.generateDefaultOrExampleValues());
		Instance.checkForDetectableErrors();
	}

	@Test
	public void testOnlyContinueIfFullfilledElseAbort() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException{
		OnlyContinueIfFulfilledElseAbort Instance = new OnlyContinueIfFulfilledElseAbort(VariableHandlerInstance, OnlyContinueIfFulfilledElseAbort.generateDefaultOrExampleValues());
		Instance.checkForDetectableErrors();
	}

	@Test
	public void testPopupWindowMessage() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException{
		PopupWindowMessage Instance = new PopupWindowMessage(VariableHandlerInstance, PopupWindowMessage.generateDefaultOrExampleValues());
		Instance.checkForDetectableErrors();
	}

	@Test
	public void testPostProcessingStateChartValues() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException{
		PostProcessingStateChartValues Instance = new PostProcessingStateChartValues(VariableHandlerInstance, PostProcessingStateChartValues.generateDefaultOrExampleValues());
		Instance.checkForDetectableErrors();
	}

	@Test
	public void testPostProcessingStateChartValuesFlexible() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException{
		PostProcessingStateChartValuesFlexible Instance = new PostProcessingStateChartValuesFlexible(VariableHandlerInstance, PostProcessingStateChartValuesFlexible.generateDefaultOrExampleValues());
		Instance.checkForDetectableErrors();
	}

	@Test
	public void testPostProcessingStepsUntilConfig() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException{
		PostProcessingStepsUntilConfig Instance = new PostProcessingStepsUntilConfig(VariableHandlerInstance, PostProcessingStepsUntilConfig.generateDefaultOrExampleValues());
		Instance.checkForDetectableErrors();
	}

	@Test
	public void testReplaceLineContent() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException{
		ReplaceLineContent Instance = new ReplaceLineContent(VariableHandlerInstance, ReplaceLineContent.generateDefaultOrExampleValues());
		Instance.checkForDetectableErrors();
	}

	@Test
	public void testSaveToTextFile() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException{
		SaveToTextFile Instance = new SaveToTextFile(VariableHandlerInstance, SaveToTextFile.generateDefaultOrExampleValues());
		Instance.checkForDetectableErrors();
	}

	@Test
	public void testSelectableWindow() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException{
		SelectableTextWindow Instance = new SelectableTextWindow(VariableHandlerInstance, SelectableTextWindow.generateDefaultOrExampleValues());
		Instance.checkForDetectableErrors();
	}

	@Test
	public void testTerminalCommand() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException{
		TerminalCommand Instance = new TerminalCommand(VariableHandlerInstance, TerminalCommand.generateDefaultOrExampleValues());
		Instance.checkForDetectableErrors();
	}

	@Test
	public void testUpload() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException{
		Upload Instance = new Upload(VariableHandlerInstance, Upload.generateDefaultOrExampleValues());
		Instance.checkForDetectableErrors();
	}

}
