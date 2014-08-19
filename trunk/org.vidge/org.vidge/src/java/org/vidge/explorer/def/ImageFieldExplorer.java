package org.vidge.explorer.def;

import org.eclipse.swt.graphics.Image;

public class ImageFieldExplorer extends AbstractFieldExplorer<Image> {

	public ImageFieldExplorer(Image obj, String labelIn) {
		super(obj, labelIn);
	}

	@Override
	public Class<?> getPropertyClass() {
		return Image.class;
	}
}