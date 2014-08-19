package org.vidge.controls.editor;

import java.io.File;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.vidge.PropertyController;
import org.vidge.controls.ObjectField;
import org.vidge.inface.IObjectDialog;
import org.vidge.util.StringUtil;

public class FileEditor extends ObjectField<File> {

	private final boolean isFile;

	public FileEditor(Composite parent, int style, PropertyController controller, boolean isFile) {
		super(parent, style, controller);
		this.isFile = isFile;
		text.setEditable(false);
		text.setBackground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WHITE));
	}

	@Override
	public void refresh(File object) {
		File file = (File) object;
		if (file != null) {
			text.setText(file.getPath());
		} else {
			text.setText(StringUtil.NN);
		}
	}

	@Override
	protected IObjectDialog<File> getDialog() {
		if (isFile) {
			return new MyFileDialog(this.getShell());
		} else {
			return new MyDirDialog(this.getShell());
		}
	}
	private static class MyFileDialog implements IObjectDialog<File> {

		private FileDialog dialog;
		private File open;

		public MyFileDialog(Shell parent) {
			dialog = new FileDialog(parent);
		}

		@Override
		public File getSelection() {
			return open == null ? null : open;
		}

		@Override
		public int open() {
			String open2 = dialog.open();
			if (open2 != null) {
				open = new File(open2);
			}
			return open == null ? Window.CANCEL : Window.OK;
		}
	}
	private static class MyDirDialog implements IObjectDialog<File> {

		private File open;
		private DirectoryDialog dialog;

		public MyDirDialog(Shell parent) {
			dialog = new DirectoryDialog(parent);
		}

		@Override
		public File getSelection() {
			return open == null ? null : open;
		}

		@Override
		public int open() {
			String open2 = dialog.open();
			if (open2 != null) {
				open = new File(open2);
			}
			return open == null ? Window.CANCEL : Window.OK;
		}
	}
}