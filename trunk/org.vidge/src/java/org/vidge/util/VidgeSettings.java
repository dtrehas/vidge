package org.vidge.util;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VidgeSettings {

	private static String filePath;
	private static String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
	private static SimpleDateFormat dateFormat = new SimpleDateFormat(dateTimeFormat);
	private static boolean caseSensitive = true;
	private static StringMatchPolicy stringMatchPolicy = StringMatchPolicy.CONTAINS;

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
					dateTimeFormat = props.getProperty("dateTimeFormat", "yyyy-MM-dd HH:mm:ss");
					dateFormat = new SimpleDateFormat(dateTimeFormat);
					caseSensitive = Boolean.valueOf(props.getProperty("caseSensitive", "true"));
					stringMatchPolicy = StringMatchPolicy.valueOf(props.getProperty("stringMatchPolicy", StringMatchPolicy.CONTAINS.name()));
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

	public static String getDateTimeFormat() {
		return dateTimeFormat;
	}

	public static String formatDate(Date date) {
		return dateFormat.format(date);
	}

	public static StringMatchPolicy getStringMatchPolicy() {
		return stringMatchPolicy;
	}
}
