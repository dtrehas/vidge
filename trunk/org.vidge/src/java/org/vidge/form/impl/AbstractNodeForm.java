package org.vidge.form.impl;

import java.util.ArrayList;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeItem;
import org.vidge.form.INodeForm;

public class AbstractNodeForm<T> implements INodeForm<T> {

	protected T input;
	protected ArrayList<INodeForm> children = new ArrayList<INodeForm>();
	protected INodeForm parent;
	protected ArrayList<IAction> actionList = new ArrayList<IAction>();
	private TreeViewer viewer;

	public int getLevel() {
		if (parent != null) {
			return parent.getLevel() + 1;
		}
		return 0;
	}

	public TreeViewer getViewer() {
		return viewer;
	}

	public void setViewer(TreeViewer viewer) {
		this.viewer = viewer;
	}

	public Image getImage() {
		return null;
	}

	public void doubleClickAction(DoubleClickEvent event) {
	}

	public void showDetails() {
	}

	public INodeForm getParent() {
		return parent;
	}

	// when the tree requests children
	public INodeForm[] getChildren() {
		return children.toArray(new INodeForm[0]);
	}

	@Override
	public String toString() {
		if (input == null) {
			return "-";
		}
		if (input.toString() == null) {
			return "--";
		}
		return input.toString().trim();
	}

	public void setParent(INodeForm parent) {
		this.parent = parent;
	}

	public void addChild(INodeForm child) {
		children.add(child);
		child.setParent(this);
	}

	public void removeChild(INodeForm child) {
		children.remove(child);
	}

	public IAction[] getActions() {
		return actionList.toArray(new IAction[0]);
	}

	public void setInput(T input) {
		this.input = input;
	}

	public T getInput() {
		return input;
	}

	public INodeForm getParentInternal() {
		return null;
	}

	// when we need to load all children at once
	public INodeForm[] getChildrenInternal() {
		return children.toArray(new INodeForm[0]);
	}

	public int getChildCount() {
		return children.size();
	}

	public void removeAllChild() {
		children.clear();
	}

	public INodeForm getRoot() {
		if (parent != null) {
			return parent.getRoot();
		}
		return this;
	}

	public TreePath getTreePath() {
		if (parent != null) {
			return parent.getTreePath().createChildPath(this);
		}
		return new TreePath(new INodeForm[] {
			this
		});
	}

	public boolean equals(Object object) {
		if (!(object instanceof AbstractNodeForm)) {
			return false;
		}
		AbstractNodeForm rhs = (AbstractNodeForm) object;
		return this.input.equals(rhs.input);
	}

	@Override
	public int hashCode() {
		return input.hashCode();
	}

	public INodeForm getSelected(Object selection) {
		INodeForm result = null;
		if (input != null) {
			if (input.equals(selection)) {
				result = this;
			} else {
				for (INodeForm<T> nodeForm : getChildren()) {
					if (nodeForm != null) {
						result = nodeForm.getSelected(selection);
						if (result != null) {
							break;
						}
					}
				}
			}
		}
		return result;
	}

	public String getText(int index) {
		return "";
	}

	@Override
	public void setChecked(TreeItem treeItem) {
	}

	@Override
	public Boolean isChecked() {
		return false;
	}

	@Override
	public void setItem(TreeItem item) {
	}
}
