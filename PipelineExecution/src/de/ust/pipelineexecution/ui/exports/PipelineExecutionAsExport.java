package de.ust.pipelineexecution.ui.exports;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import org.muml.codegen.componenttype.export.ui.Activator;
import org.muml.core.export.operation.AbstractFujabaExportOperation;
import org.muml.core.export.operation.IFujabaExportOperation;
import org.muml.core.export.pages.AbstractFujabaExportSourcePage;
import org.muml.core.export.pages.ElementSelectionMode;
import org.muml.core.export.wizard.AbstractFujabaExportWizard;
import org.muml.pim.instance.ComponentInstanceConfiguration;
import org.muml.psm.allocation.SystemAllocation;

import de.ust.arduinocliutilizer.paths.DefaultConfigDirectoryAndFilePath;
import de.ust.arduinocliutilizer.worksteps.exceptions.FQBNErrorEception;
import de.ust.arduinocliutilizer.worksteps.exceptions.NoArduinoCLIConfigFileException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.AbortPipelineException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.FaultyDataException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.InOrOutKeyNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ParameterMismatchException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.ProjectFolderPathNotSetExceptionMUMLACGPPA;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StepNotMatched;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StructureException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.steps.PipelineStep;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.Compile;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ComponentCodeGeneration;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ContainerCodeGeneration;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ContainerTransformation;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.LookupBoardBySerialNumber;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.OnlyContinueIfFulfilledElseAbort;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.DialogMessage;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.SelectableTextWindow;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.Upload;
import de.ust.mumlacgppa.pipeline.paths.PipelineSettingsDirectoryAndFilePaths;
import de.ust.mumlacgppa.pipeline.reader.PipelineSettingsReader;
import projectfolderpathstorageplugin.ProjectFolderPathNotSetException;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

public class PipelineExecutionAsExport extends AbstractFujabaExportWizard {

	protected AbstractFujabaExportSourcePage sourceSystemAllocationPage;
	protected AbstractFujabaExportSourcePage sourceComponentInstancePage;
	protected Path completePipelineSettingsFilePath;
	protected boolean pipelineSettingsFileNotFound;
	protected boolean pipelineSettingsErrorDetected;

	protected PipelineSettingsReader PSRInstance;
	protected ArrayList<PipelineStep> usedSequence;
	protected boolean containerTransformationToBePerformed;
	protected boolean containerCodeGenerationToBePerformed;
	protected boolean componentCodeGenerationToBePerformed;

	@Override
	public String wizardGetId() {
		return "pipelineexecution.ui.exports.PipelineExecutionAsExport";
	}

	protected void checkWhichImprovisationsToPerform() {
		containerTransformationToBePerformed = false;
		containerCodeGenerationToBePerformed = false;
		componentCodeGenerationToBePerformed = false;

		for (PipelineStep currentStep : usedSequence) {
			if (currentStep.getClass().getSimpleName().equals(ContainerTransformation.nameFlag)) {
				// T3.2: Deployment Configuration aka Container Transformation:
				containerTransformationToBePerformed = true;
				sourceSystemAllocationPage = generateSourceSystemAllocationPage();
				addPage(sourceSystemAllocationPage);
				try {
					generateFolderIfNecessary(
							currentStep.getResolvedPathContentOfInput("muml_containerFileDestination").getParent());
				} catch (VariableNotDefinedException | StructureException | InOrOutKeyNotDefinedException | FaultyDataException e) {
					exceptionFeedback(e);
				}
			} else if (currentStep.getClass().getSimpleName().equals(ContainerCodeGeneration.nameFlag)) {
				// T3.6 and T3.7: Container Code Generation
				containerCodeGenerationToBePerformed = true;
				try {
					generateFolderIfNecessary(currentStep
							.getResolvedPathContentOfInput("arduinoContainersDestinationFolder").getParent());
				} catch (VariableNotDefinedException | StructureException | InOrOutKeyNotDefinedException | FaultyDataException e) {
					exceptionFeedback(e);
				}
			} else if (currentStep.getClass().getSimpleName().equals(ComponentCodeGeneration.nameFlag)) {
				// T3.3: Component Code Generation
				componentCodeGenerationToBePerformed = true;
				sourceComponentInstancePage = generateSourceComponentInstancePage();
				addPage(sourceComponentInstancePage);
				try {
					generateFolderIfNecessary(currentStep
							.getResolvedPathContentOfInput("arduinoContainersDestinationFolder").getParent());
				} catch (VariableNotDefinedException | StructureException | InOrOutKeyNotDefinedException | FaultyDataException e) {
					exceptionFeedback(e);
				}
			}
		}
	}

