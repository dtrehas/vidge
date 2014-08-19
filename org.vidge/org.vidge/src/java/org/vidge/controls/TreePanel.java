package org.vidge.controls;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.langcom.locale.LocalizedString;
import org.vidge.FormRegistry;
import org.vidge.SharedImages;
import org.vidge.Vidge;
import org.vidge.VidgeException;
import org.vidge.VidgeResources;
import org.vidge.controls.editor.ListEditor;
import org.vidge.controls.editor.ListEditorInput;
import org.vidge.dialog.ObjectListDialog;
import org.vidge.dialog.SingleObjectDialog;
import org.vidge.explorer.def.StringExplorer;
import org.vidge.inface.IEntityExplorer;
import org.vidge.inface.IObjectDialog;
import org.vidge.inface.IPropertyExplorer;
import org.vidge.util.FormContext;
import org.vidge.util.StringUtil;
import org.vidge.util.TypeUtil;
import org.vidge.util.ValueAction;

@SuppressWarnings("rawtypes")
public class TreePanel {

	private static final String TREE_VIEW_OF = Messages.TreePanel_0;
	private static final String SELECTED = Messages.TreePanel_1;
	public static final int LIST_TO_EDITOR = Vidge.ALLACTIONS << 7 << 2;
	private Tree tree;
	private Section section;
	private IEntityExplorer expIn;
	private String title;
	private NodeBundle selected;
	private NodeBundle root;
	private int style = 0;

	public TreePanel(Object rootObject) {
		setRoot(rootObject);
	}

	public TreePanel(Object rootObject, int style) {
		this.style = style;
		setRoot(rootObject);
	}

	public void setRoot(Object rootObject) {
		expIn = getExplorer(FormContext.TREE, rootObject);
		expIn.explore(rootObject);
		createRoot(expIn);
	}

	private IEntityExplorer getExplorer(FormContext context, NodeBundle bundle) {
		IEntityExplorer explorer = getExplorer(context, bundle.value);
		if (explorer == null) {
			return expIn;
		}
		return explorer;
	}

	private IEntityExplorer getExplorer(FormContext context, Object object) {
		if (object == null) {
			return null;
		}
		IEntityExplorer entityExplorer = FormRegistry.getEntityExplorer(context.name(), object);
		if (entityExplorer == null || object.getClass().equals(Class.class)) {
			return null;
		}
		entityExplorer.explore(object);
		return entityExplorer;
	}

	private IEntityExplorer getExplorer(FormContext context, TreeItem item) {
		IEntityExplorer entityExplorer = null;
		NodeBundle nodeBundle = (NodeBundle) item.getData();
		if (nodeBundle.value == null) {
			entityExplorer = FormRegistry.getEntityExplorer(context.name(), nodeBundle.clazz);
		} else {
			entityExplorer = FormRegistry.getEntityExplorer(context.name(), nodeBundle.value.getClass());
			if (entityExplorer != null) {
				entityExplorer.explore(nodeBundle.value);
			}
		}
		if (entityExplorer != null && StringExplorer.class.equals(entityExplorer.getClass())) {
			return null;
		}
		return entityExplorer;
	}

	public TreePanel(Composite parent, Object rootObject) {
		this(rootObject);
		createViewer(parent);
	}

	public TreePanel(Composite parent, Object rootObject, String title) {
		this(rootObject);
		this.title = title;
		createViewer(parent);
	}

	public TreePanel(IEntityExplorer expIn) {
		this.expIn = expIn;
		createRoot(expIn);
	}

	private void createRoot(IEntityExplorer expIn) {
		NodeBundle bundle = new NodeBundle(expIn.getInput(), null);
		selected = bundle;
		root = bundle;
		title = TREE_VIEW_OF + expIn.getInputClass().getSimpleName();
	}

