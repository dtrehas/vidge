package org.vidge.inface;

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;


public interface ITableForm<T> extends IForm<T>{

	public Image getColumnImage( int columnIndex);
	public Color getBackground();
	public Color getForeground();
	boolean isSupportAction(IAction action);
	IAction[] getContextActions();

}
