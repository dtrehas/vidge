package org.vidge.controls.adapters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.vidge.PropertyController;
import org.vidge.controls.editor.FontEditor;

/**
 * @author nemo
 */
public class FieldAdapterFontEditor extends AbstractFieldAdapter {

	private FontEditor editor;

	public FieldAdapterFontEditor(PropertyController controller, Composite parent) {
		super(controller, parent);
		editor.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	@Override
	protected void createControl(Composite parent) {
		editor = new FontEditor(parent, SWT.NONE, controller);
	}

	@Override
	public Control getControl() {
		return editor;
	}

	public Object getVisualValue() {
		return editor.getSelection();
	}

	public void setVisualValue(Object newValue) {
		editor.setSelection((Font) newValue);
	}
}
