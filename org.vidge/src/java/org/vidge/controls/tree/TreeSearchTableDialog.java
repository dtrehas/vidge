package org.vidge.controls.tree;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.vidge.VidgeResources;
import org.vidge.SharedImages;
import org.vidge.controls.chooser.ObjectChooser;
import org.vidge.inface.IForm;
import org.vidge.inface.INodeForm;
import org.vidge.util.PositionUtillity;

public class TreeSearchTableDialog extends Dialog {

	private static final String COLON = " : ";//$NON-NLS-1$
	private static final int border = 0;
	private static final String SPACE = "  "; //$NON-NLS-1$
	private Text searchText;
	private ObjectTree tree;
	private ObjectChooser chooser;
	private List<INodeForm> objectList = new ArrayList<INodeForm>();

	public TreeSearchTableDialog(Shell shell, ObjectTree tree) {
		super(shell);
		this.tree = tree;
		setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.BORDER | SWT.ON_TOP | SWT.RESIZE | getDefaultOrientation());
	}

	@Override
	protected Control createContents(Composite parent) {
		parent.setSize(500, 450);
		PositionUtillity.center(parent);
		return super.createContents(parent);
	}

	protected Button createButton(Composite parent, Image image) {
		Button button = new Button(parent, SWT.PUSH);
		button.setImage(image);
		button.setLayoutData(new GridData());
		return button;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginLeft = border;
		layout.marginRight = border;
		layout.marginBottom = border;
		layout.marginTop = border;
		parent.setLayout(layout);
		Button button = createButton(parent, VidgeResources.getInstance().getImage(SharedImages.SEARCH));
		button.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				tree.searchNodes(searchText.getText());
				setSearchLabelText();
			}
		});
		button = createButton(parent, VidgeResources.getInstance().getImage(SharedImages.CANCEL));
		button.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				tree.clearFound();
				TreeSearchTableDialog.this.close();
			}
		});
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 5;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		setSearchLabelText();
		setSearchLabelText();
		Label searchLabel = new Label(composite, SWT.WRAP);
		searchLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchLabel.setText(Messages.TreeSearchTableDialog_0);
		searchText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		searchText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchText.setText(tree.getTreeSearchString());
		chooser = new ObjectChooser(
			parent,
			ObjectChooser.BORDER,
			((Class<? extends IForm<INodeForm>>) NodeTableForm.class),
			objectList);
		chooser.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		chooser.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				tree.setSelection((INodeForm) chooser.getSelection());
			}
		});
		return composite;
	}

	private void setSearchLabelText() {
		if ((tree != null) && (tree.getNodeIterator() != null)) {
			this.getShell().setText(
				Messages.TreeSearchDialog_0
					+ COLON
					+ tree.getTreeSearchString()
					+ COLON
					+ SPACE
					+ tree.getFoundCount());
		}
	}

	public void setFound(List<INodeForm> found) {
		objectList = found;
		chooser.setInput(objectList);
		chooser.refresh();
		setSearchLabelText();
	}
}
