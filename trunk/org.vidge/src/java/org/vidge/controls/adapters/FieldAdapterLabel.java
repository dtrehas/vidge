package org.vidge.controls.adapters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.vidge.PropertyController;

public class FieldAdapterLabel extends AbstractFieldAdapter {

	private Label label;

	public FieldAdapterLabel(PropertyController controller, Composite parent) {
		super(controller, parent);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	@Override
	protected void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.BORDER);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginTop = 0;
		gridLayout.marginBottom = 0;
		gridLayout.marginHeight = 2;
		composite.setLayout(gridLayout);
		label = new Label(composite, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	@Override
	public Control getControl() {
		return label;
	}

	public Object getVisualValue() {
		return label.getText();
	}

	public void setVisualValue(Object newValue) {
		label.setText(newValue == null ? "" : newValue.toString()); //$NON-NLS-1$
	}

	public void clear() {
		label.setText(""); //$NON-NLS-1$
		controller.getExplorer().setValue(null);
	}
}
