package org.vidge.controls.tree.hierarchy;

import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.swt.widgets.Event;

public interface IHierarchyProvider<T> {

	public List<T> getChildren(T input, Class<T> clazz);

	public T getParent(T input);

	public int getChildCount(T input);

	public Method exploreMethod(Object obj, String methodName);

	public Object invokeMethod(Object obj, Method method);

	public List<String> getColumnList();

	public void handleEvent(Event event);

	public int getRootCount();

	public Object checkData(Object data);

	public boolean validateData(Object data);

	public boolean isMultipleSelect();

	public void commitChanges(Object changes);
}