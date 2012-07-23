package org.vidge.inface;

import java.util.List;

public interface IValuesProvider<T> {

	List<T> getValidvalues(Object source, String context);
}
