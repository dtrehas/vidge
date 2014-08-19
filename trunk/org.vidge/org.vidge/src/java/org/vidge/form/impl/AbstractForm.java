package org.vidge.form.impl;

import org.vidge.form.IForm;

/**
 * Interface for building forms may be extended by : <br>
 * <b> IFormInputChangeListener </b>- in case if you want to know where input object changed <br>
 * <b> IFormDataProvider</b> - ij case if you want to retrieve some data by example from database table<br>
 * <b>IFormFactory</b> - in case if you want to control the creation of child entities <br>
 * <b>IColorForm</b> - in case if you want to color a table row or other widget <br>
 * <b>IFormFactory</b> - in case if you want to control the creation of child entities<br>
 * <b>IFormView</b> - in case if you want use specific toString method depends on context
 * 
 * @author user
 * 
 * @param <O>
 */
public abstract class AbstractForm<O> implements IForm<O> {

	protected O input;

	public AbstractForm() {
	}

	public void setInput(O input) {
		this.input = input;
	}

	public O getInput() {
		return input;
	}
}
