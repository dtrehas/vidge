package org.vidge.controls.adapters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.vidge.PropertyController;
import org.vidge.controls.TextField;

/**
 * @author nemo
 */
public class FieldAdapterPassword extends FieldAdapterText {

	public FieldAdapterPassword(PropertyController controller, Composite parent) {
		super(controller, parent);
	}

	@Override
	protected void createControl(Composite parent) {
		text = new TextField(parent, SWT.PASSWORD | SWT.SINGLE, controller);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addModifyListener(this);
		text.addKeyListener(this);
	}
}
