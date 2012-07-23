package org.vidge.form.validator;

import java.math.BigDecimal;

import org.vidge.util.StringUtil;

public class BigDecimalValidator implements IValidator<BigDecimal> {

	private static final String HELP = Messages.BigDecimalValidator_0 + Integer.MIN_VALUE + StringUtil.PP + Integer.MAX_VALUE;
	private String help = HELP;
	private BigDecimal newValue;

	public boolean validatePartial(Object value) {
		help = HELP;
		return StringUtil.toString(value).matches("^\\-?[0-9]*\\.?[0-9]*([0-9]+[E|e]\\-?([0-9]+\\.)?[0-9]*)?$"); //$NON-NLS-1$
	}

	public boolean validateComplete(Object value) {
		help = HELP;
		try {
			if ((value == null) || (value.toString().length() == 0)) {
				return true;
			}
			newValue = new BigDecimal(StringUtil.toString(value));
			return true;
		} catch (Exception e) {
			help = Messages.BigDecimalValidator_3 + e.getMessage();
			return false;
		}
	}

	public String getHelp() {
		return help;
	}

	public BigDecimal getMarshalledValue() {
		return newValue;
	}
}
