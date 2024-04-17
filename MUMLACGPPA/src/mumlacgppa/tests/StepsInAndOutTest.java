package mumlacgppa.tests;

import static org.junit.Assert.*;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import mumlacgppa.pipeline.parts.exceptions.ParameterMismatchException;
import mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import mumlacgppa.pipeline.parts.exceptions.StructureException;
import mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import mumlacgppa.pipeline.parts.steps.Keywords;
import mumlacgppa.pipeline.parts.storage.VariableContent;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;


//TODO: Trigger Exceptions intentionally!

public class StepsInAndOutTest implements Keywords{
	InternalTestVariableHandlerClearStorageAccess VariableHandlerInstance;
	private static Yaml yaml;
	

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception{
		ProjectFolderPathStorage.projectFolderPath = Paths.get("/home/muml/MUMLProjects/Overtaking-Cars-Baumfalk");
	}

	@BeforeClass
	public static void setUpClass(){
		yaml = new Yaml();
	}
	
	@Before
	public void setUp(){
		VariableHandlerInstance = new InternalTestVariableHandlerClearStorageAccess();
		VariableHandlerInstance.setVariableValue("testTrueVariable", new VariableContent("true") );
		VariableHandlerInstance.setVariableValue("testFalseVariable", new VariableContent("false") );
		VariableHandlerInstance.setVariableValue("testNumberVariable", new VariableContent("1") );
		VariableHandlerInstance.setVariableValue("testTextVariable", new VariableContent("HelloWorld") );
		
	}
	
	@After
	public void tearDown(){
		VariableHandlerInstance.clearVariableHandlerStorage();
	}
	
	// In:
	
	@Test
	public void testConstructorVariantsEqualIn() throws VariableNotDefinedException, StructureException, ProjectFolderPathNotSetExceptionMUMLACGPPA {
		String testYamlTextDirectTrue=
				inFlag + ":\n"
				+ "  input1: direct true\n";
		Map <String, Map <String, String>> testMapDirectTrue = yaml.load(testYamlTextDirectTrue);
		testMapDirectTrue.put(outFlag, new HashMap<String, String>());
		
		InternalTestDummyStep testInstance1 = new InternalTestDummyStep(VariableHandlerInstance, testYamlTextDirectTrue);
		InternalTestDummyStep testInstance2 = new InternalTestDummyStep(VariableHandlerInstance, testMapDirectTrue);
	    assertEquals( testInstance2.getIn(), testInstance1.getIn() );
	    assertEquals( testInstance2.getOut(), testInstance1.getOut() );
	    
	}
	
	// Direct:
	
	@Test
	public void testInDirectTrue() throws VariableNotDefinedException, StructureException, ProjectFolderPathNotSetExceptionMUMLACGPPA {
		String testYamlTextDirectTrue=
				inFlag + ":\n"
				+ "  input1: direct true\n";
		Map <String, Map <String, String>> testMapDirectTrue = yaml.load(testYamlTextDirectTrue);
		testMapDirectTrue.put(outFlag, new HashMap<String, String>());
		
		InternalTestDummyStep testInstance = new InternalTestDummyStep(VariableHandlerInstance, testYamlTextDirectTrue);
	    assertEquals( testMapDirectTrue.get(inFlag), testInstance.getIn() );
	    assertEquals( testMapDirectTrue.get(outFlag), testInstance.getOut() );
	    
	    assertEquals( "true", testInstance.handleInputTestFunction("input1").getContent());
	}

	@Test
	public void testInDirectFalse() throws VariableNotDefinedException, StructureException, ProjectFolderPathNotSetExceptionMUMLACGPPA {
		String testYamlTextDirectFalse=
				inFlag + ":\n"
				+ "  input1: direct false\n";
		Map <String, Map <String, String>> testMapDirectFalse= yaml.load(testYamlTextDirectFalse);
		testMapDirectFalse.put(outFlag, new HashMap<String, String>());

		InternalTestDummyStep testInstance = new InternalTestDummyStep(VariableHandlerInstance, testYamlTextDirectFalse);
	    assertEquals( testMapDirectFalse.get(inFlag), testInstance.getIn() );
	    assertEquals( testMapDirectFalse.get(outFlag), testInstance.getOut() );
	    
	    assertEquals( "false", testInstance.handleInputTestFunction("input1").getContent());
	}

