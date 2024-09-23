package de.ust.pipelineexecution.ui.exports;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class InfoWindow extends WizardPage {
	private Button dummyButton;
	private String message;

	public InfoWindow(String pageTitle, String title, String message) {
		super(pageTitle);
		setTitle(pageTitle);
		setDescription("Important information");
		this.message = message;
	}
	
	@Override
	public void createControl(Composite parent) {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;

		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(gridLayout);

		dummyButton = new Button(container, SWT.RADIO);
		dummyButton.setText(message);

		setControl(container);
	}

}
