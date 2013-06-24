package org.vidge.inface;

import org.eclipse.swt.graphics.Color;

public interface IColorForm<T> {

	public Color getBackground(T element);

	public Color getForeground(T element);
}