	@Test
	public void testInInvertedDirectTrue() throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, ProjectFolderPathNotSetExceptionMUMLACGPPA {
		String testYamlTextInvertedDirectTrue =
				inFlag + ":\n"
				+ "  input1: not direct true\n";
		Map <String, Map <String, String>> testMapInvertedDirectTrue = yaml.load(testYamlTextInvertedDirectTrue);
		testMapInvertedDirectTrue.put(outFlag, new HashMap<String, String>());

		InternalTestDummyStep testInstance = new InternalTestDummyStep(VariableHandlerInstance, testYamlTextInvertedDirectTrue);
	    assertEquals( testMapInvertedDirectTrue.get(inFlag), testInstance.getIn() );
	    assertEquals( testMapInvertedDirectTrue.get(outFlag), testInstance.getOut() );
	    
	    assertEquals( "false", testInstance.handleInputTestFunction("input1").getContent());
	}

	@Test
	public void testInInvertedDirectFalse() throws VariableNotDefinedException, StructureException, ProjectFolderPathNotSetExceptionMUMLACGPPA {
		String testYamlTextInvertedDirectFalse =
				inFlag + ":\n"
				+ "  input1: not direct false\n";
		Map <String, Map <String, String>> testMapInvertedDirectFalse = yaml.load(testYamlTextInvertedDirectFalse);
		testMapInvertedDirectFalse.put(outFlag, new HashMap<String, String>());
		
		InternalTestDummyStep testInstance = new InternalTestDummyStep(VariableHandlerInstance, testYamlTextInvertedDirectFalse);
	    assertEquals( testMapInvertedDirectFalse.get(inFlag), testInstance.getIn() );
	    assertEquals( testMapInvertedDirectFalse.get(outFlag), testInstance.getOut() );
	    
	    assertEquals( "true", testInstance.handleInputTestFunction("input1").getContent());
	}


	@Test
	public void testInDirectNumber() throws VariableNotDefinedException, StructureException, ProjectFolderPathNotSetExceptionMUMLACGPPA {
		String testYamlTextDirectNumber=
				inFlag + ":\n"
				+ "  numberTest: direct 1\n";
		Map <String, Map <String, String>> testMapDirectNumber = yaml.load(testYamlTextDirectNumber);
		testMapDirectNumber.put(outFlag, new HashMap<String, String>());
		
		InternalTestDummyStep testInstance = new InternalTestDummyStep(VariableHandlerInstance, testYamlTextDirectNumber);
	    assertEquals( testMapDirectNumber.get(inFlag), testInstance.getIn() );
	    assertEquals( testMapDirectNumber.get(outFlag), testInstance.getOut() );
	    
	    assertEquals( "1", testInstance.handleInputTestFunction("numberTest").getContent());
	}

	@Test
	public void testInDirectString() throws VariableNotDefinedException, StructureException, ProjectFolderPathNotSetExceptionMUMLACGPPA {
		String testYamlTextDirectString=
				inFlag + ":\n"
				+ "  stringTest: direct HelloWorld\n";
		Map <String, Map <String, String>> testMapDirectString = yaml.load(testYamlTextDirectString);
		testMapDirectString.put(outFlag, new HashMap<String, String>());
		
		InternalTestDummyStep testInstance = new InternalTestDummyStep(VariableHandlerInstance, testYamlTextDirectString);
	    assertEquals( testMapDirectString.get(inFlag), testInstance.getIn() );
	    assertEquals( testMapDirectString.get(outFlag), testInstance.getOut() );
	    
	    assertEquals( "HelloWorld", testInstance.handleInputTestFunction("stringTest").getContent());
	}

	
	// From:
	
