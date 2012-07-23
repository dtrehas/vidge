package org.vidge.inface;

import org.eclipse.swt.widgets.Control;

public interface IFieldAdapter {

	public Control getControl();

	public Object getValue();

	public void setValue(Object newValue);

	public void setEnabled(boolean enabled);

	public boolean isEnabled();

	public void clear();

	public String getHelpMessage();

	public boolean isValid();

	public void validate();

	public void inValidate();

	public void refreshControl();

	public boolean isVolatile();
}
