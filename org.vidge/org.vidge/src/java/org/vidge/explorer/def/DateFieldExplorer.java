package org.vidge.explorer.def;

import java.util.Date;

public class DateFieldExplorer extends AbstractFieldExplorer<Date> {

	public DateFieldExplorer(Date obj, String labelIn) {
		super(obj, labelIn);
	}

	@Override
	public Class<?> getPropertyClass() {
		return Date.class;
	}
}