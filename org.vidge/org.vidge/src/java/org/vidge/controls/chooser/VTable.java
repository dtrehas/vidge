package org.vidge.controls.chooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.vidge.FormRegistry;
import org.vidge.SharedImages;
import org.vidge.Vidge;
import org.vidge.VidgeException;
import org.vidge.VidgeResources;
import org.vidge.form.IFormComparator;
import org.vidge.inface.IEntityExplorer;
import org.vidge.inface.IPropertyExplorer;
import org.vidge.util.FormContext;
import org.vidge.util.StringUtil;

public class VTable<T> extends Composite {

	private static final int _50 = 90;
	private static final int _40 = 40;
	private static final int _30 = 30;
	public static final int NO_NUM_COLUMN = Vidge.ALLACTIONS << 8;
	public static final int PAGES = Vidge.ALLACTIONS << 9;
	public static final int NO_LINES = Vidge.ALLACTIONS << 10;
	public static final int NO_HEADER = Vidge.ALLACTIONS << 11;
	protected Table table;
	protected List<T> objectList = new ArrayList<T>();
	protected List<T> visibleList = new ArrayList<T>();
	protected IEntityExplorer tableExplorer;
	protected List<VColumn<T>> columns = new ArrayList<VColumn<T>>();
	protected boolean isNumColumn;
	protected PageManager pageManager;
	protected int from = 0, count = 0, addNum = 1;
	protected String[] columnStrings = new String[0];
	protected IFormComparator<T> comparator = new DefaultTableComparator<T>();
	private Menu popupMenu;
	private TableEditor editor;

	public VTable(Composite parent, Class<T> objClass, List<T> listIn, int style, DefaultTableComparator<T> comparator) {
		this(parent, objClass, listIn, style);
		this.comparator = comparator;
	}

	public VTable(Composite parent, Class<T> objClass, List<T> listIn, int style) {
		this(parent, FormRegistry.getEntityExplorerTh(FormContext.TABLE.name(), objClass), listIn, style);
	}

	public VTable(Composite parent, Class<T> objClass, List<T> listIn) {
		super(parent, SWT.NONE);
		initEnv(FormRegistry.getEntityExplorerTh(FormContext.TABLE.name(), objClass));
		createTable();
		createContextMenu();
		setInput(listIn);
	}

	public VTable(Composite parent, IEntityExplorer tableExplorerIn, List<T> listIn, int style) {
		super(parent, style);
		initEnv(tableExplorerIn);
		createTable();
		if ((getStyle() & PAGES) != 0) {
			addNum = 0;
			createPaginator(style);
		}
		createContextMenu();
		setInput(listIn);
	}

	@SuppressWarnings("unchecked")
	private void initEnv(IEntityExplorer tableExplorerIn) {
		isNumColumn = (getStyle() & NO_NUM_COLUMN) == 0;
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginBottom = 0;
		setLayout(layout);
		this.tableExplorer = tableExplorerIn;
		if (tableExplorerIn == null) {
			throw new RuntimeException("***Not found IEntityExplorer ! ");
		}
		if (tableExplorer.getComparator() != null) {
			comparator = tableExplorer.getComparator();
		}
	}

	protected void createTable() {
		int styleInt = getStyle() | SWT.VIRTUAL | SWT.FULL_SELECTION | SWT.V_SCROLL;
		table = new Table(this, styleInt);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.setLinesVisible((getStyle() & NO_LINES) == 0);
		table.setHeaderVisible((getStyle() & NO_HEADER) == 0);
		createColumns();
		editor = new TableEditor(table);
		table.addListener(SWT.SetData, new Listener() {

			@Override
			public void handleEvent(Event event) {
				TableItem item = (TableItem) event.item;
				int index = table.indexOf(item);
				T obj = visibleList.get(index);
				if (obj == null) {
					event.doit = false;
				} else {
					fillItem(item, index, obj);
				}
			}
		});
		addEditorCapability();
	}

