package org.vidge.explorer.def;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.vidge.explorer.AbstractPropertyExplorer;
import org.vidge.util.StringUtil;

public class StringFieldExplorer extends AbstractPropertyExplorer {

	private String obj;

	public StringFieldExplorer(String obj) {
		super(false);
		this.obj = obj;
		source = obj;
		label = StringUtil.capitalize("string");
	}

	@Override
	public String getPropertyName() {
		return label;
	}

	@Override
	public Class<?> getPropertyClass() {
		return String.class;
	}

	@Override
	public Object getValueInt() {
		return obj;
	}

	@Override
	public Object getValue(Object input) {
		return input;
	}

	@Override
	public void setValue(Object value) {
		this.obj = (String) value;
	}

	@Override
	public void setValueInt(Object value) {
		obj = (String) value;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof StringFieldExplorer)) {
			return false;
		}
		StringFieldExplorer rhs = (StringFieldExplorer) obj;
		return this.obj.equals(rhs.obj);
	}

	@Override
	public int hashCode() {
		return obj.hashCode();
	}

	@Override
	public boolean hasValidValues() {
		return getPropertyClass().isEnum();
	}

	@Override
	public String getDescription() {
		return label;
	}

	@Override
	public Control getEditor(Control parent) {
		Text text = new Text((Composite) parent, SWT.BORDER);
		return text;
	}
}