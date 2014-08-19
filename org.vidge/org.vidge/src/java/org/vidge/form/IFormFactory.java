package org.vidge.form;

/**
 * For user control of instantiation, deletion and modifcation of objects
 * 
 * @author user
 * 
 */
public interface IFormFactory {

	/**
	 * When you want to set instantiation under control
	 * 
	 * @param inputClass
	 * @param context
	 * @return
	 */
	public Object newInstance(Class<?> inputClass, Object context);

	/**
	 * When you want to to set deletion under control
	 * 
	 * @param inputClass
	 * @param context
	 * @return
	 */
	public boolean removeInstance(Class<?> inputClass, Object context);

	/**
	 * Calls when user push OK button in dialog
	 */
	public void instanceApply();

	/**
	 * Calls when user push Cancel button in dialog
	 */
	public void instanceCancel();

	/**
	 * Calls before a paintng a form
	 * 
	 * @param context
	 * @return
	 */
	public Object checkInstance(Object context);
}
