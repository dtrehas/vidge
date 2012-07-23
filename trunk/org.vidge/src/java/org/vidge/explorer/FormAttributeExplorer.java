package org.vidge.explorer;

import org.vidge.inface.IAttribute;
import org.vidge.util.VisualControlType;
import org.vidge.util.VisualProperty;


/**
 * 
 * @author nemo
 */
public class FormAttributeExplorer implements IAttribute {

	private final VisualProperty visualProperty;
	
	public FormAttributeExplorer(final VisualProperty visualProperty) {
		this.visualProperty = visualProperty;
	}

	public VisualControlType getControl() {
		return visualProperty.control();
	}

	public String getDescription() {
		return visualProperty.description();
	}

	public int getHeight() {
		return visualProperty.height();
	}

	public boolean getIsRequired() {
		return visualProperty.required();
	}

	public String getLabel() {
		return visualProperty.label();
	}

	public int getOrder() {
		return visualProperty.order();
	}

	public int getWidth() {
		return visualProperty.width();
	}

}