	protected boolean handlePipelineSettingsFile() {
		if (Files.exists(completePipelineSettingsFilePath) && Files.isRegularFile(completePipelineSettingsFilePath)) {
			pipelineSettingsFileNotFound = false;
		} else {
			pipelineSettingsFileNotFound = true;
			InfoWindow pipelineSettingsMissingInfoWindow = new InfoWindow("The pipeline settings file is missing!",
					"The pipeline settings file is missing!",
					"The pipeline settings file is missing!\n" + "Generate one this way:\n"
							+ "(Right click on a .muml file)/\"MUMLACGPPA\"/\n"
							+ "\"Generate default/example pipeline settings\"");
			addPage(pipelineSettingsMissingInfoWindow);
			return true;
		}

		try {
			PSRInstance = new PipelineSettingsReader(
					new PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer(),
					completePipelineSettingsFilePath);
			PSRInstance.validateOrder();
			pipelineSettingsErrorDetected = false;
		} catch (FileNotFoundException | StructureException | StepNotMatched
				| ProjectFolderPathNotSetExceptionMUMLACGPPA | VariableNotDefinedException | FaultyDataException
				| ParameterMismatchException e) {
			pipelineSettingsErrorDetected = true;
			InfoWindow arduinoCLISettingsMissingInfoWindow = new InfoWindow("Errors in the pipeline!",
					"Errors in the pipeline settings!",
					"The validation detected errors in the pipeline or while reading it.\n"
							+ "This is the generated error message:\n" + e.getMessage());
			addPage(arduinoCLISettingsMissingInfoWindow);
			e.printStackTrace();
			return true;
		} catch (Exception e) {
			pipelineSettingsErrorDetected = true;
			InfoWindow arduinoCLISettingsMissingInfoWindow = new InfoWindow("Errors in the pipeline!",
					"Errors in the pipeline settings!",
					"The validation detected errors in the pipeline or while reading it.\n"
							+ "This is the generated error message:\n" + e.getMessage());
			addPage(arduinoCLISettingsMissingInfoWindow);
			e.printStackTrace();
			return true;
		}
		return false;
	}

	protected boolean handleArduinoCLIUtilizerCheck(Path projectPath) {
		boolean checkForACLIUSettingsFile = false;
		for (PipelineStep currentStep : usedSequence) {
			if ((currentStep.getClass().getSimpleName().equals(Compile.nameFlag))
					|| (currentStep.getClass().getSimpleName().equals(Upload.nameFlag))
					|| (currentStep.getClass().getSimpleName().equals(LookupBoardBySerialNumber.nameFlag))) {
				checkForACLIUSettingsFile = true;
				break;
			}
		}
		if (checkForACLIUSettingsFile) {
			Path completeACLIUSettingsFilePath = projectPath
					.resolve(DefaultConfigDirectoryAndFilePath.CONFIG_DIRECTORY_FOLDER_NAME)
					.resolve(DefaultConfigDirectoryAndFilePath.CONFIG_FILE_NAME);
			if (!(Files.exists(completeACLIUSettingsFilePath) && Files.isRegularFile(completeACLIUSettingsFilePath))) {
				InfoWindow arduinoCLISettingsMissingInfoWindow = new InfoWindow(
						"The ArduinoCLI settings file is missing!", "The ArduinoCLI settings file is missing!",
						"The ArduinoCLI settings file is missing and at least step that requires it has been found.\n"
								+ "So if you are using a stet that relies on the ArduinoCLIUtilizer project,\n"
								+ "Generate one this way:\n" + "(Right click on a .muml file)/\"MUMLACGPPA\"/\n"
								+ "\"Generate ArduinoCLIUtilizer config file\"");
				addPage(arduinoCLISettingsMissingInfoWindow);
				return true;
			}
		}
		return false;
	}

