package org.vidge.controls.adapters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.vidge.PropertyController;
import org.vidge.controls.chooser.PartListEditorField;

/**
 * @author nemo
 */
public class FieldAdapterPartListEditor extends AbstractFieldAdapter {

	private PartListEditorField partListEditorField;

	public FieldAdapterPartListEditor(PropertyController controller, Composite parent) {
		super(controller, parent);
		partListEditorField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	@Override
	protected void createControl(Composite parent) {
		try {
			partListEditorField = new PartListEditorField(parent, SWT.NONE, controller);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public Control getControl() {
		return partListEditorField;
	}

	public Object getVisualValue() {
		return partListEditorField.getSelection();
	}

	public void setVisualValue(Object newValue) {
		partListEditorField.setSelection(newValue);
	}
}
