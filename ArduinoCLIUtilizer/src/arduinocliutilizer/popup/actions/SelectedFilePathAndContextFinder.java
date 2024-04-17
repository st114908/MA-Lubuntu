package arduinocliutilizer.popup.actions;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

interface SelectedFilePathAndContextFinder {
	
	public static IResource getRessourceOfSelectedFile(){
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
		final IResource selectedResource = (IResource) selection.getFirstElement();
		return selectedResource;
	}

	public static IProject getProjectOfSelectedFile(){
		IProject result = getRessourceOfSelectedFile().getProject(); 
		return result;
	}
	
	public static Path getProjectPathOfSelectedFile(){
		Path result = Paths.get(getProjectOfSelectedFile().getRawLocation().toString());
		return result;
	}
	

	public static IFile getSelectedFile(){
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
		final IFile selectedFile = (IFile) ((IStructuredSelection) selection).getFirstElement();
		return selectedFile;
	}
	
	public static Path getParentPathOfSelectedFile(){
		Path result = Paths.get(getSelectedFile().getRawLocation().toString()).getParent(); 
		return result;
	}

	public static Path getPathOfSelectedFile(){
		Path result = Paths.get(getSelectedFile().getRawLocation().toString()); 
		return result;
	}

}