package org.vidge.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.langcom.LangUtils;
import org.osgi.framework.Bundle;

public class DSPreferences {

	private static final String LOCK = "#";
	private static final String ENCODING = "UTF-8";
	private static final String DATASOURCE_PROPERTIES = "datasource.properties";
	private static final String NEW_LINE = "\r\n";
	private static final String DATASOURCE = "<datasource>";
	private String filePath;
	private DataSource current;
	private List<DataSource> sourceList = new ArrayList<DataSource>();

	public DSPreferences() throws IOException {
		File newFile = new File("C:\\my\\sss1\\org.vidge.test\\datasource.properties");
		filePath = newFile.getPath();
		load();
	}

	public void load() throws IOException {
		try {
			String text = FileUtils.readFileToString(new File(filePath), ENCODING).trim();
			if ((text != null) && (text.length() > 0)) {
				String[] datasources = text.split(DATASOURCE);
				for (String datasource : datasources) {
					datasource = datasource.trim();
					String[] items = datasource.split(NEW_LINE);
					DataSource source = new DataSource(parseItem(items[0]), parseItem(items[1]), parseItem(items[2]), parseItem(items[3]), Boolean.valueOf(parseItem(items[4])));
					if (source.isCurrent()) {
						current = source;
					} else {
						sourceList.add(source);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String parseItem(String item) {
		int indexOf = item.indexOf("=");
		if (indexOf > 0) {
			return item.substring(indexOf + 1);
		}
		return "";
	}

	public void resolvePath(String bundleName, String fileName) throws IOException {
		Bundle bundle = Platform.getBundle(bundleName);
		URL url = FileLocator.find(bundle, new Path("/" + fileName), null); //$NON-NLS-1$
		filePath = FileLocator.toFileURL(url).getPath();
	}

	public void save() throws FileNotFoundException, IOException {
		StringBuilder builder = new StringBuilder();
		fillDataSource("", builder, current);
		for (DataSource dataSource : sourceList) {
			fillDataSource(LOCK, builder, dataSource);
		}
		new File(filePath).delete();
		new File(filePath).createNewFile();
		FileUtils.writeStringToFile(new File(filePath), builder.toString().trim(), ENCODING);
	}

	private void fillDataSource(String prefix, StringBuilder builder, DataSource dataSource) {
		builder.append(prefix + "name=");
		builder.append(LangUtils.getDefault(dataSource.getName()));
		builder.append(NEW_LINE);
		builder.append(prefix + "url=");
		builder.append(LangUtils.getDefault(dataSource.getURL()));
		builder.append(NEW_LINE);
		builder.append(prefix + "user=");
		builder.append(LangUtils.getDefault(dataSource.getUser()));
		builder.append(NEW_LINE);
		builder.append(prefix + "password=");
		builder.append(LangUtils.getDefault(dataSource.getPassword()));
		builder.append(NEW_LINE);
		builder.append(prefix + "current=");
		builder.append(LangUtils.getDefault(dataSource.isCurrent()));
		builder.append(NEW_LINE);
		builder.append(DATASOURCE);
		builder.append(NEW_LINE);
		builder.append(NEW_LINE);
	}

	public DataSource getCurrent() {
		return current;
	}

	public List<DataSource> getSourceList() {
		return sourceList;
	}

	public List getNameList() {
		List<String> names = new ArrayList<String>();
		if (current != null) {
			names.add(current.getName());
		}
		for (DataSource dataSource : sourceList) {
			names.add(dataSource.getName());
		}
		return names;
	}

	public DataSource getDataSource(String name) {
		for (DataSource dataSource : sourceList) {
			if (dataSource.getName().equals(name)) {
				return dataSource;
			}
		}
		if ((current != null) && current.getName().equals(name)) {
			return current;
		}
		return null;
	}

	public void setCurrent(DataSource current2) {
		if ((current != null) && (!current2.getName().equals(current.getName()))) {
			sourceList.remove(current);
			current.setCurrent(false);
			sourceList.add(current);
		}
		sourceList.remove(current2);
		current = current2;
		current.setCurrent(true);
	}

	public String getFilePath() {
		return filePath;
	}

	public void addDataCurrent(DataSource current2) {
		current = current2;
		sourceList.add(current2);
	}
}
