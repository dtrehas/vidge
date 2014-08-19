package org.vidge;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.vidge.controls.adapters.AbstractFieldAdapter;
import org.vidge.inface.IPropertyExplorer;
import org.vidge.util.StringUtil;
import org.vidge.util.TypeUtil;
import org.vidge.util.VisualControlType;

public class PropertyController implements Comparable<PropertyController>, FocusListener {

	private static final String CHILD_OBJECTS_NOT_ALLOWED_FOR_UNSAVED_PARENTS = "Child objects not allowed for unsaved parents.";
	private static final String NN = StringUtil.NN;
	public final IPropertyExplorer explorer;
	private final PlainForm plainForm;
	private AbstractFieldAdapter fieldAdapter;
	private VisualControlType controlType = VisualControlType.TEXT;
	private Label nameControl;

	PropertyController(PlainForm plainForm, IPropertyExplorer explorer) {
		this.plainForm = plainForm;
		this.explorer = explorer;
		controlType = TypeUtil.checkControlType(plainForm, explorer);
	}

	public IPropertyExplorer getExplorer() {
		return explorer;
	}

	public Control getNameControl(Composite parent) {
		nameControl = new Label(parent, SWT.NONE);
		if (explorer.getLabel() != null) {
			nameControl.setText(StringUtil.capitalize(explorer.getLabel()));
		}
		return nameControl;
	}

	public Control getValueControl(Composite parent) {
		fieldAdapter = FieldAdapterFactory.getFieldAdapter(controlType, this, parent);
		if (controlType.equals(VisualControlType.OBJECT_EDITOR) || controlType.equals(VisualControlType.OBJECT_WIZARD)
			|| controlType.equals(VisualControlType.IMAGE)) {
			if (!explorer.hasValidValues()) {
				fieldAdapter.setEnabled(explorer.isAllowChild());
				if (!explorer.isAllowChild()) {
					fieldAdapter.setToolTipText(CHILD_OBJECTS_NOT_ALLOWED_FOR_UNSAVED_PARENTS);
				}
			}
		}
		return fieldAdapter.getControl();
	}

	public int compareTo(PropertyController o) {
		return explorer.compareTo(o.getExplorer());
	}

	public VisualControlType getControlType() {
		return controlType;
	}

	public Control getValueControl() {
		return fieldAdapter.getControl();
	}

	public Control getNameControl() {
		return nameControl;
	}

	public boolean isValid() {
		if (fieldAdapter == null) {
			return true;
		}
		fieldAdapter.inValidate();
		return fieldAdapter.isValid();
	}

	public String getHelpMessage() {
		if (fieldAdapter == null) {
			return NN;
		}
		return fieldAdapter.getHelpMessage();
	}

	public AbstractFieldAdapter getFieldAdapter() {
		return fieldAdapter;
	}

	public boolean setFocus() {
		fieldAdapter.getControl().removeFocusListener(this);
		boolean setFocus = fieldAdapter.getControl().setFocus();
		plainForm.statusChanged(this);
		fieldAdapter.getControl().addFocusListener(this);
		return setFocus;
	}

	@Override
	public void focusGained(FocusEvent e) {
		plainForm.setFocusedIndex(this);
		fieldAdapter.refreshControl();
	}

	@Override
	public void focusLost(FocusEvent e) {
		plainForm.setLastLostFocus(this);
	}

	public void inValidate() {
		plainForm.statusChanged(this);
	}

	public void refreshView() {
		plainForm.refreshView();
	}

	@Override
	public String toString() {
		return "PropertyController [controlType=" + controlType + ", nameControl=" + nameControl.getText() + "]  " + explorer.getEntityExplorer();
	}
}