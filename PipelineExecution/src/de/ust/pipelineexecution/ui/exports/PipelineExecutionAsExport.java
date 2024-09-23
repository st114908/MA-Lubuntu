package de.ust.pipelineexecution.ui.exports;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
import de.ust.mumlacgppa.pipeline.parts.exceptions.StepNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StepNotMatched;
import de.ust.mumlacgppa.pipeline.parts.exceptions.StructureException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.TypeMismatchException;
import de.ust.mumlacgppa.pipeline.parts.exceptions.VariableNotDefinedException;
import de.ust.mumlacgppa.pipeline.parts.steps.PipelineStep;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.Compile;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ComponentCodeGeneration;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.ContainerTransformation;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.LookupBoardBySerialNumber;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.OnlyContinueIfFulfilledElseAbort;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.PipelineStepDictionaryMUMLPostProcessingAndArduinoCLIUtilizer;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.DialogMessage;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.SelectableTextWindow;
import de.ust.mumlacgppa.pipeline.parts.steps.mumlpostprocessingandarduinocli.Upload;
import de.ust.mumlacgppa.pipeline.paths.PipelineSettingsDirectoryAndFilesPaths;
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
	protected boolean containerTransformationToBePerformed;
	protected boolean componentCodeGenerationToBePerformed;

	@Override
	public String wizardGetId() {
		return "pipelineexecution.ui.exports.PipelineExecutionAsExport";
	}


	protected PipelineStep getNextStepRespectiveSequence() {
		return PSRInstance.getNextPipelineSequenceStep();
	}

	protected boolean hasNextStepRespectiveSequence() {
		return PSRInstance.hasNextPipelineSequenceStep();
	}
	
	protected void resetRespectiveSequence() {
		PSRInstance.resetPipelineSequenceProgress();
	}
	
	protected void checkWhichWorkaroundsToPerform() {
		containerTransformationToBePerformed = false;
		componentCodeGenerationToBePerformed = false;
		resetRespectiveSequence();

		PipelineStep currentStep;
		while(hasNextStepRespectiveSequence()){
			currentStep = getNextStepRespectiveSequence();
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
			PSRInstance.checkForDetectableErrors();
			pipelineSettingsErrorDetected = false;
		} catch (FileNotFoundException | StructureException | StepNotMatched | StepNotDefinedException
				| ProjectFolderPathNotSetExceptionMUMLACGPPA | VariableNotDefinedException | FaultyDataException
				| ParameterMismatchException | TypeMismatchException e) {
			pipelineSettingsErrorDetected = true;
			InfoWindow errorInfoWindow = new InfoWindow("Errors in the pipeline!",
					"Errors in the pipeline settings!",
					"The validation detected errors in the pipeline or while reading it.\n"
							+ "This is the generated error message:\n" + e.getMessage());
			addPage(errorInfoWindow);
			e.printStackTrace();
			return true;
		} catch (Exception e) { // For other errors:
			pipelineSettingsErrorDetected = true;
			InfoWindow errorInfoWindow = new InfoWindow("Errors in the pipeline!",
					"Errors in the pipeline settings!",
					"The validation detected errors in the pipeline or while reading it.\n"
							+ "This is the generated error message:\n" + e.getMessage());
			addPage(errorInfoWindow);
			e.printStackTrace();
			return true;
		}
		return false;
	}

	protected boolean handleArduinoCLIUtilizerCheck(Path projectPath) {
		boolean checkForACLIUSettingsFile = false;
		resetRespectiveSequence();
		PipelineStep currentStep;
		while(hasNextStepRespectiveSequence()){
			currentStep = getNextStepRespectiveSequence();
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
		resetRespectiveSequence();
		PipelineStep currentStep;
		while(hasNextStepRespectiveSequence()){
			currentStep = getNextStepRespectiveSequence();
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
					"Due to access restrictions no windows DialogMessages and SelectableTextWindows\n"
							+ "can be created directly or indireclty from the export wizard process.\n"
							+ "(Or at least no way could be found.)\n"
							+ "So please check the console of the Eclipse workbench instance that\n"
							+ "you used to start the MUML-Plug-Ins as Eclipse application.\n");
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

		ProjectFolderPathStorage.project = targetProject;
		ProjectFolderPathStorage.projectFolderPath = projectPath;
		completePipelineSettingsFilePath = projectPath
				.resolve(PipelineSettingsDirectoryAndFilesPaths.PIPELINE_SETTINGS_DIRECTORY_FOLDER)
				.resolve(PipelineSettingsDirectoryAndFilesPaths.PIPELINE_SETTINGS_FILE_NAME);
		
		
		boolean problemsWithPipelineSettingsFile = handlePipelineSettingsFile();
		if(problemsWithPipelineSettingsFile){
			return;
		}

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
		checkWhichWorkaroundsToPerform();

		// Information about steps with windows that appear.
		handleStepsWithWindowCheck();

		InfoWindow readyToStartPipelineInfoWindow = new InfoWindow("Pipeline execution",
				"Pipeline execution ready to start.",
				"The execution of the pipeline is ready to start.\n" + "Click \"Finish\" to start it.");
		addPage(readyToStartPipelineInfoWindow);

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
				resetRespectiveSequence();
				PipelineStep currentStep;
				while(hasNextStepRespectiveSequence()){
					currentStep = getNextStepRespectiveSequence();
					System.out.println("Performing step: " + currentStep.getClass().getSimpleName());
					System.out.println("Data: " + currentStep.toString());
					try {
						if (containerTransformationToBePerformed
								&& (currentStep.getClass().getSimpleName().equals(ContainerTransformation.nameFlag))) {
							// T3.2: Deployment Configuration aka Container
							// Transformation
							doExecuteContainerTransformationPart(sourceElementsSystemAllocation,
									(ContainerTransformation) currentStep, progressMonitor);
						} else if (componentCodeGenerationToBePerformed
								&& (currentStep.getClass().getSimpleName().equals(ComponentCodeGeneration.nameFlag))) {
							// T3.3: Component Code Generation
							doExecuteComponentCodeGeneration(sourceElementsComponentInstance,
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
				System.out.println("Execution finished successfully!");
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

	protected void doExecuteContainerTransformationPart(
			final EObject[] sourceElementsSystemAllocation, ContainerTransformation step,
			IProgressMonitor progressMonitor)
			throws VariableNotDefinedException, StructureException, InOrOutKeyNotDefinedException, FaultyDataException {
		ContainerTransformationWorkaround ContainerTransformationHandlerInstance = new ContainerTransformationWorkaround(
				step);
		ContainerTransformationHandlerInstance.performContainerTransformation(
				sourceElementsSystemAllocation, progressMonitor, editingDomain);
	}

	protected void doExecuteComponentCodeGeneration(
			final EObject[] sourceElementsComponentInstance, ComponentCodeGeneration step,
			IProgressMonitor progressMonitor)
			throws VariableNotDefinedException, StructureException, InOrOutKeyNotDefinedException, FaultyDataException {
		ComponentCodeGenerationWorkaround componentCodeGenerationHandlerInstance = new ComponentCodeGenerationWorkaround(
				step);
		componentCodeGenerationHandlerInstance.performComponentCodeGeneration(
				sourceElementsComponentInstance, progressMonitor);
	}

	protected void refreshWorkSpace(final IFile selectedFile) throws CoreException {
		selectedFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
	}

	protected void printTextWindowMessage(String message) {
		System.out.println(
				"\n\n\nDue to Access restrictions no SelectableTextWindow can be created from the export wizard process.\n");
		System.out.println("(Or at least no way could be found.)");
		System.out.println("So here the output in the console:\n\n");
		System.out.println(message);
	}

	protected void printDialogWindowMessage(String message) {
		System.out.println(
				"\n\n\nDue to Access restrictions no windows for e.g. messages can be created from the export wizard process without aborting the pipeline.\n");
		System.out.println("(Or at least no way could be found.)");
		System.out.println("So here the output in the console:\n");
		System.out.println(message);
		System.out.println("\n\n");
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
		// so here an improvisation/workaround:
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
		/* 
		 * Eclipse doesn't allow message dialogs or text windows from an export wizard or a class called by it,
		 * so the messages get printed on the console of the starting eclipse instance.
		 */
		if (currentStep.getClass().getSimpleName().equals(DialogMessage.nameFlag)) {
			if (currentStep.getContentOfInput("condition").getBooleanContent()) {
				printDialogWindowMessage(currentStep.getContentOfInput("message").getContent());
			}
		} else if (currentStep.getClass().getSimpleName().equals(SelectableTextWindow.nameFlag)) {
			if (currentStep.getContentOfInput("condition").getBooleanContent()) {
				printTextWindowMessage(currentStep.getContentOfInput("message").getContent());
			}
		} else if (currentStep.getClass().getSimpleName().equals(OnlyContinueIfFulfilledElseAbort.nameFlag)) {
			if (!currentStep.getContentOfInput("condition").getBooleanContent()) {
				printDialogWindowMessage(currentStep.getContentOfInput("message").getContent());
				// For some reason export wizards are allowed to cause an error dialog when aborting, so it gets utilized.
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
