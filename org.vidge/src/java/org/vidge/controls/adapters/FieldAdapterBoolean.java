package org.vidge.controls.adapters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.vidge.PropertyController;

public class FieldAdapterBoolean extends AbstractFieldAdapter {

	private Button button;
	protected SelectionListener selectionListener = new SelectionAdapter() {

		public void widgetSelected(SelectionEvent e) {
			explorer.setValue(!(Boolean) explorer.getValue());
			controller.inValidate();
		}
	};

	public FieldAdapterBoolean(PropertyController controller, Composite parent) {
		super(controller, parent);
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.addSelectionListener(selectionListener);
	}

	@Override
	public Control getControl() {
		return button;
	}

	@Override
	protected void createControl(Composite parent) {
		button = new Button(parent, SWT.CHECK);
	}

	public Object getVisualValue() {
		return button.getSelection();
	}

	public void setVisualValue(Object newValue) {
		button.setSelection(newValue == null ? false : ((Boolean) newValue).booleanValue());
	}
}
