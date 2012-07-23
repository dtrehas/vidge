package org.vidge.controls.tree;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.vidge.controls.tree.hierarchy.HierarchyDataProvider;
import org.vidge.controls.tree.hierarchy.IHierarchyProvider;
import org.vidge.form.FastBlindNodeForm;
import org.vidge.inface.INodeForm;
import org.vidge.util.PositionUtillity;


public class ObjectTreeDialog extends StatusDialog {

	private ObjectTree tree;
	private Object selection;
	private String title;
	private Collection objList;
	private INodeForm root;
	private IHierarchyProvider hierarchyDataProvider;

	public ObjectTreeDialog(Shell parent, String title, Collection objList,IHierarchyProvider hierarchyDataProvider) {
		super(parent);
		this.title = title;
		this.objList = objList;
		this.hierarchyDataProvider = hierarchyDataProvider;
		setTitle(title);
		setStatusLineAboveButtons(true);
	}

	public ObjectTreeDialog(Shell parent, String title, INodeForm root) {
		super(parent);
		this.title = title;
		this.root = root;
		setTitle(title);
		setStatusLineAboveButtons(true);
	}

	@Override
	protected Control createContents(Composite parent) {
		parent.getShell().setSize(600, 550);
		PositionUtillity.center(parent.getShell());
		return super.createContents(parent);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		tree = new ObjectTree(parent, ObjectTree.NODES_LINES | ObjectTree.BORDER);
		tree.setExpandAll(false);
		tree.getViewer().setAutoExpandLevel(2);
		if (root == null) {
			if (hierarchyDataProvider == null) {
				hierarchyDataProvider = new HierarchyDataProvider();
				IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
				progressService.showInDialog(
					PlatformUI.getWorkbench().getDisplay().getActiveShell(),
					new Job(HierarchyDataProvider.TASK_NAME) {

						{
							this.schedule();
						}

						@Override
						protected IStatus run(IProgressMonitor monitor) {
							PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

								public void run() {
									try {
										root = new FastBlindNodeForm(title,hierarchyDataProvider,objList.iterator().next().getClass(),true);
										//((FastBlindNodeForm)root).setHierarchyDataProvider(hierarchyDataProvider,objList.iterator().next().getClass());
										tree.setInput(root);
									} catch (Throwable e) {
										e.printStackTrace();
									}
								}
							});
							return Status.OK_STATUS;
						}

					});
			} else {
				root = new FastBlindNodeForm(title,hierarchyDataProvider,objList.iterator().next().getClass(),true);
				tree.setInput(root);
			}
		}else {
			tree.setInput(root);
		}
		if (selection != null) {
			tree.setSelection(selection);
		}
		tree.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				if (tree.getSelection() != null) {
					selection = tree.getSelection().getInput();
				}
				refreshOkButtonStatus();
			}
		});
		return tree.getControl();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		refreshOkButtonStatus();
	}

	protected void refreshOkButtonStatus() {
		Button okButton = getButton(IDialogConstants.OK_ID);
		if (okButton != null) {
			okButton.setEnabled(selection != null);
		}
	}

	public Object getSelection() {
		return selection;
	}

	public void setSelection(Object selection) {
		this.selection = selection;
	}

}
