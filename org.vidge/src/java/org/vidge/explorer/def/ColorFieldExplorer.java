package org.vidge.explorer.def;

import org.eclipse.swt.graphics.Color;

public class ColorFieldExplorer extends AbstractFieldExplorer<Color> {

	public ColorFieldExplorer(Color obj, String labelIn) {
		super(obj, labelIn);
	}

	@Override
	public Class<?> getPropertyClass() {
		return Color.class;
	}
}