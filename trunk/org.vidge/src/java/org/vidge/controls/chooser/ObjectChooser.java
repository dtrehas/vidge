package org.vidge.controls.chooser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.vidge.SharedImages;
import org.vidge.VidgeResources;
import org.vidge.explorer.FormExplorer;
import org.vidge.inface.IEntityExplorer;
import org.vidge.inface.IForm;

public class ObjectChooser<T> {

	private Model<T> model;
	private Menu popupMenu;
	TableViewer tableViewer;
	private FormToolkit toolkit;
	public static final int START = SWT.APPLICATION_MODAL;
	public static final int TITLE = START << 1;
	public static final int TOOLKIT = START << 3;
	public static final int BORDER = START << 4;
	public static final int NO_NUM_COLUMN = START << 5;
	public static final int NO_PAGES = START << 6;
	public static final int NO_LINES = START << 7;
	public static final int NO_HEADER = START << 8;
	public static final int NO_FULL_SELECTION = START << 9;
	public static final int NONE = SWT.NONE;
	private Composite parentPane;
	private T selection;
	private List selectionList = new ArrayList<T>();
	private ArrayList<T> input = new ArrayList<T>();
	private PageManager pageManager;
	private int style;
	private boolean sortable = true;
	private List<ISortListener> sortListeners = new ArrayList<ISortListener>();

	public ObjectChooser(Composite parent, int style, IEntityExplorer entityExplorer, Collection<T> objectList, T... selected) {
		this(parent, style, new Model<T>(entityExplorer, style), objectList, selected);
	}

	public ObjectChooser(Composite parent, int style, Class<? extends IForm<T>> formClass, Collection<T> objectList, T... selected) {
		this(parent, style, new Model<T>(new FormExplorer(formClass), style), objectList, selected);
	}

	public ObjectChooser(Composite parent, int style, Model<T> modelIn, Collection<T> objectList, T... selected) {
		this.style = style;
		this.model = modelIn;
		model.setChooser(this);
		createViews(parent);
		createContextMenu();
		setInput(objectList);
		if (selected != null) {
			for (T obj : selected) {
				selectionList.add(obj);
			}
		}
	}

	private void createViews(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		layout.verticalSpacing = 0;
		int borderStyle = SWT.NONE;
		if ((style & BORDER) != 0) {
			borderStyle = SWT.BORDER;
		}
		if ((style & TOOLKIT) != 0) {
			toolkit = new FormToolkit(parent.getDisplay());
			parentPane = toolkit.createComposite(parent, borderStyle);
		} else {
			parentPane = new Composite(parent, borderStyle);
		}
		parentPane.setLayout(layout);
		createViewer(parentPane, style);
		createFooter(parentPane);
	}

