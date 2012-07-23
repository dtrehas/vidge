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
public class FieldAdapterObjectEditor extends AbstractFieldAdapter {

	private ObjectEditor editor;

	public FieldAdapterObjectEditor(PropertyController controller, Composite parent) {
		super(controller, parent);
		editor.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	@Override
	protected void createControl(Composite parent) {
		editor = new ObjectEditor(parent, SWT.NONE, controller);
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		editor.setEnabled(enabled);
	}

	@Override
	public void setToolTipText(String string) {
		editor.getText().setText(string);
	}

	@Override
	public Control getControl() {
		return editor;
	}

	public Object getVisualValue() {
		return editor.getSelection();
	}

	@SuppressWarnings("unchecked")
	public void setVisualValue(Object newValue) {
		editor.refresh(newValue);
	}

	@Override
	public boolean isVolatile() {
		return false;
	}

	@Override
	public void refreshControl() {
		setVisualValue(controller.getExplorer().getValue());
	}

	@Override
	public void inValidate() {
	}
}
