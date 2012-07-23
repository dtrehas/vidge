package org.vidge.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.vidge.util.PositionUtillity;

public abstract class FrameDialog extends Dialog {

	private final String title;

	public FrameDialog(Shell parentShell, String title) {
		super(parentShell);
		this.title = title;
	}

	@Override
	protected Control createContents(Composite parent) {
		parent.setSize(AbstractObjectDialog.DEFAULT_SIZE);
		PositionUtillity.center(parent);
		return super.createContents(parent);
	}

	protected Control createDialogArea(Composite parent) {
		this.getShell().setText(title);
		Composite content = createContent(parent);
		content.setLayoutData(new GridData(GridData.FILL_BOTH));
		return content;
	}

	public abstract Composite createContent(Composite parent);

	protected void closeAction() {
		FrameDialog.this.close();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button createButton = createButton(parent, IDialogConstants.CLOSE_ID, Messages.FrameDialog_0, false);
		createButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				closeAction();
			}
		});
	}
}
