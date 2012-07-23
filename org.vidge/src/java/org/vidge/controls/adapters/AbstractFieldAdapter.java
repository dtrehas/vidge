package org.vidge.controls.adapters;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.vidge.PropertyController;
import org.vidge.form.validator.IValidator;
import org.vidge.form.validator.ReadOnlyValidator;
import org.vidge.inface.IPropertyExplorer;
import org.vidge.util.StringUtil;

@SuppressWarnings("rawtypes")
public abstract class AbstractFieldAdapter {

	protected boolean valid = true;
	protected IPropertyExplorer explorer;
	protected PropertyController controller;
	protected IValidator validator;

	protected abstract void createControl(Composite parent);

	public abstract Control getControl();

	public abstract Object getVisualValue();

	public abstract void setVisualValue(Object newValue);

	public AbstractFieldAdapter(PropertyController controller, Composite parent) {
		this.controller = controller;
		this.explorer = controller.getExplorer();
		createControl(parent);
		getControl().setToolTipText(explorer.getDescription());
		validator = explorer.getValidator();
		refreshControl();
		getControl().addFocusListener(controller);
	}

	public boolean isVolatile() {
		return true;
	}

	public void clear() {
		controller.getExplorer().setValue(null);
		valid = validator.validateComplete(null);
		setVisualValue(null);
		controller.inValidate();
	}

	public void refreshControl() {
		inValidate();
		setVisualValue(validator.getMarshalledValue());
	}

	public String getHelpMessage() {
		return (validator != null) ? explorer.getLabel() + StringUtil.DDOT + validator.getHelp() : explorer.getLabel(); //$NON-NLS-1$ 
	}

	public void inValidate() {
		valid = validator.validateComplete(explorer.getValue());
	}

	public void validateAndSave() {
		if (validator instanceof ReadOnlyValidator) {
			return;
		}
		inValidate();
		explorer.setValue(validator.getMarshalledValue());
		controller.inValidate();
	}

	public void setEnabled(boolean enabled) {
		getControl().setEnabled(enabled);
	}

	public boolean isEnabled() {
		return getControl().isEnabled();
	}

	public boolean isValid() {
		return valid;
	}

	public void setToolTipText(String string) {
	}
}
