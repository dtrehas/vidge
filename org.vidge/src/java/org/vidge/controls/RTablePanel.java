package org.vidge.controls;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.vidge.FormRegistry;
import org.vidge.SharedImages;
import org.vidge.VidgeResources;
import org.vidge.controls.chooser.ObjectChooser;
import org.vidge.dialog.SingleObjectDialog;
import org.vidge.explorer.FormExplorer;
import org.vidge.inface.IChangeListener;
import org.vidge.inface.IEntityExplorer;
import org.vidge.inface.IForm;
import org.vidge.inface.IObjectDialog;
import org.vidge.inface.ValueAction;
import org.vidge.util.FormContext;

@SuppressWarnings("rawtypes")
public class RTablePanel<T> {

	public static final int ALLACTIONS = SWT.APPLICATION_MODAL;
	public static final int REFRESH = ALLACTIONS << 1;
	public static final int CREATE = ALLACTIONS << 2;
	public static final int EDIT = ALLACTIONS << 3;
	public static final int REMOVE = ALLACTIONS << 4;
	public static final int NONE = ALLACTIONS << 5;
	private ObjectChooser<T> vtable;
	private IEntityExplorer expIn;
	private List<T> objectList;
	private Composite section;
	private final Class<T> objClass;
	private String title;
	private int style = ALLACTIONS;
	private ArrayList<IChangeListener> listenerList = new ArrayList<IChangeListener>();
	private Class<IForm<T>> formClass;

	public RTablePanel(Class<T> objClass, List<T> listIn, int style) {
		this(objClass, listIn);
		this.style = style;
	}

	public RTablePanel(Composite parent, Class<T> objClass, List<T> objectList, int style) {
		this(parent, objClass, objectList);
		this.style = style;
	}

	public RTablePanel(Class<T> objClass, List<T> listIn) {
		this.objClass = objClass;
		this.objectList = (listIn == null ? new ArrayList<T>() : listIn);
		expIn = FormRegistry.getEntityExplorer(FormContext.TABLE.name(), objClass);
	}

	public RTablePanel(Composite parent, Class<T> objClass, List<T> objectList) {
		this(objClass, objectList);
		createViewer(parent);
	}

	public RTablePanel(Composite parent, Class<T> objClass, List<T> objectList, String title) {
		this(objClass, objectList);
		this.title = title;
		createViewer(parent);
	}

	@SuppressWarnings("unchecked")
	public RTablePanel(Class<T> objClass, List<T> listIn, Class formClass, int style) {
		this(objClass, listIn, style);
		this.formClass = formClass;
		expIn = new FormExplorer<T>(formClass);
	}

	public void addChangeListener(IChangeListener listener) {
		listenerList.add(listener);
	}

	private void fireTableChanged() {
		for (IChangeListener listener : listenerList) {
			listener.changed();
		}
	}

	public Composite createViewer(Composite parent) {
		section = new Composite(parent, style);
		section.setLayout(new FillLayout());
		if (formClass == null) {
			vtable = new ObjectChooser(section, ObjectChooser.NO_PAGES | SWT.CHECK, objClass, objectList);
		} else {
			vtable = new ObjectChooser(section, ObjectChooser.NO_PAGES | SWT.CHECK, new FormExplorer<T>(formClass), objectList);
		}
		vtable.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				if (expIn.edit()) {
					if ((style & ALLACTIONS) != 0 || (style & EDIT) != 0) {
						if (vtable.getSelection() != null) {
							editItem();
						}
					}
				}
			}
		});
		return section;
	}

	public ToolBar getClient() {
		ToolBar bar = new ToolBar(section, SWT.FLAT);
		if (expIn.create()) {
			if ((style & ALLACTIONS) != 0 || (style & CREATE) != 0) {
				ToolItem item = new ToolItem(bar, SWT.PUSH);
				item.setImage(VidgeResources.getInstance().getImage(SharedImages.ACTION_PLUS));
				item.setToolTipText("Add Item");
				item.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						addItem();
					}
				});
			}
		}
		if (expIn.remove()) {
			if ((style & ALLACTIONS) != 0 || (style & REMOVE) != 0) {
				ToolItem item = new ToolItem(bar, SWT.PUSH);
				item.setImage(VidgeResources.getInstance().getImage(SharedImages.ACTION_MINUS));
				item.setToolTipText("Remove Item");
				item.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						deleteItem();
					}
				});
			}
		}
		if (expIn.edit()) {
			if ((style & ALLACTIONS) != 0 || (style & EDIT) != 0) {
				ToolItem item = new ToolItem(bar, SWT.PUSH);
				item.setImage(VidgeResources.getInstance().getImage(SharedImages.ACTION_EDIT));
				item.setToolTipText("Edit Item");
				item.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						editItem();
					}
				});
			}
		}
		if (expIn.refresh()) {
			if ((style & ALLACTIONS) != 0 || (style & REFRESH) != 0) {
				ToolItem item = new ToolItem(bar, SWT.PUSH);
				item.setToolTipText("Refresh");
				item.setImage(VidgeResources.getInstance().getImage(SharedImages.ACTION_REFRESH));
				item.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						refresh();
					}
				});
			}
		}
		return bar;
	}

	public void addItem() {
		IEntityExplorer entityExplorer = FormRegistry.getEntityExplorer(FormContext.CREATE.name(), objClass);
		IObjectDialog<T> dialog = new SingleObjectDialog<T>(entityExplorer, "Add Item", null);
		if (dialog.open() == Window.OK) {
			entityExplorer.doInputChanged(dialog.getSelection(), ValueAction.SAVE, null);
			refresh();
			fireTableChanged();
		}
	}

	@SuppressWarnings("unchecked")
	public void editItem() {
		T selection = vtable.getSelection();
		if (selection == null) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "No selection", "You have to select Item for editing");
		} else {
			IEntityExplorer entityExplorer = FormRegistry.getEntityExplorer(FormContext.EDIT.name(), objClass);
			entityExplorer.explore(selection);
			IObjectDialog<T> dialog = new SingleObjectDialog<T>(entityExplorer, "Edit Item", null);
			if (dialog.open() == Window.OK) {
				selection = (T) entityExplorer.doInputChanged(dialog.getSelection(), ValueAction.UPDATE, null);
				refresh();
				fireTableChanged();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void deleteItem() {
		T selection = vtable.getSelection();
		if (selection == null) {
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "No selection", "You have to select Item for removing");
		} else {
			if (MessageDialog.openConfirm(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Remove Item", "Do you really want to remove This item ?")) { //$NON-NLS-1$
				IEntityExplorer entityExplorer = FormRegistry.getEntityExplorer(FormContext.EDIT.name(), objClass);
				selection = (T) entityExplorer.doInputChanged(selection, ValueAction.DELETE, null);
				refresh();
				fireTableChanged();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void refresh() {
		objectList = expIn.getData();
		if (objectList != null) {
			vtable.setInput(objectList);
		}
	}

	public Table getTable() {
		return vtable.getTableViewer().getTable();
	}

	public void setInput(List<T> list) {
		vtable.setInput(list);
	}

	public void addDoubleClickListener(IDoubleClickListener listener) {
		vtable.addDoubleClickListener(listener);
	}

	public T getSelection() {
		return vtable.getSelection();
	}

	public void addSelectionListener(SelectionListener listener) {
		// vtable.addSelectionChangedListener(listener);
	}

	public void removeSelectionListener(SelectionListener listener) {
		// vtable.removeSelectionListener(listener);
	}

	public void showFilterDialog() {
		vtable.showFilterDialog();
	}

	public void removeFiltering() {
		vtable.removeFiltering();
	}
}
