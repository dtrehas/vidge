package org.vidge.action;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.jface.action.Action;
import org.langcom.logic.ILogic;
import org.langcom.logic.LogicValue;
import org.vidge.dialog.LogicTreeDialog;

public class BusinessAction extends Action {

	public static final String DEFAULT_MESSAGE = Messages.BusinessAction_1;
	public static final String NO_SELECTED_OBJECTS = Messages.BusinessAction_2;
	public static final String OBJECT_SELECTED = Messages.BusinessAction_3;
	protected boolean isCanRun = true;
	protected ILogic logic;
	protected Collection selectedObjects;

	public void checkEnablement(Collection selectedObjects) {
		this.selectedObjects = selectedObjects;
		setLogic(new LogicValue(OBJECT_SELECTED, NO_SELECTED_OBJECTS, ((selectedObjects != null) && (selectedObjects.size() > 0))));
		checkEnabled();
	}

	public void checkEnablement(Object... selected) {
		if ((selected != null) && (selected.length > 0))
			selectedObjects = Arrays.asList(selected);
		setLogic(new LogicValue(OBJECT_SELECTED, NO_SELECTED_OBJECTS, ((selected != null) && (selected.length > 0))));
		checkEnabled();
	}

	public void createDefaultNode() {
		logic = new LogicValue(OBJECT_SELECTED, NO_SELECTED_OBJECTS, false);
	}

	public void checkEnabled() {
		if (logic != null) {
			setEnabled(logic.check());
		} else {
			setEnabled(true);
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		isCanRun = enabled;
		// super.setEnabled(enabled);
	}

	public void setEnabledSuper(boolean enabled) {
		super.setEnabled(enabled);
	}

	/**
	 * This method must be reimplemented if you sure that this action allways has the enabled state
	 */
	@Override
	public void run() {
		checkEnabled();
		if (isCanRun) {
			runChecked();
		} else {
			new LogicTreeDialog(Messages.BusinessAction_0, logic);
		}
	}

	/**
	 * This method must be reimplemented if you want to check the enablement of action
	 */
	public void runChecked() {
	}

	public ILogic getLogic() {
		return logic;
	}

	public void setLogic(ILogic logic) {
		this.logic = logic;
	}
}
