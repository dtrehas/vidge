package org.vidge.controls.adapters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.vidge.PropertyController;
import org.vidge.controls.editor.ObjectEditor;

/**
 * @author nemo
 */
public class FieldAdapterObjectWizard extends AbstractFieldAdapter {

	private ObjectEditor chooserField;

	public FieldAdapterObjectWizard(PropertyController controller, Composite parent) {
		super(controller, parent);
		chooserField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	@Override
	protected void createControl(Composite parent) {
		chooserField = new ObjectEditor(parent, SWT.NONE, controller);
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
