package org.vidge.controls.chooser;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.Date;

import org.vidge.inface.IPropertyExplorer;

public class DefaultTableComparator<T> implements Comparator<T> {

	protected boolean asc;
	protected IPropertyExplorer explorer;

	public void init(IPropertyExplorer explorer, boolean asc) {
		this.explorer = explorer;
		this.asc = asc;
	}

	@Override
	public int compare(T o1, T o2) {
		int result = 0;
		if (explorer == null) {
			return o1.toString().compareTo(o2.toString());
		}
		Object value = explorer.getValue(o1);
		Object value2 = explorer.getValue(o2);
		if (value == null) {
			if (value2 == null) {
				return 0;
			} else {
				return -1;
			}
		} else {
			if (value2 == null) {
				return 1;
			}
		}
		Class<?> klass = explorer.getPropertyClass();
		if (klass.equals(String.class)) {
			if (asc) {
				result = value.toString().compareTo(value2.toString());
			} else {
				result = value2.toString().compareTo(value.toString());
			}
		} else if (klass.equals(Integer.class)) {
			if (asc) {
				result = ((Integer) value).compareTo(((Integer) value2));
			} else {
				result = ((Integer) value2).compareTo(((Integer) value));
			}
		} else if (klass.equals(Long.class)) {
			if (asc) {
				result = ((Long) value).compareTo(((Long) value2));
			} else {
				result = ((Long) value2).compareTo(((Long) value));
			}
		} else if (klass.equals(Boolean.class)) {
			if (asc) {
				result = ((Boolean) value).compareTo(((Boolean) value2));
			} else {
				result = ((Boolean) value2).compareTo(((Boolean) value));
			}
		} else if (klass.equals(BigDecimal.class)) {
			if (asc) {
				result = ((BigDecimal) value).compareTo(((BigDecimal) value2));
			} else {
				result = ((BigDecimal) value2).compareTo(((BigDecimal) value));
			}
		} else if (klass.equals(Date.class)) {
			if (asc) {
				result = ((Date) value).compareTo(((Date) value2));
			} else {
				result = ((Date) value2).compareTo(((Date) value));
			}
		} else if (klass.equals(Double.class)) {
			if (asc) {
				result = ((Double) value).compareTo(((Double) value2));
			} else {
				result = ((Double) value2).compareTo(((Double) value));
			}
		} else if (klass.equals(Float.class)) {
			if (asc) {
				result = ((Float) value).compareTo(((Float) value2));
			} else {
				result = ((Float) value2).compareTo(((Float) value));
			}
		} else if (klass.equals(BigInteger.class)) {
			if (asc) {
				result = ((BigInteger) value).compareTo(((BigInteger) value2));
			} else {
				result = ((BigInteger) value2).compareTo(((BigInteger) value));
			}
		} else {
			if (asc) {
				result = value.toString().compareTo(value2.toString());
			} else {
				result = value2.toString().compareTo(value.toString());
			}
		}
		return result;
	}
}
