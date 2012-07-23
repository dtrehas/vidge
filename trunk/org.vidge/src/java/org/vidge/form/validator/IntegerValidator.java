package org.vidge.form.validator;

public class IntegerValidator implements IValidator<Integer> {

	private LongValidator longValidator;

	public IntegerValidator() {
		this(Integer.MAX_VALUE, Integer.MIN_VALUE);
	}

	public IntegerValidator(Integer maxValue, Integer minValue) {
		longValidator = new LongValidator(Long.valueOf(maxValue), Long.valueOf(minValue));
	}

	public boolean validatePartial(Object value) {
		return longValidator.validatePartial(value);
	}

	public boolean validateComplete(Object value) {
		return longValidator.validateComplete(value);
	}

	public String getHelp() {
		return longValidator.getHelp();
	}

	public Integer getMarshalledValue() {
		return longValidator.getMarshalledValue() == null ? null : longValidator.getMarshalledValue()
			.intValue();
	}
}
