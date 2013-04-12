package org.vidge.controls;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.vidge.FormRegistry;
import org.vidge.SharedImages;
import org.vidge.Vidge;
import org.vidge.VidgeResources;
import org.vidge.controls.chooser.IPageListener;
import org.vidge.controls.chooser.NTable;
import org.vidge.controls.chooser.VTable;
import org.vidge.dialog.SingleObjectDialog;
import org.vidge.explorer.FormExplorer;
import org.vidge.inface.IChangeListener;
import org.vidge.inface.IEntityExplorer;
import org.vidge.inface.IForm;
import org.vidge.inface.IFormInputChangeListener;
import org.vidge.inface.IObjectDialog;
import org.vidge.inface.ValueAction;
import org.vidge.util.FormContext;

@SuppressWarnings("rawtypes")
public class TablePanel<T> {

	private VTable<T> vtable;
	private IEntityExplorer expIn;
	private List<T> objectList;
	private Section section;
	private final Class<T> objClass;
	private String title;
	private int style = Vidge.ALLACTIONS;
	private ArrayList<IChangeListener> listenerList = new ArrayList<IChangeListener>();
	private ArrayList<IFormInputChangeListener> changeListenerList = new ArrayList<IFormInputChangeListener>();
	private Class<IForm<T>> formClass;

	public TablePanel(Class<T> objClass, List<T> listIn) {
		this(objClass, listIn, Vidge.ALLACTIONS);
	}

	public TablePanel(Composite parent, Class<T> objClass, List<T> objectList, int style) {
		this(objClass, objectList, style);
	}

	public TablePanel(Composite parent, Class<T> objClass, List<T> objectList) {
		this(objClass, objectList, Vidge.ALLACTIONS);
		createViewer(parent);
	}

	public TablePanel(Composite parent, Class<T> objClass, List<T> objectList, String title) {
		this(objClass, objectList, Vidge.ALLACTIONS);
		this.title = title;
		createViewer(parent);
	}

	@SuppressWarnings("unchecked")
	public TablePanel(Class<T> objClass, List<T> listIn, Class formClass, int style) {
		this(objClass, listIn, style);
		this.formClass = formClass;
		expIn = new FormExplorer<T>(formClass);
	}

	public TablePanel(Class<T> objClass, List<T> listIn, int style) {
		this.objClass = objClass;
		this.style = style;
		this.objectList = (listIn == null ? new ArrayList<T>() : listIn);
		expIn = FormRegistry.getEntityExplorer(FormContext.TABLE.name(), objClass);
	}

	public void addChangeListener(IChangeListener listener) {
		listenerList.add(listener);
	}

	public void addFormChangeListener(IFormInputChangeListener listener) {
		changeListenerList.add(listener);
	}

	private void fireTableChanged() {
		for (IChangeListener listener : listenerList) {
			listener.changed();
		}
	}

	private void fireFormChanged(Object value, ValueAction action, String attribute) {
		for (IFormInputChangeListener listener : changeListenerList) {
			listener.doInputChanged(value, action, attribute);
		}
	}

