package org.vidge.controls.editor;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Shell;
import org.vidge.PropertyController;
import org.vidge.controls.ObjectField;
import org.vidge.inface.IObjectDialog;

public class FontEditor extends ObjectField<Font> {

	public FontEditor(Composite parent, int style, PropertyController controller) {
		super(parent, style, controller);
		getText().setEditable(false);
	}

	@Override
	public void refresh(Font object) {
		Font font = object;
		if (font != null) {
			String style = "";
			switch (font.getFontData()[0].getStyle()) {
				case SWT.NORMAL:
					style = "Normal";
					break;
				case SWT.BOLD:
					style = "Bold";
					break;
				case SWT.ITALIC:
					style = "Italic";
					break;
			}
			text.setText("Name: " + font.getFontData()[0].getName() + " Size: " + font.getFontData()[0].getHeight() + " Style: " + style);
		} else {
			text.setText("");
		}
	}

	@Override
	protected IObjectDialog<Font> getDialog() {
		return new MyFontDialog(this.getShell());
	}
	private class MyFontDialog implements IObjectDialog<Font> {

		private FontDialog fontDialog;
		private FontData open;

		public MyFontDialog(Shell parent) {
			fontDialog = new FontDialog(parent);
			fontDialog.setFontList(((Font) controller.getExplorer().getValue()).getFontData());
		}

		@Override
		public Font getSelection() {
			return open == null ? null : new Font(fontDialog.getParent().getDisplay(), open);
		}

		@Override
		public int open() {
			open = fontDialog.open();
			return open == null ? Window.CANCEL : Window.OK;
		}
	}
}