	protected void handleStepsWithWindowCheck() {
		boolean stepWithWindowFound = false;
		for (PipelineStep currentStep : usedSequence) {
			if ((currentStep.getClass().getSimpleName().equals(DialogMessage.nameFlag))
					|| (currentStep.getClass().getSimpleName().equals(SelectableTextWindow.nameFlag))
					|| (currentStep.getClass().getSimpleName().equals(OnlyContinueIfFulfilledElseAbort.nameFlag))) {
				stepWithWindowFound = true;
				break;
			}
		}
		if (stepWithWindowFound) {
			InfoWindow selectableTextWindowsInfoWindow = new InfoWindow("Messages won't work!",
					"DialogMessages and SelectableTextWindows won't work!",
					"Due to Access restrictions no windows DialogMessages and SelectableTextWindows\n"
							+ "can be created from the export wizard process.\n"
							+ "(Or at least no way could be found.)\n"
							+ "So please check the console of the Eclipse window that\n"
							+ "you used to run the MUML-Plug-Ins as Eclipse application.\n");
			addPage(selectableTextWindowsInfoWindow);
		}
	}

	@Override
	public void addPages() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
		final IResource selectedResource = (IResource) selection.getFirstElement();

		final IProject targetProject = selectedResource.getProject();
		Path projectPath = Paths.get(targetProject.getRawLocation().toString());

		ProjectFolderPathStorage.projectFolderPath = projectPath;
		completePipelineSettingsFilePath = projectPath
				.resolve(PipelineSettingsDirectoryAndFilePaths.PIPELINE_SETTINGS_DIRECTORY_FOLDER)
				.resolve(PipelineSettingsDirectoryAndFilePaths.PIPELINE_SETTINGS_FILE_NAME);

		boolean problemsWithPipelineSettingsFile = handlePipelineSettingsFile();
		if(problemsWithPipelineSettingsFile){
			return;
		}

		usedSequence = PSRInstance.getPipelineSequence();
		// Search for steps that require the ArduinoCLIUtilizer to be able to
		// work.
		// (It is assumed to be loaded, so this will just check for the
		// ArduinoCLI settings file.
		boolean problemsWithArduinoCLIUtilizer = handleArduinoCLIUtilizerCheck(projectPath);
		if(problemsWithArduinoCLIUtilizer){
			return;
		}
		
		// Search for some steps that require some preparations but
		// neither allow that they get done right before they are needed
		// nor that their values are regardless of the execution logic just
		// maybe initialized
		// due to doExecute requiring the requested values to be "final or
		// effectively final" (or with other words not maybe given values).
		// .getClass().getSimpleName()
		checkWhichImprovisationsToPerform();

		// Information about steps with windows that appear.
		handleStepsWithWindowCheck();

		InfoWindow readyToStartPipelineIfoWindow = new InfoWindow("Pipeline execution",
				"Pipeline execution ready to start.",
				"The execution of the pipeline is ready to start.\n" + "Click \"Finish\" to start it.");
		addPage(readyToStartPipelineIfoWindow);

