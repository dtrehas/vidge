package org.vidge.dialog;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.vidge.Messages;
import org.vidge.util.PositionUtillity;
import org.vidge.util.StringUtil;

public class VidgeErrorDialog extends IconAndMessageDialog {

	private final Throwable err;
	private Point size = new Point(700, 400);
	private final String title;
	private Combo combo;
	private Text text;

	public VidgeErrorDialog(Shell parentShell, String title, String mesg, Throwable err) {
		super(parentShell);
		this.title = title;
		message = mesg;
		this.err = err;
		setShellStyle(SWT.CLOSE | SWT.MAX | SWT.MIN | SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL | SWT.RESIZE | getDefaultOrientation());
	}

	public static int open(Shell parentShell, String title, String mesg, Throwable err) {
		return new VidgeErrorDialog(parentShell, title, mesg, err).open();
	}

	@Override
	protected Control createContents(Composite parent) {
		parent.setSize(size);
		PositionUtillity.center(parent);
		Control control = super.createContents(parent);
		this.getShell().setText(StringUtil.defaultIfEmpty(title, Messages.VidgeErrorDialog_Err_header));
		return control;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createCombo(parent);
		createButton(parent, IDialogConstants.CLIENT_ID, Messages.VidgeErrorDialog_0, true);
		createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, true);
	}

	protected void createCombo(Composite parent) {
		((GridLayout) parent.getLayout()).numColumns++;
		Label label = new Label(parent, SWT.NONE);
		label.setText(Messages.VidgeErrorDialog_File_econding);
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		combo = new Combo(parent, SWT.READ_ONLY);
		combo.add(Charset.defaultCharset().displayName());
		for (Entry<String, Charset> entry : Charset.availableCharsets().entrySet()) {
			combo.add(entry.getKey());
		}
		combo.select(0);
		addControl(parent, combo);
	}

	protected void addControl(Composite parent, Control control) {
		((GridLayout) parent.getLayout()).numColumns++;
		control.setFont(JFaceResources.getDialogFont());
		control.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.CLIENT_ID == buttonId) {
			saveToFile();
		} else if (IDialogConstants.CLOSE_ID == buttonId) {
			cancelPressed();
		}
	}

	@SuppressWarnings("nls")
	private void saveToFile() {
		FileDialog fileDialog = new FileDialog(getShell(), SWT.SAVE);
		String open = fileDialog.open();
		if (open != null) {
			try {
				String data = "";
				if (err == null) {
					data = message;
				} else {
					data = StringUtil.buildErrorTraceString(err);
				}
				FileUtils.writeStringToFile(new File(open + ".err"), data, combo.getText()); //$NON-NLS-1$
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		createMessageArea(parent);
		if (err == null) {
		} else {
			text = new Text(parent, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
			text.setForeground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_RED));
			GridData childData = new GridData(GridData.FILL_BOTH);
			childData.horizontalSpan = 2;
			text.setLayoutData(childData);
			text.setFont(parent.getFont());
			text.setText(StringUtil.buildErrorTraceString(err));
		}
		return parent;
	}

	@Override
	protected Image getImage() {
		return getErrorImage();
	}
}
