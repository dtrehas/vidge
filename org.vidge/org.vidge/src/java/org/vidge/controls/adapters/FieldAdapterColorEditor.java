package org.vidge.controls.adapters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.vidge.PropertyController;
import org.vidge.controls.editor.ColorEditor;

/**
 * @author nemo
 */
public class FieldAdapterColorEditor extends AbstractFieldAdapter {

	private ColorEditor editor;

	public FieldAdapterColorEditor(PropertyController controller, Composite parent) {
		super(controller, parent);
	}

	@Override
	protected void createControl(Composite parent) {
		editor = new ColorEditor(parent, SWT.NONE, controller);
		editor.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	@Override
	public Control getControl() {
		return editor;
	}

	@Override
	public Object getVisualValue() {
		return editor.getSelection();
	}

	@Override
	public void setVisualValue(Object newValue) {
		editor.setSelection((Color) newValue);
	}
}
