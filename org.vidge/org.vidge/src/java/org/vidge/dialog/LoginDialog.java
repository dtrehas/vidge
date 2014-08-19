package org.vidge.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.vidge.util.PositionUtillity;
import org.vidge.util.StringUtil;

public class LoginDialog extends Dialog {

	private String login = StringUtil.NN;
	private String password = StringUtil.NN;
	private boolean posAbs = false;

	public LoginDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.TOOL | SWT.FLAT | SWT.APPLICATION_MODAL);
	}

	/**
	 * 
	 * @param parentShell
	 * @param posAbs
	 *            - is a dialog position is absolute
	 */
	public LoginDialog(Shell parentShell, boolean posAbs) {
		super(parentShell);
		this.posAbs = posAbs;
		setShellStyle(SWT.TOOL | SWT.FLAT | SWT.APPLICATION_MODAL);
	}

	@Override
	protected Control createContents(Composite parent) {
		if (posAbs) {
			PositionUtillity.centerAbs(parent, 300, 150, 200, 0);
		} else {
			PositionUtillity.center(parent);
		}
		Control control = super.createContents(parent);
		return control;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// parent.setBackgroundImage(VidgeResources.getInstance().getImage(SharedImages.DIAGONAL));
		// parent.setBackgroundMode(SWT.INHERIT_DEFAULT);
		// parent.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_YELLOW));
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		FillLayout layout = new FillLayout(SWT.VERTICAL);
		layout.marginWidth = 10;
		layout.marginHeight = 3;
		layout.spacing = 3;
		composite.setLayout(layout);
		Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.LoginDialog_Login);
		final Text loginText = new Text(composite, SWT.BORDER);
		loginText.setText(login);
		loginText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				login = loginText.getText();
			}
		});
		label = new Label(composite, SWT.SHADOW_IN);
		label.setText(Messages.LoginDialog_Password);
		final Text passwordText = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		passwordText.setText(password);
		passwordText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				password = passwordText.getText();
			}
		});
		composite.pack();
		return composite;
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}

	public void setLogin(String string) {
		login = string;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
