package org.vidge.form.validator;

import org.vidge.util.StringUtil;

public class DoubleValidator implements IValidator<Double> {

	private static final String HELP = Messages.DoubleValidator_0 + Double.MIN_VALUE + StringUtil.PP + Double.MAX_VALUE;
	private String help = HELP;
	private Double newValue;

	public boolean validatePartial(Object value) {
		help = HELP;
		return StringUtil.toString(value).matches("^\\-?[0-9]*\\.?[0-9]*([0-9]+[E|e]\\-?([0-9]+\\.)?[0-9]*)?$"); //$NON-NLS-1$
	}

	public boolean validateComplete(Object value) {
		help = HELP;
		try {
			newValue = Double.parseDouble(StringUtil.toString(value));
			return true;
		} catch (Exception e) {
			help = e.getMessage();
			return false;
		}
	}

	public String getHelp() {
		return help;
	}

	public Double getMarshalledValue() {
		return newValue;
	}
}
