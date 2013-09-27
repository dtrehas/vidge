package org.vidge.controls.chooser;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.langcom.locale.LocalizedString;
import org.vidge.PropertyController;
import org.vidge.controls.ObjectField;
import org.vidge.dialog.AbstractObjectDialog;
import org.vidge.inface.IObjectDialog;
import org.vidge.util.PositionUtillity;

public class PartListEditorField<T> extends ObjectField<Object> {

	private PartListEditorDialog dialog;

	public PartListEditorField(Composite parent, int style, PropertyController controller) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		super(parent, style, controller);
		text.setEditable(true);
	}
	private class PartListEditorDialog extends Dialog implements IObjectDialog<T> {

		private PartListChooser<T> partListChooser;

		private PartListEditorDialog() {
			super(PartListEditorField.this.getShell());
			setShellStyle(SWT.CLOSE | SWT.RESIZE);
		}

		@Override
		protected Control createContents(Composite parent) {
			parent.setSize(AbstractObjectDialog.DEFAULT_SIZE);
			PositionUtillity.center(parent);
			return super.createContents(parent);
		}

		protected Control createDialogArea(Composite parent) {
			partListChooser = new PartListChooser<T>(parent, controller);
			partListChooser.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
			return partListChooser.getControl();
		}

		@SuppressWarnings("unchecked")
		@Override
		public T getSelection() {
			return (T) partListChooser.getResult();
		}
	}

	@Override
	protected void keyPressedInText() {
		setSelection(new LocalizedString(text.getText()));
	}

	@Override
	protected IObjectDialog getDialog() {
		if (dialog == null) {
			dialog = new PartListEditorDialog();
		}
		return dialog;
	}

	@Override
	protected T getSelectionInternal() {
		return (T) controller.getExplorer().getValue();
	}
}