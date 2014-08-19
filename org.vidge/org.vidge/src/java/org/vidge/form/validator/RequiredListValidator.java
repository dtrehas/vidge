package org.vidge.form.validator;

import java.util.List;

public class RequiredListValidator<F> extends AbstractValidator<List<F>> {

	public RequiredListValidator() {
		setHelp("This list can not be empty");
	}

	@SuppressWarnings("unchecked")
	public boolean validateComplete(Object value) {
		if (value != null) {
			return ((List<F>) value).size() > 0;
		}
		return false;
	}

	public boolean validatePartial(Object value) {
		return true;
	}
}
