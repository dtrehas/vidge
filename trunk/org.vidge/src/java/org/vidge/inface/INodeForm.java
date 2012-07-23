package org.vidge.inface;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeItem;

public interface INodeForm<T> extends IForm<T> {

	INodeForm getParent();

	INodeForm getRoot();

	INodeForm[] getChildren();

	INodeForm getParentInternal();

	INodeForm[] getChildrenInternal();
	
	void setChecked(TreeItem treeItem);

	public Boolean isChecked();
	
	Image getImage();

	int getChildCount();

	void setParent(INodeForm parent);

	void addChild(INodeForm child);

	void removeChild(INodeForm child);

	void removeAllChild();

	IAction[] getActions();

	void doubleClickAction(DoubleClickEvent event);

	void showDetails();

	public TreeViewer getViewer();

	public void setViewer(TreeViewer viewer);

	TreePath getTreePath();

	public int getLevel();

	public INodeForm getSelected(Object selection);

	void setItem(TreeItem item);
}
