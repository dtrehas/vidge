package org.vidge.controls.adapters;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.vidge.PropertyController;
import org.vidge.controls.editor.FileEditor;

/**
 * @author nemo
 */
public class FieldAdapterFileEditor extends AbstractFieldAdapter {

	private FileEditor editor;

	public FieldAdapterFileEditor(PropertyController controller, Composite parent) {
		super(controller, parent);
		editor.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	@Override
	protected void createControl(Composite parent) {
		editor = new FileEditor(parent, SWT.NONE, controller, true);
	}

	@Override
	public Control getControl() {
		return editor;
	}

	public Object getVisualValue() {
		return editor.getSelection();
	}

	public void setVisualValue(Object newValue) {
		if (newValue instanceof String) {
			newValue = new File((String) newValue);
		}
		editor.setSelection((File) newValue);
	}
}
