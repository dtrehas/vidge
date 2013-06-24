package org.vidge.explorer;

import org.vidge.inface.IPropertyExplorer;
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

	public int compareTo(IPropertyExplorer o) {
		int compareTo = Integer.valueOf(getOrder()).compareTo(Integer.valueOf(o.getOrder()));
		if (compareTo == 0) {
			compareTo = getLabel().compareTo(o.getLabel());
		}
		return compareTo;
	}

	public String getLabel() {
		return label;
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
	public int getOrder() {
		return 9;
	}
}