	@Test
	public void testInFromTrueVariable() throws VariableNotDefinedException, StructureException, ProjectFolderPathNotSetExceptionMUMLACGPPA {
		String testYamlTextFromTrueVariable =
				inFlag + ":\n"
				+ "  input1: from testTrueVariable\n";
		Map <String, Map <String, String>> testMapFromTrueVariable = yaml.load(testYamlTextFromTrueVariable);
		testMapFromTrueVariable.put(outFlag, new HashMap<String, String>());
		
		InternalTestDummyStep testInstance = new InternalTestDummyStep(VariableHandlerInstance, testYamlTextFromTrueVariable);
	    assertEquals( testMapFromTrueVariable.get(inFlag), testInstance.getIn() );
	    assertEquals( testMapFromTrueVariable.get(outFlag), testInstance.getOut() );
	    
	    assertEquals( "true", testInstance.handleInputTestFunction("input1").getContent());
	}
	
	
	@Test
	public void testInFromFalseVariable() throws VariableNotDefinedException, StructureException, ProjectFolderPathNotSetExceptionMUMLACGPPA {
		String testYamlTextFromFalseVariable =
				inFlag + ":\n"
				+ "  input1: from testFalseVariable\n";
		Map <String, Map <String, String>> testMapFromFalseVariable = yaml.load(testYamlTextFromFalseVariable);
		testMapFromFalseVariable.put(outFlag, new HashMap<String, String>());
		
		InternalTestDummyStep testInstance = new InternalTestDummyStep(VariableHandlerInstance, testYamlTextFromFalseVariable);
	    assertEquals( testMapFromFalseVariable.get(inFlag), testInstance.getIn() );
	    assertEquals( testMapFromFalseVariable.get(outFlag), testInstance.getOut() );
	    
	    assertEquals( "false", testInstance.handleInputTestFunction("input1").getContent());
	}
	

	@Test
	public void testStringBasedConstructorInvertedFromTrueVariable() throws VariableNotDefinedException, StructureException, ProjectFolderPathNotSetExceptionMUMLACGPPA {
		String testYamlTextInvertedFromTrueVariable =
				inFlag + ":\n"
				+ "  input1: not from testTrueVariable\n";
		Map <String, Map <String, String>> testMapInvertedFromTrueVariable = yaml.load(testYamlTextInvertedFromTrueVariable);
		testMapInvertedFromTrueVariable.put(outFlag, new HashMap<String, String>());
		
		InternalTestDummyStep testInstance = new InternalTestDummyStep(VariableHandlerInstance, testYamlTextInvertedFromTrueVariable);
	    assertEquals( testMapInvertedFromTrueVariable.get(inFlag), testInstance.getIn() );
	    assertEquals( testMapInvertedFromTrueVariable.get(outFlag), testInstance.getOut() );
	    
	    assertEquals( "false", testInstance.handleInputTestFunction("input1").getContent());
	}
	
	
	@Test
	public void testStringBasedConstructorInvertedFromFalseVariable() throws VariableNotDefinedException, StructureException, ProjectFolderPathNotSetExceptionMUMLACGPPA {
		String testYamlTextInvertedFromFalseVariable =
				inFlag + ":\n"
				+ "  input1: not from testFalseVariable\n";
		Map <String, Map <String, String>> testMapInvertedFromFalseVariable = yaml.load(testYamlTextInvertedFromFalseVariable);
		testMapInvertedFromFalseVariable.put(outFlag, new HashMap<String, String>());
		
		InternalTestDummyStep testInstance = new InternalTestDummyStep(VariableHandlerInstance, testYamlTextInvertedFromFalseVariable);
	    assertEquals( testMapInvertedFromFalseVariable.get(inFlag), testInstance.getIn() );
	    assertEquals( testMapInvertedFromFalseVariable.get(outFlag), testInstance.getOut() );
	    
	    assertEquals( "true", testInstance.handleInputTestFunction("input1").getContent());
	}
	
	

	@Test
	public void testInFromNumberVariable() throws VariableNotDefinedException, StructureException, ProjectFolderPathNotSetExceptionMUMLACGPPA {
		String testYamlTextFromNumberVariable =
				inFlag + ":\n"
				+ "  numberTest: from testNumberVariable\n";
		Map <String, Map <String, String>> testMapFromNumberVariable = yaml.load(testYamlTextFromNumberVariable);
		testMapFromNumberVariable.put(outFlag, new HashMap<String, String>());
		
		InternalTestDummyStep testInstance = new InternalTestDummyStep(VariableHandlerInstance, testYamlTextFromNumberVariable);
	    assertEquals( testMapFromNumberVariable.get(inFlag), testInstance.getIn() );
	    assertEquals( testMapFromNumberVariable.get(outFlag), testInstance.getOut() );
	    
	    assertEquals( VariableHandlerInstance.getVariableValue("testNumberVariable").getContent(),
	    		testInstance.handleInputTestFunction("numberTest").getContent());
	}

