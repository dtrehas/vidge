package org.vidge.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * This class is a container for information about database connection needed while create connections
 * 
 * @author nemo
 * 
 */
public class DataSource {

	private String url;
	private String user;
	private String password;
	private String contextId;
	private String name = "";
	private boolean current;
	private Integer inxalue = 9;

	public DataSource(String name, String url, String user, String password, boolean current) {
		setURL(url);
		this.user = user;
		this.password = password;
		this.name = name;
		this.current = current;
	}

	public DataSource(String filePath) throws IOException {
		loadData(filePath);
	}

	public DataSource() {
	}

	private void loadData(String filePath) throws FileNotFoundException, IOException {
		if (filePath == null) {
			throw new FileNotFoundException("***DataSource  Error -  Empty path!!!");
		}
		File file = new File(filePath);
		if (!file.exists()) {
			throw new FileNotFoundException("***DataSource  Error -  FileNotFoundException  " + filePath);
		}
		Properties props = new Properties();
		FileInputStream inStream = new FileInputStream(filePath);
		try {
			props.load(inStream);
		} finally {
			inStream.close();
		}
		setURL(props.getProperty("url"));
		user = props.getProperty("user");
		password = props.getProperty("password");
		current = Boolean.valueOf(props.getProperty("current"));
	}

	public String getPassword() {
		return password;
	}

	public String getURL() {
		return url;
	}

	public String getUser() {
		return user;
	}

	public String getContextId() {
		return contextId;
	}

	public String getName() {
		return name;
	}

	public boolean isCurrent() {
		return current;
	}

	@Override
	public String toString() {
		return contextId + "  " + url + "  " + user;
	}

	public void setCurrent(boolean b) {
		current = b;
	}

	public void setPassword(String string) {
		password = string;
	}

	public void setUser(String string) {
		user = string;
	}

	public void setURL(String string) {
		url = string;
	}

	public void setName(String name2) {
		this.name = name2;
	}

	public Integer getInxalue() {
		return inxalue;
	}

	public void setInxalue(Integer inxalue) {
		this.inxalue = inxalue;
	}
}
