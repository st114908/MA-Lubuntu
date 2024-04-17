package arduinocliutilizer.popup.actions;

import java.io.IOException;

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
			ProjectFolderPathStorage.projectFolderPath = SelectedFilePathAndContextFinder.getProjectPathOfSelectedFile();
			ArduinoCLIUtilizerConfigGenerator generator = new ArduinoCLIUtilizerConfigGenerator(); 
			String result = generator.generateConfigFileAndHandleMessageTexts();
			MessageDialog.openInformation(
				shell,
				"ArduinoCLIUtilizer",
				result);
		}
		catch (IOException e) {
			MessageDialog.openInformation(
				shell,
				"ArduinoCLIUtilizer",
				"IOException occured!\n"+
					"Please make sure that nothing blocks the generation of\n"+
						"the directory automatisationConfig and the file arduinoCLIUtilizerConfig.yaml"+
							"and then try again.");
			e.printStackTrace();
		} catch (ProjectFolderPathNotSetException e) {
			MessageDialog.openInformation(
					shell,
					"ArduinoCLIUtilizer",
					e.getMessage());
			e.printStackTrace();
		}
		finally {
			// Refresh Project
			try {
				SelectedFilePathAndContextFinder.getProjectOfSelectedFile().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
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
