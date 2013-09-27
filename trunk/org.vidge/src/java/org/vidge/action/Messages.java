package org.vidge.action;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.vidge.actions.messages"; //$NON-NLS-1$

	private Messages() {
	}
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	public static String BusinessAction_0;
	public static String BusinessAction_1;
	public static String BusinessAction_2;
	public static String BusinessAction_3;
	public static String ADD;
	public static String REMOVE;
	public static String EDIT;
	public static String REFRESH;
}
