package org.vidge.controls.chooser;

import java.math.BigDecimal;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.TableColumn;
import org.langcom.locale.EnumLocalizer;
import org.vidge.SharedImages;
import org.vidge.inface.IPropertyExplorer;

public class Column {

	enum SortedState {
		NOT_SORTED, SORTED_DOWN, SORTED_UP;
	}
	public static final String defaultFilterMask = ""; //$NON-NLS-1$
	String filterMask = defaultFilterMask;
	ColumnFilter columnFilter = new ColumnFilter();
	private ColumnSorter columnSorter = new ColumnSorter(this);
	SortedState sortedState = SortedState.NOT_SORTED;
	private SharedImages currentHeaderImage = SharedImages.TABLE_HEADER_DEFAULT;
	private TableColumn tableColumn;
	private boolean filtered = false;
	private final IPropertyExplorer explorer;

	Column(IPropertyExplorer explorer) {
		this.explorer = explorer;
	}

	@SuppressWarnings("rawtypes")
	public boolean isNumeric() {
		if (explorer == null) {
			return true;
		}
		Class klass;
		try {
			klass = explorer.getPropertyClass();
		} catch (Exception e) {
			return false;
		}
		return ((klass == BigDecimal.class) || (klass == Long.class) || (klass == Integer.class) || (klass == Float.class));
	}

	public int getWidth() {
		if (explorer == null) {
			return 40;
		}
		return explorer.getVisualAreaWidth();
	}

	public String getFilterMask() {
		return filterMask;
	}

	public void setFilterMask(String filterMask) {
		this.filterMask = filterMask;
	}

	public boolean isFiltered() {
		return filtered;
	}

	public boolean isSorted() {
		return sortedState != SortedState.NOT_SORTED;
	}

	public void setFiltered(boolean filtered) {
		this.filtered = filtered;
	}

	public TableColumn getTableColumn() {
		return tableColumn;
	}

	public SharedImages getImage() {
		if (!isSorted()) {
			return SharedImages.NONE;
		}
		return currentHeaderImage;
	}

	public void setDefault() {
		sortedState = SortedState.NOT_SORTED;
		currentHeaderImage = SharedImages.TABLE_HEADER_DEFAULT;
	}

	public void toggle() {
		switch (sortedState) {
			case NOT_SORTED:
				currentHeaderImage = SharedImages.TABLE_HEADER_DOWN;
				sortedState = SortedState.SORTED_DOWN;
				break;
			case SORTED_DOWN:
				currentHeaderImage = SharedImages.TABLE_HEADER_UP;
				sortedState = SortedState.SORTED_UP;
				break;
			default:
				setDefault();
		}
	}

	public String getCellText(Object obj) {
		String objString = "";//$NON-NLS-1$
		try {
			if (explorer != null) {
				Object propertyValue = explorer.getValue(obj);
				if (propertyValue instanceof Enum) {
					objString = EnumLocalizer.getLocalized((Enum<?>) propertyValue);
				} else if (propertyValue instanceof Boolean) {
					objString = Messages.toLocalizedString((Boolean) propertyValue);
				} else {
					objString = propertyValue == null ? "" : propertyValue.toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return objString;
	}

	@Override
	public boolean equals(Object obj) {
		if ((obj instanceof Column) && (((Column) obj).tableColumn.equals(tableColumn))) {
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return tableColumn.hashCode();
	}

	public void setTableColumn(TableColumn tc) {
		this.tableColumn = tc;
	}
	private class ColumnFilter extends ViewerFilter {

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (filtered && filterMask.equals(defaultFilterMask)) {
				return getCellText(element).equals(defaultFilterMask);
			}
			return filtered ? getCellText(element).toLowerCase().contains(filterMask.toLowerCase()) : true;
		}
	}

	public ColumnFilter getColumnFilter() {
		return columnFilter;
	}

	public ColumnSorter getColumnSorter() {
		return columnSorter;
	}

	public String getLabel() {
		return (explorer != null) ? explorer.getLabel() : Messages.Column_NN;
	}

	public IPropertyExplorer getExplorer() {
		return explorer;
	}
}