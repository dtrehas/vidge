package org.vidge.controls.adapters;

import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.vidge.PropertyController;
import org.vidge.controls.DateEditor;

/**
 * @author nemo
 */
public class FieldAdapterDateEditor extends AbstractFieldAdapter {

	private DateEditor editor;
	private ModifyListener listener = new ModifyListener() {

		public void modifyText(ModifyEvent e) {
			validateAndSave();
		}
	};

	public FieldAdapterDateEditor(PropertyController controller, Composite parent) {
		super(controller, parent);
		editor.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		editor.addModifyListener(listener);
	}

	@Override
	protected void createControl(Composite parent) {
		editor = new DateEditor(parent, SWT.NONE);
	}

	@Override
	public Control getControl() {
		return editor;
	}

	public Object getVisualValue() {
		return editor.getDate();
	}

	public void setVisualValue(Object newValue) {
		editor.setDate((Date) newValue);
	}
}
