package org.vidge.inface;

import org.vidge.util.VisualControlType;


public interface IAttribute {
	
	String getLabel();
	
	VisualControlType getControl();
	
	int getOrder();
	
	boolean getIsRequired();
	
	String getDescription();
	
	int getWidth();
	
	int getHeight();
}


