package org.vidge;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.vidge.messages"; //$NON-NLS-1$

	private Messages() {
	}
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	public static String ApplicationActionBarAdvisor_FILE;
	public static String ApplicationActionBarAdvisor_WINDOW;
	public static String ApplicationActionBarAdvisor_HELP;
	public static String CapActionBarAdvisor_Show_View;
	public static String CapActionBarAdvisor_Wizards;
	public static String PlainForm_0;
	public static String VidgeErrorDialog_0;
	public static String VidgeErrorDialog_Err_header;
	public static String VidgeErrorDialog_File_econding;
}
