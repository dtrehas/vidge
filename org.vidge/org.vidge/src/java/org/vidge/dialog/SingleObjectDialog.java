package org.vidge.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.vidge.FormRegistry;
import org.vidge.PlainForm;
import org.vidge.explorer.FormExplorer;
import org.vidge.form.IForm;
import org.vidge.inface.IEntityExplorer;
import org.vidge.status.IStatusListener;
import org.vidge.status.PropertyStatus;
import org.vidge.util.FormContext;

public class SingleObjectDialog<F> extends AbstractObjectDialog<F> {

	private PlainForm form;

	public SingleObjectDialog(Class<F> clazz, String title) {
		super(FormRegistry.getEntityExplorer(FormContext.CREATE.name(), clazz), title, DEFAULT_SIZE);
	}

	public SingleObjectDialog(IForm<F> form, String title) {
		super(new FormExplorer<F>(form), title, DEFAULT_SIZE);
	}

	public SingleObjectDialog(IEntityExplorer entityExplorer, String title) {
		super(entityExplorer, title, DEFAULT_SIZE);
	}

	public SingleObjectDialog(IEntityExplorer entityExplorer, String title, Point size) {
		super(entityExplorer, title, size);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		form = new PlainForm(explorer, false);
		Composite composite = form.getPane(parent, SWT.FILL);
		form.inValidate(null);
		setMessage(form.getCurrentStatus().getMessage());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		form.addStatusListener(new IStatusListener() {

			public void statusChanged(PropertyStatus status) {
				SingleObjectDialog.this.setMessage(status.getMessage());
				if (getButton(IDialogConstants.OK_ID) != null) {
					getButton(IDialogConstants.OK_ID).setEnabled(status.isValid());
				}
			}
		});
		return composite;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(form.isValid());
	}

	@Override
	protected void okPressed() {
		super.okPressed();
	}

	@Override
	public F getSelection() {
		// form.flush();
		return super.getSelection();
	}
}
