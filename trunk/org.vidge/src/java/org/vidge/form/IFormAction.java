package org.vidge.form;

import java.util.List;

import org.eclipse.jface.action.IAction;

public interface IFormAction<T> extends IForm<T> {

	boolean isSupportAction(IAction action);

	List<IAction> getContextActions();
}
