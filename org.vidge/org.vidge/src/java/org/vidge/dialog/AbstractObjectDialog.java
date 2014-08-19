package org.vidge.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.vidge.explorer.def.StringExplorer;
import org.vidge.inface.IEntityExplorer;
import org.vidge.inface.IObjectDialog;
import org.vidge.util.PositionUtillity;

public abstract class AbstractObjectDialog<F> extends TitleAreaDialog implements IObjectDialog<F> {

	public static final Point DEFAULT_SIZE = new Point(800, 500);
	protected IEntityExplorer explorer;
	protected Point size;
	protected final String title;
	protected F selection;

	public AbstractObjectDialog(IEntityExplorer entityExplorer, String title, Point size) {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		this.explorer = entityExplorer;
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
			if (explorer.getDescription() == null) {
				setMessage(explorer.getDescription());
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

	@SuppressWarnings("unchecked")
	@Override
	public F getSelection() {
		if (explorer != null && explorer.getClass().equals(StringExplorer.class)) {
			return (F) explorer.getInput();
		}
		return selection;
	}

	public void setSize(Point point) {
		size = point;
	}
}
