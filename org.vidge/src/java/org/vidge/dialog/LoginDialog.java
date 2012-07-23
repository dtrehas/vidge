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

public class LoginDialog extends Dialog {

	private String login = "";
	private String password;

	public LoginDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.TOOL | SWT.FLAT | SWT.APPLICATION_MODAL);
	}

	@Override
	protected Control createContents(Composite parent) {
		PositionUtillity.center(parent);
		Control control = super.createContents(parent);
		return control;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
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
}
