package org.vidge.controls.chooser;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.vidge.SharedImages;
import org.vidge.VidgeResources;
import org.vidge.util.PositionUtillity;

class FilterDialog<T> extends Dialog {

	private Filter filter;
	private final ObjectChooser<T> chooser;

	FilterDialog(ObjectChooser<T> chooser) {
		super(chooser.getControl().getShell());
		this.chooser = chooser;
		setShellStyle(SWT.CLOSE | SWT.RESIZE);
		setDefaultImage(VidgeResources.getInstance().getImage(SharedImages.FILTER));
	}

	@Override
	protected Control createContents(Composite parent) {
		parent.getShell().setText(Messages.FilterDialog_FILTERS);
		PositionUtillity.center(parent);
		return super.createContents(parent);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite client = new Composite(parent, SWT.BORDER);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		client.setLayout(layout);
		filter = new Filter(client, chooser);
		filter.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		client.setLayoutData(new GridData(GridData.FILL_BOTH));
		return client;
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		Composite composite = new Composite(parent, SWT.NORMAL);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout layout = new GridLayout();
		layout.marginWidth = 30;
		layout.marginHeight = 5;
		composite.setLayout(layout);
		composite.setFont(parent.getFont());
		Button button;
		button = createButton(composite, IDialogConstants.CLIENT_ID, Messages.FilterDialog_CLOSE, false);
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent event) {
				close();
			}
		});
		button = createButton(composite, IDialogConstants.CLIENT_ID, Messages.FilterDialog_CLEAR_FILTERS, false);
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent event) {
				filter.removeFiltering();
			}
		});
		return composite;
	}
}