	@Test
	public void testInFromTextVariable() throws VariableNotDefinedException, StructureException, ProjectFolderPathNotSetExceptionMUMLACGPPA {
		String testYamlTextFromTextVariable =
				inFlag + ":\n"
				+ "  textTest: from testTextVariable\n";
		Map <String, Map <String, String>> testMapFromTextVariable = yaml.load(testYamlTextFromTextVariable);
		testMapFromTextVariable.put(outFlag, new HashMap<String, String>());
		
		InternalTestDummyStep testInstance = new InternalTestDummyStep(VariableHandlerInstance, testYamlTextFromTextVariable);
	    assertEquals( testMapFromTextVariable.get(inFlag), testInstance.getIn() );
	    assertEquals( testMapFromTextVariable.get(outFlag), testInstance.getOut() );
	    
	    assertEquals( VariableHandlerInstance.getVariableValue("testTextVariable").getContent(),
	    		testInstance.handleInputTestFunction("textTest").getContent());
	}
	
	
	// Out:
	
	@Test
	public void testConstructorVariantsEqualOut() throws VariableNotDefinedException, StructureException, ProjectFolderPathNotSetExceptionMUMLACGPPA {
		String testYamlTextDirectTrue=
				outFlag + ":\n"
				+ "  outTest: dummy\n";
		Map <String, Map <String, String>> testMapDirectTrue = yaml.load(testYamlTextDirectTrue);
		testMapDirectTrue.put(inFlag, new HashMap<String, String>());
		
		InternalTestDummyStep testInstance1 = new InternalTestDummyStep(VariableHandlerInstance, testYamlTextDirectTrue);
		InternalTestDummyStep testInstance2 = new InternalTestDummyStep(VariableHandlerInstance, testMapDirectTrue);
	    assertEquals( testInstance2.getIn(), testInstance1.getIn() );
	    assertEquals( testInstance2.getOut(), testInstance1.getOut() );
	    
	}
	
	
	@Test
	public void testOutNewVariable() throws VariableNotDefinedException, StructureException, ProjectFolderPathNotSetExceptionMUMLACGPPA {
		String newVariableName = "newVariable";
		String newVariableValue = "newValue";
		String testYamlTextSetNewVariable =
				outFlag + ":\n"
				+ "  outTest: "+ newVariableName + "\n";
		Map <String, Map <String, String>> testMapSetNewVariable = yaml.load(testYamlTextSetNewVariable);
		testMapSetNewVariable.put(inFlag, new HashMap<String, String>());
		
		InternalTestDummyStep testInstance = new InternalTestDummyStep(VariableHandlerInstance, testYamlTextSetNewVariable);
	    assertEquals( testMapSetNewVariable.get(inFlag), testInstance.getIn() );
	    assertEquals( testMapSetNewVariable.get(outFlag), testInstance.getOut() );
	    
	    assertFalse(VariableHandlerInstance.isVariableInitialized(newVariableName));
	    
	    testInstance.handleOutputTestFunction("outTest", new VariableContent(newVariableValue) );
	    
	    assertTrue(VariableHandlerInstance.isVariableInitialized(newVariableName));
	    
	    assertEquals(newVariableValue, VariableHandlerInstance.getVariableValue(newVariableName).getContent());
	}

	@Test
	public void testOutAlreadyExistingVariable() throws VariableNotDefinedException, StructureException, ProjectFolderPathNotSetExceptionMUMLACGPPA {
		String alreadyExistingVariableName = "testTextVariable";
		String newVariableValue = "newText";
		
		String testYamlTextSetAlreadyExistingVariable =
				outFlag + ":\n"
				+ "  outTest: "+ alreadyExistingVariableName + "\n";
		Map <String, Map <String, String>> testMapSetAlreadyExistingVariable = yaml.load(testYamlTextSetAlreadyExistingVariable);
		testMapSetAlreadyExistingVariable.put(inFlag, new HashMap<String, String>());
		
		InternalTestDummyStep testInstance = new InternalTestDummyStep(VariableHandlerInstance, testYamlTextSetAlreadyExistingVariable);
	    assertEquals( testMapSetAlreadyExistingVariable.get(inFlag), testInstance.getIn() );
	    assertEquals( testMapSetAlreadyExistingVariable.get(outFlag), testInstance.getOut() );
	    
	    assertTrue(VariableHandlerInstance.isVariableInitialized(alreadyExistingVariableName));
	    
	    testInstance.handleOutputTestFunction("outTest", new VariableContent(newVariableValue) );
	    
	    assertTrue(VariableHandlerInstance.isVariableInitialized(alreadyExistingVariableName));
	    
	    assertEquals(newVariableValue, VariableHandlerInstance.getVariableValue(alreadyExistingVariableName).getContent());
	}
	
