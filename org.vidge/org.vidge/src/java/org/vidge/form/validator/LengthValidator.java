package org.vidge.form.validator;

import org.vidge.util.StringUtil;

public class LengthValidator implements IValidator<Object> {

	private static final String PLEASE_ENTER_A_STRING = Messages.LengthValidator_0;
	String message;
	protected String newValue;
	private final Long maxLength;
	private final Long minLength;
	private IValidator<?> validator;

	public LengthValidator(IValidator<?> validator, Long maxLength, Long minLength) {
		this(maxLength, minLength);
		this.validator = validator;
	}

	public LengthValidator(Long maxLength, Long minLength) {
		this.maxLength = maxLength;
		this.minLength = minLength;
		assignMessage();
	}

	public LengthValidator(Integer maxLength, Integer minLength) {
		this.maxLength = (maxLength == null ? null : Long.valueOf(maxLength));
		this.minLength = (minLength == null ? null : Long.valueOf(minLength));
		assignMessage();
	}

	private void assignMessage() {
		message = PLEASE_ENTER_A_STRING + StringUtil.SP3 + Messages.LengthValidator_1 + StringUtil.SP3 + StringUtil.defaultIfEmpty(minLength) + StringUtil.DASH + StringUtil.defaultIfEmpty(maxLength)
			+ StringUtil.SP3 + Messages.LengthValidator_3;
	}

	public String getHelp() {
		return message + StringUtil.SP2 + (validator != null ? validator.getHelp() : StringUtil.NN);
	}

	public boolean validateComplete(Object value) {
		newValue = (value == null ? null : value.toString());
		if ((value == null) || (value.equals(StringUtil.NN) || (value.toString().trim().length() == 0))) {
			newValue = null;
			return true;
		}
		if (minLength != null) {
			try {
				if (newValue.length() < minLength) {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		}
		if (maxLength != null) {
			if (newValue.length() > maxLength) {
				// newValue = newValue.substring(0, maxLength.intValue());
				return false;
			}
		}
		if (validator != null) {
			return validator.validateComplete(value);
		}
		return true;
	}

	public boolean validatePartial(Object value) {
		try {
			if (value == null) {
				newValue = null;
				return true;
			}
			newValue = newValue + value.toString().trim();
			if (maxLength != null) {
				if (newValue.length() > maxLength) {
					return false;
				}
			}
			if (validator != null) {
				return validator.validatePartial(value);
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public Object getMarshalledValue() {
		if (validator != null) {
			validator.validateComplete(newValue);
			return validator.getMarshalledValue();
		}
		return newValue;
	}
}
