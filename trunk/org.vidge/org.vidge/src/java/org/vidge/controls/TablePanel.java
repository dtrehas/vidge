package org.vidge.controls;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
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
import org.vidge.VidgeException;
import org.vidge.VidgeResources;
import org.vidge.controls.chooser.IPageListener;
import org.vidge.controls.chooser.NTable;
import org.vidge.controls.chooser.VTable;
import org.vidge.dialog.SingleObjectDialog;
import org.vidge.explorer.FormExplorer;
import org.vidge.form.IFormInputChangeListener;
import org.vidge.inface.IChangeListener;
import org.vidge.inface.IEntityExplorer;
import org.vidge.inface.IObjectDialog;
import org.vidge.util.FormContext;
import org.vidge.util.StringUtil;
import org.vidge.util.ValueAction;

@SuppressWarnings("rawtypes")
public class TablePanel<T> {

	private static final int ENTER_KEY = 16777296;
	private static final int ENTER_KEY1 = 13;
	private static final int MINUS_KEY = 16777261;
	private static final int PLUS_KEY = 16777259;
	private VTable<T> vtable;
	private IEntityExplorer expIn;
	private List<T> objectList;
	private Section section;
	private final Class<T> objClass;
	private String title;
	private int style = Vidge.ALLACTIONS;
	private ArrayList<IChangeListener> listenerList = new ArrayList<IChangeListener>();
	private ArrayList<IFormInputChangeListener> changeListenerList = new ArrayList<IFormInputChangeListener>();

	public TablePanel(Class<T> objClass, List<T> listIn) {
		this(objClass, listIn, Vidge.ALLACTIONS);
	}

	public TablePanel(Composite parent, Class<T> objClass, List<T> objectList, int style) {
		this(objClass, objectList, style);
		createViewer(parent);
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

	public TablePanel(Class<T> objClass, List<T> listIn, FormExplorer formExplorer, int style) {
		this.objClass = objClass;
		this.style = style;
		this.objectList = (listIn == null ? new ArrayList<T>() : listIn);
		expIn = formExplorer;
	}

	@SuppressWarnings("unchecked")
	public TablePanel(Class<T> objClass, List<T> listIn, Class formClass, int style) {
		this.objClass = objClass;
		this.style = style;
		this.objectList = (listIn == null ? new ArrayList<T>() : listIn);
		expIn = new FormExplorer<T>(formClass);
	}

	public TablePanel(Class<T> objClass, List<T> listIn, int style) {
		this.objClass = objClass;
		this.style = style;
		this.objectList = (listIn == null ? new ArrayList<T>() : listIn);
		expIn = FormRegistry.getEntityExplorer(FormContext.TABLE.name(), objClass);
		if (expIn == null) {
			throw new VidgeException(Messages.TablePanel_ExplorerNotFound + objClass);
		}
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
				section.setText(title != null ? title : StringUtil.NN);
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
				section.setText(title != null ? title : StringUtil.NN);
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
						editItem();
					}
				}
			}
		});
		vtable.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == PLUS_KEY) {
					if ((style & Vidge.ALLACTIONS) != 0 || (style & Vidge.CREATE) != 0) {
						addItem();
					}
				} else if (e.keyCode == MINUS_KEY) {
					if ((style & Vidge.ALLACTIONS) != 0 || (style & Vidge.REMOVE) != 0) {
						deleteItem();
					}
				} else if (e.keyCode == ENTER_KEY || e.keyCode == ENTER_KEY1) {
					if ((style & Vidge.ALLACTIONS) != 0 || (style & Vidge.EDIT) != 0) {
						editItem();
					}
				}
			}
		});
		return result;
	}

	protected void createTable(Composite parent) {
		if ((style & Vidge.NO_VIRTUAL) != 0) {
			if (expIn == null) {
				vtable = new NTable<T>(parent, objClass, objectList, style);
			} else {
				vtable = new NTable<T>(parent, expIn, objectList, style);
			}
		} else {
			if (expIn == null) {
				vtable = new VTable<T>(parent, objClass, objectList, style);
			} else {
				vtable = new VTable<T>(parent, expIn, objectList, style);
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
				item.setToolTipText(Messages.TablePanel_AddItem);
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
				item.setToolTipText(Messages.TablePanel_RemoveItem);
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
				item.setToolTipText(Messages.TablePanel_EditItem);
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
				item.setToolTipText(Messages.TablePanel_Refresh);
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
		IObjectDialog<T> dialog = new SingleObjectDialog<T>(entityExplorer, Messages.TablePanel_AddItem, null);
		if (dialog.open() == Window.OK) {
			Object object = entityExplorer.doInputChanged(dialog.getSelection(), ValueAction.SAVE, null);
			entityExplorer.setInput(object);
			fireTableChanged();
			fireFormChanged(dialog.getSelection(), ValueAction.SAVE, null);
			refresh();
		} else {
			Object object = entityExplorer.doInputChanged(dialog.getSelection(), ValueAction.CANCEL, null);
			entityExplorer.setInput(object);
			fireFormChanged(dialog.getSelection(), ValueAction.CANCEL, null);
		}
	}

	@SuppressWarnings("unchecked")
	public void editItem() {
		T selection = vtable.getSelection();
		if (selection == null) {
			selectionWarning();
		} else {
			IEntityExplorer entityExplorer = FormRegistry.getEntityExplorer(FormContext.EDIT.name(), objClass);
			entityExplorer.explore(selection);
			IObjectDialog<T> dialog = new SingleObjectDialog<T>(entityExplorer, Messages.TablePanel_EditItem, null);
			if (dialog.open() == Window.OK) {
				selection = (T) entityExplorer.doInputChanged(dialog.getSelection(), ValueAction.UPDATE, null);
				fireTableChanged();
				fireFormChanged(dialog.getSelection(), ValueAction.UPDATE, null);
				refresh();
			} else {
				selection = (T) entityExplorer.doInputChanged(dialog.getSelection(), ValueAction.CANCEL, null);
				fireFormChanged(dialog.getSelection(), ValueAction.CANCEL, null);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void deleteItem() {
		T selection = vtable.getSelection();
		if (selection == null) {
			selectionWarning();
		} else {
			if (MessageDialog.openConfirm(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Remove Item", Messages.TablePanel_RemoveQuestion)) { //$NON-NLS-1$
				IEntityExplorer entityExplorer = FormRegistry.getEntityExplorer(FormContext.EDIT.name(), objClass);
				selection = (T) entityExplorer.doInputChanged(selection, ValueAction.DELETE, null);
				fireTableChanged();
				fireFormChanged(selection, ValueAction.DELETE, null);
				refresh();
			}
		}
	}

	private void selectionWarning() {
		MessageDialog.openInformation(
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
			Messages.TablePanel_NoSelection,
			Messages.TablePanel_SelectItem);
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
		if (section != null) {
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

	public void addContextMenu(Action action, int index) {
		vtable.addContextMenu(action, index);
	}

	public void addContextMenu(Image image, String text, int index, Runnable runnable) {
		vtable.addContextMenu(image, text, index, runnable);
	}

	public VTable<T> getVtable() {
		return vtable;
	}

	public List<T> getSelectionList() {
		return vtable.getSelectionList();
	}

	public List<T> getCheckedList() {
		return vtable.getCheckedList();
	}
}