	// Out and in:

	@Test
	public void testConstructorVariantsEqualInAndOut() throws VariableNotDefinedException, StructureException, ProjectFolderPathNotSetExceptionMUMLACGPPA {
		String testYamlTextDirectTrue=
				inFlag + ":\n"
				+ "  input1: direct true\n"
				+ outFlag + ":\n"
				+ "  outTest: dummy\n";
		Map <String, Map <String, String>> testMapDirectTrue = yaml.load(testYamlTextDirectTrue);
		
		InternalTestDummyStep testInstance1 = new InternalTestDummyStep(VariableHandlerInstance, testYamlTextDirectTrue);
		InternalTestDummyStep testInstance2 = new InternalTestDummyStep(VariableHandlerInstance, testMapDirectTrue);
	    assertEquals( testInstance2.getIn(), testInstance1.getIn() );
	    assertEquals( testInstance2.getOut(), testInstance1.getOut() );
	}
	
	@Test
	public void testOneInstanceOutNewVariableAndAnotherReadsIt() throws VariableNotDefinedException, StructureException, ProjectFolderPathNotSetExceptionMUMLACGPPA {
		String newVariableName = "newVariable";
		String newVariableValue = "newValue";
		
		String testYamlTextSetNewVariable =
				outFlag + ":\n"
				+ "  outTest: "+ newVariableName + "\n";
		Map <String, Map <String, String>> testMapSetNewVariable = yaml.load(testYamlTextSetNewVariable);
		testMapSetNewVariable.put(inFlag, new HashMap<String, String>());
		
		InternalTestDummyStep testInstanceSetter = new InternalTestDummyStep(VariableHandlerInstance, testYamlTextSetNewVariable);
	    testInstanceSetter.handleOutputTestFunction("outTest", new VariableContent(newVariableValue) );
	    
	    String testYamlTextGetNewVariable =
				inFlag + ":\n"
				+ "  inTest: from " + newVariableName + "\n";
	    Map <String, Map <String, String>> testMapGetNewVariable = yaml.load(testYamlTextGetNewVariable);
		testMapGetNewVariable.put(outFlag, new HashMap<String, String>());

		InternalTestDummyStep testInstanceGetter = new InternalTestDummyStep(VariableHandlerInstance, testYamlTextGetNewVariable);
	    
	    assertEquals(newVariableValue, testInstanceGetter.handleInputTestFunction("inTest").getContent() );
	}
	
	
	@Test
	public void testValidation() throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException, ProjectFolderPathNotSetExceptionMUMLACGPPA {
		String testYamlTextCompleteDefinitionContent =
				inFlag + ":\n"
				+ "  input1: from testTrueVariable\n"
				+ "  input2: from testFalseVariable\n"
				+ outFlag + ":\n"
				+ "  output1: testNumberVariable\n"
				+ "  output2: testTextVariable\n";
		
		InternalTestDummyStep testInstance = new InternalTestDummyStep(VariableHandlerInstance, testYamlTextCompleteDefinitionContent);
		testInstance.validate();
	}
	
	
	/*
	// This won't work anymore since the Steps have been changed to working with a given VariableHandler.
	@Test
	public void testYamlConstructor() throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException {
		String testYamlTextCompleteDefinitionContent =
				inFlag + ":\n"
				+ "  input1: from testTrueVariable\n"
				+ "  input2: from testFalseVariable\n"
				+ outFlag + ":\n"
				+ "  output1: testNumberVariable\n"
				+ "  output2: testTextVariable\n";
		
		Constructor constructor = new Constructor(InternalTestDummyStep.class, new LoaderOptions());//InternalTestDummyStep.class is root
		TypeDescription carDescription = new TypeDescription(InternalTestDummyStep.class);
		constructor.addTypeDescription(carDescription);
		Yaml yaml2 = new Yaml(constructor);
		InternalTestDummyStep testInstance = yaml2.load(testYamlTextCompleteDefinitionContent);
		testInstance.validate();
	}
	*/
}
