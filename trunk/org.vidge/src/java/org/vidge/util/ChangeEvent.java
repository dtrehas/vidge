package org.vidge.util;


public class ChangeEvent extends ChangeEventObject {
	
	private static final long serialVersionUID = 3585316371129439690L;

	public boolean doIt = true;
	
	public String message = "";

	private final Object oldValue;

	public ChangeEvent(Object source) {
		this(source, source);
	}

	public ChangeEvent(Object oldValue, Object newValue) {
		super(newValue);
		this.oldValue = oldValue;
	}

	public Object getOldValue() {
		return oldValue;
	}

	public Object getNewValue() {
		return getSource();
	}

}
