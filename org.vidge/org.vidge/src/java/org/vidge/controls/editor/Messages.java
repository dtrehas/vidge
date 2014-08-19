package org.vidge.controls.editor;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.vidge.controls.editor.messages"; //$NON-NLS-1$
	public static String ImageEditor_1;
	public static String ImageEditor_2;
	public static String ObjectEditor_0;
	public static String ObjectEditor_1;
	public static String ObjectEditor_2;
	public static String RawResourceDialog_Select_file;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
