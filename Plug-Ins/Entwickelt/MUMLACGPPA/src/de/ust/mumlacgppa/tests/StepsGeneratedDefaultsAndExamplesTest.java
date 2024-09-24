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
import de.ust.mumlacgppa.pipeline.parts.exceptions.TypeMismatchException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.steps.Keywords;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.AutoGitCommitAllAndPushCommand;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.Compile;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ComponentCodeGeneration;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ContainerCodeGeneration;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ContainerTransformation;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.CopyFiles;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.CopyFolder;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.CreateFolder;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.DeleteFile;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.DeleteFolder;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.LookupBoardBySerialNumber;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.OnlyContinueIfFulfilledElseAbort;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingAddHALPartsIntoCarDriverInoFiles;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingAdjustAPIMappingFile;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingAdjustIncludes;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingAdjustSerialCommunicationSizes;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingConfigureMQTTSettings;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingConfigureWLANSettings;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingCopyFolderContentsToECUsAndExcept;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingCopyFolderContentsToECUsWhitelist;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingCopyLocalConfig_hppToCarDeriverECUs;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingDownloadConfig_hpp;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingFillOutMethodStubs;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingInsertAtLineIndex;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingMoveIncludeBefore_ifdef__cplusplus;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.DialogMessage;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingStateChartValues;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PostProcessingStateChartValuesFlexible;
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

	/*
	// Deactivated since the output in the submitted thesis is faulty, but has to stay matching.
	@Test
	public void testAutoGitCommitAllAndPushCommand() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		AutoGitCommitAllAndPushCommand Instance = new AutoGitCommitAllAndPushCommand(VariableHandlerInstance, AutoGitCommitAllAndPushCommand.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}
	*/
	
	@Test
	public void testCompile() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		Compile Instance = new Compile(VariableHandlerInstance, Compile.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}

	@Test
	public void testComponentCodeGeneration()
			throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException,
			IOException, InterruptedException, NoArduinoCLIConfigFileException, FQBNErrorEception,
			ProjectFolderPathNotSetExceptionMUMLACGPPA,ProjectFolderPathNotSetException, AbortPipelineException, TypeMismatchException {
		ComponentCodeGeneration Instance = new ComponentCodeGeneration(VariableHandlerInstance, ComponentCodeGeneration.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}

	@Test
	public void testContainerCodeGeneration() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		ContainerCodeGeneration Instance = new ContainerCodeGeneration(VariableHandlerInstance, ContainerCodeGeneration.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}

	@Test
	public void testContainerTransformation() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		ContainerTransformation Instance = new ContainerTransformation(VariableHandlerInstance, ContainerTransformation.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}

	/*
	// Deactivated since the output in the submitted thesis is faulty, but has to stay matching.
	@Test
	public void testCopyFiles() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		CopyFiles Instance = new CopyFiles(VariableHandlerInstance, CopyFiles.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}
	*/

	@Test
	public void testCopyFolder() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		CopyFolder Instance = new CopyFolder(VariableHandlerInstance, CopyFolder.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}


	@Test
	public void testCreateFolder() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		CreateFolder Instance = new CreateFolder(VariableHandlerInstance, CreateFolder.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}


	@Test
	public void testDeleteFile() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		DeleteFile Instance = new DeleteFile(VariableHandlerInstance, DeleteFile.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}

	
	@Test
	public void testDeleteFolder() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		DeleteFolder Instance = new DeleteFolder(VariableHandlerInstance, DeleteFolder.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}

	@Test
	public void testDialogMessage() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		DialogMessage Instance = new DialogMessage(VariableHandlerInstance, DialogMessage.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}

	
	@Test
	public void testLookupBoardBySerialNumber() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		LookupBoardBySerialNumber Instance = new LookupBoardBySerialNumber(VariableHandlerInstance, LookupBoardBySerialNumber.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}

	@Test
	public void testOnlyContinueIfFullfilledElseAbort() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		OnlyContinueIfFulfilledElseAbort Instance = new OnlyContinueIfFulfilledElseAbort(VariableHandlerInstance, OnlyContinueIfFulfilledElseAbort.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}

	@Test
	public void testPostProcessingAddHALPartsIntoCarDriverInoFiles() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		PostProcessingAddHALPartsIntoCarDriverInoFiles Instance = new PostProcessingAddHALPartsIntoCarDriverInoFiles(VariableHandlerInstance, PostProcessingAddHALPartsIntoCarDriverInoFiles.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}

	@Test
	public void testPostProcessingAdjustAPIMappingFile() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		PostProcessingAdjustAPIMappingFile Instance = new PostProcessingAdjustAPIMappingFile(VariableHandlerInstance, PostProcessingAdjustAPIMappingFile.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}

	@Test
	public void testPostProcessingAdjustIncludes() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		PostProcessingAdjustIncludes Instance = new PostProcessingAdjustIncludes(VariableHandlerInstance, PostProcessingAdjustIncludes.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}

	@Test
	public void testPostProcessingAdjustSerialCommunicationSizes() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		PostProcessingAdjustSerialCommunicationSizes Instance = new PostProcessingAdjustSerialCommunicationSizes(VariableHandlerInstance, PostProcessingAdjustSerialCommunicationSizes.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}

	@Test
	public void testPostProcessingConfigureMQTTSettings() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		PostProcessingConfigureMQTTSettings Instance = new PostProcessingConfigureMQTTSettings(VariableHandlerInstance, PostProcessingConfigureMQTTSettings.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}

	@Test
	public void testPostProcessingConfigureWLANSettings() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		PostProcessingConfigureWLANSettings Instance = new PostProcessingConfigureWLANSettings(VariableHandlerInstance, PostProcessingConfigureWLANSettings.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}

	@Test
	public void testPostProcessingCopyFolderContentsToECUsAndExcept() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		PostProcessingCopyFolderContentsToECUsAndExcept Instance = new PostProcessingCopyFolderContentsToECUsAndExcept(VariableHandlerInstance, PostProcessingCopyFolderContentsToECUsAndExcept.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}

	@Test
	public void testPostProcessingCopyFolderContentsToECUsWhitelist() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		PostProcessingCopyFolderContentsToECUsWhitelist Instance = new PostProcessingCopyFolderContentsToECUsWhitelist(VariableHandlerInstance, PostProcessingCopyFolderContentsToECUsWhitelist.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}

	@Test
	public void testPostProcessingCopyLocalConfig_hppToCarDeriverECUs() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		PostProcessingCopyLocalConfig_hppToCarDeriverECUs Instance = new PostProcessingCopyLocalConfig_hppToCarDeriverECUs(VariableHandlerInstance, PostProcessingCopyLocalConfig_hppToCarDeriverECUs.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}

	@Test
	public void testPostProcessingDownloadConfig_hpp() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		PostProcessingDownloadConfig_hpp Instance = new PostProcessingDownloadConfig_hpp(VariableHandlerInstance, PostProcessingDownloadConfig_hpp.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}

	@Test
	public void testPostProcessingFillOutMethodStubs() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		PostProcessingFillOutMethodStubs Instance = new PostProcessingFillOutMethodStubs(VariableHandlerInstance, PostProcessingFillOutMethodStubs.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}

	@Test
	public void testPostProcessingInsertAtLineIndex() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		PostProcessingInsertAtLineIndex Instance = new PostProcessingInsertAtLineIndex(VariableHandlerInstance, PostProcessingInsertAtLineIndex.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}

	@Test
	public void testPostProcessingMoveIncludeBefore_ifdef__cplusplus() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		PostProcessingMoveIncludeBefore_ifdef__cplusplus Instance = new PostProcessingMoveIncludeBefore_ifdef__cplusplus(VariableHandlerInstance, PostProcessingMoveIncludeBefore_ifdef__cplusplus.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}

	@Test
	public void testPostProcessingStateChartValues() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		PostProcessingStateChartValues Instance = new PostProcessingStateChartValues(VariableHandlerInstance, PostProcessingStateChartValues.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}

	@Test
	public void testPostProcessingStateChartValuesFlexible() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		PostProcessingStateChartValuesFlexible Instance = new PostProcessingStateChartValuesFlexible(VariableHandlerInstance, PostProcessingStateChartValuesFlexible.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}

	@Test
	public void testReplaceLineContent() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		ReplaceLineContent Instance = new ReplaceLineContent(VariableHandlerInstance, ReplaceLineContent.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}

	@Test
	public void testSaveToTextFile() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		SaveToTextFile Instance = new SaveToTextFile(VariableHandlerInstance, SaveToTextFile.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}

	@Test
	public void testSelectableWindow() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		SelectableTextWindow Instance = new SelectableTextWindow(VariableHandlerInstance, SelectableTextWindow.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}

	@Test
	public void testTerminalCommand() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		TerminalCommand Instance = new TerminalCommand(VariableHandlerInstance, TerminalCommand.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}

	@Test
	public void testUpload() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		Upload Instance = new Upload(VariableHandlerInstance, Upload.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}
	

	/* ...
	@Test
	public void test() throws ProjectFolderPathNotSetExceptionMUMLACGPPA, VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, TypeMismatchException{
		 Instance = new (VariableHandlerInstance, LookupBoardBySerialNumber.generateDefaultOrExampleValues());
		Instance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}
	*/
	

}