	public Composite createViewer(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		section = toolkit.createSection(parent, Section.TITLE_BAR | Section.DESCRIPTION);
		section.setTextClient(getClient(section));
		// section.setText(title);
		toolkit.createCompositeSeparator(section);
		tree = new Tree(section, SWT.VIRTUAL | SWT.SINGLE);
		toolkit.adapt(tree);
		section.setClient(tree);
		tree.setItemCount(1);
		addMouseAndSelectionListener();
		addDataListener();
		// addDND(parent.getDisplay());
		return section;
	}

	private void addDataListener() {
		tree.addListener(SWT.SetData, new Listener() {

			public void handleEvent(Event event) {
				TreeItem item = (TreeItem) event.item;
				TreeItem parentItem = item.getParentItem();
				if (parentItem == null) {
					IEntityExplorer explorer = getExplorer(FormContext.TREE, root);
					item.setText(obtainStr(root.value));
					item.setItemCount(explorer.getPropertyList().size());
					item.setImage(VidgeResources.getInstance().getImage(SharedImages.OBJECTS));
					item.setData(root);
					item.setExpanded(true);
				} else {
					NodeBundle bundle = (NodeBundle) parentItem.getData();
					if (bundle.isCollection) {
						buildMember(item, parentItem);
					} else {
						buildOrdinal(item, parentItem);
					}
				}
			}

			private void buildMember(TreeItem item, TreeItem parentItem) {
				int indexOf = parentItem.indexOf(item);
				Object parentvalue = ((NodeBundle) parentItem.getData()).value;
				Class<?> propertyClass = parentvalue.getClass();
				Object value = null;
				if (Collection.class.isAssignableFrom(propertyClass)) {
					value = ((Collection) parentvalue).toArray()[indexOf];
				} else if (Map.class.isAssignableFrom(propertyClass)) {
					value = ((Map) parentvalue).values().toArray()[indexOf];
				} else if (propertyClass.isArray()) {
					value = Array.get(parentvalue, indexOf);
				}
				NodeBundle nodeBundle = new NodeBundle(value, null);
				nodeBundle.value = value;
				item.setData(nodeBundle);
				item.setImage(obtainImage(value, VidgeResources.getInstance().getImage(SharedImages.OBJECTS)));
				item.setText(obtainStr(value));
				propertyClass = ((NodeBundle) parentItem.getData()).propertyType;
				if (!TypeUtil.isPrimitive(propertyClass)) {
					IEntityExplorer explorer = FormRegistry.getEntityExplorer(FormContext.TREE.name(), propertyClass);
					item.setItemCount(explorer.getPropertyList().size());
				} else {
					item.setItemCount(0);
				}
			}

			private String obtainStr(Object value) {
				String result = StringUtil.NN;
				if (value == null || value.toString() == null || value.toString().equalsIgnoreCase(StringUtil.NULL)) {
					return result;
				}
				if (LocalizedString.class.isAssignableFrom(value.getClass())) {
					result = ((LocalizedString) value).getDefaultLocalString();
				} else if (Collection.class.isAssignableFrom(value.getClass())) {
					result = ((Collection) value).toString();
				} else if (Map.class.isAssignableFrom(value.getClass())) {
					result = ((Map) value).toString();
				} else if (value.getClass().isArray()) {
					result = Arrays.toString((Object[]) value);
				} else {
					IEntityExplorer explorer = getExplorer(FormContext.TREE, value);
					if (explorer == null) {
						result = value.toString();
					} else {
						result = explorer.getString(value);
					}
				}
				if (result == null) {
					result = StringUtil.NN;
				}
				return result;
			}

			private void buildOrdinal(TreeItem item, TreeItem parentItem) {
				IEntityExplorer parentEx = getExplorer(FormContext.TREE, (NodeBundle) parentItem.getData());
				int indexOf = parentItem.indexOf(item);
				IPropertyExplorer propertyExplorer = parentEx.getProperty(indexOf);
				Object value = propertyExplorer.getValue();
				if (value == null) {
					item.setText(propertyExplorer.getLabel());
					item.setImage(VidgeResources.getInstance().getImage(SharedImages.BSQUARE));
					item.setItemCount(0);
					Class<?> propertyClass = propertyExplorer.getPropertyClass();
					Class propertyType = TypeUtil.getGenericClass(propertyClass, propertyExplorer.getPropertyType());
					NodeBundle nodeBundle = new NodeBundle(propertyExplorer.getPropertyName(), propertyType);
					// nodeBundle.value = value;
					item.setData(nodeBundle);
					return;
				}
				Class<?> propertyClass = propertyExplorer.getPropertyClass();
				if (Collection.class.isAssignableFrom(propertyClass)) {
					item.setText(propertyExplorer.getLabel());
					item.setItemCount(((Collection) value).size());
					Class propertyType = TypeUtil.getGenericClass(propertyClass, propertyExplorer.getPropertyType());
					item.setImage(obtainImage(value, VidgeResources.getInstance().getImage(SharedImages.BARCHART)));
					NodeBundle nodeBundle = new NodeBundle(value, propertyExplorer.getPropertyName(), propertyType);
					// nodeBundle.value = value;
					item.setData(nodeBundle);
					return;
				} else if (Map.class.isAssignableFrom(propertyClass)) {
					item.setText(propertyExplorer.getLabel());
					item.setItemCount(((Map) value).size());
					Class propertyType = TypeUtil.getGenericClass(propertyClass, propertyExplorer.getPropertyType());
					item.setImage(obtainImage(value, VidgeResources.getInstance().getImage(SharedImages.BARCHART)));
					NodeBundle nodeBundle = new NodeBundle(value, propertyExplorer.getPropertyName(), propertyType);
					// nodeBundle.value = value;
					item.setData(nodeBundle);
					return;
				} else if (propertyClass.isArray()) {
					item.setText(propertyExplorer.getLabel());
					item.setItemCount(Array.getLength(value));
					Class propertyType = TypeUtil.getGenericClass(propertyClass, propertyExplorer.getPropertyType());
					item.setImage(obtainImage(value, VidgeResources.getInstance().getImage(SharedImages.CARDFILE)));
					NodeBundle nodeBundle = new NodeBundle(value, propertyExplorer.getPropertyName(), propertyType);
					// nodeBundle.value = value;
					item.setData(nodeBundle);
					return;
				} else {
					item.setItemCount(0);
					item.setImage(VidgeResources.getInstance().getImage(SharedImages.BALL));
					item.setText(propertyExplorer.getLabel() + StringUtil.DDOT + obtainStr(value));
					if (!TypeUtil.isPrimitive(propertyClass)) {
						IEntityExplorer explorer = FormRegistry.getEntityExplorer(FormContext.TREE.name(), propertyClass);
						if (explorer != null && !StringExplorer.class.equals(explorer.getClass())) {
							item.setImage(obtainImage(value, VidgeResources.getInstance().getImage(SharedImages.OBJECTS)));
							item.setData(new NodeBundle(value, propertyExplorer.getPropertyName()));
							item.setItemCount(explorer.getPropertyList().size());
						}
					}
				}
			}
		});
	}