		final IFile selectedFile = (IFile) ((IStructuredSelection) selection).getFirstElement();
		try {
			refreshWorkSpace(selectedFile);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return
	 */
	protected AbstractFujabaExportSourcePage generateSourceComponentInstancePage() {
		return new AbstractFujabaExportSourcePage("source", toolkit, getResourceSet(), initialSelection) {
			@Override
			public String wizardPageGetSourceFileExtension() {
				return "muml";
			}

			@Override
			public boolean wizardPageSupportsSourceModelElement(EObject element) {
				return element instanceof ComponentInstanceConfiguration;
			}

			@Override
			public ElementSelectionMode wizardPageGetSupportedSelectionMode() {
				return ElementSelectionMode.ELEMENT_SELECTION_MODE_MULTI;
			}
		};
	}

	/**
	 * @return
	 */
	protected AbstractFujabaExportSourcePage generateSourceSystemAllocationPage() {
		return new AbstractFujabaExportSourcePage("source", toolkit, getResourceSet(), initialSelection) {

			@Override
			public String wizardPageGetSourceFileExtension() {
				return "muml";
			}

			@Override
			public boolean wizardPageSupportsSourceModelElement(EObject element) {
				return element instanceof SystemAllocation;
			}

			@Override
			public ElementSelectionMode wizardPageGetSupportedSelectionMode() {
				return ElementSelectionMode.ELEMENT_SELECTION_MODE_SINGLE;
			}

		};
	}

	protected AbstractFujabaExportOperation executeSequenceOperation(final IFile selectedFile,
			final IProject targetProject, final EObject[] sourceElementsSystemAllocation,
			final EObject[] sourceElementsComponentInstance) {
		return new AbstractFujabaExportOperation() {
			@Override
			protected IStatus doExecute(IProgressMonitor progressMonitor) {
				for (PipelineStep currentStep : usedSequence) {
					System.out.println("Performing step: " + currentStep.getClass().getSimpleName());
					System.out.println("Data: " + currentStep.toString());
					try {
						if (containerTransformationToBePerformed
								&& (currentStep.getClass().getSimpleName().equals(ContainerTransformation.nameFlag))) {
							// T3.2: Deployment Configuration aka Container
							// Transformation
							doExecuteContainerTransformationPart(targetProject, sourceElementsSystemAllocation,
									(ContainerTransformation) currentStep, progressMonitor);
						} else if (currentStep.getClass().getSimpleName().equals(ContainerCodeGeneration.nameFlag)) {
							// T3.6 and T3.7: Container Code Generation
							if (containerCodeGenerationToBePerformed) {
								doExecuteContainerCodeGeneration(targetProject, (ContainerCodeGeneration) currentStep,
										progressMonitor);
							}
						} else if (componentCodeGenerationToBePerformed
								&& (currentStep.getClass().getSimpleName().equals(ComponentCodeGeneration.nameFlag))) {
							// T3.3: Component Code Generation
							doExecuteComponentCodeGeneration(targetProject, sourceElementsComponentInstance,
									(ComponentCodeGeneration) currentStep, progressMonitor);
						} else {
							handleOtherPipelineSteps(currentStep);
						}
						refreshWorkSpace(selectedFile);
					} catch (VariableNotDefinedException | StructureException | FaultyDataException
							| ParameterMismatchException | IOException | InterruptedException
							| NoArduinoCLIConfigFileException | FQBNErrorEception | ProjectFolderPathNotSetException
							| AbortPipelineException | InOrOutKeyNotDefinedException | CoreException e) {
						return exceptionFeedback(e);
					}
				}
				System.out.println("Pipeline finished successfully!");
				return Status.OK_STATUS;
			}
		};
	}

	protected void generateFolderIfNecessary(Path directoriesPath) {
		if (!Files.isDirectory(directoriesPath)) {
			try {
				Files.createDirectories(directoriesPath);
			} catch (IOException e) {
				exceptionFeedback(e);
			}
		}
	}

	protected void doExecuteContainerTransformationPart(final IProject targetProject,
			final EObject[] sourceElementsSystemAllocation, ContainerTransformation step,
			IProgressMonitor progressMonitor)
			throws VariableNotDefinedException, StructureException, InOrOutKeyNotDefinedException, FaultyDataException {
		ContainerTransformationImprovisation ContainerTransformationHandlerInstance = new ContainerTransformationImprovisation(
				step);
		ContainerTransformationHandlerInstance.performContainerTransformation(targetProject,
				sourceElementsSystemAllocation, progressMonitor, editingDomain);
	}

	protected void doExecuteContainerCodeGeneration(final IProject targetProject, ContainerCodeGeneration step,
			IProgressMonitor progressMonitor)
			throws VariableNotDefinedException, StructureException, InOrOutKeyNotDefinedException, FaultyDataException {
		ContainerCodeGenerationImprovisation ContainerCodeGenerationHandlerInstance = new ContainerCodeGenerationImprovisation(
				step);
		ContainerCodeGenerationHandlerInstance.performContainerCodeGeneration(targetProject, progressMonitor);
	}

	protected void doExecuteComponentCodeGeneration(final IProject targetProject,
			final EObject[] sourceElementsComponentInstance, ComponentCodeGeneration step,
			IProgressMonitor progressMonitor)
			throws VariableNotDefinedException, StructureException, InOrOutKeyNotDefinedException, FaultyDataException {
		ComponentCodeGenerationImprovisation componentCodeGenerationHandlerInstance = new ComponentCodeGenerationImprovisation(
				step);
		componentCodeGenerationHandlerInstance.performComponentCodeGeneration(targetProject,
				sourceElementsComponentInstance, progressMonitor);
	}

	protected void refreshWorkSpace(final IFile selectedFile) throws CoreException {
		selectedFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
	}

	protected void displayTextWindow(String message) {
		/*
		 * Display display = Display.getCurrent(); final Shell shellListWindow =
		 * new Shell(display); GridLayout gridLayout = new GridLayout(1, true);
		 * gridLayout.marginWidth = 5; gridLayout.marginHeight = 5;
		 * shellListWindow.setLayout(gridLayout);
		 * 
		 * final Text text = new Text(shellListWindow, SWT.MULTI | SWT.BORDER |
		 * SWT.WRAP | SWT.READ_ONLY | SWT.V_SCROLL); text.setText(message);
		 * GridData gridData = new GridData(GridData.FILL, GridData.FILL, true,
		 * true); text.setLayoutData(gridData);
		 * 
		 * shellListWindow.setText("An Error occured!"); // Sets the title!
		 * shellListWindow.setBounds(200, 200, 550, 500);
		 * shellListWindow.open(); while (!shellListWindow.isDisposed()) { if
		 * (!display.readAndDispatch()) display.sleep(); } //display.dispose();
		 * // Causes crash, but if commented out no problems.
		 */

		System.out.println(
				"\n\n\nDue to Access restrictions no SelectableTextWindow can be created from the export wizard process.\n");
		System.out.println("(Or at least no way could be found.)");
		System.out.println("So here the output in the console:\n\n");
		System.out.println(message);
	}

	protected void displayPopupWindow(String message) {
		/*
		 * Display display = Display.getDefault();//.getCurrent(); final Shell
		 * shellWindowMessage = new Shell(display);
		 * MessageDialog.openInformation( shellWindowMessage,
		 * "Pipeline message", message);
		 */

		System.out.println(
				"\n\n\nDue to Access restrictions no windows for e.g. messages can be created from the export wizard process without aborting the pipeline.\n");
		System.out.println("(Or at least no way could be found.)");
		System.out.println("So here the output in the console:\n\n");
		System.out.println(message);
	}

	protected IStatus exceptionFeedback(Exception e) {
		// The frameworks already does this with the not commented out code
		// below
		// if(display){
		// displayTextWindow(e.getMessage());
		// }
		// The following will trigger a popupWindow, but this code structure
		// can't be used for messages during the pipeline execution.
		
		// Somehow A manual written "throws ... NoSuchFileException" for java.nio.file.NoSuchFileException won't get accepted in the catch clauses,
		// so here an improvisation:
		if(e.getClass().getSimpleName().contains("NoSuchFileException")){
			IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "File not found: " + e.getMessage(), e);
			Activator.getDefault().getLog().log(status);
			return status;
		}
		
		IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
		Activator.getDefault().getLog().log(status);
		return status;
	}

