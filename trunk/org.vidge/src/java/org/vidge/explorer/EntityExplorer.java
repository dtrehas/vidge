package org.vidge.explorer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.vidge.inface.IEntityExplorer;
import org.vidge.inface.IFormDataProvider;
import org.vidge.inface.IFormFactory;
import org.vidge.inface.IFormInputChangeListener;
import org.vidge.inface.IPropertyExplorer;
import org.vidge.inface.ValueAction;

@SuppressWarnings("rawtypes")
public abstract class EntityExplorer implements IEntityExplorer, IFormInputChangeListener, IFormDataProvider, IFormFactory {

	private List<IPropertyExplorer> attributes = new ArrayList<IPropertyExplorer>();
	private Map<String, IPropertyExplorer> attributesMap = new HashMap<String, IPropertyExplorer>();
	private List<IEntityExplorer> childList = new ArrayList<IEntityExplorer>();
	protected Object input;

	public List<IPropertyExplorer> getPropertyList() {
		return attributes;
	}

	public List<?> getValidValues() {
		return new ArrayList<Object>();
	}

	public boolean isGroup() {
		return false;
	}

	public void clear() {
		attributes.clear();
		input = null;
	}

	@Override
	public void addChild(IEntityExplorer child) {
		childList.add(child);
	}

	@Override
	public void explore(Object input) {
		clear();
		setInput(input);
	}

	public Image getImage(Object element, int columnIndex) {
		if (columnIndex == 0) {
			return null;
		}
		if (getPropertyList().size() <= columnIndex) {
			return null;
		}
		return getPropertyList().get(columnIndex - 1).getImage(element);
	}

	public Color getBackground(Object element) {
		return null;
	}

	public Color getForeground(Object element) {
		return null;
	}

	@Override
	public Class<? extends Object> getInputClass() {
		return input.getClass();
	}

	public Object getInput() {
		return input;
	}

	public void setInput(Object input) {
		this.input = input;
	}

	@Override
	public Object doInputChanged(Object value, ValueAction action, String attribute) {
		parentChanged(value, action, attribute);
		return value;
	}

	protected void parentChanged(Object value, ValueAction action, String attribute) {
		for (IEntityExplorer entityExplorer : childList) {
			entityExplorer.doInputChanged(value, action, attribute);
		}
	}

	@Override
	public String getLabel() {
		return input.toString();
	}

	@Override
	public List<?> getData() {
		return null;
	}

	public void addPropertyChangeListener(IPropertyChangeListener listener) {
	}

	@Override
	public boolean allowParts() {
		return true;
	}

	public boolean addProperty(IPropertyExplorer e) {
		attributesMap.put(e.getPropertyName(), e);
		e.setEntityExplorer(this);
		return attributes.add(e);
	}

	public IPropertyExplorer getProperty(String key) {
		return attributesMap.get(key);
	}

	public IPropertyExplorer getProperty(int index) {
		return attributes.get(index);
	}

	public List<IPropertyExplorer> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<IPropertyExplorer> attributes) {
		this.attributes = attributes;
		for (IPropertyExplorer attr : attributes) {
			attributesMap.put(attr.getPropertyName(), attr);
			attr.setEntityExplorer(this);
		}
	}

	public boolean create() {
		return true;
	}

	public boolean edit() {
		return true;
	}

	public boolean remove() {
		return true;
	}

	@Override
	public boolean refresh() {
		return true;
	}

	@Override
	public Object newInstance(Class<?> inputClass) {
		try {
			return inputClass.newInstance();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
}
