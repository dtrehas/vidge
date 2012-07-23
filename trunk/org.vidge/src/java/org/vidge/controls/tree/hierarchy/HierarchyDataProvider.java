package org.vidge.controls.tree.hierarchy;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;

public class HierarchyDataProvider<T> extends AbstractHierarchyProvider<T> implements Serializable {

	private static final long serialVersionUID = 9082650760857741813L;
	public static final String TASK_NAME = Messages.HierarchyDataProvider_0;
	protected Map<Object, List<T>> nodesLists = new HashMap<Object, List<T>>();
	protected List<T> objectList;

	public HierarchyDataProvider() {
	}

	public HierarchyDataProvider(List<T> objectList) {
		this(objectList, null);
		this.objectList = objectList;
	}

	public HierarchyDataProvider(List<T> objectList, IProgressMonitor monitor) {
		fill(objectList, monitor);
	}

	public void setObject(T input) {
		if ((!nodesLists.containsKey(input)) && (!nodesLists.containsValue(input))) {
			List<T> emptyList = Collections.emptyList();
			emptyList.add(input);
			fill(emptyList);
		} else {
			if (nodesLists.containsValue(input)) {
				Object parent = getParent(input);
				Method getParent = exploreMethod(input, "getParent"); //$NON-NLS-1$;
				try {
					Object parentOld = invokeMethod(input, getParent);
					
					if (!equalsObjects(parent, parentOld)) {
						List<T> parentChildren = nodesLists.get(parent);
						if (parentChildren == null) {
							parentChildren = new ArrayList<T>();
							nodesLists.put(parent, parentChildren);
						}
						parentChildren.add(input);
						List<T> parentOldChildren = nodesLists.get(parentOld);
						if (parentOldChildren != null) {
							parentOldChildren.remove(input);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private boolean equalsObjects(Object parent, Object parentOld) {
		if ((parent == null) && (parentOld == null)) {
			return true;
		}
		if ((parent != null) && (parentOld != null)) {
			return parent.equals(parentOld);
		}
		return false;
	}

	public void removeObject(T input) {
		for (Entry<Object, List<T>> entry : nodesLists.entrySet()) {
			if (entry.getValue().contains(input)) {
				entry.getValue().remove(input);
			}
		}
		nodesLists.remove(input);
	}

	/* (non-Javadoc)
	 * @see org.langcom.hierarchy.IHierarchyProvider#getChildren(java.lang.Object)
	 */
	@Override
	public List<T> getChildren(T input,Class<T> clazz) {
		List<T> children = nodesLists.get(input);
		if (children == null) {
			children = new LinkedList<T>();
		}
		return children;
	}

	/* (non-Javadoc)
	 * @see org.langcom.hierarchy.IHierarchyProvider#getParent(java.lang.Object)
	 */
	@Override
	public T getParent(T input) {
		for (Entry<Object, List<T>> entry : nodesLists.entrySet()) {
			if (entry.getValue().contains(input)) {
				return (T) entry.getKey();
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.langcom.hierarchy.IHierarchyProvider#getChildCount(java.lang.Object)
	 */
	@Override
	public int getChildCount(T input) {
		return getChildren(input,(Class<T>) objectList.get(0).getClass()).size();
	}

	public void fill(List<T> objectList) {
		fill(objectList, null);
	}

	public void fill(List<T> objectList, IProgressMonitor monitor) {
		if (monitor != null) {
			monitor.beginTask(TASK_NAME, objectList.size());
		}
		Method getParent = null;
		if (objectList.size() > 0) {
			T first = objectList.get(0);
			// for avoid exploring in cycle
			getParent = exploreMethod(first, "getParent"); //$NON-NLS-1$;
		}
		if (getParent != null) {
			for (T obj : objectList) {
				if (monitor != null) {
					monitor.worked(1);
					if (monitor.isCanceled()) {
						break;
					}
				}
				try {
					Object parent = invokeMethod(obj, getParent);
					if ((parent != null) && (!objectList.contains(parent))) {
						parent = null;
					}
					List<T> parentChildren = nodesLists.get(parent);
					if (parentChildren == null) {
						parentChildren = new ArrayList<T>();
						nodesLists.put(parent, parentChildren);
					}
					parentChildren.add(obj);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Map<Object, List<T>> getNodesLists() {
		return nodesLists;
	}
}
