package org.vidge.controls.calendar;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.vidge.controls.calendar.messages"; //$NON-NLS-1$

	private Messages() {
	}
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	public static String DateChooser_day;
	public static String DateChooser_month;
	public static String DateChooser_year;
	public static String DateChooser_hour;
	public static String DateChooser_minute;
	public static String DateChooser_second;
	public static String DateChooser_date_separator;
	public static String DateChooser_time_separator;
}
