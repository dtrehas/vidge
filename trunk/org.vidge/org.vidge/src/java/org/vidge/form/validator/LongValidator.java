package org.vidge.form.validator;

import org.vidge.util.StringUtil;

public class LongValidator implements IValidator<Long> {

	private String help;
	private Long marshaledValue;
	private final Long max;
	private final Long min;

	public LongValidator() {
		this(Long.MAX_VALUE, Long.MIN_VALUE);
	}

	public LongValidator(Long max, Long min) {
		this.max = max;
		this.min = min;
		help = Messages.LongValidator_0 + StringUtil.toString(max) + StringUtil.PP + StringUtil.toString(min);
	}

	public boolean validateComplete(Object value) {
		if ((value == null) || (value.equals(StringUtil.NN))) {
			marshaledValue = null;
			return true;
		}
		try {
			marshaledValue = Long.parseLong(value + StringUtil.NN);
			if ((min != null) && (min > marshaledValue)) {
				return false;
			}
			if (max != null && max < marshaledValue) {
				return false;
			}
			return true;
		} catch (Exception e) {
			help = e.getMessage();
			return false;
		}
	}

	public boolean validatePartial(Object value) {
		if (!StringUtil.toString(value).matches("^\\-?[0-9]*$")) { //$NON-NLS-1$
			return false;
		}
		return true;
	}

	public String getHelp() {
		return help;
	}

	public Long getMarshalledValue() {
		return marshaledValue;
	}
}
