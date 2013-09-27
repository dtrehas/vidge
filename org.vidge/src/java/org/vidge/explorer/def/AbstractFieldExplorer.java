package org.vidge.explorer.def;

import org.vidge.explorer.AbstractPropertyExplorer;
import org.vidge.inface.IPropertyExplorer;

public abstract class AbstractFieldExplorer<T> extends AbstractPropertyExplorer {

	private T obj;

	public AbstractFieldExplorer(T obj, String labelIn) {
		super(false);
		this.obj = obj;
		source = obj;
		label = labelIn;
	}

	@Override
	public String getPropertyName() {
		return label;
	}

	@Override
	public Object getValueInt() {
		return obj;
	}

	@Override
	public Object getValue(Object input) {
		return input;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setValue(Object value) {
		this.obj = (T) value;
	}

	@Override
	public void setValueInt(Object value) {
		obj = (T) value;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AbstractFieldExplorer)) {
			return false;
		}
		AbstractFieldExplorer rhs = (AbstractFieldExplorer) obj;
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
		return false;
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