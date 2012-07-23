package org.vidge.form;

import java.util.Collection;


public abstract class AbstractObjectForm<O> extends AbstractForm {
	
	public abstract void create();
	public abstract void edit();
	public abstract void save();
	public abstract boolean canAdd();
	public abstract boolean canEdit();
	public abstract boolean canDelete();	
	public abstract Collection<O> getObjectList();
	public abstract int getStyle();
	public abstract String getTitle();

}
