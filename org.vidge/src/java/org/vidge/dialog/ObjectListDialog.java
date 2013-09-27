package org.vidge.dialog;

import java.util.ArrayList;
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


	public ObjectListDialog(String title, IEntityExplorer entityExplorer, IPropertyExplorer propertyExplorer) {
		super(entityExplorer, title, DEFAULT_SIZE,propertyExplorer);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, true);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		okPressed();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setSelection() {
		selection = (List<F>) propertyExplorer.getValue();
		if (selection == null) {
			selection = new ArrayList<F>();
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