	private void addEditorCapability() {
		for (IPropertyExplorer explorer : tableExplorer.getPropertyList()) {
			if (explorer.isNeedTableEditor()) {
				editor = new TableEditor(table);
				editor.horizontalAlignment = SWT.LEFT;
				editor.grabHorizontal = true;
				editor.minimumWidth = explorer.getVisualAreaWidth() < 0 ? 50 : explorer.getVisualAreaWidth();
				table.addSelectionListener(new SelectionAdapter() {

					public void widgetSelected(SelectionEvent e) {
						Control oldEditor = editor.getEditor();
						if (oldEditor != null) {
							oldEditor.dispose();
						}
						final TableItem item = (TableItem) e.item;
						if (item == null || item.isDisposed()) {
							return;
						}
						Point pt = getDisplay().getCursorLocation();
						pt = getDisplay().getFocusControl().toControl(pt);
						for (int i = (isNumColumn ? 1 : 0); i < table.getColumnCount(); i++) {
							Rectangle rect = item.getBounds(i);
							if (rect.contains(pt)) {
								final IPropertyExplorer explorer = tableExplorer.getPropertyList().get(i - (isNumColumn ? 1 : 0));
								if (explorer.isNeedTableEditor()) {
									final Control newEditor = explorer.getEditor(table);
									if (newEditor != null) {
										newEditor.setFocus();
										editor.setEditor(newEditor, item, i);
										newEditor.addListener(SWT.MouseWheel, new Listener() {

											@Override
											public void handleEvent(Event event) {
												newEditor.dispose();
											}
										});
										newEditor.addListener(SWT.Dispose, new Listener() {

											@Override
											public void handleEvent(Event event) {
												refresh();
											}
										});
									}
									break;
								}
							}
						}
					}
				});
				break;
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void fillItem(TableItem item, int index, T obj) {
		for (int a = 0; a < columns.size(); a++) {
			if ((a == 0) && isNumColumn) {
				columnStrings[a] = (index + from + addNum) + StringUtil.NN;
			} else {
				VColumn<T> tColumn = columns.get(a);
				columnStrings[a] = tColumn.getCellText(obj);
			}
		}
		item.setText(columnStrings);
		item.setData(obj);
		Color background2 = tableExplorer.getBackground(obj);
		if (background2 != null) {
			item.setBackground(background2);
		}
	}

	protected void createColumns() {
		List<ColumnLayoutData> dataList = new ArrayList<ColumnLayoutData>();
		if (isNumColumn) {
			columns.add(new VColumn<T>(this, null));
			dataList.add(new ColumnPixelData(_40, true));
		}
		for (IPropertyExplorer ex : tableExplorer.getPropertyList()) {
			columns.add(new VColumn<T>(this, ex));
			if (ex.getVisualAreaWidth() < 0) {
				dataList.add(new ColumnWeightData(_30, _50, true));
			} else {
				dataList.add(new ColumnPixelData(ex.getVisualAreaWidth(), true));
			}
		}
		TableLayout layout = new TableLayout();
		for (int a = 0; a < columns.size(); a++) {
			VColumn<T> tColumn = columns.get(a);
			layout.addColumnData(dataList.get(a));
			TableColumn tc = new TableColumn(table, tColumn.isNumeric() ? (SWT.RIGHT) : (SWT.LEFT), a);
			if (tColumn.explorer == null) {
				tc.setText(StringUtil.Num);
				continue;
			}
			tc.setData(tColumn.getExplorer());
			tc.setText(tColumn.explorer.getLabel());
			tc.setToolTipText(tColumn.explorer.getLabel());
			if ((a > 0) || isNumColumn) {
				tc.setImage(VidgeResources.getInstance().getImage(SharedImages.NONE));
			}
			// tc.setMoveable(true);
			tc.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					TableColumn selectedColumn = (TableColumn) e.getSource();
					int columnIndex = table.indexOf(selectedColumn);
					if ((columnIndex == 0) && isNumColumn) {
						e.doit = false;
						return;
					}
					selectColumn(columnIndex);
				}
			});
		}
		table.setLayout(layout);
		columnStrings = new String[columns.size()];
		provideImages();
	}

	public void selectColumn(int columnIndex) {
		toggleColumn(columnIndex);
		provideImages();
		visibleRefresh();
	}

	private void provideImages() {
		for (int a = 0; a < table.getColumnCount(); a++) {
			VColumn<T> column = columns.get(a);
			if (column.explorer != null) {
				if (column.currentImage != SharedImages.TABLE_HEADER_DEFAULT) {
					table.getColumn(a).setImage(VidgeResources.getInstance().getImage(column.currentImage));
				} else {
					table.getColumn(a).setImage(null);
				}
			}
		}
	}

