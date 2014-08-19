package org.vidge.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.vidge.controls.BrowserPage;
import org.vidge.inface.IObjectDialog;
import org.vidge.util.PositionUtillity;

public class ResourceDialog extends FrameDialog implements IObjectDialog<String> {

	private BrowserPage browserPage;
	private String url;
	private static int GET_FILE = IDialogConstants.CLIENT_ID << 2;
	private int styleIn = 0;

	public ResourceDialog(Shell parentShell, String title, String url) {
		super(parentShell, title);
		this.url = url;
		setShellStyle(SWT.CLOSE | SWT.RESIZE);
	}

	public ResourceDialog(Shell parentShell, String title, String url, int styleIn) {
		super(parentShell, title);
		this.url = url;
		this.styleIn = styleIn;
		setShellStyle(SWT.CLOSE | SWT.RESIZE);
	}

	@Override
	protected Control createContents(Composite parent) {
		parent.setSize(AbstractObjectDialog.DEFAULT_SIZE);
		PositionUtillity.center(parent);
		return super.createContents(parent);
	}

	@Override
	public Composite createContent(Composite parent) {
		browserPage = new BrowserPage(parent, url, styleIn);
		return browserPage.getControl();
	}

	@Override
	public String getSelection() {
		return url;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		if ((styleIn & GET_FILE) != 0) {
			createButton(parent, GET_FILE, Messages.ResourceDialog_Open, false);
		}
		super.createButtonsForButtonBar(parent);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (GET_FILE == buttonId) {
			openFileDialog();
		}
	}

	private void openFileDialog() {
		FileDialog dialog = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		final String open = dialog.open();
		if (open != null) {
			url = open;
		}
	}
}
