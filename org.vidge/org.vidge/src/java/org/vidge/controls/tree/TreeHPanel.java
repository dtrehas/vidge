package org.vidge.controls.tree;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.vidge.controls.tree.hierarchy.IHierarchyProvider;
import org.vidge.util.BoolPair;

public class TreeHPanel<T> extends Composite {

	private Tree tree;
	private Set<BoolPair> selectionSet = new HashSet<BoolPair>();

	public TreeHPanel(Composite parent, final IHierarchyProvider<T> hierarchyProvider) {
		super(parent, SWT.NONE);
		setLayout(new FillLayout());
		if (hierarchyProvider != null) {
			int style = SWT.VIRTUAL | SWT.SINGLE;
			if (hierarchyProvider.isMultipleSelect()) {
				style = style | SWT.CHECK;
			}
			tree = new Tree(this, SWT.VIRTUAL | SWT.CHECK);
			tree.setHeaderVisible(false);
			tree.addListener(SWT.SetData, new Listener() {

				@SuppressWarnings("nls")
				public void handleEvent(Event event) {
					hierarchyProvider.handleEvent(event);
				}
			});
			hierarchyProvider.customizeTree(tree);
			tree.setItemCount(hierarchyProvider.getRootCount());
		}
		tree.addSelectionListener(new SelectionListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem item = (TreeItem) e.item;
				T selection = (T) hierarchyProvider.checkData(item);
				if (hierarchyProvider.isMultipleSelect()) {
					if (item.getGrayed()) {
						item.setChecked(false);
						e.doit = false;
					} else {
						selectionSet.add(new BoolPair(selection, item.getChecked()));
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	public Set<BoolPair> getSelectionSet() {
		return selectionSet;
	}
}
