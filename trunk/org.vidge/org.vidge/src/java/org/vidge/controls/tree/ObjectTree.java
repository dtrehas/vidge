package org.vidge.controls.tree;

import java.text.MessageFormat;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;
import org.vidge.SharedImages;
import org.vidge.VidgeResources;
import org.vidge.form.INodeForm;
import org.vidge.form.impl.DefaultNodeForm;
import org.vidge.form.impl.NodeIterator;
import org.vidge.inface.INodeIterator;
import org.vidge.job.AbstractJob;
import org.vidge.job.JobStarter;
import org.vidge.util.StringUtil;

public class ObjectTree {

	private ObjectTreeModel model;
	private Menu popupMenu;
	private TreeViewer viewer;
	private Composite parentComposite;
	private INodeForm selection = null;
	public static final int NONE = 0;
	public static final int BORDER = 2;
	public static final int NODES_LINES = 4;
	public static final int NO_IMAGES = 1 << 12;
	private int treeStyle = SWT.H_SCROLL | SWT.V_SCROLL;
	boolean inputAsRoot = false;
	boolean expandAll = true;
	private INodeForm input;
	private INodeForm<?> root;
	private String searchString = StringUtil.NN; //$NON-NLS-1$
	public Action searchAction, refreshAction;
	private INodeIterator nodeIterator;
	private TreeSearchTableDialog dialog;

	public ObjectTree(Composite parent, int style) {
		this.model = new ObjectTreeModel(this, ((style & NO_IMAGES) == 0));
		createViews(parent, style);
	}

