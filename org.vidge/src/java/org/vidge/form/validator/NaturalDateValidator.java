package org.vidge.form.validator;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.vidge.util.StringUtil;

public class NaturalDateValidator implements IValidator<Date> {

	private static final String PLEASE_SELECT_A_DATE = Messages.NaturalDateValidator_0;
	String message = PLEASE_SELECT_A_DATE;
	private boolean canEmpty;
	private Date maximumDate;
	private Date minimumDate;
	private SimpleDateFormat dateFormat = new SimpleDateFormat(Messages.NaturalDateValidator_date_template);
	private Date newValue;

	public NaturalDateValidator(Date maximumDate, Date minimumDate, boolean canEmpty) {
		this.canEmpty = canEmpty;
		this.maximumDate = maximumDate;
		this.minimumDate = minimumDate;
	}

	public String getHelp() {
		return message;
	}

	public boolean validateComplete(Object value) {
		if (value == null) {
			if (!canEmpty) {
				message = Messages.NaturalDateValidator_1 + StringUtil.SP2 + PLEASE_SELECT_A_DATE;
				return false;
			}
			return true;
		}
		newValue = (Date) value;
		if ((maximumDate != null) && (newValue.getTime() > maximumDate.getTime())) {
			message = Messages.NaturalDateValidator_2 + StringUtil.SP2 + dateFormat.format(maximumDate);
			return false;
		}
		if ((minimumDate != null) && (newValue.getTime() < minimumDate.getTime())) {
			message = Messages.NaturalDateValidator_3 + StringUtil.SP2 + dateFormat.format(minimumDate);
			return false;
		}
		return true;
	}

	public boolean validatePartial(Object value) {
		return true;
	}

	public Date getMarshalledValue() {
		return newValue;
	}
}
