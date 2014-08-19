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
import org.vidge.util.StringUtil;
import org.vidge.util.VisualControlType;

/**
 * @author nemo
 */
public class FieldAdapterNumber extends AbstractFieldAdapter implements KeyListener, ModifyListener {

	protected TextField text;

	public FieldAdapterNumber(PropertyController controller, Composite parent) {
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

	@Override
	public void modifyText(ModifyEvent e) {
		String visualValue = (String) getVisualValue();
		valid = validator.validateComplete(visualValue.isEmpty() ? null : visualValue.toString());
		explorer.setValue(validator.getMarshalledValue());
		controller.inValidate();
	}

	@Override
	public Control getControl() {
		return text;
	}

	@Override
	public Object getVisualValue() {
		return text.getText();
	}

	@Override
	public void setVisualValue(Object newValue) {
		text.setText(newValue == null ? StringUtil.NN : newValue.toString()); //$NON-NLS-1$
	}

	@Override
	public void keyPressed(KeyEvent e) {
		valid = validator.validatePartial(e.character);
		if (!valid && !Character.isISOControl(e.character)) {
			e.doit = valid;
			return;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		Object visualValue = getVisualValue();
		if (visualValue != null && visualValue.toString().length() > 0 && !visualValue.toString().equals("-") && !visualValue.toString().equals(".")) {
			valid = validator.validateComplete(visualValue == null ? StringUtil.NN : visualValue.toString());
			if (!valid) {
				setVisualValue(validator.getMarshalledValue());
			}
		} else {
			validator.validateComplete(StringUtil.NN);
		}
	}
}
