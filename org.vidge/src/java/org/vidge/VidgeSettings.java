package org.vidge;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vidge.util.StringMatchPolicy;

public class VidgeSettings {

	private static String filePath;
	private static String dateTimeFormatStr = "yyyy-MM-dd kk:mm:ss";
	private static String dateFormatStr = "yyyy-MM-dd";
	private static SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatStr);
	private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat(dateTimeFormatStr);
	private static boolean caseSensitive = true;
	private static StringMatchPolicy stringMatchPolicy = StringMatchPolicy.CONTAINS;
	private static boolean useObjectExplorer = false;
	private static boolean lazyCollections = true;

	public static void init(String filePathIn) {
		filePath = filePathIn;
		load();
	}

	public static void load() {
		if (filePath != null && new File(filePath).exists()) {
			try {
				FileInputStream inStream = new FileInputStream(filePath);
				try {
					Properties props = new Properties();
					props.load(inStream);
					dateFormat = new SimpleDateFormat(props.getProperty("dateFormat", dateFormatStr));
					dateTimeFormat = new SimpleDateFormat(props.getProperty("dateTimeFormat", dateTimeFormatStr));
					caseSensitive = Boolean.valueOf(props.getProperty("caseSensitive", "true"));
					stringMatchPolicy = StringMatchPolicy.valueOf(props.getProperty("stringMatchPolicy", StringMatchPolicy.CONTAINS.name()));
					useObjectExplorer = Boolean.valueOf(props.getProperty("useObjectExplorer", "true"));
					lazyCollections = Boolean.valueOf(props.getProperty("lazyCollections", "true"));
				} finally {
					inStream.close();
				}
			} catch (Exception e) {
				Logger.getLogger(VidgeSettings.class.getName()).log(Level.SEVERE, "***Error by load Vidge settings", e);
			}
		} else {
			Logger.getLogger(VidgeSettings.class.getName()).info("*** Not found file vidge.properties - let's use the default values  " + filePath);
		}
	}

	public static boolean getCaseSensitive() {
		return caseSensitive;
	}

	public static String formatDate(Date date) {
		return dateFormat.format(date);
	}

	public static String formatDateTime(Date date) {
		return dateTimeFormat.format(date);
	}

	public static StringMatchPolicy getStringMatchPolicy() {
		return stringMatchPolicy;
	}

	public static boolean getUseObjectExplorer() {
		return useObjectExplorer;
	}
}
