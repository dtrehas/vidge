package org.vidge.form.validator;

import org.vidge.util.StringUtil;

public class DoubleValidator implements IValidator<Double> {

	private static final String HELP = Messages.DoubleValidator_0 + Double.MIN_VALUE + StringUtil.PP + Double.MAX_VALUE;
	private String help = HELP;
	private Double newValue;

	public boolean validatePartial(Object value) {
		help = HELP;
		if (value.getClass().equals(Character.class)) {
			Character character = (Character) value;
			if (Character.isDigit(character)) {
				return true;
			} else if (character.equals('.') || character.equals('-')) {
				return true;
			}
			return false;
		}
		return false;
	}

	public boolean validateComplete(Object value) {
		help = HELP;
		try {
			if (value != null) {
				newValue = Double.valueOf(StringUtil.toString(value));
			}
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