	protected void handleOtherPipelineSteps(PipelineStep currentStep)
			throws VariableNotDefinedException, StructureException, FaultyDataException, ParameterMismatchException,
			IOException, InterruptedException, NoArduinoCLIConfigFileException, FQBNErrorEception,
			ProjectFolderPathNotSetException, AbortPipelineException, InOrOutKeyNotDefinedException {
		if (currentStep.getClass().getSimpleName().equals(DialogMessage.nameFlag)) {
			if (currentStep.getContentOfInput("condition").getBooleanContent()) {
				displayPopupWindow(currentStep.getContentOfInput("message").getContent());
			}
		} else if (currentStep.getClass().getSimpleName().equals(SelectableTextWindow.nameFlag)) {
			if (currentStep.getContentOfInput("condition").getBooleanContent()) {
				displayTextWindow(currentStep.getContentOfInput("message").getContent());
			}
		} else if (currentStep.getClass().getSimpleName().equals(OnlyContinueIfFulfilledElseAbort.nameFlag)) {
			if (!currentStep.getContentOfInput("condition").getBooleanContent()) {
				displayPopupWindow(currentStep.getContentOfInput("message").getContent());
				throw new AbortPipelineException(currentStep.getContentOfInput("message").getContent());
			}
		} else {
			currentStep.execute();
		}
	}

