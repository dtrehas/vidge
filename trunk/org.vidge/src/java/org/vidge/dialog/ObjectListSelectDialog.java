package org.vidge.dialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.vidge.FormRegistry;
import org.vidge.inface.IChangeListener;
import org.vidge.inface.IEntityExplorer;
import org.vidge.inface.IPropertyExplorer;
import org.vidge.util.FormContext;

public class ObjectListSelectDialog<F> extends AbstractSimpleDialog<List<F>> {

	private boolean singleSelect = false;
	private IPropertyExplorer propertyExplorer;
	private ListSelectPanel<F> panel;
	private Class<F> klassIn;

	@SuppressWarnings("unchecked")
	public ObjectListSelectDialog(String title, IEntityExplorer entityExplorer, IPropertyExplorer propertyExplorer, boolean singleSelect) {
		super(entityExplorer, title, DEFAULT_SIZE);
		this.propertyExplorer = propertyExplorer;
		this.singleSelect = singleSelect;
		selection = (List<F>) propertyExplorer.getValidValues();
	}

	public ObjectListSelectDialog(String title, Class<F> klassIn, List<F> list, boolean singleSelect) {
		super(FormRegistry.getEntityExplorer(FormContext.TABLE.name(), klassIn), title, DEFAULT_SIZE);
		this.klassIn = klassIn;
		this.singleSelect = singleSelect;
		selection = list;
	}

	protected Control createDialogArea(Composite parent) {
		this.getShell().setText(title);
		if (propertyExplorer == null && klassIn != null) {
			panel = new ListSelectPanel<F>(parent, null, klassIn, selection);
		} else {
			panel = new ListSelectPanel<F>(parent, explorer, propertyExplorer, singleSelect);
		}
		panel.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		panel.getTable().addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				okPressed();
			}
		});
		panel.addChangeListener(new IChangeListener() {

			@Override
			public void changed() {
				selection = panel.getSelection();
			}
		});
		return panel.getControl();
	}

	public void setInput(Collection<F> objectList) {
		if (selection == null) {
			selection = new ArrayList<F>();
		}
		selection.addAll(objectList);
		if (panel.getTable() != null && selection != null) {
			panel.setInput(selection);
			panel.getTable().refresh();
		}
	}

	@Override
	protected void okPressed() {
		selection = panel.getSelection();
		super.okPressed();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}
}
