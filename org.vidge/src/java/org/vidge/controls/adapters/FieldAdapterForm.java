package org.vidge.controls.adapters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.Section;
import org.vidge.FormRegistry;
import org.vidge.PlainForm;
import org.vidge.PropertyController;
import org.vidge.SharedImages;
import org.vidge.VidgeResources;
import org.vidge.controls.CustomButton;
import org.vidge.inface.IEntityExplorer;

public class FieldAdapterForm extends AbstractFieldAdapter {

	private PlainForm form;
	private Composite pane;
	private Section section;
	private IEntityExplorer entityExplorer;

	public FieldAdapterForm(PropertyController controller, Composite parent) {
		super(controller, parent);
	}

	@Override
	public Control getControl() {
		return section;
	}

	@Override
	protected void createControl(Composite parent) {
		section = new Section(parent, Section.TITLE_BAR | Section.CLIENT_INDENT | Section.TWISTIE | Section.DESCRIPTION);
		section.setLayout(new GridLayout());
		// section.setBackground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_GRAY));
		// section.setBackgroundMode(SWT.INHERIT_FORCE);
		createHeader(explorer.getLabel());
		entityExplorer = FormRegistry.getEntityExplorer(explorer.getPropertyClass());
		entityExplorer.setContext(explorer.getEntityExplorer().getInput());
		form = new PlainForm(entityExplorer, false);
		pane = form.getPane(section, SWT.NONE);
		if (explorer.getValue() == null) {
			form.setEnabled(false);
		} else {
			entityExplorer.checkInput();
			form.inValidate(null);
			form.refreshView();
		}
		section.setClient(pane);
	}

	private void createHeader(String name) {
		Composite composite = new Composite(section, SWT.NONE);
		section.setTextClient(composite);
		section.setText(name);
		GridLayout layout = new GridLayout(3, false);
		layout.marginRight = 2;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if (explorer.getValue() == null) {
			label.setText("                                 ");
		} else {
			label.setText(explorer.getValue().toString());
		}
		CustomButton button = new CustomButton(composite, VidgeResources.getInstance().getImage(SharedImages.ACTION_PLUS2), "Create");
		button.setLayoutData(new GridData(GridData.END));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				entityExplorer.createInput();
				form.setEnabled(true);
				form.inValidate(null);
				form.refreshView();
				controller.inValidate();
				controller.refreshView();
			}
		});
		CustomButton clearButton = new CustomButton(composite, VidgeResources.getInstance().getImage(SharedImages.DELETE), "Delete");
		clearButton.setLayoutData(new GridData(GridData.END));
		clearButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				form.clear();
				entityExplorer.removeInput();
				controller.inValidate();
				controller.refreshView();
				form.setEnabled(false);
			}
		});
	}

	@Override
	public Object getVisualValue() {
		return null;
	}

	@Override
	public void setVisualValue(Object newValue) {
	}
}
