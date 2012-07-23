package org.vidge.controls.adapters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.vidge.PropertyController;
import org.vidge.controls.TextField;
import org.vidge.util.VisualControlType;

/**
 * @author nemo
 */
public class FieldAdapterText extends AbstractFieldAdapter implements KeyListener, ModifyListener {

	protected TextField text;

	public FieldAdapterText(PropertyController controller, Composite parent) {
		super(controller, parent);
	}

	@Override
	protected void createControl(Composite parent) {
		text = new TextField(parent, ((controller.getControlType() == VisualControlType.TEXTAREA) ? (SWT.WRAP | SWT.MULTI) : SWT.SINGLE), controller);
		if (controller.getControlType() == VisualControlType.TEXTAREA) {
			text.setLayoutData(new GridData(GridData.FILL_BOTH));
		} else {
			text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		}
		text.addModifyListener(this);
		text.addKeyListener(this);
	}

	public void modifyText(ModifyEvent e) {
		String visualValue = (String) getVisualValue();
		valid = validator.validateComplete(visualValue == null ? "" : visualValue.toString());
		explorer.setValue(validator.getMarshalledValue());
		controller.inValidate();
	}

	@Override
	public Control getControl() {
		return text;
	}

	public Object getVisualValue() {
		return text.getText();
	}

	public void setVisualValue(Object newValue) {
		text.setText(newValue == null ? "" : newValue.toString()); //$NON-NLS-1$
	}

	public void keyPressed(KeyEvent e) {
		try {
			valid = validator.validatePartial(e.character);
			if (!valid && !Character.isISOControl(e.character)) {
				e.doit = false;
				return;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void keyReleased(KeyEvent e) {
	}
}
