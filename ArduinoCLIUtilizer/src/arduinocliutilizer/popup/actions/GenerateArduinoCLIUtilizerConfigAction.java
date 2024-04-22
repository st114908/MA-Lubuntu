package arduinocliutilizer.popup.actions;

import java.io.IOException;
import java.nio.file.Path;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import arduinocliutilizer.configgenerator.ArduinoCLIUtilizerConfigGenerator;
import arduinocliutilizer.paths.DefaultConfigDirectoryAndFilePath;
import projectfolderpathstorageplugin.ProjectFolderPathNotSetException;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

public class GenerateArduinoCLIUtilizerConfigAction implements IObjectActionDelegate, SelectedFilePathAndContextFinder{

	private Shell shell;
	
	/**
	 * Constructor for Action1.
	 */
	public GenerateArduinoCLIUtilizerConfigAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		
		//IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		//ISelection selection = window.getSelectionService().getSelection("org.eclipse.jdt.ui.PackageExplorer");
		
		// See mechatronicuml-cadapter-component-container/org.muml.arduino.adapter.container.ui/src/org/muml/arduino/adapter/container/ui/popupMenus/AcceleoGenerateArduinoContainerCodeAction.java
		//IFile selectedFile = (IFile) ((IStructuredSelection) selection).toList().get(0);
		//URI modelURI = URI.createPlatformResourceURI(selectedFile.getFullPath().toString(), true);
		//IContainer parent = selectedFile.getParent();
		//IProject project = selectedFile.getProject();
		
		try {
			ProjectFolderPathStorage.projectFolderPath = SelectedFilePathAndContextFinder.getProjectPathOfSelectedFileByRessource();
			ArduinoCLIUtilizerConfigGenerator generator = new ArduinoCLIUtilizerConfigGenerator();
			String completeConfigFilePath = generator.getCompleteConfigFilePath().toString();
			if(generator.checkIfArduinoCLIUtilizerConfigFileExistsAtDefaultLocation()){
				SelectedFilePathAndContextFinder.getProjectOfSelectedFileByRessource().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
				MessageDialog.openInformation(
						shell,
						"ArduinoCLIUtilizer",
						"The ArduinoCLIUtilizer config file already exists!\n"
						+ "See " + completeConfigFilePath
						);
				return;
			}
			
			try{
				boolean generated = generator.generateArduinoCLIUtilizerConfigFile();
				if(generated){
					if(generator.checkIfArduinoCLIUtilizerConfigFileExistsAtDefaultLocation()){
						MessageDialog.openInformation(
								shell,
								"ArduinoCLIUtilizer",
								"Successfully generated the ArduinoCLIUtilizer config file!\n"
								+ "You can find it at " + completeConfigFilePath
								);
					}
					else{
						MessageDialog.openInformation(
							shell,
							"ArduinoCLIUtilizer",
							"Successfully generated the ArduinoCLIUtilizer config file!\n"
							+ "You can find it at " + completeConfigFilePath + "\n"
							+ "But the ArduinoCLI file (arduino-cli) can neither be found at the default path nor\n"
							+ "be accessed.\n"
							+ "Either install the ArduinoCLI there or\n"
							+ "adjust the setting arduinoCLIDirectory!"
							);
					}
					
				}
			}
			catch (IOException e) {
				MessageDialog.openInformation(
					shell,
					"ArduinoCLIUtilizer",
					"IOException occured!\n"
					+ "Please make sure that nothing blocks the generation of\n"
					+ generator.getCompleteConfigFilePath().toString() + "\n"
					+ "and then try again."
					);
				e.printStackTrace();
			}
		} catch (ProjectFolderPathNotSetException e) {
			MessageDialog.openInformation(
					shell,
					"ArduinoCLIUtilizer",
					e.getMessage());
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			// Refresh Project
			try {
				SelectedFilePathAndContextFinder.getProjectOfSelectedFileByRessource().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