	private Image obtainImage(Object value, Image defaultImg) {
		Image result = defaultImg;
		IEntityExplorer explorer = FormRegistry.getEntityExplorer(FormContext.TREE.name(), value);
		if (explorer != null) {
			result = explorer.getImage(value);
		}
		if (result == null) {
			return defaultImg;
		}
		return result;
	}

	private void addMouseAndSelectionListener() {
		tree.addMouseListener(new MouseAdapter() {

			public void mouseDown(MouseEvent e) {
				if (e.button == 3) {
					try {
						createResourceMenu(tree);
					} catch (RuntimeException e1) {
						e1.printStackTrace();
					}
				}
			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				TreeItem[] selection = tree.getSelection();
				NodeBundle nodeBundle = null;
				if (selection.length == 0) {
					return;
				} else {
					if (selection[0].getData() == null) {
						return;
					}
				}
				nodeBundle = (NodeBundle) selection[0].getData();
				if (nodeBundle.equals(root)) {
					// return;
				}
				IEntityExplorer explorer = getExplorer(FormContext.EDIT, selection[0]);
				if (nodeBundle.isCollection) {
					editCollection();
				} else if (explorer != null) {
					explorer.explore(nodeBundle.value);
					editItem(explorer, nodeBundle);
				} else {
					MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.TreePanel_2, Messages.TreePanel_3);
				}
			}
		});
		tree.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem item = (TreeItem) e.item;
				selected = (NodeBundle) item.getData();
				section.setDescription(SELECTED + item.getText());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	protected Control getClient(Composite parent) {
		ToolBar bar = new ToolBar(parent, SWT.FLAT);
		ToolItem item = new ToolItem(bar, SWT.PUSH);
		item.setToolTipText(Messages.TablePanel_Refresh);
		item.setImage(VidgeResources.getInstance().getImage(SharedImages.ACTION_REFRESH));
		item.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				refresh();
			}
		});
		return bar;
	}

	public void refresh() {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

			public void run() {
				try {
					if (!section.isDisposed()) {
						section.setDescription(SELECTED);
						tree.clearAll(true);
						tree.setItemCount(1);
					}
				} catch (Throwable e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	private void createResourceMenu(final Control parent) {
		TreeItem[] selection = tree.getSelection();
		NodeBundle nodeBundle = null;
		parent.setMenu(null);
		if (selection.length == 0) {
			return;
		} else {
			if (selection[0].getData() == null) {
				return;
			}
		}
		nodeBundle = (NodeBundle) selection[0].getData();
		Menu menu = new Menu(parent.getShell(), SWT.POP_UP | SWT.BAR);
		MenuItem item = null;
		parent.setMenu(menu);
		IEntityExplorer explorer = getExplorer(FormContext.TREE, selection[0]);
		if (nodeBundle.isCollection) {
			item = new MenuItem(menu, SWT.PUSH);
			item.setText(Messages.TreePanel_5);
			item.setImage(VidgeResources.getInstance().getImage(SharedImages.DBEDT));
			item.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					editCollection();
				}
			});
		} else if (explorer != null) {
			if (nodeBundle.propertyName == null) {
				item = new MenuItem(menu, SWT.PUSH);
				item.setText(Messages.TablePanel_EditItem);
				item.setImage(VidgeResources.getInstance().getImage(SharedImages.ACTION_EDIT));
				item.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						editItem();
					}
				});
			} else {
				if (nodeBundle.value == null) {
					item = new MenuItem(menu, SWT.PUSH);
					item.setText(Messages.TablePanel_AddItem);
					item.setImage(VidgeResources.getInstance().getImage(SharedImages.ACTION_PLUS));
					item.addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(SelectionEvent e) {
							addItem();
						}
					});
				} else {
					item = new MenuItem(menu, SWT.PUSH);
					item.setText(Messages.TablePanel_EditItem);
					item.setImage(VidgeResources.getInstance().getImage(SharedImages.ACTION_EDIT));
					item.addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(SelectionEvent e) {
							editItem();
						}
					});
					item = new MenuItem(menu, SWT.PUSH);
					item.setText(Messages.TablePanel_RemoveItem);
					item.setImage(VidgeResources.getInstance().getImage(SharedImages.ACTION_MINUS));
					item.addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(SelectionEvent e) {
							deleteItem();
						}
					});
				}
			}
		}
		menu.setVisible(true);
	}

	protected void clearCollection() {
		TreeItem[] selection = tree.getSelection();
		if (selection.length == 0) {
			selectWarning();
		} else {
			if (MessageDialog.openConfirm(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Remove Items", Messages.TreePanel_12)) { //$NON-NLS-1$
				NodeBundle parentBundle = (NodeBundle) selection[0].getParentItem().getData();
				NodeBundle nodeBundle = (NodeBundle) selection[0].getData();
				IEntityExplorer collectionExplorer = getExplorer(FormContext.EDIT, parentBundle);
				IPropertyExplorer propertyExplorer = collectionExplorer.getProperty(nodeBundle.propertyName);
				// List data = collectionExplorer.getData();
				// for(Object object: data) {
				// collectionExplorer.doPart(object, ValueAction.DELETE, nodeBundle.propertyName);
				// }
				propertyExplorer.setValue(null);
				refresh();
			}
		}
	}

	private void selectWarning() {
		MessageDialog.openInformation(
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
			Messages.TablePanel_NoSelection,
			Messages.TablePanel_SelectItem);
	}

	private void editItem() {
		TreeItem[] selection = tree.getSelection();
		if (selection.length == 0) {
			selectWarning();
		} else {
			IObjectDialog dialog = null;
			NodeBundle nodeBundle = (NodeBundle) selection[0].getData();
			if (nodeBundle != null) {
				Object value = nodeBundle.value;
				if (value == null) {
					addItem();
					return;
				}
				IEntityExplorer explorer = getExplorer(FormContext.EDIT, value);
				IPropertyExplorer propertyExplorer = explorer.getProperty(nodeBundle.propertyName);
				dialog = new SingleObjectDialog(explorer, Messages.TablePanel_EditItem);
				if (dialog.open() == Window.OK) {
					Object selection2 = dialog.getSelection();
					if (propertyExplorer != null) {
						propertyExplorer.setValue(selection2);
					} else {
						selection2 = explorer.doInputChanged(selection2, ValueAction.UPDATE, null);
					}
					refresh();
				} else {
					Object selection2 = dialog.getSelection();
					if (selection2 != null) {
						selection2 = explorer.doInputChanged(selection2, ValueAction.CANCEL, null);
					}
				}
			}
		}
	}

	private void editItem(IEntityExplorer explorer, NodeBundle nodeBundle) {
		IObjectDialog dialog = null;
		if (nodeBundle != null) {
			Object value = nodeBundle.value;
			if (value == null) {
				addItem();
				return;
			}
			dialog = new SingleObjectDialog(explorer, Messages.TablePanel_EditItem, null);
			if (dialog.open() == Window.OK) {
				IPropertyExplorer propertyExplorer = explorer.getProperty(nodeBundle.propertyName);
				Object selection2 = dialog.getSelection();
				if (propertyExplorer != null) {
					propertyExplorer.setValue(selection2);
				} else {
					selection2 = explorer.doInputChanged(selection2, ValueAction.UPDATE, null);
				}
				refresh();
			} else {
				Object selection2 = dialog.getSelection();
				if (selection2 != null) {
					selection2 = explorer.doInputChanged(selection2, ValueAction.CANCEL, null);
				}
			}
		}
	}

	private void editCollection() {
		TreeItem[] selection = tree.getSelection();
		if (selection.length == 0) {
			selectWarning();
		} else {
			IObjectDialog dialog = null;
			NodeBundle nodeBundle = (NodeBundle) selection[0].getData();
			if (nodeBundle != null) {
				TreeItem parentItem = selection[0].getParentItem();
				NodeBundle parentBundle = (NodeBundle) parentItem.getData();
				Class propertyClass = nodeBundle.propertyType;
				IEntityExplorer explorer = FormRegistry.getEntityExplorer(FormContext.EDIT.name(), propertyClass);
				IEntityExplorer collectionExplorer = getExplorer(FormContext.EDIT, parentBundle);
				IPropertyExplorer propertyExplorer = collectionExplorer.getProperty(nodeBundle.propertyName);
				if (propertyExplorer == null) {
					MessageDialog.openError(getSection().getShell(), Messages.TablePanel_EditItem, Messages.TreePanel_20 + nodeBundle.propertyName);
					return;
				}
				if ((style & LIST_TO_EDITOR) != 0) {
					try {
						PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow()
							.getActivePage()
							.openEditor(new ListEditorInput<IPropertyExplorer>(propertyExplorer.getLabel(), explorer, propertyExplorer, this), ListEditor.ID);
					} catch (PartInitException e) {
						throw new VidgeException(e);
					}
				} else {
					dialog = new ObjectListDialog(propertyExplorer.getLabel(), explorer, propertyExplorer);
					if (dialog.open() == Window.OK) {
						Object value = dialog.getSelection();
						if (propertyClass.isArray()) {
							value = ((List) dialog.getSelection()).toArray();
						}
						propertyExplorer.setValue(value);
						refresh();
					}
				}
			}
		}
	}

	private void deleteItem() {
		TreeItem[] selection = tree.getSelection();
		if (selection.length == 0) {
			selectWarning();
		} else {
			TreeItem item = selection[0];
			NodeBundle nodeBundle = (NodeBundle) item.getData();
			if (item.getParentItem() == null) {
				MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), Messages.TreePanel_23, Messages.TreePanel_24);
			} else {
				if (MessageDialog.openConfirm(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					Messages.TablePanel_RemoveItem,
					Messages.TablePanel_RemoveQuestion)) { //$NON-NLS-1$
					TreeItem parentItem = item.getParentItem();
					IEntityExplorer parentEx = getExplorer(FormContext.EDIT, (NodeBundle) parentItem.getData());
					IPropertyExplorer propertyExplorer = parentEx.getProperty(nodeBundle.propertyName);
					if (nodeBundle != null && propertyExplorer != null) {
						propertyExplorer.setValue(null);
						refresh();
					}
				}
			}
		}
	}

	private void addItem() {
		TreeItem[] selection = tree.getSelection();
		if (selection.length == 0) {
			selectWarning();
		} else {
			IObjectDialog dialog = null;
			TreeItem item = selection[0];
			NodeBundle nodeBundle = (NodeBundle) item.getData();
			if (nodeBundle != null) {
				TreeItem parentItem = item.getParentItem();
				IEntityExplorer parentEx = getExplorer(FormContext.EDIT, (NodeBundle) parentItem.getData());
				IPropertyExplorer propertyExplorer = parentEx.getProperty(nodeBundle.propertyName);
				IEntityExplorer explorer = FormRegistry.getEntityExplorer(FormContext.CREATE.name(), propertyExplorer.getPropertyClass());
				dialog = new SingleObjectDialog(explorer, Messages.TablePanel_AddItem, null);
				if (dialog.open() == Window.OK) {
					propertyExplorer.setValue(dialog.getSelection());
				}
				refresh();
			}
		}
	}

	public Section getSection() {
		return section;
	}

	@SuppressWarnings("unchecked")
	public <T> T getSelected() {
		if (selected != null) {
			return (T) selected.value;
		}
		return null;
	}
	private static class NodeBundle {

		Object value;
		String propertyName;
		Class propertyType;
		boolean isCollection = false;
		Class clazz;

		public NodeBundle(Object value, String propertyName) {
			this.value = value;
			this.propertyName = propertyName;
		}

		public NodeBundle(Object value2, String propertyName2, Class propertyType) {
			this(value2, propertyName2);
			this.propertyType = propertyType;
			isCollection = true;
		}

		public NodeBundle(String propertyName2, Class clazz) {
			this.clazz = clazz;
			this.propertyName = propertyName2;
		}
	}

	public void addSelectionListener(SelectionListener listener) {
		tree.addSelectionListener(listener);
	}

	public Tree getTree() {
		return tree;
	}

	public Control getControl() {
		return section;
	}

	public void setEnabled(boolean enabled) {
		tree.setEnabled(enabled);
	}

	private void addDND(final Display display) {
		Transfer[] types = new Transfer[] {
			TextTransfer.getInstance()
		};
		int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;
		final DragSource source = new DragSource(tree, operations);
		source.setTransfer(types);
		final TreeItem[] dragSourceItem = new TreeItem[1];
		source.addDragListener(new DragSourceListener() {

			public void dragStart(DragSourceEvent event) {
				TreeItem[] selection = tree.getSelection();
				if (selection.length > 0 && selection[0].getItemCount() == 0) {
					event.doit = true;
					dragSourceItem[0] = selection[0];
				} else {
					event.doit = false;
				}
			};

			public void dragSetData(DragSourceEvent event) {
				event.data = dragSourceItem[0].getText();
			}

			public void dragFinished(DragSourceEvent event) {
				if (event.detail == DND.DROP_MOVE)
					dragSourceItem[0].dispose();
				dragSourceItem[0] = null;
			}
		});
		DropTarget target = new DropTarget(tree, operations);
		target.setTransfer(types);
		target.addDropListener(new DropTargetAdapter() {

			public void dragOver(DropTargetEvent event) {
				event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
				if (event.item != null) {
					TreeItem item = (TreeItem) event.item;
					NodeBundle bundle = (NodeBundle) item.getData();
					IEntityExplorer entityExplorer = getExplorer(FormContext.EDIT, bundle);
					IPropertyExplorer propertyExplorer = entityExplorer.getProperty(bundle.propertyName);
					Point pt = display.map(null, tree, event.x, event.y);
					Rectangle bounds = item.getBounds();
					if (pt.y < bounds.y + bounds.height / 3) {
						event.feedback |= DND.FEEDBACK_INSERT_BEFORE;
					} else if (pt.y > bounds.y + 2 * bounds.height / 3) {
						event.feedback |= DND.FEEDBACK_INSERT_AFTER;
					} else {
						event.feedback |= DND.FEEDBACK_SELECT;
					}
				}
			}

			public void drop(DropTargetEvent event) {
				if (event.data == null) {
					event.detail = DND.DROP_NONE;
					return;
				}
				String text = (String) event.data;
				if (event.item == null) {
					TreeItem item = new TreeItem(tree, SWT.NONE);
					item.setText(text);
				} else {
					TreeItem item = (TreeItem) event.item;
					Point pt = display.map(null, tree, event.x, event.y);
					Rectangle bounds = item.getBounds();
					TreeItem parent = item.getParentItem();
					if (parent != null) {
						TreeItem[] items = parent.getItems();
						int index = 0;
						for (int i = 0; i < items.length; i++) {
							if (items[i] == item) {
								index = i;
								break;
							}
						}
						if (pt.y < bounds.y + bounds.height / 3) {
							TreeItem newItem = new TreeItem(parent, SWT.NONE, index);
							newItem.setText(text);
						} else if (pt.y > bounds.y + 2 * bounds.height / 3) {
							TreeItem newItem = new TreeItem(parent, SWT.NONE, index + 1);
							newItem.setText(text);
						} else {
							TreeItem newItem = new TreeItem(item, SWT.NONE);
							newItem.setText(text);
						}
					} else {
						TreeItem[] items = tree.getItems();
						int index = 0;
						for (int i = 0; i < items.length; i++) {
							if (items[i] == item) {
								index = i;
								break;
							}
						}
						if (pt.y < bounds.y + bounds.height / 3) {
							TreeItem newItem = new TreeItem(tree, SWT.NONE, index);
							newItem.setText(text);
						} else if (pt.y > bounds.y + 2 * bounds.height / 3) {
							TreeItem newItem = new TreeItem(tree, SWT.NONE, index + 1);
							newItem.setText(text);
						} else {
							TreeItem newItem = new TreeItem(item, SWT.NONE);
							newItem.setText(text);
						}
					}
				}
			}
		});
	}
}
