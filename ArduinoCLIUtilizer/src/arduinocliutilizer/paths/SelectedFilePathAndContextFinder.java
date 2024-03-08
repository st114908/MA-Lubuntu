package arduinocliutilizer.paths;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public interface SelectedFilePathAndContextFinder {
	
	public static IFile findSelectedFile() {
		// From VonC's answer on https://stackoverflow.com/questions/585802/how-to-get-the-selected-node-in-the-package-explorer-from-an-eclipse-plugin
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		ISelection selection = window.getSelectionService().getSelection("org.eclipse.jdt.ui.PackageExplorer");
		// See mechatronicuml-cadapter-component-container/org.muml.arduino.adapter.container.ui/src/org/muml/arduino/adapter/container/ui/popupMenus/AcceleoGenerateArduinoContainerCodeAction.java
		IFile selectedFile = (IFile) ((IStructuredSelection) selection).toList().get(0);
		return selectedFile;
	}

	public static String getProjectOfSelectedFile(){
		IFile selectedFile = findSelectedFile();
		String result = selectedFile.getProject().getLocation().toString(); 
		return result;
	}

	public static String getLocationOfSelectedFile(){
		IFile selectedFile = findSelectedFile();
		String result = selectedFile.getLocation().toString(); 
		return result;
	}
	
	public static String getParentOfSelectedFile(){
		IFile selectedFile = findSelectedFile();
		String result = selectedFile.getParent().getLocation().toString(); 
		return result;
	}
	
	public static String getCompiledFilesDirectory(){
		return SelectedFilePathAndContextFinder.getParentOfSelectedFile() + "/CompiledFiles";
	}
	
	
}
