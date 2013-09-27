package org.vidge.form;

import java.util.List;

/**
 * This interface intended to use when you field is abstract class : you have to sow selection dialog - which type of object there must be created
 * 
 * @author user
 * 
 */
public interface IFormAbstractFactory {

	public List<Class<?>> getAncestorList();

	public String getAncestorFriendlyName(Class<?> clazz);
}
