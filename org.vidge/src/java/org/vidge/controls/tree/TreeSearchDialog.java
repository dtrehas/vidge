package org.vidge.controls.tree;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
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
import org.vidge.SharedImages;
import org.vidge.VidgeResources;
import org.vidge.util.PositionUtillity;

public class TreeSearchDialog extends Dialog {

	private static final String COLON = " : ";//$NON-NLS-1$
	private static final int border = 0;
	private static final String SPACE = "  "; //$NON-NLS-1$
	private Text searchText;
	private Label searchLabel;
	private ObjectTree tree;

	public TreeSearchDialog(Shell shell, ObjectTree tree) {
		super(shell);
		this.tree = tree;
		setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.BORDER | SWT.ON_TOP | SWT.RESIZE | getDefaultOrientation());
	}

	@Override
	protected Control createContents(Composite parent) {
		// parent.setSize(400,200);
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
		layout.numColumns = 6;
		layout.marginLeft = border;
		layout.marginRight = border;
		layout.marginBottom = border;
		layout.marginTop = border;
		parent.setLayout(layout);
		Button button = createButton(parent, VidgeResources.getInstance().getImage(SharedImages.FIRST));
		button.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				tree.showFirst();
				setSearchLabelText();
			}

		});
		button = createButton(parent, VidgeResources.getInstance().getImage(SharedImages.PREVIOUS));
		button.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				tree.showPrevious();
				setSearchLabelText();
			}

		});
		button = createButton(parent, VidgeResources.getInstance().getImage(SharedImages.NEXT));
		button.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				tree.showNext();
				setSearchLabelText();
			}

		});
		button = createButton(parent, VidgeResources.getInstance().getImage(SharedImages.LAST));
		button.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				tree.showLast();
				setSearchLabelText();
			}

		});
		button = createButton(parent, VidgeResources.getInstance().getImage(SharedImages.SEARCH));
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
				TreeSearchDialog.this.close();
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
		searchLabel = new Label(composite, SWT.NONE);
		setSearchLabelText();
		searchLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		searchText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchText.setText(tree.getTreeSearchString());
		searchText.addKeyListener(new KeyAdapter() {

			public void keyReleased(KeyEvent e) {
				TreeSearchDialog.this.getShell().setSize(
					TreeSearchDialog.this.getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT));
			}

		});
		return composite;
	}

	private void setSearchLabelText() {
		this.getShell().setText(Messages.TreeSearchDialog_0 + COLON + tree.getTreeSearchString());
		searchLabel.setText(Messages.TreeSearchDialog_0
			+ COLON
			+ SPACE
			+ tree.getFoundCount()
			+ SPACE
			+ Messages.TreeSearchDialog_1
			+ COLON
			+ SPACE
			+ (tree.getFoundCount() > 0 ? (tree.getCurrent() + 1) : "0"));
	}

}
