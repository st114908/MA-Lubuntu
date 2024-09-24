package de.ust.mumlacgppa.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import de.ust.mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.InOrOutKeyNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ParameterMismatchException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StructureException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.TypeMismatchException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.steps.Keywords;
import de.ust.mumlacgppa.pipeline.parts.storage.VariableContent;
import de.ust.mumlacgppa.pipeline.parts.storage.VariableTypes;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

//TODO: Trigger Exceptions intentionally!

public class StepsInAndOutTest implements Keywords, VariableTypes{
	InternalTestVariableHandlerClearStorageAccess VariableHandlerInstance;
	private static Yaml yaml;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ProjectFolderPathStorage.projectFolderPath = Paths.get("/home/muml/MUMLProjects/Overtaking-Cars-Baumfalk");
	}

	@BeforeClass
	public static void setUpClass() {
		yaml = new Yaml();
	}

	@Before
	public void setUp() {
		VariableHandlerInstance = new InternalTestVariableHandlerClearStorageAccess();
		
		VariableHandlerInstance.setVariableAsInitializedForValidation("testTrueVariable", BooleanType);
		VariableHandlerInstance.setVariableValue("testTrueVariable", new VariableContent("true"));
		
		VariableHandlerInstance.setVariableAsInitializedForValidation("testFalseVariable", BooleanType);
		VariableHandlerInstance.setVariableValue("testFalseVariable", new VariableContent("false"));

		VariableHandlerInstance.setVariableAsInitializedForValidation("testNumberVariable", NumberType);
		VariableHandlerInstance.setVariableValue("testNumberVariable", new VariableContent("1"));

		VariableHandlerInstance.setVariableAsInitializedForValidation("testTextVariable", StringType);
		VariableHandlerInstance.setVariableValue("testTextVariable", new VariableContent("HelloWorld"));

		VariableHandlerInstance.setVariableAsInitializedForValidation("testTextVariable2", StringType);
		VariableHandlerInstance.setVariableValue("testTextVariable2", new VariableContent("HelloWorld2"));

	}

	@After
	public void tearDown() {
		VariableHandlerInstance.clearVariableHandlerStorage();
	}

	// In:

	@Test
	public void testConstructorVariantsEqualIn()
			throws VariableNotDefinedException, StructureException, ProjectFolderPathNotSetExceptionMUMLACGPPA {
		String testYamlTextDirectTrue = inKeyword + ":\n" + "  input1: direct true\n";
		Map<String, Map<String, String>> testMapDirectTrue = yaml.load(testYamlTextDirectTrue);
		testMapDirectTrue.put(outKeyword, new HashMap<String, String>());

		InternalTestDummyStep testInstance1 = new InternalTestDummyStep(VariableHandlerInstance,
				testYamlTextDirectTrue);
		InternalTestDummyStep testInstance2 = new InternalTestDummyStep(VariableHandlerInstance, testMapDirectTrue);
		assertEquals(testInstance2.getIn(), testInstance1.getIn());
		assertEquals(testInstance2.getOut(), testInstance1.getOut());

	}

	// Direct:

	@Test
	public void testInDirectTrue() throws VariableNotDefinedException, StructureException,
			ProjectFolderPathNotSetExceptionMUMLACGPPA, InOrOutKeyNotDefinedException, FaultyDataException {
		String testYamlTextDirectTrue = inKeyword + ":\n" + "  input1: direct true\n";
		Map<String, Map<String, String>> testMapDirectTrue = yaml.load(testYamlTextDirectTrue);
		testMapDirectTrue.put(outKeyword, new HashMap<String, String>());

		InternalTestDummyStep testInstance = new InternalTestDummyStep(VariableHandlerInstance, testYamlTextDirectTrue);
		assertEquals(testMapDirectTrue.get(inKeyword), testInstance.getIn());
		assertEquals(testMapDirectTrue.get(outKeyword), testInstance.getOut());

		assertEquals("true", testInstance.handleInputTestFunction("input1").getContent());
	}

	@Test
	public void testInDirectFalse() throws VariableNotDefinedException, StructureException,
			ProjectFolderPathNotSetExceptionMUMLACGPPA, InOrOutKeyNotDefinedException, FaultyDataException {
		String testYamlTextDirectFalse = inKeyword + ":\n" + "  input1: direct false\n";
		Map<String, Map<String, String>> testMapDirectFalse = yaml.load(testYamlTextDirectFalse);
		testMapDirectFalse.put(outKeyword, new HashMap<String, String>());

		InternalTestDummyStep testInstance = new InternalTestDummyStep(VariableHandlerInstance,
				testYamlTextDirectFalse);
		assertEquals(testMapDirectFalse.get(inKeyword), testInstance.getIn());
		assertEquals(testMapDirectFalse.get(outKeyword), testInstance.getOut());

		assertEquals("false", testInstance.handleInputTestFunction("input1").getContent());
	}

	@Test
	public void testInInvertedDirectTrue() throws VariableNotDefinedException, StructureException, FaultyDataException,
			ParameterMismatchException, ProjectFolderPathNotSetExceptionMUMLACGPPA, InOrOutKeyNotDefinedException {
		String testYamlTextInvertedDirectTrue = inKeyword + ":\n" + "  input1: not direct true\n";
		Map<String, Map<String, String>> testMapInvertedDirectTrue = yaml.load(testYamlTextInvertedDirectTrue);
		testMapInvertedDirectTrue.put(outKeyword, new HashMap<String, String>());

		InternalTestDummyStep testInstance = new InternalTestDummyStep(VariableHandlerInstance,
				testYamlTextInvertedDirectTrue);
		assertEquals(testMapInvertedDirectTrue.get(inKeyword), testInstance.getIn());
		assertEquals(testMapInvertedDirectTrue.get(outKeyword), testInstance.getOut());

		assertEquals("false", testInstance.handleInputTestFunction("input1").getContent());
	}

	@Test
	public void testInInvertedDirectFalse() throws VariableNotDefinedException, StructureException,
			ProjectFolderPathNotSetExceptionMUMLACGPPA, InOrOutKeyNotDefinedException, FaultyDataException {
		String testYamlTextInvertedDirectFalse = inKeyword + ":\n" + "  input1: not direct false\n";
		Map<String, Map<String, String>> testMapInvertedDirectFalse = yaml.load(testYamlTextInvertedDirectFalse);
		testMapInvertedDirectFalse.put(outKeyword, new HashMap<String, String>());

		InternalTestDummyStep testInstance = new InternalTestDummyStep(VariableHandlerInstance,
				testYamlTextInvertedDirectFalse);
		assertEquals(testMapInvertedDirectFalse.get(inKeyword), testInstance.getIn());
		assertEquals(testMapInvertedDirectFalse.get(outKeyword), testInstance.getOut());

		assertEquals("true", testInstance.handleInputTestFunction("input1").getContent());
	}

	@Test
	public void testInDirectNumber() throws VariableNotDefinedException, StructureException,
			ProjectFolderPathNotSetExceptionMUMLACGPPA, InOrOutKeyNotDefinedException, FaultyDataException {
		String testYamlTextDirectNumber = inKeyword + ":\n" + "  numberTest: direct 1\n";
		Map<String, Map<String, String>> testMapDirectNumber = yaml.load(testYamlTextDirectNumber);
		testMapDirectNumber.put(outKeyword, new HashMap<String, String>());

		InternalTestDummyStep testInstance = new InternalTestDummyStep(VariableHandlerInstance,
				testYamlTextDirectNumber);
		assertEquals(testMapDirectNumber.get(inKeyword), testInstance.getIn());
		assertEquals(testMapDirectNumber.get(outKeyword), testInstance.getOut());

		assertEquals("1", testInstance.handleInputTestFunction("numberTest").getContent());
	}

	@Test
	public void testInDirectString() throws VariableNotDefinedException, StructureException,
			ProjectFolderPathNotSetExceptionMUMLACGPPA, InOrOutKeyNotDefinedException, FaultyDataException {
		String testYamlTextDirectString = inKeyword + ":\n" + "  stringTest: direct HelloWorld\n";
		Map<String, Map<String, String>> testMapDirectString = yaml.load(testYamlTextDirectString);
		testMapDirectString.put(outKeyword, new HashMap<String, String>());

		InternalTestDummyStep testInstance = new InternalTestDummyStep(VariableHandlerInstance,
				testYamlTextDirectString);
		assertEquals(testMapDirectString.get(inKeyword), testInstance.getIn());
		assertEquals(testMapDirectString.get(outKeyword), testInstance.getOut());

		assertEquals("HelloWorld", testInstance.handleInputTestFunction("stringTest").getContent());
	}

	// From:

	@Test
	public void testInFromTrueVariable() throws VariableNotDefinedException, StructureException,
			ProjectFolderPathNotSetExceptionMUMLACGPPA, InOrOutKeyNotDefinedException, FaultyDataException {
		String testYamlTextFromTrueVariable = inKeyword + ":\n" + "  input1: from testTrueVariable\n";
		Map<String, Map<String, String>> testMapFromTrueVariable = yaml.load(testYamlTextFromTrueVariable);
		testMapFromTrueVariable.put(outKeyword, new HashMap<String, String>());

		InternalTestDummyStep testInstance = new InternalTestDummyStep(VariableHandlerInstance,
				testYamlTextFromTrueVariable);
		assertEquals(testMapFromTrueVariable.get(inKeyword), testInstance.getIn());
		assertEquals(testMapFromTrueVariable.get(outKeyword), testInstance.getOut());

		assertEquals("true", testInstance.handleInputTestFunction("input1").getContent());
	}

	@Test
	public void testInFromFalseVariable() throws VariableNotDefinedException, StructureException,
			ProjectFolderPathNotSetExceptionMUMLACGPPA, InOrOutKeyNotDefinedException, FaultyDataException {
		String testYamlTextFromFalseVariable = inKeyword + ":\n" + "  input1: from testFalseVariable\n";
		Map<String, Map<String, String>> testMapFromFalseVariable = yaml.load(testYamlTextFromFalseVariable);
		testMapFromFalseVariable.put(outKeyword, new HashMap<String, String>());

		InternalTestDummyStep testInstance = new InternalTestDummyStep(VariableHandlerInstance,
				testYamlTextFromFalseVariable);
		assertEquals(testMapFromFalseVariable.get(inKeyword), testInstance.getIn());
		assertEquals(testMapFromFalseVariable.get(outKeyword), testInstance.getOut());

		assertEquals("false", testInstance.handleInputTestFunction("input1").getContent());
	}

	@Test
	public void testStringBasedConstructorInvertedFromTrueVariable() throws VariableNotDefinedException,
			StructureException, ProjectFolderPathNotSetExceptionMUMLACGPPA, InOrOutKeyNotDefinedException, FaultyDataException {
		String testYamlTextInvertedFromTrueVariable = inKeyword + ":\n" + "  input1: not from testTrueVariable\n";
		Map<String, Map<String, String>> testMapInvertedFromTrueVariable = yaml
				.load(testYamlTextInvertedFromTrueVariable);
		testMapInvertedFromTrueVariable.put(outKeyword, new HashMap<String, String>());

		InternalTestDummyStep testInstance = new InternalTestDummyStep(VariableHandlerInstance,
				testYamlTextInvertedFromTrueVariable);
		assertEquals(testMapInvertedFromTrueVariable.get(inKeyword), testInstance.getIn());
		assertEquals(testMapInvertedFromTrueVariable.get(outKeyword), testInstance.getOut());

		assertEquals("false", testInstance.handleInputTestFunction("input1").getContent());
	}

	@Test
	public void testStringBasedConstructorInvertedFromFalseVariable() throws VariableNotDefinedException,
			StructureException, ProjectFolderPathNotSetExceptionMUMLACGPPA, InOrOutKeyNotDefinedException, FaultyDataException {
		String testYamlTextInvertedFromFalseVariable = inKeyword + ":\n" + "  input1: not from testFalseVariable\n";
		Map<String, Map<String, String>> testMapInvertedFromFalseVariable = yaml
				.load(testYamlTextInvertedFromFalseVariable);
		testMapInvertedFromFalseVariable.put(outKeyword, new HashMap<String, String>());

		InternalTestDummyStep testInstance = new InternalTestDummyStep(VariableHandlerInstance,
				testYamlTextInvertedFromFalseVariable);
		assertEquals(testMapInvertedFromFalseVariable.get(inKeyword), testInstance.getIn());
		assertEquals(testMapInvertedFromFalseVariable.get(outKeyword), testInstance.getOut());

		assertEquals("true", testInstance.handleInputTestFunction("input1").getContent());
	}

	@Test
	public void testInFromNumberVariable() throws VariableNotDefinedException, StructureException,
			ProjectFolderPathNotSetExceptionMUMLACGPPA, InOrOutKeyNotDefinedException, FaultyDataException {
		String testYamlTextFromNumberVariable = inKeyword + ":\n" + "  numberTest: from testNumberVariable\n";
		Map<String, Map<String, String>> testMapFromNumberVariable = yaml.load(testYamlTextFromNumberVariable);
		testMapFromNumberVariable.put(outKeyword, new HashMap<String, String>());

		InternalTestDummyStep testInstance = new InternalTestDummyStep(VariableHandlerInstance,
				testYamlTextFromNumberVariable);
		assertEquals(testMapFromNumberVariable.get(inKeyword), testInstance.getIn());
		assertEquals(testMapFromNumberVariable.get(outKeyword), testInstance.getOut());

		assertEquals(VariableHandlerInstance.getVariableValue("testNumberVariable").getContent(),
				testInstance.handleInputTestFunction("numberTest").getContent());
	}

	@Test
	public void testInFromTextVariable() throws VariableNotDefinedException, StructureException,
			ProjectFolderPathNotSetExceptionMUMLACGPPA, InOrOutKeyNotDefinedException, FaultyDataException {
		String testYamlTextFromTextVariable = inKeyword + ":\n" + "  textTest: from testTextVariable\n";
		Map<String, Map<String, String>> testMapFromTextVariable = yaml.load(testYamlTextFromTextVariable);
		testMapFromTextVariable.put(outKeyword, new HashMap<String, String>());

		InternalTestDummyStep testInstance = new InternalTestDummyStep(VariableHandlerInstance,
				testYamlTextFromTextVariable);
		assertEquals(testMapFromTextVariable.get(inKeyword), testInstance.getIn());
		assertEquals(testMapFromTextVariable.get(outKeyword), testInstance.getOut());

		assertEquals(VariableHandlerInstance.getVariableValue("testTextVariable").getContent(),
				testInstance.handleInputTestFunction("textTest").getContent());
	}

	// Out:

	@Test
	public void testConstructorVariantsEqualOut() throws VariableNotDefinedException, StructureException,
			ProjectFolderPathNotSetExceptionMUMLACGPPA, InOrOutKeyNotDefinedException {
		String testYamlTextDirectTrue = outKeyword + ":\n" + "  outTest: dummy\n";
		Map<String, Map<String, String>> testMapDirectTrue = yaml.load(testYamlTextDirectTrue);
		testMapDirectTrue.put(inKeyword, new HashMap<String, String>());

		InternalTestDummyStep testInstance1 = new InternalTestDummyStep(VariableHandlerInstance,
				testYamlTextDirectTrue);
		InternalTestDummyStep testInstance2 = new InternalTestDummyStep(VariableHandlerInstance, testMapDirectTrue);
		assertEquals(testInstance2.getIn(), testInstance1.getIn());
		assertEquals(testInstance2.getOut(), testInstance1.getOut());

	}

	@Test
	public void testOutNewVariable() throws VariableNotDefinedException, StructureException,
			ProjectFolderPathNotSetExceptionMUMLACGPPA, InOrOutKeyNotDefinedException {
		String newVariableName = "newVariable";
		String newVariableValue = "newValue";
		String testYamlTextSetNewVariable = outKeyword + ":\n" + "  outTest: " + newVariableName + "\n";
		Map<String, Map<String, String>> testMapSetNewVariable = yaml.load(testYamlTextSetNewVariable);
		testMapSetNewVariable.put(inKeyword, new HashMap<String, String>());

		InternalTestDummyStep testInstance = new InternalTestDummyStep(VariableHandlerInstance,
				testYamlTextSetNewVariable);
		assertEquals(testMapSetNewVariable.get(inKeyword), testInstance.getIn());
		assertEquals(testMapSetNewVariable.get(outKeyword), testInstance.getOut());

		assertFalse(VariableHandlerInstance.isVariableInitialized(newVariableName));

		testInstance.handleOutputTestFunction("outTest", new VariableContent(newVariableValue));

		assertTrue(VariableHandlerInstance.isVariableInitialized(newVariableName));

		assertEquals(newVariableValue, VariableHandlerInstance.getVariableValue(newVariableName).getContent());
	}

	@Test
	public void testOutAlreadyExistingVariable() throws VariableNotDefinedException, StructureException,
			ProjectFolderPathNotSetExceptionMUMLACGPPA, InOrOutKeyNotDefinedException {
		String alreadyExistingVariableName = "testTextVariable";
		String newVariableValue = "newText";

		String testYamlTextSetAlreadyExistingVariable = outKeyword + ":\n" + "  outTest: " + alreadyExistingVariableName
				+ "\n";
		Map<String, Map<String, String>> testMapSetAlreadyExistingVariable = yaml
				.load(testYamlTextSetAlreadyExistingVariable);
		testMapSetAlreadyExistingVariable.put(inKeyword, new HashMap<String, String>());

		InternalTestDummyStep testInstance = new InternalTestDummyStep(VariableHandlerInstance,
				testYamlTextSetAlreadyExistingVariable);
		assertEquals(testMapSetAlreadyExistingVariable.get(inKeyword), testInstance.getIn());
		assertEquals(testMapSetAlreadyExistingVariable.get(outKeyword), testInstance.getOut());

		assertTrue(VariableHandlerInstance.isVariableInitialized(alreadyExistingVariableName));

		testInstance.handleOutputTestFunction("outTest", new VariableContent(newVariableValue));

		assertTrue(VariableHandlerInstance.isVariableInitialized(alreadyExistingVariableName));

		assertEquals(newVariableValue,
				VariableHandlerInstance.getVariableValue(alreadyExistingVariableName).getContent());
	}

	// Out and in:

	@Test
	public void testConstructorVariantsEqualInAndOut() throws VariableNotDefinedException, StructureException,
			ProjectFolderPathNotSetExceptionMUMLACGPPA, InOrOutKeyNotDefinedException {
		String testYamlTextDirectTrue = inKeyword + ":\n" + "  input1: direct true\n" + outKeyword + ":\n"
				+ "  outTest: dummy\n";
		Map<String, Map<String, String>> testMapDirectTrue = yaml.load(testYamlTextDirectTrue);

		InternalTestDummyStep testInstance1 = new InternalTestDummyStep(VariableHandlerInstance,
				testYamlTextDirectTrue);
		InternalTestDummyStep testInstance2 = new InternalTestDummyStep(VariableHandlerInstance, testMapDirectTrue);
		assertEquals(testInstance2.getIn(), testInstance1.getIn());
		assertEquals(testInstance2.getOut(), testInstance1.getOut());
	}

	@Test
	public void testOneInstanceOutNewVariableAndAnotherReadsIt() throws VariableNotDefinedException, StructureException,
			ProjectFolderPathNotSetExceptionMUMLACGPPA, InOrOutKeyNotDefinedException, FaultyDataException {
		String newVariableName = "newVariable";
		String newVariableValue = "newValue";

		String testYamlTextSetNewVariable = outKeyword + ":\n" + "  outTest: " + newVariableName + "\n";
		Map<String, Map<String, String>> testMapSetNewVariable = yaml.load(testYamlTextSetNewVariable);
		testMapSetNewVariable.put(inKeyword, new HashMap<String, String>());

		InternalTestDummyStep testInstanceSetter = new InternalTestDummyStep(VariableHandlerInstance,
				testYamlTextSetNewVariable);
		testInstanceSetter.handleOutputTestFunction("outTest", new VariableContent(newVariableValue));

		String testYamlTextGetNewVariable = inKeyword + ":\n" + "  inTest: from " + newVariableName + "\n";
		Map<String, Map<String, String>> testMapGetNewVariable = yaml.load(testYamlTextGetNewVariable);
		testMapGetNewVariable.put(outKeyword, new HashMap<String, String>());

		InternalTestDummyStep testInstanceGetter = new InternalTestDummyStep(VariableHandlerInstance,
				testYamlTextGetNewVariable);

		assertEquals(newVariableValue, testInstanceGetter.handleInputTestFunction("inTest").getContent());
	}

	@Test
	public void testValidation() throws VariableNotDefinedException, StructureException, FaultyDataException,
			ParameterMismatchException, ProjectFolderPathNotSetExceptionMUMLACGPPA, InOrOutKeyNotDefinedException, TypeMismatchException {
		String testYamlTextCompleteDefinitionContent = inKeyword + ":\n" + "  input1: from testTrueVariable\n"
				+ "  input2: from testFalseVariable\n" + outKeyword + ":\n" + "  output1: testTextVariable\n"
				+ "  output2: testTextVariable\n";

		InternalTestDummyStep testInstance = new InternalTestDummyStep(VariableHandlerInstance,
				testYamlTextCompleteDefinitionContent);
		testInstance.checkForDetectableParameterVariableAndTypeErrors(VariableHandlerInstance);
	}
	 
}
