package org.vidge.form;

import org.vidge.inface.IForm;

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