	private void createFooter(Composite parent) {
		GridLayout layout;
		if ((style & NO_PAGES) == 0) {
			Composite composite = null;
			if ((style & TOOLKIT) != 0) {
				composite = toolkit.createComposite(parent, SWT.NONE);
			} else {
				composite = new Composite(parent, SWT.NONE);
			}
			layout = new GridLayout();
			layout.numColumns = 1;
			composite.setLayout(layout);
			composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			pageManager = new PageManager(composite, style);
			pageManager.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_END));
			// pageManager.addPageListener(new IPageListener() {
			//
			// public void pageChanged(int from, int count) {
			// tableViewer.getTable().deselectAll();
			// selection = null;
			// selectionList.clear();
			// tableViewer.setInput(pageManager.getNext(model.doSort(model.doFilter(input))));
			// }
			// });
		}
	}

	String getTitle() {
		return Messages.Model_LIST_OF + input.size() + Messages.Model_ITEMS;
	}

	private void createViewer(Composite parent, int styleIn) {
		if ((style & NO_FULL_SELECTION) != 0) {
			tableViewer = new TableViewer(parent, styleIn | SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE);
		} else {
			tableViewer = new TableViewer(parent, styleIn | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		}
		if ((style & NO_LINES) != 0) {
			tableViewer.getTable().setLinesVisible(false);
		} else {
			tableViewer.getTable().setLinesVisible(true);
		}
		if ((style & NO_HEADER) != 0) {
			tableViewer.getTable().setHeaderVisible(false);
		} else {
			tableViewer.getTable().setHeaderVisible(true);
		}
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				try {
					selection = (T) ((IStructuredSelection) event.getSelection()).getFirstElement();
					selectionList.clear();
					for (Object selected : ((StructuredSelection) tableViewer.getSelection()).toArray()) {
						selectionList.add((T) selected);
					}
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			}
		});
		buildColumns();
		tableViewer.setContentProvider(model);
		tableViewer.setLabelProvider(model);
		tableViewer.setColumnProperties(model.getColumnHeaders());
		tableViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	public void addPageListener(IPageListener listener) {
		if (pageManager != null) {
			pageManager.addPageListener(listener);
		}
	}

	public void removePageListener(IPageListener listener) {
		if (pageManager != null) {
			pageManager.removePageListener(listener);
		}
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		tableViewer.addSelectionChangedListener(listener);
	}

	public void addDoubleClickListener(IDoubleClickListener listener) {
		tableViewer.addDoubleClickListener(listener);
	}

	public void removeDoubleClickListener(IDoubleClickListener listener) {
		tableViewer.removeDoubleClickListener(listener);
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		tableViewer.removeSelectionChangedListener(listener);
	}

	public void addSortListener(ISortListener listener) {
		sortListeners.add(listener);
	}

	public void fireSortChanged(Column column) {
		for (ISortListener listener : sortListeners) {
			listener.columnSorted(column);
		}
	}

	public Composite getControl() {
		return parentPane;
	}

	public int getSelectedIndex() {
		return indexOf(selection);
	}

	public void setInput(T[] input) {
		setInput(Arrays.asList(input));
	}

	public void setInput(Collection<T> inputIn) {
		input.clear();
		if (inputIn != null) {
			input.addAll(inputIn);
		}
		tableViewer.getTable().deselectAll();
		selection = null;
		selectionList.clear();
		if (pageManager != null)
			pageManager.firePageChanged();
		setInputInt();
	}

	private void setInputInt() {
		if (pageManager == null) {
			tableViewer.setInput(model.doSort(model.doFilter(input)));
		} else {
			tableViewer.setInput(model.doSort(pageManager.getNext(model.doFilter(input))));
		}
		fireSortChanged(null);
	}

	public List<T> getInput() {
		return input;
	}

	private void createContextMenu() {
		popupMenu = new Menu(tableViewer.getTable().getShell(), SWT.POP_UP);
		MenuItem menuItem = new MenuItem(popupMenu, SWT.CHECK);
		menuItem.setText(Messages.ObjectChooser_FILTERED);
		menuItem.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				((MenuItem) e.getSource()).setSelection(false);
				removeFiltering();
			}
		});
		menuItem = new MenuItem(popupMenu, SWT.PUSH);
		menuItem.setText(Messages.ObjectChooser_SHOW_FILTER_DIALOG);
		menuItem.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				showFilterDialog();
			}
		});
		tableViewer.getTable().addMouseListener(new MouseAdapter() {

			public void mouseDown(MouseEvent e) {
				if (e.button == 3) {
					popupMenu.getItem(0).setSelection(model.isFiltered());
					popupMenu.setVisible(true);
				}
			}
		});
		tableViewer.getTable().addTraverseListener(new TraverseListener() {

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

	public void showFilterDialog() {
		new FilterDialog<T>((ObjectChooser<T>) ObjectChooser.this).open();
	}

	public void addContextMenuItem(final IAction action, int index) {
		MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH, index);
		menuItem.setText(action.getText());
		menuItem.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				action.run();
			}
		});
	}

	protected void buildColumns() {
		TableLayout layout = new TableLayout();
		tableViewer.getTable().setLayout(layout);
		String[] columns = model.getColumnHeaders();
		ColumnLayoutData columnLayouts[] = getColumnsLayouts(columns.length);
		for (int i = 0; i < columns.length; i++) {
			ColumnLayoutData layoutData = columnLayouts[i];
			layout.addColumnData(layoutData);
			int styleCol = model.getColumn(i).isNumeric() ? (SWT.RIGHT) : (SWT.LEFT);
			TableColumn tc = new TableColumn(tableViewer.getTable(), styleCol, i);
			tc.setText(columns[i]);
			tc.setToolTipText(columns[i]);
			Column column = model.getColumn(i);
			column.setTableColumn(tc);
			if ((layoutData instanceof ColumnWeightData)) {
				((ColumnWeightData) layoutData).weight = model.getColumn(i).getWidth();
			}
			if ((column.getExplorer() != null) && (column.getExplorer().getBackground() != null)) {
				tableViewer.getTable().addPaintListener(new ColumnPainter(column));
			}
			tc.setData(column);
			tableViewer.addFilter(column.getColumnFilter());
			if ((i > 0) || ((style & NO_NUM_COLUMN) == 0)) {
				tc.setImage(VidgeResources.getInstance().getImage(SharedImages.NONE));
			}
			tc.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					if (sortable) {
						TableColumn selectedColumn = (TableColumn) e.getSource();
						int columnIndex = tableViewer.getTable().indexOf(selectedColumn);
						if ((columnIndex == 0) && (((style & NO_NUM_COLUMN) == 0))) {
							return;
						}
						selectColumn(columnIndex);
					}
				}
			});
		}
	}

	public void selectColumn(int columnIndex) {
		model.toggleColumn(columnIndex);
		for (int a = getFirstColumn(); a < tableViewer.getTable().getColumnCount(); a++) {
			tableViewer.getTable().getColumn(a).setImage(VidgeResources.getInstance().getImage(model.getColumnImage(a)));
		}
		setInputInt();
		fireSortChanged(model.getColumn(columnIndex));
	}

	public int getFirstColumn() {
		return ((style & NO_NUM_COLUMN) != 0) ? 0 : 1;
	}

	protected ColumnLayoutData[] getColumnsLayouts(int size) {
		ColumnLayoutData[] result = new ColumnLayoutData[size];
		for (int a = 0; a < size; a++) {
			if (((style & NO_NUM_COLUMN) == 0) && (a == 0)) {
				result[a] = new ColumnPixelData(40, false);
			} else {
				result[a] = new ColumnWeightData(100, 50, true);
			}
		}
		return result;
	}

	public void refresh() {
		tableViewer.refresh();
		fireSortChanged(null);
	}

	public int getFilterCount() {
		return model.getFilterCount();
	}

	public String getFilterLabel(int a) {
		return model.getFilterLabel(a);
	}

	public Column getColumn(int i) {
		return model.getColumn(i);
	}

	public void setFiltered(boolean selection, int index) {
		model.setFiltered(selection, index);
		setInputInt();
	}

	public void setFilterMask(String text, int index) {
		model.setFilterMask(text, index);
		setInputInt();
	}

	public void removeFiltering() {
		model.removeFiltering();
		setInputInt();
	}

	public void select(int index) {
		if (this.getInput().size() > 0) {
			setSelection((T) this.getInput().get(index));
		}
	}

	public void setSelection(T object) {
		if (object == null) {
			this.selection = null;
			tableViewer.getTable().deselectAll();
		} else {
			int indexOf = ((List) tableViewer.getInput()).indexOf(object);
			this.selection = object;
			if (indexOf >= 0) {
				tableViewer.setSelection(new StructuredSelection(selection), true);
			} else {
				if (pageManager != null) {
					indexOf = input.indexOf(object);
					if (indexOf >= 0) {
						this.selection = input.get(indexOf);
						int last = indexOf + pageManager.getCountOnPage();
						if (last >= input.size()) {
							last = input.size();
						}
						tableViewer.setInput(pageManager.getNext(model.doSort(model.doFilter(input.subList(indexOf, last)))));
						if (selection != null) {
							tableViewer.setSelection(new StructuredSelection(selection), true);
						}
					}
				}
			}
		}
	}

	public T getSelection() {
		return selection;
	}

	public void clear() {
		selection = null;
		selectionList.clear();
		input.clear();
		refresh();
	}

	public void setVisibleObject(T obj) {
		if (obj != null) {
			model.setFilteringByObject(obj);
		} else {
			this.removeFiltering();
		}
	}

	public Collection<T> getSelectionObjects() {
		return selectionList;
	}

	public Collection<T> removeSelection() {
		if ((input != null)) {
			if (selection != null) {
				input.remove(selection);
			}
			if (selectionList.size() > 0) {
				input.removeAll(selectionList);
			}
			tableViewer.setInput(input);
			refresh();
		}
		return input;
	}

	int indexOf(Object obj) {
		return input.indexOf(obj);
	}

	public void setEnabled(boolean enabled) {
		parentPane.setEnabled(enabled);
		tableViewer.getTable().setEnabled(enabled);
	}

	public void setPageItemsCount(int itemsCount) {
		if (pageManager != null) {
			pageManager.setPageItemsCount(input.size());
		}
	}

	public boolean isDisposed() {
		return tableViewer.getTable().isDisposed();
	}

	public void newPage() {
		if (pageManager != null) {
			model.setStartRowNumber(pageManager.getFrom());
		}
	}

	public void getFirstPage() {
		if (pageManager != null) {
			pageManager.getFirstPage();
		}
	}

	public int getCountOnPage() {
		if (pageManager != null) {
			return pageManager.getCountOnPage();
		}
		return -1;
	}

	public int getPageNum() {
		if (pageManager != null) {
			return pageManager.getPageNum();
		}
		return 0;
	}

	public void setItemsCount(int count) {
		if (pageManager != null) {
			pageManager.setItemsCount(count);
		}
	}

	public TableViewer getTableViewer() {
		return tableViewer;
	}
	private class ColumnPainter implements PaintListener {

		private Column column;

		public ColumnPainter(Column column) {
			this.column = column;
		}

		public void paintControl(PaintEvent e) {
			for (TableItem item : tableViewer.getTable().getItems()) {
				item.setBackground(model.getColumns().indexOf(column), column.getExplorer().getBackground());
			}
		}
	}

	public PageManager getPageManager() {
		return pageManager;
	}

	public FormToolkit getToolKit() {
		return toolkit;
	}

	public void setColumnSort(boolean sortable) {
		this.sortable = sortable;
	}
}
