package org.vidge.controls.calendar;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.widgets.Event;

public class CalendarEvent extends TypedEvent {

	private static final long serialVersionUID = 1748765567728976188L;

	public CalendarEvent(Event event) {
		super(event);
	}

	public CalendarEvent(Date time) {
		super(null);
		data = time;
	}

	public Calendar getCalendar() {
		return (Calendar) this.data;
	}
}
