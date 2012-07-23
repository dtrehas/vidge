package org.vidge.controls.tree;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.vidge.controls.tree.hierarchy.IHierarchyProvider;
import org.vidge.dialog.AbstractObjectDialog;
import org.vidge.util.BoolPair;

public class ObjectTreeEditorDialog<T> extends AbstractObjectDialog<T> {

	private final IHierarchyProvider<T> hierarchyProvider;
	private Tree tree;
	private Set<BoolPair> selectionSet = new HashSet<BoolPair>();

	public ObjectTreeEditorDialog(String title, IHierarchyProvider<T> hierarchyProvider) {
		super(null, title, DEFAULT_SIZE);
		this.hierarchyProvider = hierarchyProvider;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		if (hierarchyProvider != null) {
			int style = SWT.VIRTUAL | SWT.SINGLE;
			if (hierarchyProvider.isMultipleSelect()) {
				style = style | SWT.CHECK;
			}
			tree = new Tree(parent, style);
			tree.setLayoutData(new GridData(GridData.FILL_BOTH));
			tree.setHeaderVisible(true);
			tree.addListener(SWT.SetData, new Listener() {

				@SuppressWarnings("nls")
				public void handleEvent(Event event) {
					hierarchyProvider.handleEvent(event);
				}
			});
			if (hierarchyProvider.getColumnList().size() > 0) {
				List<String> list = hierarchyProvider.getColumnList();
				for (String string : list) {
					TreeColumn column = new TreeColumn(tree, SWT.LEFT);
					column.setText(string);
					column.setWidth(100);
					column.setMoveable(true);
				}
			}
			tree.setItemCount(hierarchyProvider.getRootCount());
		}
		tree.addSelectionListener(new SelectionListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem item = (TreeItem) e.item;
				selection = (T) hierarchyProvider.checkData(item.getData());
				getButton(IDialogConstants.OK_ID).setEnabled(hierarchyProvider.validateData(item.getData()));
				if (hierarchyProvider.isMultipleSelect()) {
					if (item.getGrayed()) {
						item.setChecked(false);
						e.doit = false;
					} else {
						selectionSet.add(new BoolPair(item.getData(), item.getChecked()));
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		return tree;
	}

	@Override
	protected void okPressed() {
		if (hierarchyProvider.isMultipleSelect()) {
			selection = null;
		}
		super.okPressed();
		hierarchyProvider.commitChanges(selectionSet);
	}
}
