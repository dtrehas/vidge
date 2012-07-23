package org.vidge.controls.tree.hierarchy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Event;

public abstract class AbstractHierarchyProvider<T> implements IHierarchyProvider<T> {

	public List<String> getColumnList() {
		return new ArrayList<String>();
	}

	public Object invokeMethod(Object obj, Method method) {
		if ((method != null) && (obj != null)) {
			try {
				return method.invoke(obj, new Object[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	// for discovering method by name
	public Method exploreMethod(Object obj, String methodName) {
		Method method = null;
		try {
			method = obj.getClass().getMethod(methodName, new Class[0]);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// e.printStackTrace();
		}
		return method;
	}

	@Override
	public void commitChanges(Object changes) {
	}

	public void handleEvent(Event event) {
	}

	public int getRootCount() {
		return 1;
	}

	public int getChildCount(T input) {
		return 1;
	}

	public List<T> getChildren(T input, Class<T> clazz) {
		return new ArrayList();
	}

	public T getParent(T input) {
		return null;
	}

	@Override
	public Object checkData(Object data) {
		return null;
	}

	@Override
	public boolean validateData(Object data) {
		return false;
	}

	@Override
	public boolean isMultipleSelect() {
		return false;
	}
}
