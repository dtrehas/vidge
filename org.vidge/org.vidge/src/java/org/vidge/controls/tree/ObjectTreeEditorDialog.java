package org.vidge.controls.tree;

import java.util.HashSet;
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
import org.eclipse.swt.widgets.TreeItem;
import org.vidge.controls.editor.IOkListener;
import org.vidge.controls.tree.hierarchy.IHierarchyProvider;
import org.vidge.dialog.AbstractObjectDialog;
import org.vidge.inface.IPropertyExplorer;
import org.vidge.util.BoolPair;

public class ObjectTreeEditorDialog<T> extends AbstractObjectDialog<T> {

	private final IHierarchyProvider<T> hierarchyProvider;
	private Tree tree;
	private Set<BoolPair> selectionSet = new HashSet<BoolPair>();
	private final IOkListener iOkListener;
	private IPropertyExplorer explorer2;

	public ObjectTreeEditorDialog(String title, IHierarchyProvider<T> hierarchyProvider, IOkListener iOkListener) {
		super(null, title, DEFAULT_SIZE);
		this.hierarchyProvider = hierarchyProvider;
		this.iOkListener = iOkListener;
	}

	public ObjectTreeEditorDialog(String label, IPropertyExplorer explorer, IHierarchyProvider<T> hierarchyProvider2, IOkListener iOkListener2) {
		this(label, hierarchyProvider2, iOkListener2);
		explorer2 = explorer;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		if (hierarchyProvider.isMultipleSelect()) {
			createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, true).addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					okPressed();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
		} else {
			super.createButtonsForButtonBar(parent);
		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(hierarchyProvider.getTitle() == null ? "Tree" : hierarchyProvider.getTitle());
		setMessage(hierarchyProvider.getMessage());
		if (hierarchyProvider != null) {
			int style = SWT.VIRTUAL | SWT.SINGLE;
			if (hierarchyProvider.isMultipleSelect()) {
				style = style | SWT.CHECK;
			}
			tree = new Tree(parent, style | SWT.FULL_SELECTION);
			tree.setLayoutData(new GridData(GridData.FILL_BOTH));
			tree.setHeaderVisible(true);
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
				selection = (T) hierarchyProvider.checkData(item.getData());
				if (getButton(IDialogConstants.OK_ID) != null) {
					getButton(IDialogConstants.OK_ID).setEnabled(hierarchyProvider.validateData(item.getData()));
				}
				if (hierarchyProvider.isMultipleSelect()) {
					if (item.getGrayed()) {
						item.setChecked(false);
						e.doit = false;
					} else {
						selectionSet.add(new BoolPair(item.getData(), item.getChecked()));
					}
				} else {
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
		} else {
			if (explorer2 != null) {
				explorer2.setValue(selection);
			}
			hierarchyProvider.setSelection(selection);
		}
		super.okPressed();
		hierarchyProvider.commitChanges(selectionSet);
		if (iOkListener != null) {
			iOkListener.okPressed();
		}
	}
}