	protected void visibleRefresh() {
		table.removeAll();
		table.setItemCount(visibleList.size());
	}

	void toggleColumn(int columnIndex) {
		for (int a = 0; a < columns.size(); a++) {
			VColumn<T> column = columns.get(a);
			if (column.explorer == null) {
				continue;
			}
			if (columnIndex == a) {
				column.toggle();
			} else {
				column.setDefault();
			}
		}
	}

	public List<T> getInput() {
		return objectList;
	}

	public void defaultSort() {
		visibleList.clear();
		visibleList.addAll(filter());
	}

	public void setInput(List<T> listIn) {
		if (listIn != null) {
			objectList = listIn;
		}
		if (pageManager != null && listIn != null) {
			pageManager.setItemsCount(listIn.size());
		}
		refresh();
	}

	public void setPageInput(List<T> listIn) {
		if (listIn == null || listIn.size() == 0) {
		} else {
			objectList = listIn;
		}
		refresh();
	}

	private void createPaginator(int style) {
		pageManager = new PageManager(this, style);
		pageManager.getControl().setLayoutData(new GridData(SWT.END, SWT.END, true, false));
		pageManager.addPageListener(new IPageListener() {

			@Override
			public void pageChanged(int from1, int count1) {
				refresh();
			}
		});
	}

	public void refresh() {
		table.removeAll();
		visibleList.clear();
		visibleList.addAll(filter());
		table.setItemCount(visibleList.size());
	}

