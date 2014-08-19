package org.vidge.util;

import java.text.NumberFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.ui.PlatformUI;
import org.vidge.VidgeSettings;

public class StringUtil {

	private static final int KB_BYTES = 1024;
	private static final int MB_BYTES = 1024 * 1024;
	private static final int GB_BYTES = 1024 * 1024 * 1024;
	private static final String BYTES = " bytes";
	private static final String KB = " KB";
	private static final String MB = " MB";
	private static final String GB = " Gb";
	public static final String Num = Messages.StringUtil_NNN;
	public static final String NN = "";
	public static final String DDD = "[...]";
	public static final String SP3 = "   ";
	public static final String SP2 = "  ";
	public static final String SP = " ";
	public static final String PP = " ..";
	public static final String DOTSP = " . ";
	public static final String DASH = " - ";
	public static final String NL = "\n";
	public static final String SEM = " ; ";
	public static final String SEMICOLON = ";";
	public static final String DDOT = " : ";
	public static final String DOT = ".";
	public static final String TAB = "\t";
	public static final String CSV = ".csv";
	public static final String NULL = "null";

	public static String toString(Object obj) {
		return obj == null ? NN : obj.toString();
	}

	public static String defaultIfEmpty(Object value) {
		return defaultIfEmpty(value, NN);
	}

	public static String defaultIfEmpty(Object value, String defaultString) {
		if (value == null) {
			return defaultString;
		}
		if (value.toString().trim().length() == 0) {
			return defaultString;
		}
		return value.toString();
	}

	public static String capitalize(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return str;
		}
		return new StringBuffer(strLen).append(Character.toTitleCase(str.charAt(0))).append(str.substring(1)).toString();
	}

	public static String buildErrorTraceString(Throwable e) {
		StringBuilder builder = new StringBuilder();
		getStatusInt(e, builder, 0);
		return builder.toString();
	}

	private static void getStatusInt(Throwable e, StringBuilder builder, int level) {
		builder.append(e + SEM);
		builder.append(repeat(SP2, level) + e.getMessage() + NL);
		for (StackTraceElement element : e.getStackTrace()) {
			builder.append(repeat(SP2, level) + SP2 + element.toString() + NL);
		}
		if (e.getCause() != null) {
			getStatusInt(e.getCause(), builder, ++level);
		}
	}

	public static String repeat(String str, int level) {
		StringBuilder builder = new StringBuilder();
		for (int a = 0; a < level; a++) {
			builder.append(str);
		}
		return builder.toString();
	}

	// private FontRegistry registry;
	public static Font getBoldFont(Font font) {
		// if(registry == null) {
		// registry = new FontRegistry(PlatformUI.getWorkbench().getDisplay());
		// }
		// registry.put("button-text", new FontData[]{new FontData("Arial", 9, SWT.BOLD)} );
		// registry.put("code", new FontData[]{new FontData("Courier New", 10, SWT.NORMAL)});
		FontData[] data = font.getFontData();
		for (int i = 0; i < data.length; i++) {
			data[i].setStyle(SWT.BOLD);
		}
		return new Font(PlatformUI.getWorkbench().getDisplay(), data);
	}

	public static Font getBoldFont(int height, boolean isBold) {
		Font font = PlatformUI.getWorkbench().getDisplay().getSystemFont();
		FontData[] data = font.getFontData();
		for (int i = 0; i < data.length; i++) {
			if (isBold) {
				data[i].setStyle(SWT.BOLD);
			}
			data[i].setHeight(height);
		}
		return new Font(PlatformUI.getWorkbench().getDisplay(), data);
	}

	public static boolean isEmpty(String label) {
		return label == null || label.trim().length() == 0;
	}

	public static boolean match(String string, String value) {
		if (!VidgeSettings.getCaseSensitive()) {
			string = string.toLowerCase();
			value = value.toLowerCase();
		}
		switch (VidgeSettings.getStringMatchPolicy()) {
			case CONTAINS:
				return string.contains(value);
			case ENDS:
				return string.endsWith(value);
			case STARTS:
				return string.startsWith(value);
			default:
				return string.contains(value);
		}
	}

	public static Object toLocalizedString(Boolean value) {
		return Messages.toLocalizedString(value);
	}

	public static String formatSize(long longSize, int decimalPos) {
		NumberFormat fmt = NumberFormat.getNumberInstance();
		if (decimalPos >= 0) {
			fmt.setMaximumFractionDigits(decimalPos);
		}
		double size = longSize;
		double val = size / GB_BYTES;
		if (val > 1) {
			return fmt.format(val).concat(GB);
		}
		val = size / MB_BYTES;
		if (val > 1) {
			return fmt.format(val).concat(MB);
		}
		val = size / KB_BYTES;
		if (val > 1) {
			return fmt.format(val).concat(KB);
		}
		return fmt.format(size).concat(BYTES);
	}
}
