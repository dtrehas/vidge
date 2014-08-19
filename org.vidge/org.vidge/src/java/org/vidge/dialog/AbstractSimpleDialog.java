package org.vidge.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.vidge.inface.IEntityExplorer;
import org.vidge.inface.IObjectDialog;
import org.vidge.inface.IPropertyExplorer;
import org.vidge.util.PositionUtillity;

public abstract class AbstractSimpleDialog<F> extends Dialog implements IObjectDialog<F> {

	public static final Point DEFAULT_SIZE = new Point(800, 500);
	protected IEntityExplorer explorer;
	protected Point size;
	protected final String title;
	protected F selection;
	protected IPropertyExplorer propertyExplorer;

	public AbstractSimpleDialog(IEntityExplorer entityExplorer, String title, Point size) {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		this.explorer = entityExplorer;
		setShellStyle(SWT.CLOSE | SWT.MAX | SWT.MIN | SWT.APPLICATION_MODAL | SWT.RESIZE | getDefaultOrientation());
		this.title = title;
		this.size = size;
		setSelection();
	}

	public AbstractSimpleDialog(IEntityExplorer entityExplorer, String title, Point size, IPropertyExplorer propertyExplorer) {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		this.explorer = entityExplorer;
		this.propertyExplorer = propertyExplorer;
		setShellStyle(SWT.CLOSE | SWT.MAX | SWT.MIN | SWT.APPLICATION_MODAL | SWT.RESIZE | getDefaultOrientation());
		this.title = title;
		this.size = size;
		setSelection();
	}

	@SuppressWarnings("unchecked")
	protected void setSelection() {
		if (explorer != null) {
			if (explorer.getInput() == null) {
				explorer.createInput();
			}
			selection = (F) explorer.getInput();
		}
	}

	@Override
	protected Control createContents(Composite parent) {
		if (size == null) {
			size = DEFAULT_SIZE;
		}
		parent.setSize(size);
		setDialogHeader();
		PositionUtillity.center(parent);
		Control control = super.createContents(parent);
		return control;
	}

	private void setDialogHeader() {
		if (explorer != null) {
			if (explorer.getHeader() == null) {
				if (title != null) {
					this.getShell().setText(title);
				}
			} else {
				this.getShell().setText(explorer.getHeader());
			}
		} else {
			if (title != null) {
				this.getShell().setText(title);
			}
		}
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.OK_ID == buttonId) {
			okPressed();
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			cancelPressed();
		}
	}

	@Override
	public F getSelection() {
		return selection;
	}

	public void setSize(Point point) {
		size = point;
	}
}
