package org.vidge.form;

import java.util.Comparator;

import org.vidge.inface.IPropertyExplorer;

public interface IFormComparator<T> extends Comparator<T> {

	public void init(IPropertyExplorer explorer, boolean asc);
}
