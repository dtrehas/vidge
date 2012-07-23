package org.vidge.form.validator;


/**
 * Class ReadOnlyValidator.  Using this validator makes a field read-only, 
 * regardless of the read/write status of the user interface control.
 * 
 * @author djo
 */
public class ReadOnlyValidator implements IValidator {
    
    private static ReadOnlyValidator readOnlyValidator = null;
	private Object value;
    
    public static ReadOnlyValidator getDefault() {
        if (readOnlyValidator == null)
            readOnlyValidator = new ReadOnlyValidator();
        return readOnlyValidator;
    }
    
    public boolean validatePartial(Object value) {
        return false;
    }

    public boolean validateComplete(Object value) {
        this.value = value;
        return true;
    }

    public String getHelp() {
        return Messages.ReadOnlyValidator_0;
    }

	public Object getMarshalledValue() {
		return value;
	}

}
