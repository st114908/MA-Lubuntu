package de.ust.mumlacgppa.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import de.ust.mumlacgppa.pipeline.parts.exceptions.InOrOutKeyNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ParameterMismatchException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StructureException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.TypeMismatchException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.steps.Keywords;
import de.ust.mumlacgppa.pipeline.parts.steps.PipelineStep;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.TerminalCommand;
import projectfolderpathstorageplugin.ProjectFolderPathNotSetException;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

public class TerminalCommandTest implements Keywords{
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
	public void testExecute()
			throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException,
			IOException, InterruptedException, NoArduinoCLIConfigFileException, FQBNErrorEception,
			ProjectFolderPathNotSetExceptionMUMLACGPPA,ProjectFolderPathNotSetException, AbortPipelineException, InOrOutKeyNotDefinedException, TypeMismatchException {
		String testYamlTextCompleteDefinitionContent =
				inKeyword + ":\n"
				+ "  terminalCommand: direct echo test\n"
				+ "  exitCodeNumberForSuccessfulExecution: direct 0\n"
				+ outKeyword + ":\n"
				+ "  ifSuccessful: ifSuccessful\n"
				+ "  exitCode: exitCode\n"
				+ "  normalFeedback: normalFeedback\n"
				+ "  errorFeedback: errorFeedback\n";
		PipelineStep testInstance = new TerminalCommand(VariableHandlerInstance, testYamlTextCompleteDefinitionContent);
		testInstance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
		testInstance.execute();
		
		assertTrue(VariableHandlerInstance.getVariableValue("ifSuccessful").getBooleanContent());
		assertEquals(0, VariableHandlerInstance.getVariableValue("exitCode").getIntContent());
		assertEquals("test\n", VariableHandlerInstance.getVariableValue("normalFeedback").getContent()); // \n because of how the stream is read.
		assertEquals("", VariableHandlerInstance.getVariableValue("errorFeedback").getContent());
	}

}
