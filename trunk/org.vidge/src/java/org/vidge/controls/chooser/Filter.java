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
import org.eclipse.ui.forms.widgets.TableWrapData;

public class Filter {

	private ArrayList<Button> checkBoxList = new ArrayList<Button>();
	private Composite client;
	private final ObjectChooser chooser;

	public Filter(Composite parent, ObjectChooser chooser) {
		this.chooser = chooser;
		init(parent);
	}
	
	private void init(Composite parent) {
		client = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		client.setLayout(layout);
		for (int a = 0; a < chooser.getFilterCount(); a++) {
			try {
				Label label = new Label(client, SWT.WRAP);
				label.setText(chooser.getFilterLabel(a));
				label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				//label.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

				FilterSwitch filterSwitch = new FilterSwitch(chooser.getColumn(a+ chooser.getFirstColumn()), client, a+chooser.getFirstColumn());
				checkBoxList.add(filterSwitch.getButton());
				filterSwitch.getButton().setLayoutData(new GridData());
				//filterSwitch.getButton().setLayoutData(new TableWrapData());
				
				TextFilterEditor txt = new TextFilterEditor(chooser.getColumn(a+chooser.getFirstColumn()), client, a+chooser.getFirstColumn());
				txt.text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
			//txt.text.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		}
		client.pack();
	}

	public Control getControl() {
		return client;
	}

	private class FilterSwitch {

		Button checkbox;

		FilterSwitch(Column column, Composite parent, final int index) {
			checkbox = new Button(parent, SWT.CHECK);
			checkbox.setToolTipText(Messages.Filter_ON_OFF_FILTER);
			checkbox.setSelection(column.isFiltered());
			checkbox.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					chooser.setFiltered(checkbox.getSelection(), index);
				}

			});
		}

		public Button getButton() {
			return checkbox;
		}
	}

	private class TextFilterEditor {

		private Text text;
		private final int index;
		private ModifyListener filterModifyListener = new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				chooser.setFilterMask(text.getText(), index);
			}

		};

		TextFilterEditor(Column column, Composite parent, int index) {
			this.index = index;
			text = new Text(parent, SWT.BORDER);
			text.setText(Column.defaultFilterMask);
			text.setText(column.getFilterMask());
			text.addModifyListener(filterModifyListener);
		}

		public void setLayoutData(TableWrapData data) {
			text.setLayoutData(data);
		}

		public String getText() {
			return text.getText();
		}

		public void setText(String string) {
			text.removeModifyListener(filterModifyListener);
			text.setText(string);
			text.addModifyListener(filterModifyListener);
		}

	}

	public void removeFiltering() {
		for (int a = 0; a < checkBoxList.size(); a++) {
			checkBoxList.get(a).setSelection(false);
		}
		chooser.removeFiltering();
	}
}