	@Override
	public IFujabaExportOperation wizardCreateExportOperation() {
		// Do nothing if the settings file couldn't be found.
		if (pipelineSettingsFileNotFound || pipelineSettingsErrorDetected) {
			return new AbstractFujabaExportOperation() {
				@Override
				protected IStatus doExecute(IProgressMonitor progressMonitor) {
					return Status.OK_STATUS;
				}
			};
		}
		
		// Here the preparations, because when doExecute(IProgressMonitor
		// progressMonitor) gets executed
		// the file handling gets a bit tricky as is evident with
		// MUML_Container2.muml_container getting generated
		// if MUML_Container.muml_container doesn't get deleted early enough.
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
		final IFile selectedFile = (IFile) ((IStructuredSelection) selection).getFirstElement();
		final IResource selectedResource = (IResource) selection.getFirstElement();
		final IProject targetProject = selectedResource.getProject();
		
		// Extra precaution.
		LookupBoardBySerialNumber.resetSearchState();
		
		// Due to doExecute requiring the requested values to be "final or
		// effecively final" (or with other words not maybe given values),
		// duplication can't be avoided with my knowledge of eclipse.
		if ((containerTransformationToBePerformed == false) && (componentCodeGenerationToBePerformed == false)) {
			return executeSequenceOperation(selectedFile, targetProject, null, null);
		} else if ((containerTransformationToBePerformed == true) && (componentCodeGenerationToBePerformed == false)) {
			// T3.2: Deployment Configuration aka Container Transformation
			final EObject[] sourceElementsSystemAllocation = sourceSystemAllocationPage.getSourceElements();
			// generateContainerTransformationFolder(containerModelsFolder);
			return executeSequenceOperation(selectedFile, targetProject, sourceElementsSystemAllocation, null);
		} else if ((containerTransformationToBePerformed == false) && (componentCodeGenerationToBePerformed == true)) {
			// T3.3: Component Code Generation
			final EObject[] sourceElementsComponentInstance = sourceComponentInstancePage.getSourceElements();
			return executeSequenceOperation(selectedFile, targetProject, null, sourceElementsComponentInstance);
		}
		// else assume both containerTransformationToBePerformed and
		// componentCodeGenerationToBePerformed to be set to true.
		else {
			// T3.2: Deployment Configuration aka Container Transformation
			final EObject[] sourceElementsSystemAllocation = sourceSystemAllocationPage.getSourceElements();
			// generateContainerTransformationFolder(containerModelsFolder);
			// T3.3: Component Code Generation
			final EObject[] sourceElementsComponentInstance = sourceComponentInstancePage.getSourceElements();
			return executeSequenceOperation(selectedFile, targetProject, sourceElementsSystemAllocation,
					sourceElementsComponentInstance);
		}

	}

}
