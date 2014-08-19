package org.vidge.explorer.def;

import org.eclipse.swt.graphics.Font;

public class FontFieldExplorer extends AbstractFieldExplorer<Font> {

	public FontFieldExplorer(Font obj, String labelIn) {
		super(obj, labelIn);
	}

	@Override
	public Class<?> getPropertyClass() {
		return Font.class;
	}
}