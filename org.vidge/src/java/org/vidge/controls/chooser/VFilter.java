package org.vidge.controls.chooser;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class VFilter<T> {

	private ArrayList<Button> checkBoxList = new ArrayList<Button>();
	private Composite client;
	private final VTable<T> table;

	public VFilter(Composite parent, VTable<T> table) {
		this.table = table;
		init(parent);
	}

	private void init(Composite parent) {
		client = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		client.setLayout(layout);
		for (int a = 0; a < table.getColumns().size(); a++) {
			try {
				VColumn<T> column = table.getColumns().get(a);
				if (column.explorer == null) {
					continue;
				}
				Label label = new Label(client, SWT.WRAP);
				label.setText(column.explorer.getLabel());
				label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				FilterSwitch filterSwitch = new FilterSwitch(column, client, a);
				checkBoxList.add(filterSwitch.getButton());
				filterSwitch.getButton().setLayoutData(new GridData());
				TextFilterEditor txt = new TextFilterEditor(column, client);
				txt.text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
		client.pack();
	}

	public Control getControl() {
		return client;
	}
	private class FilterSwitch {

		Button checkbox;

		FilterSwitch(final VColumn<T> column, Composite parent, final int index) {
			checkbox = new Button(parent, SWT.CHECK);
			checkbox.setToolTipText(Messages.Filter_ON_OFF_FILTER);
			checkbox.setSelection(column.isFiltered());
			checkbox.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					column.setFiltered(checkbox.getSelection());
				}
			});
		}

		public Button getButton() {
			return checkbox;
		}
	}
	private class TextFilterEditor {

		private Text text;
		private ModifyListener filterModifyListener = new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				column.setFilterMask(text.getText());
			}
		};
		private final VColumn<T> column;

		TextFilterEditor(VColumn<T> column, Composite parent) {
			this.column = column;
			text = new Text(parent, SWT.BORDER);
			text.setText(VColumn.defaultFilterMask);
			text.setText(column.getFilterMask());
			text.addModifyListener(filterModifyListener);
		}
	}

	public void removeFiltering() {
		for (int a = 0; a < checkBoxList.size(); a++) {
			checkBoxList.get(a).setSelection(false);
		}
		table.removeFiltering();
	}
}
