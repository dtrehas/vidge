package org.vidge.controls.chooser;

import java.util.Comparator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.vidge.controls.chooser.Column.SortedState;

class ColumnSorter extends ViewerSorter implements Comparator<Object> {

	private Column column;

	public ColumnSorter(Column column) {
		this.column = column;
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		int result = 0;
		if (column.sortedState.equals(SortedState.SORTED_UP)) {
			result = column.getCellText(e1).compareTo(column.getCellText(e2));
		} else {
			result = column.getCellText(e2).compareTo(column.getCellText(e1));
		}
		return result;
	}

	public int compare(Object o1, Object o2) {
		return compare(null, o1, o2);
	}
}
