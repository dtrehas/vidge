package org.vidge.controls.chooser;

import java.util.List;

public interface IFilter<T> {

	public List<T> filter(List<T> list);
}
