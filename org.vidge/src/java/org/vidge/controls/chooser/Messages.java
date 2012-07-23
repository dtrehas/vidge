package org.vidge.controls.chooser;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String BUNDLE_NAME = "org.vidge.controls.chooser.messages"; //$NON-NLS-1$

	private Messages() {
	}
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	public static String ObjectChooser_FILTERED;
	public static String ObjectChooser_SHOW_FILTER_DIALOG;
	public static String Column_NN;
	public static String Model_SORT_NONE;
	public static String Model_LIST_OF;
	public static String Model_ITEMS;
	public static String Model_FILTERED;
	public static String Model_ITEMS1;
	public static String Model_NONE;
	public static String Model_SORT;
	public static String FilterDialog_FILTERS;
	public static String FilterDialog_CLOSE;
	public static String FilterDialog_CLEAR_FILTERS;
	public static String Filter_ON_OFF_FILTER;
	public static String ObjectChooserField_CLEAR_FILTERS;
	public static String ObjectChooserField_FILTERS;
	public static String ObjectDialog_0;
	public static String PageManager_0;
	public static String PageManager_2;
	public static String PageManager_3;
	public static String Yes;
	public static String No;

	public static String toLocalizedString(boolean value) {
		return value ? Yes : No;
	}
}
