package org.vidge.form;

import org.vidge.util.ValueAction;

public interface IFormInputChangeListener {

	/**
	 * Allows an none saved members of input entity
	 * 
	 * @return
	 */
	boolean allowParts();

	/**
	 * Just remember if you use an embedded forms - do not forget to save it inputs here
	 * 
	 * @param value
	 * @param action
	 * @param attribute
	 * @return
	 */
	Object doInputChanged(Object value, ValueAction action, String attribute);
}
