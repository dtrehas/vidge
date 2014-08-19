package org.vidge.form.validator;

/**
 * Class NullValidator. A validator that does nothing. The default validator.
 * 
 * @author nemo
 */
public class NullValidator extends AbstractValidator<Object> {

	public NullValidator(String message) {
		setHelp(message);
	}

	public NullValidator() {
		setHelp(Messages.NullValidator_0);
	}

	public boolean validatePartial(Object value) {
		return true;
	}

	public boolean validateComplete(Object value) {
		this.marshalledValue = value;
		return true;
	}
}
