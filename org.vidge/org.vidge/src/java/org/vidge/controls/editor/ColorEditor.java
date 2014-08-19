package org.vidge.controls.editor;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.vidge.PropertyController;
import org.vidge.controls.ObjectField;
import org.vidge.inface.IObjectDialog;

public class ColorEditor extends ObjectField<Color> {

	public ColorEditor(Composite parent, int style, PropertyController controller) {
		super(parent, style, controller);
		getText().setEditable(false);
	}

	@Override
	public void refresh(Color object) {
		Color color = (Color) object;
		if (color != null) {
			text.setBackground(color);
		} else {
			text.setBackground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		}
	}

	@Override
	protected IObjectDialog<Color> getDialog() {
		return new MyColorDialog(this.getShell());
	}
	private static class MyColorDialog implements IObjectDialog<Color> {

		private ColorDialog colorDialog;
		private RGB open;

		public MyColorDialog(Shell parent) {
			colorDialog = new ColorDialog(parent);
		}

		@Override
		public Color getSelection() {
			return open == null ? null : new Color(colorDialog.getParent().getDisplay(), open);
		}

		@Override
		public int open() {
			open = colorDialog.open();
			return open == null ? Window.CANCEL : Window.OK;
		}
	}
}