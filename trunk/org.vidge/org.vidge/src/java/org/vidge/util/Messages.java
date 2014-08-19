package org.vidge.util;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.vidge.util.messages"; //$NON-NLS-1$
	public static String StringUtil_NNN;
	public static String Yes;
	public static String No;
	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}

	public static String toLocalizedString(boolean value) {
		return value ? Yes : No;
	}
}
