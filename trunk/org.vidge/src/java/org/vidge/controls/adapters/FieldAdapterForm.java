package org.vidge.controls.adapters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.vidge.FormRegistry;
import org.vidge.PlainForm;
import org.vidge.PropertyController;
import org.vidge.inface.IEntityExplorer;

public class FieldAdapterForm extends AbstractFieldAdapter {

	private PlainForm form;
	private Composite pane;
	private Group group;

	public FieldAdapterForm(PropertyController controller, Composite parent) {
		super(controller, parent);
	}

	@Override
	public Control getControl() {
		return group;
	}

	@Override
	protected void createControl(Composite parent) {
		group = new Group(parent, SWT.SHADOW_IN);
		group.setLayout(new GridLayout());
		group.setText(explorer.getLabel());
		IEntityExplorer entityExplorer = FormRegistry.getEntityExplorer(explorer.getPropertyClass());
		if (explorer.getValue() == null) {
			entityExplorer.createInput();
			explorer.setValue(entityExplorer.getInput());
		} else {
			entityExplorer.setInput(explorer.getValue());
		}
		form = new PlainForm(entityExplorer, true);
		pane = form.getPane(group, SWT.NONE);
	}

	@Override
	public Object getVisualValue() {
		return null;
	}

	@Override
	public void setVisualValue(Object newValue) {
	}
}
