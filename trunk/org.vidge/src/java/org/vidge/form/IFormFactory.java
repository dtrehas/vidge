package org.vidge.form;

public interface IFormFactory {

	public Object newInstance(Class<?> inputClass, Object context);

	public boolean removeInstance(Class<?> inputClass, Object context);

	public void instanceApply();

	public void instanceCancel();

	public Object checkInstance(Object context);
}