	public Composite createViewer(Composite parent) {
		Composite result = null;
		if ((style & Vidge.NO_TOOLKIT) != 0) {
			if ((style & Vidge.NO_ACTIONS) != 0) {
				createTable(parent);
				result = vtable;
			} else {
				section = new Section(parent, Section.TITLE_BAR);
				section.setTextClient(getClient());
				section.setText(title != null ? title : "");
				createTable(section);
				section.setClient(vtable);
				result = section;
			}
		} else {
			if ((style & Vidge.NO_ACTIONS) != 0) {
				createTable(parent);
				result = vtable;
			} else {
				FormToolkit toolkit = new FormToolkit(parent.getDisplay());
				section = toolkit.createSection(parent, Section.TITLE_BAR);
				section.setTextClient(getClient());
				toolkit.createCompositeSeparator(section);
				section.setText(title != null ? title : "");
				createTable(section);
				section.setClient(vtable);
				result = section;
			}
		}
		vtable.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				if (expIn.edit()) {
					if ((style & Vidge.ALLACTIONS) != 0 || (style & Vidge.EDIT) != 0) {
						if (vtable.getSelection() != null) {
							editItem();
						}
					}
				}
			}
		});
		return result;
	}

	protected void createTable(Composite parent) {
		if ((style & Vidge.NO_VIRTUAL) != 0) {
			if (formClass == null) {
				vtable = new NTable<T>(parent, objClass, objectList, style);
			} else {
				vtable = new NTable<T>(parent, new FormExplorer<T>(formClass), objectList, style);
			}
		} else {
			if (formClass == null) {
				vtable = new VTable<T>(parent, objClass, objectList, style);
			} else {
				vtable = new VTable<T>(parent, new FormExplorer<T>(formClass), objectList, style);
			}
		}
	}

	public ToolBar getClient() {
		ToolBar bar = new ToolBar(section, SWT.FLAT);
		createDefaultActions(bar);
		return bar;
	}

	private void createDefaultActions(ToolBar bar) {
		if (expIn.create()) {
			if ((style & Vidge.ALLACTIONS) != 0 || (style & Vidge.CREATE) != 0) {
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
			if ((style & Vidge.ALLACTIONS) != 0 || (style & Vidge.REMOVE) != 0) {
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
			if ((style & Vidge.ALLACTIONS) != 0 || (style & Vidge.EDIT) != 0) {
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
			if ((style & Vidge.ALLACTIONS) != 0 || (style & Vidge.REFRESH) != 0) {
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
	}

	public void addItem() {
		IEntityExplorer entityExplorer = FormRegistry.getEntityExplorer(FormContext.CREATE.name(), objClass);
		IObjectDialog<T> dialog = new SingleObjectDialog<T>(entityExplorer, "Add Item", null);
		if (dialog.open() == Window.OK) {
			entityExplorer.doInputChanged(dialog.getSelection(), ValueAction.SAVE, null);
			fireTableChanged();
			fireFormChanged(dialog.getSelection(), ValueAction.SAVE, null);
			refresh();
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
				fireTableChanged();
				fireFormChanged(dialog.getSelection(), ValueAction.UPDATE, null);
				refresh();
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
				fireTableChanged();
				fireFormChanged(selection, ValueAction.DELETE, null);
				refresh();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void refresh() {
		objectList = expIn.getData();
		if (objectList != null) {
			vtable.setInput(objectList);
		} else {
			vtable.refresh();
		}
	}

	public Section getSection() {
		return section;
	}

	public Table getTable() {
		return vtable.getTable();
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
		vtable.addSelectionListener(listener);
	}

	public void removeSelectionListener(SelectionListener listener) {
		vtable.removeSelectionListener(listener);
	}

	public void setText(String string) {
		if (section != null)
			section.setText(string);
	}

	public void showFilterDialog() {
		vtable.showFilterDialog();
	}

	public void removeFiltering() {
		vtable.removeFiltering();
	}

	public Control getControl() {
		return section == null ? vtable : section;
	}

	public void setActions(final IAction... actions) {
		ToolBar bar = new ToolBar(section, SWT.FLAT);
		createDefaultActions(bar);
		for (final IAction action : actions) {
			ToolItem item = new ToolItem(bar, SWT.PUSH);
			if (action.getText() != null)
				item.setText(action.getText());
			if (action.getToolTipText() != null)
				item.setToolTipText(action.getToolTipText());
			if (action.getImageDescriptor() != null)
				item.setImage(action.getImageDescriptor().createImage());
			item.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					action.run();
				}
			});
		}
		section.setTextClient(bar);
	}

	public List<T> getInput() {
		return vtable.getInput();
	}

	public void setPageListener(IPageListener iPageListener) {
		vtable.setPageListener(iPageListener);
	}

	public void setPageInput(List<T> page) {
		vtable.setPageInput(page);
	}

	public void setCountValues(int[] countValues) {
		vtable.getPageManager().setCountValues(countValues);
	}

	public IEntityExplorer getEntityExplorer() {
		return expIn;
	}
}
