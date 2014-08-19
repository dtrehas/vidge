package org.vidge.form.validator;

import org.vidge.util.StringUtil;

public abstract class AbstractValidator<T> implements IValidator<T> {

	protected T marshalledValue;
	private String message = StringUtil.NN;

	public String getHelp() {
		return message;
	}

	protected void setHelp(String mess) {
		this.message = mess;
	}

	protected void clearHelp() {
		message = StringUtil.NN;
	}

	public T getMarshalledValue() {
		return marshalledValue;
	}

	protected void setMarshalledValue(T newValue) {
		marshalledValue = newValue;
	}
}
