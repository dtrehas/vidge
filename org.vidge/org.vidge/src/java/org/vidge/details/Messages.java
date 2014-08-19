package org.vidge.details;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.vidge.details.messages"; //$NON-NLS-1$

	private Messages() {
	}
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	public static String DetailsView_NO_SELECTED_OBJECTS;
}
