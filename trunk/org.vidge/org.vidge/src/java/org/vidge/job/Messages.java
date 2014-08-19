package org.vidge.job;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.vidge.job.messages"; //$NON-NLS-1$

	private Messages() {
	}
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	public static String DownloadJob_0;
	public static String DownloadJob_1;
	public static String DownloadJob_2;
	public static String SaveJob_0;
	public static String SaveJob_1;
	public static String SaveJob_2;
	public static String WorkJob_0;
	public static String WorkJob_1;
	public static String WorkJob_2;
}
