package org.vidge.controls.adapters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.vidge.PropertyController;
import org.vidge.controls.editor.ResourceEditor;

public class FieldAdapterImage extends AbstractFieldAdapter {

	private ResourceEditor editor;

	public FieldAdapterImage(PropertyController controller, Composite parent) {
		super(controller, parent);
	}

	@Override
	protected void createControl(Composite parent) {
		editor = new ResourceEditor(parent, SWT.NONE, controller);
	}

	@Override
	public Control getControl() {
		return editor;
	}

	public Object getVisualValue() {
		return editor.getSelection();
	}

	public void setVisualValue(Object newValue) {
		editor.setSelectionInt((byte[]) newValue);
	}

	@Override
	public boolean isVolatile() {
		return false;
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		editor.setEnabled(enabled);
	}

	@Override
	public void setToolTipText(String string) {
		editor.setText(string);
	}

	@Override
	public void refreshControl() {
		setVisualValue(controller.getExplorer().getValue());
	}

	@Override
	public void inValidate() {
	}
}
