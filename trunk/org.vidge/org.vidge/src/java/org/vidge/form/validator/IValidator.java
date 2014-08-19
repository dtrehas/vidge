package org.vidge.form.validator;

public interface IValidator<T> {

	public boolean validatePartial(Object value);

	public boolean validateComplete(Object value);

	public String getHelp();

	public T getMarshalledValue();
}
