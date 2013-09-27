package org.vidge.form;

import org.eclipse.swt.graphics.Color;

public interface IFormColor<T> {

	public Color getBackground(T element);

	public Color getForeground(T element);
}
