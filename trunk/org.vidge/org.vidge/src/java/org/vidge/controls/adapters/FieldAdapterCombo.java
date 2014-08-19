package org.vidge.controls.adapters;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.langcom.locale.EnumLocalizer;
import org.vidge.PropertyController;
import org.vidge.util.StringUtil;

public class FieldAdapterCombo extends AbstractFieldAdapter {

	private static final String EMPTY = "-    ";
	protected Combo combo;
	private SelectionListener selectionListener = new SelectionAdapter() {

		@Override
		public void widgetSelected(SelectionEvent e) {
			Object visualValue = getVisualValue();
			explorer.setValue(visualValue);
			controller.inValidate();
			combo.select(findSelectedObject(explorer.getValue()));
		}
	};

	@SuppressWarnings("rawtypes")
	public FieldAdapterCombo(PropertyController controller, Composite parent) {
		super(controller, parent);
		Class klass = controller.getExplorer().getPropertyClass();
		List<?> validValues = explorer.getValidValues();
		if (validValues != null) {
			combo.add(EMPTY);
			for (Object object : validValues) {
				if (klass.isEnum()) {
					combo.add(EnumLocalizer.getLocalized((Enum) object));
				} else {
					combo.add(object.toString());
				}
			}
		}
		combo.select(findSelectedObject(explorer.getValue()));
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(selectionListener);
	}

	@Override
	protected void createControl(Composite parent) {
		combo = new Combo(parent, SWT.READ_ONLY);
	}

	@Override
	public Control getControl() {
		return combo;
	}

	@Override
	public Object getVisualValue() {
		int selectionIndex = combo.getSelectionIndex();
		if (selectionIndex == 0) {
			return null;
		}
		return (selectionIndex >= 0) ? explorer.getValidValues().get(selectionIndex - 1) : null;
	}

	protected int findSelectedObject(Object toSelect) {
		if (toSelect != null) {
			List<?> validValues = explorer.getValidValues();
			if (validValues != null) {
				return validValues.indexOf(toSelect) + 1;
			}
		} else {
			return 0;
		}
		return -1;
	}

	@Override
	public void setVisualValue(Object newValue) {
		// combo.setText(newValue.toString());
		int selection = findSelectedObject(newValue);
		if (selection >= 0) {
			combo.select(selection);
		} else {
			combo.deselectAll();
			combo.setText(StringUtil.NN); //$NON-NLS-1$
		}
	}
}