	protected List<T> filter() {
		if (isFiltered()) {
			final List<T> filteredList = new ArrayList<T>(objectList);
			try {
				PlatformUI.getWorkbench().getProgressService().busyCursorWhile(new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						for (VColumn<T> column : columns) {
							if (column.isFiltered()) {
								Iterator<T> iterator = filteredList.iterator();
								while (iterator.hasNext()) {
									String value = column.getCellText(iterator.next());
									if (column.getFilterMask().length() > 0) {
										if (value != null) {
											if (!StringUtil.match(value.toString(), column.getFilterMask())) {
												iterator.remove();
											}
										} else {
											iterator.remove();
										}
									} else {
										if (value != null) {
											iterator.remove();
										}
									}
								}
							}
						}
					}
				});
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return pageList(filteredList);
		} else {
			return pageList(objectList);
		}
	}

	private List<T> pageList(List<T> list) {
		if (pageManager != null) {
			count = pageManager.getCount();
			from = pageManager.getFrom();
			int to = from + count;
			if (list.size() > from) {
				if (to > list.size()) {
					to = list.size();
				}
				return list.subList(from - 1, to);
			} else {
				return list;
			}
		} else {
			return list;
		}
	}

	public void addDoubleClickListener(final IDoubleClickListener listener) {
		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				listener.doubleClick(null);
			}
		});
	}

	@SuppressWarnings("unchecked")
	public T getSelection() {
		T selection = null;
		if (table.getSelectionCount() > 0) {
			selection = (T) table.getSelection()[0].getData();
		}
		return selection;
	}

	public List<T> getSelectionList() {
		List<T> result = new ArrayList<T>();
		for (TableItem item : table.getSelection()) {
			T obj = visibleList.get(table.indexOf(item));
			if (obj != null) {
				result.add(obj);
			}
		}
		return result;
	}

	public List<T> getCheckedList() {
		List<T> result = new ArrayList<T>();
		for (TableItem item : table.getItems()) {
			if (item.getChecked()) {
				T obj = visibleList.get(table.indexOf(item));
				if (obj != null) {
					result.add(obj);
				}
			}
		}
		return result;
	}

	public Table getTable() {
		return table;
	}

	public void addSelectionListener(SelectionListener listener) {
		table.addSelectionListener(listener);
	}

	public void removeSelectionListener(SelectionListener listener) {
		table.removeSelectionListener(listener);
	}

	void sortList(final boolean asc, final IPropertyExplorer explorer) {
		comparator.init(explorer, asc);
		Collections.sort(visibleList, comparator);
	}

	public void setComparator(IFormComparator<T> comparator) {
		this.comparator = comparator;
	}

	public List<VColumn<T>> getColumns() {
		return columns;
	}

	public void removeFiltering() {
		for (VColumn<T> column : columns) {
			column.setFiltered(false);
		}
		refresh();
	}

	public boolean isFiltered() {
		for (VColumn<T> column : columns) {
			if (column.isFiltered()) {
				return true;
			}
		}
		return false;
	}

	private void createContextMenu() {
		final Menu popupMenu = createMenuInt();
		table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				if (e.button == 3) {
					popupMenu.getItem(0).setSelection(isFiltered());
					popupMenu.setVisible(true);
				}
			}
		});
		table.addTraverseListener(new TraverseListener() {

			@Override
			public void keyTraversed(TraverseEvent e) {
				Table table = (Table) e.widget;
				int selectionIndex = table.getSelectionIndex();
				if (selectionIndex == table.getItemCount() - 1) {
					table.setSelection(0);
					table.showSelection();
				} else if (selectionIndex == 0) {
					table.setSelection(table.getItemCount() - 1);
					table.showSelection();
				}
			}
		});
	}

	protected Menu createMenuInt() {
		popupMenu = new Menu(table.getShell(), SWT.POP_UP);
		new MenuItem(popupMenu, SWT.SEPARATOR);
		MenuItem menuItem = new MenuItem(popupMenu, SWT.CHECK);
		menuItem.setText(Messages.ObjectChooser_FILTERED);
		menuItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				((MenuItem) e.getSource()).setSelection(false);
				removeFiltering();
			}
		});
		menuItem = new MenuItem(popupMenu, SWT.PUSH);
		menuItem.setText(Messages.ObjectChooser_SHOW_FILTER_DIALOG);
		menuItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				showFilterDialog();
			}
		});
		menuItem = new MenuItem(popupMenu, SWT.CHECK);
		menuItem.setText("Export to 'Tab' delimited CSV");
		menuItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					saveToCSV();
				} catch (IOException e1) {
					throw new RuntimeException(e1);
				}
			}
		});
		return popupMenu;
	}

	public void saveToCSV() throws IOException {
		FileDialog dialog = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.SAVE);
		String fileName = dialog.open();
		if (fileName != null) {
			StringBuilder builder = new StringBuilder();
			for (VColumn<T> column : columns) {
				if (column.explorer != null) {
					builder.append(column.explorer.getLabel());
					builder.append(StringUtil.TAB);
				} else {
					builder.append(StringUtil.Num);
					builder.append(StringUtil.TAB);
				}
			}
			builder.append(StringUtil.NL);
			for (TableItem item : table.getItems()) {
				for (int a = 0; a < columns.size(); a++) {
					builder.append(item.getText(a));
					builder.append(StringUtil.TAB);
				}
				builder.append(StringUtil.NL);
			}
			FileWriter writer = new FileWriter(new File(fileName.endsWith(StringUtil.CSV) ? fileName : (fileName + StringUtil.CSV)));
			try {
				writer.write(builder.toString());
				writer.flush();
			} finally {
				writer.close();
			}
		}
	}

	public void showFilterDialog() {
		new VFilterDialog<T>(this).open();
	}

	public void setTotalItemsCount(int totalItemsCount) {
		if (pageManager == null) {
			throw new VidgeException("*** Page Manager is null - check you creation style please");
		}
		pageManager.setItemsCount(totalItemsCount);
	}

	public void setPageListener(IPageListener iPageListener) {
		if (pageManager == null) {
			throw new VidgeException("*** Page Manager is null - check you creation style please");
		}
		pageManager.clearListeners();
		pageManager.addPageListener(iPageListener);
	}

	public PageManager getPageManager() {
		return pageManager;
	}

	public void addContextMenu(final Action action, int index) {
		MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH, index);
		menuItem.setText(action.getText());
		if (action.getImageDescriptor() != null) {
			menuItem.setImage(action.getImageDescriptor().createImage());
		}
		menuItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				action.run();
			}
		});
	}

	public void addContextMenu(Image image, String text, int index, final Runnable runnable) {
		MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH, index);
		menuItem.setText(text);
		menuItem.setImage(image);
		menuItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				runnable.run();
			}
		});
	}

	public void clear() {
		objectList.clear();
		refresh();
	}

	public void addKeyListener(KeyListener listener) {
		table.addKeyListener(listener);
	}
}
