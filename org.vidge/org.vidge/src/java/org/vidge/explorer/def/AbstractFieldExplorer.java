package org.vidge.explorer.def;

import org.vidge.explorer.AbstractPropertyExplorer;

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

	@Override
	public boolean hasValidValues() {
		return false;
	}

	@Override
	public String getDescription() {
		return label;
	}
}