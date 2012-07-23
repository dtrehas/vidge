package org.vidge.controls.editor;

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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.vidge.SharedImages;
import org.vidge.VidgeResources;
import org.vidge.controls.chooser.Filter;
import org.vidge.controls.chooser.ObjectChooser;
import org.vidge.dialog.AbstractObjectDialog;
import org.vidge.dialog.SingleObjectDialog;
import org.vidge.inface.IEntityExplorer;
import org.vidge.inface.IPropertyExplorer;
import org.vidge.inface.ValueAction;
import org.vidge.util.FormContext;
import org.vidge.util.TypeUtil;

public class ListEditor<F> extends Composite {

	private Filter filter;
	private ObjectChooser<F> chooser;
	private IEntityExplorer tableExplorer;
	private IEntityExplorer addExplorer;
	private IEntityExplorer editExplorer;
	private Class<?> klass;
	private String propertyName;
	private Section section;
	protected List<F> selection;
	protected IEntityExplorer explorer;
	private Point size = AbstractObjectDialog.DEFAULT_SIZE;

	public ListEditor(Composite parent, int style) {
		super(parent, style);
	}

	public void init(IEntityExplorer entityExplorer, IPropertyExplorer propertyExplorer) {
		setValue(propertyExplorer);
		klass = TypeUtil.getGenericClass(entityExplorer.getInputClass(), propertyExplorer.getPropertyType());
		tableExplorer = TypeUtil.getExplorer(klass, FormContext.TABLE.name(), propertyExplorer);
		addExplorer = TypeUtil.getExplorer(klass, FormContext.CREATE.name(), propertyExplorer);
		editExplorer = TypeUtil.getExplorer(klass, FormContext.EDIT.name(), propertyExplorer);
		propertyName = propertyExplorer.getPropertyName();
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

	protected Control createContent(Composite parent, String title) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		section = toolkit.createSection(parent, Section.TITLE_BAR);
		section.setTextClient(getClient(section));
		section.setText(title);
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
		toolkit.createCompositeSeparator(section);
		createTableSection(section);
		section.setClient(chooser.getControl());
		return section;
	}

	private Control getClient(Composite parent) {
		ToolBar bar = new ToolBar(parent, SWT.FLAT);
		ToolItem item = new ToolItem(bar, SWT.PUSH);
		item.setImage(VidgeResources.getInstance().getImage(SharedImages.ACTION_PLUS));
		item.setToolTipText("Add Item");
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				addItem();
			}
		});
		item = new ToolItem(bar, SWT.PUSH);
		item.setImage(VidgeResources.getInstance().getImage(SharedImages.ACTION_MINUS));
		item.setToolTipText("Remove Item");
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				removeItem();
			}
		});
		item = new ToolItem(bar, SWT.PUSH);
		item.setImage(VidgeResources.getInstance().getImage(SharedImages.ACTION_EDIT));
		item.setToolTipText("Edit Item");
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				editItem();
			}
		});
		return bar;
	}

	private void removeItem() {
		F selectionItem = chooser.getSelection();
		if (selectionItem == null) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "No selection", "You have to select Item for removing");
		} else {
			if (MessageDialog.openConfirm(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Remove Item", "Do you reall want to remove This item: " + selectionItem + " ?")) { //$NON-NLS-1$
				selection.remove(selectionItem);
				chooser.setInput(selection);
				explorer.doInputChanged(selectionItem, ValueAction.DELETE, propertyName);
			}
		}
	}

	private void editItem() {
		F selectionItem = chooser.getSelection();
		if (selectionItem == null) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "No selection", "You have to select Item for editing");
		} else {
			editExplorer.explore(selectionItem);
			SingleObjectDialog<F> dialog = new SingleObjectDialog<F>(editExplorer, "Edit Item", size);
			if (dialog.open() == Window.OK) {
				dialog.getSelection();
				chooser.refresh();
			}
		}
	}

	private void createTableSection(Composite parent) {
		chooser = new ObjectChooser<F>(parent, (ObjectChooser.TITLE | ObjectChooser.TOOLKIT | ObjectChooser.NO_PAGES), tableExplorer, selection);
		chooser.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		chooser.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				F selectionItem = chooser.getSelection();
				if (selectionItem != null) {
					editItem();
				}
			}
		});
	}

	private void addItem() {
		try {
			addExplorer.explore(klass.newInstance());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		SingleObjectDialog<F> dialog = new SingleObjectDialog<F>(addExplorer, "Add Item", null);
		if (dialog.open() == Window.OK) {
			selection.add(dialog.getSelection());
			chooser.setInput(selection);
		}
	}
}
