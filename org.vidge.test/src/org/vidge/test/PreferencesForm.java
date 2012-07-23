package org.vidge.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.vidge.inface.IForm;
import org.vidge.util.VisualControlType;
import org.vidge.util.VisualProperty;

public class PreferencesForm implements IForm<DSPreferences> {

	private DSPreferences input;
	private DataSource current;
	private final ListenerList listenerList = new ListenerList(ListenerList.IDENTITY);

	public PreferencesForm() {
		try {
			input = new DSPreferences();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		current = input.getCurrent();
		if (current == null) {
			current = new DataSource();
		}
	}

	@VisualProperty(order = 1, required = true, control = VisualControlType.EDITABLE_COMBO)
	public String getName() {
		if (current == null) {
			return "";
		}
		return current.getName();
	}

	public void setName(String name) {
		if (((name == null) || (name.length() == 0)) && ((current != null) && (current.getName().equals(name)))) {
			return;
		}
		name = name.trim();
		DataSource current2 = input.getDataSource(name);
		if (current2 == null) {
			current2 = new DataSource();
			current2.setName(name);
		}
		current2.setCurrent(true);
		current = current2;
		input.addDataCurrent(current2);
		// try {
		// input.save();
		// } catch (FileNotFoundException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		fireChanged("", current2.getName());
	}

	private void fireChanged(String oldValue, String newValue) {
		PropertyChangeEvent evt = new PropertyChangeEvent(this, "name", oldValue, newValue);
		for (Object listener : listenerList.getListeners()) {
			try {
				((IPropertyChangeListener) listener).propertyChange(evt);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	public void addPropertyChangeListener(IPropertyChangeListener listener) {
		listenerList.add(listener);
	}

	public List getNameValidValues() {
		return input.getNameList();
	}

	@VisualProperty(order = 2, required = true, control = VisualControlType.EDITABLE_COMBO)
	public String getURL() {
		if (current == null) {
			return "";
		}
		return current.getURL();
	}

	public List getURLValidValues() {
		return new ArrayList();
	}

	public void setURL(String string) {
		if (current != null) {
			current.setURL(string);
		}
	}

	@VisualProperty(order = 4)
	public String getUser() {
		if (current == null) {
			return "";
		}
		return current.getUser();
	}

	public void setUser(String string) {
		if (current != null) {
			current.setUser(string);
		}
	}

	@VisualProperty(order = 5, required = true)
	public String getPassword() {
		if (current == null) {
			return "";
		}
		return current.getPassword();
	}

	public void setPassword(String string) {
		if (current != null) {
			current.setPassword(string);
		}
	}

	@Override
	public DSPreferences getInput() {
		return input;
	}

	@Override
	public void setInput(DSPreferences input) {
		this.input = input;
	}

	public DataSource getCurrent() {
		return current;
	}

	@VisualProperty(order = 6, required = true)
	public Integer getInxalue() {
		return current.getInxalue();
	}

	public void setInxalue(Integer inxalue) {
		current.setInxalue(inxalue);
	}
}
