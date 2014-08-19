package org.vidge.form.validator;

import java.math.BigDecimal;

public class NumberFieldLengthValidator extends AbstractValidator<String> {

	private BigDecimal tempValue;
	private final int fieldLength;
	public static final String string = Messages.NumberFieldLengthValidator_0;

	public NumberFieldLengthValidator(int fieldLength) {
		this.fieldLength = fieldLength;
		setHelp(string + fieldLength);
	}

	public boolean validateComplete(Object value) {
		if ((value != null) && (value.toString().length() != fieldLength)) {
			setHelp(string + fieldLength);
			return false;
		}
		if (value != null) {
			setMarshalledValue(value.toString());
		}
		return true;
	}

	public boolean validatePartial(Object value) {
		try {
			if (value != null) {
				tempValue = new BigDecimal(value.toString());
				if (value.toString().length() > fieldLength) {
					return false;
				}
				setMarshalledValue(tempValue.toString());
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
