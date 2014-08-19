package org.vidge.details;

import org.apache.commons.lang.ObjectUtils;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Event;

class DetailProperty {

	private String name;
	private Object value;
	private IAction action;
	private DetailPropertyType propertyType = DetailPropertyType.STRING;
	private Font font;

	DetailProperty(String name, Object value) {
		this(name, value, DetailPropertyType.STRING);
	}

	DetailProperty(String name, Object value, DetailPropertyType type) {
		this(type);
		this.name = name;
		this.value = value;
	}

	DetailProperty(String name, Object value, IAction action) {
		this(name, value);
		this.action = action;
		if (action != null) {
			propertyType = DetailPropertyType.ACTION;
		}
	}

	DetailProperty(DetailPropertyType type) {
		this.propertyType = type;
	}

	public DetailProperty(String name, Object value, Font font) {
		this(name, value);
		this.font = font;
	}

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		return ObjectUtils.toString(value);
	}

	public DetailPropertyType getPropertyType() {
		return propertyType;
	}

	public void getRunAction() {
		Event event = new Event();
		event.data = value;
		action.runWithEvent(event);
	}

	public void setName(String name) {
		this.name = name;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}
}
