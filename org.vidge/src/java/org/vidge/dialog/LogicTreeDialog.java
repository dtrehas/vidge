package org.vidge.dialog;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.langcom.logic.ICompositeLogic;
import org.langcom.logic.ILogic;
import org.vidge.SharedImages;
import org.vidge.VidgeResources;
import org.vidge.controls.tree.ObjectTree;
import org.vidge.details.Details;
import org.vidge.form.impl.AbstractNodeForm;
import org.vidge.util.PositionUtillity;

public class LogicTreeDialog extends TitleAreaDialog {

	private ObjectTree tree;
	private ILogic root;
	private String title;

	public LogicTreeDialog(String title, ILogic root) {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL | SWT.RESIZE | getDefaultOrientation());
		this.title = title;
		this.root = root;
		this.open();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	}

	@Override
	protected Control createContents(Composite parent) {
		parent.getShell().setSize(600, 400);
		PositionUtillity.center(parent.getShell());
		return super.createContents(parent);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		this.setTitle(title);
		this.setMessage(Messages.LogicTreeDialog_0);
		tree = new ObjectTree(parent, ObjectTree.NODES_LINES | ObjectTree.BORDER);
		tree.setExpandAll(true);
		tree.setInputAsRoot(false);
		tree.setInput(new LogicNodeForm(root));
		return tree.getControl();
	}
	private class LogicNodeForm extends AbstractNodeForm<ILogic> {

		private Image image;

		public LogicNodeForm(ILogic node) {
			this.input = node;
			if (input.check()) {
				image = VidgeResources.getInstance().getImage(SharedImages.TICK);
			} else {
				image = VidgeResources.getInstance().getImage(SharedImages.DELETE);
			}
			if (input instanceof ICompositeLogic) {
				ICompositeLogic compositeLogic = (ICompositeLogic) input;
				for (ILogic logicNode : compositeLogic.getChildren()) {
					LogicNodeForm logicNodeForm = new LogicNodeForm(logicNode);
					addChild(logicNodeForm);
				}
			}
		}

		@Override
		public Image getImage() {
			return image;
		}

		@Override
		public void showDetails() {
			Details details = new Details(Messages.LogicTreeDialog_1);
			try {
				details.putProperty(Messages.LogicTreeDialog_4, input.getDescription());
				details.putProperty(Messages.LogicTreeDialog_2, input.check());
				details.putProperty(Messages.LogicTreeDialog_3, input.getDetailedDescription());
				if (input.isImpossibleChecking()) {
					String cause = input.getImpossibilityCause();
					if (StringUtils.isBlank(cause)) {
						cause = Messages.LogicTreeDialog_6;
					}
					details.putProperty(Messages.LogicTreeDialog_5, cause);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			details.show();
		}
	}
}
