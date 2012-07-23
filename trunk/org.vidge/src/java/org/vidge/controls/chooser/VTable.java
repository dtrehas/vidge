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
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
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
import org.vidge.VidgeResources;
import org.vidge.inface.IEntityExplorer;
import org.vidge.inface.IPropertyExplorer;
import org.vidge.util.FormContext;
import org.vidge.util.StringUtil;

public class VTable<T> extends Composite {

	private static final int _50 = 90;
	private static final int _40 = 40;
	public static final int NO_NUM_COLUMN = SWT.APPLICATION_MODAL << 12;
	public static final int PAGES = SWT.APPLICATION_MODAL << 14;
	public static final int NO_LINES = SWT.APPLICATION_MODAL << 15;
	public static final int NO_HEADER = SWT.APPLICATION_MODAL << 16;
	protected Table table;
	protected List<T> objectList = new ArrayList<T>();
	protected List<T> visibleList = new ArrayList<T>();
	protected IEntityExplorer tableExplorer;
	protected List<VColumn<T>> columns = new ArrayList<VColumn<T>>();
	protected boolean isNumColumn;
	protected VPageManager pageManager;
	protected int from = 0, count = 0, addNum = 1;
	protected String[] columnStrings = new String[0];
	protected DefaultTableComparator<T> comparator = new DefaultTableComparator<T>();

	public VTable(Composite parent, Class<T> objClass, List<T> listIn, int style, DefaultTableComparator<T> comparator) {
		this(parent, objClass, listIn, style);
		this.comparator = comparator;
	}

	public VTable(Composite parent, Class<T> objClass, List<T> listIn, int style) {
		this(parent, FormRegistry.getEntityExplorer(FormContext.TABLE.name(), objClass), listIn, style);
	}

	public VTable(Composite parent, IEntityExplorer tableExplorerIn, List<T> listIn, int style) {
		super(parent, style);
		isNumColumn = (getStyle() & NO_NUM_COLUMN) == 0;
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginBottom = 0;
		setLayout(layout);
		this.tableExplorer = tableExplorerIn;
		if (tableExplorerIn == null) {
			throw new RuntimeException("***Not found IEntityExplorer ");
		}
		createTable();
		if ((getStyle() & PAGES) != 0) {
			addNum = 0;
			createPaginator(style);
		}
		createContextMenu();
		setInput(listIn);
	}

	protected void createTable() {
		table = new Table(this, getStyle() | SWT.VIRTUAL | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.SINGLE);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.setLinesVisible((getStyle() & NO_LINES) == 0);
		table.setHeaderVisible((getStyle() & NO_HEADER) == 0);
		createColumns();
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
	}

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
	}

	protected void createColumns() {
		List<ColumnLayoutData> dataList = new ArrayList<ColumnLayoutData>();
		if (isNumColumn) {
			columns.add(new VColumn<T>(this, null));
			dataList.add(new ColumnPixelData(_40, true));
		}
		Collections.sort(tableExplorer.getPropertyList());
		for (IPropertyExplorer ex : tableExplorer.getPropertyList()) {
			columns.add(new VColumn<T>(this, ex));
			dataList.add(new ColumnWeightData(Math.abs(ex.getVisualAreaWidth()), _50, true));
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
			tc.setText(tColumn.explorer.getLabel());
			tc.setToolTipText(tColumn.explorer.getLabel());
			if ((a > 0) || isNumColumn) {
				tc.setImage(VidgeResources.getInstance().getImage(SharedImages.NONE));
			}
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
		// table.redraw();
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
		objectList.clear();
		if (listIn == null || listIn.size() == 0) {
		} else {
			objectList.addAll(listIn);
		}
		if (pageManager != null && listIn != null) {
			pageManager.setItemsCount(listIn.size());
		}
		refresh();
	}

	private void createPaginator(int style) {
		pageManager = new VPageManager(this, style);
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
							if (column.isFiltered() && column.getFilterMask().length() > 0) {
								Iterator<T> iterator = filteredList.iterator();
								while (iterator.hasNext()) {
									Object value = column.explorer.getValue(iterator.next());
									if (value != null && !value.toString().contains(column.getFilterMask())) {
										iterator.remove();
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

	public void setComparator(DefaultTableComparator<T> comparator) {
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
		final Menu popupMenu = new Menu(table.getShell(), SWT.POP_UP);
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
}
