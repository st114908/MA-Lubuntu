package de.ust.arduinocliutilizer.popup.actions;

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

import de.ust.arduinocliutilizer.worksteps.exceptions.NoArduinoCLIConfigFileException;
import de.ust.arduinocliutilizer.worksteps.functions.ConnectedBoardsFinder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import projectfolderpathstorageplugin.ProjectFolderPathNotSetException;
import projectfolderpathstorageplugin.ProjectFolderPathStorage;

public class ListAllConnectedArduinoBoardsAction implements IObjectActionDelegate, SelectedFilePathAndContextFinder {

	private Shell shell;
	
	/**
	 * Constructor for Action1.
	 */
	public ListAllConnectedArduinoBoardsAction() {
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
		ProjectFolderPathStorage.projectFolderPath = getProjectPathOfSelectedFileByRessource();
		Path targetFilePath = getPathOfSelectedFile();
		try{
			//Search board(s) this way, because this way the text is directly formated easy to read and to copy from.
			ConnectedBoardsFinder ConnectedBoardsFinderInstance = new ConnectedBoardsFinder(targetFilePath);
			String foundBoardsYAMLText = ConnectedBoardsFinderInstance.getResponseFeedback().getNormalFeedback(); 
			
			if(ConnectedBoardsFinderInstance.getNumberOfBoards() == 0){
				MessageDialog.openInformation(
					shell,
					"ArduinoCLIUtilizer: Board search",
					"No boards found!");
				return;
			}
			
			// Based on the second post on https://www.eclipse.org/forums/index.php/t/141647/ by Veronika Irvine
			// Display display = new Display(); //We already have one.
			Display display = Display.getCurrent();
			final Shell shellListWindow = new Shell(display);
			GridLayout gridLayout = new GridLayout(1, true);
			gridLayout.marginWidth = 5;
			gridLayout.marginHeight = 5;
			shellListWindow.setLayout(gridLayout);
	
			final Text text = new Text(shellListWindow, SWT.MULTI | SWT.BORDER | SWT.WRAP
			| SWT.READ_ONLY | SWT.V_SCROLL);
			text.setText(foundBoardsYAMLText);
			GridData gridData = new GridData(GridData.FILL, GridData.FILL, true,
			true);
			text.setLayoutData(gridData);
	
			shellListWindow.setText("ArduinoCLIUtilizer: All connected Arduino boards"); // Sets the title!
			shellListWindow.setBounds(200, 200, 550, 500);
			shellListWindow.open();
			while (!shellListWindow.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}
			//display.dispose(); // Causes crash, but if commented out no problems.
		}
		catch (IOException e) {
			MessageDialog.openInformation(
				shell,
				"ArduinoCLIUtilizer",
				"IOException occured!\n"+
				"Please make sure that none of the infolved files are opened by a program and then try again.");
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			MessageDialog.openInformation(
					shell,
					"ArduinoCLIUtilizer",
					"The process has been interrupted!");
			e.printStackTrace();
		}
		catch (NoArduinoCLIConfigFileException | ProjectFolderPathNotSetException e) {
			MessageDialog.openInformation(
				shell,
				"ArduinoCLIUtilizer",
				e.getMessage());
				e.printStackTrace();
		} 
		finally {
			// Refresh Project
			try {
				getRessourceOfSelectedFile().getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
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
