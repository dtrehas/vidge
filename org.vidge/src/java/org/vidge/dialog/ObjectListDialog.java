package org.vidge.dialog;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.vidge.inface.IChangeListener;
import org.vidge.inface.IEntityExplorer;
import org.vidge.inface.IPropertyExplorer;

public class ObjectListDialog<F> extends AbstractSimpleDialog<List<F>> {

	private final IPropertyExplorer propertyExplorer;

	public ObjectListDialog(String title, IEntityExplorer entityExplorer, IPropertyExplorer propertyExplorer) {
		super(entityExplorer, title, DEFAULT_SIZE);
		this.propertyExplorer = propertyExplorer;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
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
	protected Control createDialogArea(Composite parent) {
		final ListPanel<F> listPanel = new ListPanel<F>(parent, explorer, propertyExplorer);
		listPanel.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		selection = listPanel.getSelection();
		listPanel.addChangeListener(new IChangeListener() {

			@Override
			public void changed() {
				selection = listPanel.getSelection();
			}
		});
		return listPanel.getControl();
	}

	public void setSize(Point point) {
		size = point;
	}
}
