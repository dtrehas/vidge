package org.vidge.controls.chooser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.vidge.SharedImages;
import org.vidge.inface.IEntityExplorer;
import org.vidge.inface.IPropertyExplorer;
import org.vidge.util.StringUtil;

class Model<T> implements IStructuredContentProvider, ITableLabelProvider, IColorProvider {

	private static final String SORT_NONE = Messages.Model_SORT_NONE;
	private int currentSorterIndex = 0;
	private List<Column> columns = new ArrayList<Column>();
	private Viewer viewer;
	private String filterString = StringUtil.NN; //$NON-NLS-1$
	private String sortString = SORT_NONE;
	private final int style;
	public static final int NO_NUM_COLUMN = 64;
	private int startRowNumber = 1;
	private IEntityExplorer entityExplorer;
	private ObjectChooser chooser;
	private int filteredSize;

	public int getStartRowNumber() {
		return startRowNumber;
	}

	public void setStartRowNumber(int startRowNumber) {
		this.startRowNumber = startRowNumber;
	}

	Model(IEntityExplorer entityExplorer, int style) {
		this.entityExplorer = entityExplorer;
		this.style = style;
		init();
	}

	private void init() {
		if ((style & NO_NUM_COLUMN) == 0) {
			columns.add(new Column(null));
		}
		Collections.sort(entityExplorer.getPropertyList());
		for (IPropertyExplorer ex : entityExplorer.getPropertyList()) {
			columns.add(new Column(ex));
		}
	}

	String getDescription() {
		return sortString + filterString;
	}

	int getFilterCount() {
		return entityExplorer.getPropertyList().size();
	}

	String getFilterLabel(int a) {
		return entityExplorer.getPropertyList().get(a).getLabel();
	}

	String[] getColumnHeaders() {
		String[] headers = new String[columns.size()];
		for (int a = 0; a < headers.length; a++) {
			headers[a] = columns.get(a).getLabel();
		}
		return headers;
	}

	List<T> doSort(List<T> list) {
		// Collections.sort(list, columns.get(currentSorterIndex).getColumnSorter());
		return list;
	}

	List<T> doFilter(List<T> input) {
		boolean filtered = false;
		for (Column column : columns) {
			if (column.isFiltered()) {
				filtered = true;
				break;
			}
		}
		if (!filtered) {
			return input;
		}
		List<T> filteredValues = new ArrayList<T>();
		boolean isOneFilter = false;
		filterString = Messages.Model_FILTERED;
		for (int b = getFirstColumn(); b < columns.size(); b++) {
			Column column = columns.get(b);
			if (column.isFiltered()) {
				filterString = filterString + " " + column.getLabel(); //$NON-NLS-1$
				isOneFilter = true;
			}
		}
		for (T value : input) {
			filtered = true;
			if (isOneFilter) {
				for (int b = getFirstColumn(); b < columns.size(); b++) {
					ViewerFilter filter = columns.get(b).getColumnFilter();
					if (!filter.select(null, null, value)) {
						filtered = false;
						break;
					}
				}
			}
			if (filtered) {
				filteredValues.add(value);
			}
		}
		filterString = filterString + (isOneFilter ? "  :  " + filteredValues.size() + Messages.Model_ITEMS1 : Messages.Model_NONE); //$NON-NLS-1$
		filteredSize = filteredValues.size();
		return filteredValues;
	}

	public int getFilteredSize() {
		return filteredSize;
	}

	void toggleColumn(int columnIndex) {
		for (int a = getFirstColumn(); a < columns.size(); a++) {
			Column column = columns.get(a);
			if (columnIndex == a) {
				column.toggle();
				currentSorterIndex = columnIndex;
				if (!column.isSorted()) {
					sortString = SORT_NONE;
					currentSorterIndex = 0;
				} else {
					sortString = Messages.Model_SORT + columns.get(currentSorterIndex).getLabel();
				}
			} else {
				column.setDefault();
			}
		}
	}

	private int getFirstColumn() {
		return ((style & NO_NUM_COLUMN) == 0) ? 1 : 0;
	}

	String getCellText(Object obj, int index) {
		return ((index == 0) && ((style & NO_NUM_COLUMN) == 0)) ? String.valueOf(chooser.indexOf(obj) + startRowNumber) : columns.get(index).getCellText(obj);
	}

	Column getColumn(int i) {
		return columns.get(i);
	}

	SharedImages getColumnImage(int i) {
		return columns.get(i).getImage();
	}

	void setFilteringByObject(Object object) {
		for (int a = getFirstColumn(); a < columns.size(); a++) {
			Column column = columns.get(a);
			column.setFilterMask(column.getCellText(object));
			column.setFiltered(true);
		}
		viewer.refresh();
	}

	void removeFiltering() {
		for (int a = getFirstColumn(); a < columns.size(); a++) {
			columns.get(a).setFiltered(false);
		}
	}

	String getFilterString() {
		StringBuilder builder = new StringBuilder();
		for (int a = getFirstColumn(); a < columns.size(); a++) {
			if (columns.get(a).isFiltered()) {
				builder.append(columns.get(a).getLabel());
				builder.append(" = ");
				builder.append(columns.get(a).getFilterMask());
				builder.append("  ");
			}
		}
		return builder.toString();
	}

	void setFilterMask(String text, int index) {
		columns.get(index).setFilterMask(text);
	}

	void setFiltered(boolean selection, int index) {
		columns.get(index).setFiltered(selection);
	}

	boolean isFiltered() {
		if (getFilterString() == StringUtil.NN) { //$NON-NLS-1$
			return false;
		}
		return true;
	}

	String getToolTipText(Object object) {
		StringBuilder builder = new StringBuilder();
		for (int a = 0; a < entityExplorer.getPropertyList().size(); a++) {
			try {
				builder.append(entityExplorer.getPropertyList().get(a).getLabel());
				builder.append(" ");
				builder.append(getCellText(object, a));
				builder.append("\n");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return builder.toString();
	}

	@SuppressWarnings("unchecked")
	public Object[] getElements(Object input) {
		return ((List<T>) input).toArray();
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;
	}

	public String getColumnText(Object element, int columnIndex) {
		return getCellText(element, columnIndex);
	}

	public void addListener(ILabelProviderListener listener) {
	}

	public boolean isLabelProperty(Object element, String property) {
		return true;
	}

	public void removeListener(ILabelProviderListener listener) {
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return entityExplorer.getImage(element, columnIndex);
	}

	public Color getBackground(Object element) {
		return entityExplorer.getBackground(element);
	}

	public Color getForeground(Object element) {
		return entityExplorer.getForeground(element);
	}

	public List<Column> getColumns() {
		return columns;
	}

	void setChooser(ObjectChooser<T> chooserIn) {
		chooser = chooserIn;
	}
}
