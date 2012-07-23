package org.vidge.controls.adapters;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.vidge.PropertyController;

public class FieldAdapterEditableCombo extends FieldAdapterCombo implements KeyListener, ModifyListener {

	public FieldAdapterEditableCombo(PropertyController controllerIn, Composite parent) {
		super(controllerIn, parent);
		combo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				explorer.setValue(getVisualValue());
				controller.inValidate();
			}
		});
		validator = explorer.getValidator();
	}

	@Override
	protected void createControl(Composite parent) {
		super.createControl(parent);
		combo.addModifyListener(this);
		combo.addKeyListener(this);
	}

	@Override
	public void modifyText(ModifyEvent e) {
		// System.out.println("-------------sdf---------" + e);
		String visualValue = (String) getVisualValue();
		if (visualValue != null) {
			valid = validator.validateComplete(visualValue.isEmpty() ? null : visualValue.toString());
			explorer.setValue(validator.getMarshalledValue());
		}
		controller.inValidate();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		try {
			valid = validator.validatePartial(e.character);
			if (!valid && !Character.isISOControl(e.character)) {
				e.doit = false;
				return;
			} else {
				Object visualValue = getVisualValue();
				valid = valid && validator.validateComplete(visualValue == null ? "" : visualValue.toString() + e.character);
				if (!valid && !Character.isISOControl(e.character)) {
					e.doit = false;
					return;
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}
