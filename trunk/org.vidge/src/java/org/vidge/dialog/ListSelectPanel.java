package org.vidge.dialog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.Section;
import org.vidge.controls.chooser.VTable;
import org.vidge.form.validator.IValidator;
import org.vidge.inface.IChangeListener;
import org.vidge.inface.IEntityExplorer;
import org.vidge.inface.IPropertyExplorer;
import org.vidge.util.TypeUtil;

public class ListSelectPanel<F> {

	private VTable<F> table;
	private Set<F> selection = new HashSet<F>();
	private IPropertyExplorer propertyExplorer;
	private Section section;
	private ArrayList<IChangeListener> listenerList = new ArrayList<IChangeListener>();

	@SuppressWarnings("unchecked")
	public ListSelectPanel(Composite parent, IEntityExplorer entityExplorer, IPropertyExplorer propertyExplorer, boolean singleSelect) {
		this.propertyExplorer = propertyExplorer;
		Class<F> klass = (Class<F>) TypeUtil.getGenericClass(entityExplorer.getInputClass(), propertyExplorer.getPropertyType());
		if (singleSelect) {
			createContentSingle(parent, klass, (List<F>) propertyExplorer.getValidValues(), (F) propertyExplorer.getValue());
		} else {
			createContent(parent, klass, (List<F>) propertyExplorer.getValidValues(), (List<F>) propertyExplorer.getValue());
		}
	}

	public ListSelectPanel(Composite parent, Class<F> klass, List<F> list, List<F> selection) {
		createContent(parent, klass, list, selection);
	}

	public ListSelectPanel(Composite parent, F selection, Class<F> klass, List<F> list) {
		createContentSingle(parent, klass, list, selection);
	}

	public void addChangeListener(IChangeListener listener) {
		listenerList.add(listener);
	}

	private void fireTableChanged() {
		for (IChangeListener listener : listenerList) {
			listener.changed();
		}
	}

	private void createContentSingle(Composite parent, Class<F> klass, List<F> list, F value) {
		if (value != null)
			selection.add(value);
		section = new Section(parent, Section.TITLE_BAR);
		section.setText(propertyExplorer == null ? "" : propertyExplorer.getValidator().getHelp());
		table = new VTable<F>(section, klass, list, SWT.CHECK);
		section.setClient(table);
		for (TableItem item : table.getTable().getItems()) {
			item.getText();
			if (selection.contains(item.getData())) {
				item.setChecked(true);
			}
		}
		table.addSelectionListener(new SelectionAdapter() {

			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem tableItem = (TableItem) e.item;
				F selObj = (F) tableItem.getData();
				int indexOf = table.getTable().indexOf(tableItem);
				boolean selected = table.getTable().isSelected(indexOf);
				selection.clear();
				if (selected) {
					tableItem.setChecked(!tableItem.getChecked());
				}
				if (!tableItem.getChecked()) {
					return;
				}
				selection.add(selObj);
				for (TableItem item : table.getTable().getItems()) {
					if (!item.equals(tableItem)) {
						item.setChecked(false);
					}
				}
				if (propertyExplorer != null) {
					IValidator<?> validator = propertyExplorer.getValidator();
					if (validator != null) {
						validator.validateComplete(selObj);
						section.setText(validator.getHelp());
					}
				}
				fireTableChanged();
			}
		});
	}

	private void createContent(Composite parent, Class<F> klass, List<F> list, List<F> selectionList) {
		this.selection.addAll(selectionList);
		section = new Section(parent, Section.TITLE_BAR);
		section.setText(propertyExplorer == null ? "" : propertyExplorer.getValidator().getHelp());
		table = new VTable<F>(section, klass, list, SWT.CHECK);
		section.setClient(table);
		for (TableItem item : table.getTable().getItems()) {
			item.getText();
			if (selection.contains(item.getData())) {
				item.setChecked(true);
			}
		}
		table.addSelectionListener(new SelectionAdapter() {

			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem tableItem = (TableItem) e.item;
				int indexOf = table.getTable().indexOf(tableItem);
				boolean selected = table.getTable().isSelected(indexOf);
				selection.clear();
				if (selected) {
					tableItem.setChecked(!tableItem.getChecked());
				}
				for (TableItem item : table.getTable().getItems()) {
					item.getText();
					if (item.getChecked()) {
						selection.add((F) item.getData());
					}
				}
				if (propertyExplorer != null) {
					IValidator<?> validator = propertyExplorer.getValidator();
					if (validator != null) {
						validator.validateComplete(getSelection());
						section.setText(propertyExplorer.getValidator().getHelp());
					}
				}
				fireTableChanged();
			}
		});
	}

	public void setInput(List<F> list) {
		table.setInput(list);
	}

	public List<F> getSelection() {
		return new ArrayList<F>(selection);
	}

	public VTable<?> getTable() {
		return table;
	}

	public Control getControl() {
		return section;
	}

	public void setEnabled(boolean enabled) {
		table.setEnabled(enabled);
	}
}
