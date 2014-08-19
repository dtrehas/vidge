package org.vidge.form.impl;

import java.io.Serializable;
import java.util.List;

import org.vidge.controls.tree.hierarchy.IHierarchyProvider;
import org.vidge.form.INodeForm;

public class FastBlindNodeForm extends AbstractNodeForm<Object> implements Serializable {

	private static final long serialVersionUID = 4606333740166924623L;
	private IHierarchyProvider provider;
	private boolean isRoot = false;
	private Class clazz;

	public FastBlindNodeForm(Object input, IHierarchyProvider hierarchyDataProvider, Class clazz, boolean root) {
		this.input = input;
		isRoot = root;
		setHierarchyDataProvider(hierarchyDataProvider, clazz);
		createChildren();
	}

	@Override
	public INodeForm[] getChildren() {
		List list = provider.getChildren(!isRoot ? input : null, clazz);
		if (list.size() != this.getChildCount()) {
			createChildren();
		}
		return super.getChildren();
	}

	public void createChildren() {
		children.clear();
		List list = provider.getChildren(!isRoot ? input : null, clazz);
		for (int i = 0; i < list.size(); i++) {
			FastBlindNodeForm child = new FastBlindNodeForm(list.get(i), provider, clazz, false);
			child.setHierarchyDataProvider(provider, clazz);
			addChild(child);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String toString() {
		if (provider != null) {
			Object name = provider.invokeMethod(input, provider.exploreMethod(input, "getName")); //$NON-NLS-1$
		}
		return super.toString();
	}

	// @Override
	// public int getChildCount() {
	// if (getLevel() == 1) {
	// return (children.size() == 0) ? 1 : super.getChildCount();
	// }
	// return provider.getChildCount(input);
	// }
	public void setHierarchyDataProvider(IHierarchyProvider hierarchyDataProvider, Class clazz) {
		this.provider = hierarchyDataProvider;
		this.clazz = clazz;
	}

	public void setRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}

	public Class getClazz() {
		return clazz;
	}

	public IHierarchyProvider getProvider() {
		return provider;
	}

	public boolean getIsRoot() {
		return isRoot;
	}
}