	private void createViews(Composite parent, int style) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.verticalSpacing = 0;
		int borderStyle = SWT.NONE;
		if ((style & BORDER) != 0) {
			borderStyle = SWT.BORDER;
		}
		if ((style & NODES_LINES) == 0) {
			treeStyle = treeStyle | SWT.FULL_SELECTION;
		}
		if ((style & SWT.VIRTUAL) != 0) {
			treeStyle = treeStyle | SWT.VIRTUAL;
		}
		if ((style & SWT.CHECK) != 0) {
			treeStyle = treeStyle | SWT.CHECK;
		}
		parentComposite = new Composite(parent, borderStyle);
		parentComposite.setLayout(layout);
		createViewer(parentComposite);
		parentComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		searchAction = new TreeSearchAction();
		refreshAction = new TreeRefreshAction();
	}

	private void createViewer(Composite parent) {
		viewer = new TreeViewer(parent, treeStyle);
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		if ((treeStyle & SWT.VIRTUAL) == 0) {
			viewer.addSelectionChangedListener(new ISelectionChangedListener() {

				public void selectionChanged(SelectionChangedEvent event) {
					selection = (INodeForm) ((IStructuredSelection) event.getSelection()).getFirstElement();
					selectionChangedAction(event);
				}
			});
			viewer.setContentProvider(model.getContentProvider());
			viewer.setLabelProvider(model.getLabelProvider());
			viewer.getTree().addSelectionListener(model.getSelectionListener());
			viewer.getTree().addTreeListener(model.getTreeListener());
			viewer.getControl().addMouseListener(new MouseAdapter() {

				public void mouseDown(MouseEvent e) {
					if (e.button == 3) {
						try {
							selection = (INodeForm) ((IStructuredSelection) viewer.getSelection()).getFirstElement();
							createContextMenu();
							popupMenu.setVisible(true);
						} catch (RuntimeException e1) {
							e1.printStackTrace();
						}
					}
				}
			});
			viewer.addDoubleClickListener(new IDoubleClickListener() {

				public void doubleClick(DoubleClickEvent event) {
					doubleClickAction(event);
				}
			});
		}
	}

	public void doubleClickAction(DoubleClickEvent event) {
		if (selection != null) {
			selection.doubleClickAction(event);
		}
	}

	public void selectionChangedAction(SelectionChangedEvent event) {
		if (selection != null) {
			selection.showDetails();
			if (selection.getActions() != null) {
				makeActions();
			}
		}
	}

	protected void makeActions() {
	}

	protected boolean openQuestion(String title, String message, String selectedName) {
		return MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), title, MessageFormat.format(message, selectedName));
	}

	public void setInput(INodeForm inputObj) {
		input = inputObj;
		root = new DefaultNodeForm();
		if (input != null) {
			INodeForm subroot = input;
			if (!inputAsRoot) {
				subroot.getParentInternal();
				while (subroot.getParent() != null) {
					subroot = subroot.getParent();
					subroot.getParentInternal();
				}
			}
			if (input.toString().equals(StringUtil.NN)) { //$NON-NLS-1$
				root = input;
			} else {
				root.addChild(subroot);
			}
			fillChidren(subroot);
		}
		viewer.setInput(root);
		viewer.refresh();
		if (input != null) {
			setSelection(input.getInput());
			viewer.getTree().showSelection();
			if (expandAll) {
				viewer.expandAll();
			}
		}
		if (nodeIterator == null) {
			nodeIterator = new NodeIterator(root);
		}
	}

	private void fillChidren(INodeForm current) {
		INodeForm[] children = current.getChildrenInternal();
		for (int a = 0; a < children.length; a++) {
			if (!(current.equals(children[a]))) {
				fillChidren(children[a]);
			}
		}
	}

	private void createContextMenu() {
		popupMenu = new Menu(viewer.getControl().getShell(), SWT.POP_UP);
		createMenuItem(searchAction);
		createMenuItem(refreshAction);
		if (selection != null) {
			for (final IAction action : selection.getActions()) {
				createMenuItem(action);
			}
		}
	}

	private void createMenuItem(final IAction action) {
		MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
		menuItem.setText(action.getText());
		if (action.getImageDescriptor() != null) {
			menuItem.setImage(action.getImageDescriptor().createImage());
		}
		menuItem.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				action.run();
			}
		});
	}

	public void refresh() {
		if (selection != null) {
			TreePath path = selection.getTreePath();
			viewer.expandToLevel(path, 1);
		}
		viewer.refresh();
	}

	public INodeForm getSelection() {
		return selection;
	}

	public void clear() {
		this.selection = null;
		setInput(null);
	}

	public TreeViewer getViewer() {
		return viewer;
	}

	public Control getControl() {
		return parentComposite;
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		viewer.addSelectionChangedListener(listener);
	}

	public void addDoubleClickListener(IDoubleClickListener listener) {
		viewer.addDoubleClickListener(listener);
	}

	public void removeDoubleClickListener(IDoubleClickListener listener) {
		viewer.removeDoubleClickListener(listener);
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		viewer.removeSelectionChangedListener(listener);
	}

	INodeForm getInput() {
		return input;
	}

	public boolean isExpandAll() {
		return expandAll;
	}

	public void setExpandAll(boolean expandAll) {
		this.expandAll = expandAll;
	}

	public boolean isInputAsRoot() {
		return inputAsRoot;
	}

	public void setInputAsRoot(boolean inputAsRoot) {
		this.inputAsRoot = inputAsRoot;
	}

	public INodeForm getRoot() {
		return root;
	}

	public void setExpandedNode(TreePath selectedPath) {
		viewer.setExpandedTreePaths(new TreePath[] {
			selectedPath
		});
	}

	public void refresh(INodeForm selected) {
		viewer.refresh(selected);
	}

	public void setSelection(Object obj) {
		if (obj instanceof INodeForm) {
			this.selection = (INodeForm) obj;
		} else {
			this.selection = root.getSelected(obj);
		}
		if (selection != null) {
			selection.showDetails();
			viewer.expandToLevel(selection.getTreePath(), 1);
			viewer.setSelection(new TreeSelection(selection.getTreePath()));
			viewer.getTree().showSelection();
		}
	}

	public void setNodeIterator(INodeIterator iterator) {
		this.nodeIterator = iterator;
	}

	public void searchNodes(String filterString) {
		clearFound();
		this.searchString = filterString;
		JobStarter.startJob(new SearchJob());
	}

	public int getFoundCount() {
		return nodeIterator.count();
	}

	public void showFirst() {
		setSelection(nodeIterator.first());
	}

	public void showPrevious() {
		setSelection(nodeIterator.previous());
	}

	public void showLast() {
		setSelection(nodeIterator.last());
	}

	public void showNext() {
		setSelection(nodeIterator.next());
	}

	public void clearFound() {
		nodeIterator.clear();
	}

	public String getTreeSearchString() {
		return searchString;
	}

	public int getCurrent() {
		return nodeIterator.current();
	}
	private class TreeRefreshAction extends Action {

		public TreeRefreshAction() {
			super(Messages.ObjectTree_0, VidgeResources.getInstance().getImageDescriptor(SharedImages.ACTION_REFRESH));
			setToolTipText(Messages.ObjectTree_1);
			setDescription(Messages.ObjectTree_2);
		}

		@Override
		public void run() {
			refresh();
		}
	}
	public class TreeSearchAction extends CommonSearchAction {

		@Override
		public void run() {
			dialog = new TreeSearchTableDialog(ObjectTree.this.getControl().getShell(), ObjectTree.this);
			dialog.open();
		}
	}
	private class SearchJob extends AbstractJob {

		public SearchJob() {
			super(Messages.ObjectTree_4, Messages.ObjectTree_5, Messages.ObjectTree_6);
		}

		@Override
		protected void processShow() {
			dialog.setFound(nodeIterator.getFound());
		}

		@Override
		protected void runInternal() throws Exception {
			nodeIterator.find(searchString);
		}
	}

	public INodeIterator getNodeIterator() {
		return nodeIterator;
	}

	public void setEnabled(boolean enabled) {
		viewer.getTree().setEnabled(enabled);
	}
}
