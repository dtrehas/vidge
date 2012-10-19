package org.vidge.dialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;
import org.vidge.SharedImages;
import org.vidge.VidgeResources;
import org.vidge.controls.chooser.VTable;
import org.vidge.inface.IChangeListener;
import org.vidge.inface.IEntityExplorer;
import org.vidge.inface.IPropertyExplorer;
import org.vidge.inface.ValueAction;
import org.vidge.util.FormContext;
import org.vidge.util.TypeUtil;

public class ListPanel<F> {

	private VTable<F> table;
	private IEntityExplorer addExplorer;
	private IEntityExplorer editExplorer;
	private Class<F> klass;
	private String propertyName;
	private Section section;
	private final IPropertyExplorer propertyExplorer;
	protected List<F> selection;
	private final IEntityExplorer explorer;
	private ArrayList<IChangeListener> listenerList = new ArrayList<IChangeListener>();

	@SuppressWarnings("unchecked")
	public ListPanel(Composite parent, IEntityExplorer entityExplorer, IPropertyExplorer propertyExplorer) {
		this.explorer = entityExplorer;
		this.propertyExplorer = propertyExplorer;
		setValue(propertyExplorer);
		klass = (Class<F>) TypeUtil.getGenericClass(entityExplorer.getInputClass(), propertyExplorer.getPropertyType());
		addExplorer = TypeUtil.getExplorer(klass, FormContext.CREATE.name(), propertyExplorer);
		editExplorer = TypeUtil.getExplorer(klass, FormContext.EDIT.name(), propertyExplorer);
		propertyName = propertyExplorer.getPropertyName();
		section = new Section(parent, Section.TITLE_BAR);
		section.setTextClient(getClient(section));
		section.setText(propertyExplorer.getValidator().getHelp());
		table = new VTable<F>(section, klass, selection, SWT.NONE);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				F selectionItem = table.getSelection();
				if (selectionItem != null) {
					editItem();
				}
			}
		});
		section.setClient(table);
	}

	public void addChangeListener(IChangeListener listener) {
		listenerList.add(listener);
	}

	private void fireTableChanged() {
		for (IChangeListener listener : listenerList) {
			listener.changed();
		}
	}

	@SuppressWarnings({
			"unchecked", "rawtypes"
	})
	protected void setValue(IPropertyExplorer propertyExplorer) {
		Object value = propertyExplorer.getValue();
		if (value == null) {
			selection = new ArrayList<F>();
		} else {
			if (Set.class.isAssignableFrom(value.getClass())) {
				selection = new ArrayList<F>((Collection<? extends F>) value);
			} else if (Map.class.isAssignableFrom(value.getClass())) {
				selection = new ArrayList<F>(((Map) value).values());
			} else if (value.getClass().isArray()) {
				F[] array = (F[]) value;
				selection = new ArrayList<F>();
				for (int a = 0; a < array.length; a++) {
					selection.add(array[a]);
				}
			} else {
				selection = (List<F>) value;
			}
		}
	}

	private Control getClient(Composite parent) {
		ToolBar bar = new ToolBar(parent, SWT.FLAT);
		ToolItem item = new ToolItem(bar, SWT.PUSH);
		item.setImage(VidgeResources.getInstance().getImage(SharedImages.ACTION_PLUS));
		item.setToolTipText(Messages.ObjectListDialog_0);
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				addItem();
			}
		});
		item = new ToolItem(bar, SWT.PUSH);
		item.setImage(VidgeResources.getInstance().getImage(SharedImages.ACTION_MINUS));
		item.setToolTipText(Messages.ObjectListDialog_1);
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				removeItem();
			}
		});
		item = new ToolItem(bar, SWT.PUSH);
		item.setImage(VidgeResources.getInstance().getImage(SharedImages.ACTION_EDIT));
		item.setToolTipText(Messages.ObjectListDialog_2);
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				editItem();
			}
		});
		return bar;
	}

	private void addItem() {
		try {
			addExplorer.explore(addExplorer.createInput());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		SingleObjectDialog<F> dialog = new SingleObjectDialog<F>(addExplorer, Messages.ObjectListDialog_3, null);
		if (dialog.open() == Window.OK) {
			selection.add(dialog.getSelection());
			table.setInput(selection);
			propertyExplorer.getValidator().validateComplete(selection);
			section.setText(propertyExplorer.getValidator().getHelp());
			fireTableChanged();
		}
	}

	private void removeItem() {
		F selectionItem = table.getSelection();
		if (selectionItem == null) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.ObjectListDialog_4, Messages.ObjectListDialog_5);
		} else {
			if (MessageDialog.openConfirm(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Remove Item", Messages.ObjectListDialog_6 + selectionItem + " ?")) { //$NON-NLS-1$ //$NON-NLS-3$
				selection.remove(selectionItem);
				propertyExplorer.getValidator().validateComplete(selection);
				section.setText(propertyExplorer.getValidator().getHelp());
				table.setInput(selection);
				explorer.doInputChanged(selectionItem, ValueAction.DELETE, propertyName);
				fireTableChanged();
			}
		}
	}

	private void editItem() {
		F selectionItem = table.getSelection();
		if (selectionItem == null) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.ObjectListDialog_8, Messages.ObjectListDialog_9);
		} else {
			editExplorer.explore(selectionItem);
			SingleObjectDialog<F> dialog = new SingleObjectDialog<F>(editExplorer, Messages.ObjectListDialog_10);
			if (dialog.open() == Window.OK) {
				dialog.getSelection();
				propertyExplorer.getValidator().validateComplete(selection);
				section.setText(propertyExplorer.getValidator().getHelp());
				table.refresh();
				fireTableChanged();
			}
		}
	}

	public Control getControl() {
		return section;
	}

	public List<F> getSelection() {
		return selection;
	}
}
