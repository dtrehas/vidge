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
import org.eclipse.ui.PlatformUI;
import org.vidge.SharedImages;
import org.vidge.VidgeResources;
import org.vidge.util.PositionUtillity;

class VFilterDialog<T> extends Dialog {

	private VFilter<T> filter;
	private final VTable<T> table;

	VFilterDialog(VTable<T> table) {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		this.table = table;
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
		Composite client = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 15;
		layout.marginHeight = 5;
		client.setLayout(layout);
		filter = new VFilter<T>(client, table);
		filter.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		client.setLayoutData(new GridData(GridData.FILL_BOTH));
		return client;
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(new GridLayout());
		Button button;
		button = createButton(composite, IDialogConstants.CLIENT_ID, Messages.FilterDialog_CLOSE, false);
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent event) {
				close();
			}
		});
		button = createButton(composite, IDialogConstants.CLIENT_ID, Messages.FilterDialog_CLEAR_FILTERS, false);
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent event) {
				filter.removeFiltering();
			}
		});
		return composite;
	}
}
