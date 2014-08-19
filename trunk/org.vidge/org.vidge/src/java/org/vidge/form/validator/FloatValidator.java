package org.vidge.form.validator;

import org.vidge.util.StringUtil;

public class FloatValidator implements IValidator<Float> {

	private static final String HELP = Messages.FloatValidator_0 + Float.MIN_VALUE + StringUtil.PP + Float.MAX_VALUE;
	private String help = HELP;
	private Float newValue;

	public boolean validatePartial(Object value) {
		help = HELP;
		return StringUtil.toString(value).matches("^\\-?[0-9]*\\.?[0-9]*([0-9]+[E|e]\\-?([0-9]+\\.)?[0-9]*)?$"); //$NON-NLS-1$
	}

	public boolean validateComplete(Object value) {
		help = HELP;
		try {
			newValue = Float.parseFloat(StringUtil.toString(value));
			return true;
		} catch (Exception e) {
			help = e.getMessage();
			return false;
		}
	}

	public String getHelp() {
		return help;
	}

	public Float getMarshalledValue() {
		return newValue;
	}
}
