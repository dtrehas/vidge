package org.vidge.controls.adapters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.vidge.PropertyController;
import org.vidge.controls.ObjectWizardField;

/**
 * @author nemo
 */
@SuppressWarnings("rawtypes")
public class FieldAdapterObjectWizard extends AbstractFieldAdapter {

	private ObjectWizardField chooserField;

	public FieldAdapterObjectWizard(PropertyController controller, Composite parent) {
		super(controller, parent);
		chooserField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	@Override
	protected void createControl(Composite parent) {
		chooserField = new ObjectWizardField(parent, SWT.NONE, controller);
	}

	@Override
	public Control getControl() {
		return chooserField;
	}

	public Object getVisualValue() {
		return chooserField.getSelection();
	}

	public void setVisualValue(Object newValue) {
		chooserField.setSelection(newValue);
	}
}
