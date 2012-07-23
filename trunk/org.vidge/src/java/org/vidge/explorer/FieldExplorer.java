package org.vidge.explorer;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.vidge.inface.IPropertyExplorer;
import org.vidge.util.StringUtil;
import org.vidge.util.TypeUtil;

public class FieldExplorer extends AbstractPropertyExplorer {

	private final Field field;

	public FieldExplorer(Object obj, Field field, boolean allowChild) {
		super(allowChild);
		this.field = field;
		source = obj;
		label = StringUtil.capitalize(field.getName());
		field.setAccessible(true);
	}

	@Override
	public String getPropertyName() {
		return field.getType().getName();
	}

	@Override
	public Class<?> getPropertyClass() {
		return field.getType();
	}

	@Override
	public Object getValueInt() {
		return invokeInternal();
	}

	@Override
	public Object getValue(Object input) {
		try {
			return field.get(input);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void setValueInt(Object value) {
		if (value != null) {
			Class<?> clazz = field.getType();
			if (!clazz.equals(value.getClass())) {
				value = TypeUtil.convertValue(value, clazz);
			}
		}
		try {
			field.set(source, value);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FieldExplorer)) {
			return false;
		}
		FieldExplorer rhs = (FieldExplorer) obj;
		return this.field.equals(rhs.field);
	}

	@Override
	public int hashCode() {
		return field.hashCode();
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

	private Object invokeInternal() {
		try {
			return field.get(source);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Type getPropertyType() {
		return field.getGenericType();
	}
}