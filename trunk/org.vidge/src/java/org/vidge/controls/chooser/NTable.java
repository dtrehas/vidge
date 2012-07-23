package org.vidge.controls.chooser;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.vidge.inface.IEntityExplorer;

public class NTable<T> extends VTable<T> {

	public NTable(Composite parent, Class<T> objClass, List<T> listIn) {
		super(parent, objClass, listIn, SWT.NONE);
	}

	public NTable(Composite parent, Class<T> objClass, List<T> listIn, int style) {
		super(parent, objClass, listIn, style);
	}

	public NTable(Composite parent, Class<T> objClass, List<T> listIn, int style, DefaultTableComparator<T> comparator) {
		super(parent, objClass, listIn, style, comparator);
	}

	public NTable(Composite parent, IEntityExplorer tableExplorerIn, List<T> listIn, int style) {
		super(parent, tableExplorerIn, listIn, style);
	}

	@Override
	protected void createTable() {
		table = new Table(this, getStyle() | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.SINGLE);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.setLinesVisible((getStyle() & NO_LINES) == 0);
		table.setHeaderVisible((getStyle() & NO_HEADER) == 0);
		createColumns();
	}

	@Override
	public void refresh() {
		table.removeAll();
		visibleList.clear();
		visibleList.addAll(filter());
		int index = 0;
		for (T obj : visibleList) {
			fillItem(new TableItem(table, SWT.NONE), index++, obj);
		}
	}

	@Override
	protected void visibleRefresh() {
		table.removeAll();
		int index = 0;
		for (T obj : visibleList) {
			fillItem(new TableItem(table, SWT.NONE), index++, obj);
		}
	}
}
