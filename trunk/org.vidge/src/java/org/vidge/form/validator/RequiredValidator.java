package org.vidge.form.validator;

import org.vidge.util.StringUtil;

@SuppressWarnings("rawtypes")
public class RequiredValidator implements IValidator {

	private static final String MESSAGE = Messages.RequiredValidator_0;
	private final IValidator validator;
	private String help;
	private Object value;

	public RequiredValidator(IValidator validator) {
		this.validator = validator;
	}

	public boolean validatePartial(Object value) {
		return (validator != null ? validator.validatePartial(value) : true);
	}

	public boolean validateComplete(Object value) {
		this.value = value;
		if (value == null || ((value instanceof String) && StringUtil.isEmpty(value.toString()))) {
			help = MESSAGE;
			if (validator != null) {
				validator.validateComplete(null);
			}
			return false;
		}
		help = StringUtil.NN;
		return (validator != null ? validator.validateComplete(value) : true);
	}

	public String getHelp() {
		return help + StringUtil.SP2 + (validator != null ? validator.getHelp() : StringUtil.NN);
	}

	public Object getMarshalledValue() {
		return (validator != null ? validator.getMarshalledValue() : value);
	}
}
