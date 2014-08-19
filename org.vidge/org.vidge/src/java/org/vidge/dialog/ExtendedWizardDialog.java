package org.vidge.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.vidge.util.PositionUtillity;

public class ExtendedWizardDialog extends WizardDialog {

	private Composite buttonTabComposite;
	private final IWizard newWizard;
	private TabButton[] tabs;
	private TabButton selectedTab;
	private int style;
	private Button closeButton;
	private Point size;
	private Point offSet;

	public ExtendedWizardDialog(Shell parentShell, IWizard nWizard) {
		super(parentShell, nWizard);
		this.newWizard = nWizard;
		setShellStyle(SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE);
	}

	public ExtendedWizardDialog(Shell parentShell, IWizard nWizard, Point size) {
		super(parentShell, nWizard);
		this.newWizard = nWizard;
		this.size = size;
		setShellStyle(SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE);
	}

	public ExtendedWizardDialog(Shell parentShell, IWizard nWizard, Point size, Point offSet) {
		super(parentShell, nWizard);
		this.newWizard = nWizard;
		this.size = size;
		this.offSet = offSet;
		setShellStyle(SWT.CLOSE | SWT.MIN | SWT.MAX | SWT.RESIZE);
	}

	public ExtendedWizardDialog(Shell parentShell, IWizard nWizard, int style) {
		this(parentShell, nWizard);
		this.style = style;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		if ((style & SWT.CLOSE) != 0) {
			closeButton = createButton(parent, IDialogConstants.FINISH_ID, IDialogConstants.CLOSE_LABEL, true);
			closeButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					close();
				}
			});
		} else {
			super.createButtonsForButtonBar(parent);
		}
	}

	@Override
	protected Control createContents(Composite parent) {
		if (size == null) {
			parent.setSize(720, 450);
		} else {
			parent.setSize(size.x, size.y);
		}
		PositionUtillity.center(parent);
		if (offSet != null) {
			Rectangle bounds = parent.getBounds();
			bounds.x = bounds.x + offSet.x;
			bounds.y = bounds.y + offSet.y;
			parent.setBounds(bounds);
		}
		Control control = super.createContents(parent);
		return control;
	}

	private void populateButtonTab() {
		IWizardPage[] pages = newWizard.getPages();
		if (pages.length > 1) {
			tabs = new TabButton[pages.length];
			TabButton prevButton = null;
			for (int i = 0; i < pages.length; i++) {
				IWizardPage page = pages[i];
				tabs[i] = new TabButton(page, i, prevButton);
				tabs[i].setText("  " + page.getName()); //$NON-NLS-1$
				tabs[i].setToolTipText(page.getName());
				tabs[i].checkEnabled();
				prevButton = tabs[i];
			}
			buttonTabComposite.pack();
		}
	}

	public void setTabText(IWizardPage page, String text) {
		for (TabButton button : tabs) {
			if (button.page.equals(page)) {
				button.setText(text);
				return;
			}
		}
	}

	private void checkTabs(Button selected) {
		for (TabButton b : tabs) {
			b.setSelection(b.button.equals(selected));
		}
	}

	@Override
	public void updateButtons() {
		if (tabs != null) {
			for (TabButton b : tabs) {
				b.checkEnabled();
			}
		}
		try {
			super.updateButtons();
		} catch (Exception e) {
		}
	}
	private class TabButton {

		private final Button button;
		private final IWizardPage page;
		private final int index;
		private final TabButton prevButton;

		public TabButton(final IWizardPage page, int i, TabButton prevButton) {
			this.page = page;
			this.index = i;
			this.prevButton = prevButton;
			button = new Button(buttonTabComposite, SWT.FLAT | SWT.TOGGLE);
			ExtendedWizardDialog.this.addPageChangedListener(new IPageChangedListener() {

				@Override
				public void pageChanged(PageChangedEvent event) {
					if (event.getSelectedPage().equals(page)) {
						checkTabs(button);
					}
				}
			});
			button.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					selectButton();
				}
			});
			button.addKeyListener(new KeyAdapter() {

				@Override
				public void keyPressed(KeyEvent e) {
					if (e.keyCode == 32) {
						checkTabs(button);
						ExtendedWizardDialog.this.showPage(page);
					}
				}
			});
			if (index == 0) {
				button.setSelection(true);
				selectedTab = this;
			}
		}

		private void selectButton() {
			if (button.getSelection()) {
				checkTabs(button);
				ExtendedWizardDialog.this.showPage(page);
			}
		}

		public void checkEnabled() {
			if (index != 0) {
				button.setEnabled(page.isPageComplete() && ((prevButton != null) && (prevButton.button.isEnabled()))
					&& ((selectedTab.page.getNextPage() != null) ? selectedTab.page.canFlipToNextPage() : true));
			}
		}

		public void setSelection(boolean b) {
			button.setSelection(b);
			if (b) {
				selectedTab = this;
			}
		}

		public void setToolTipText(String name) {
			button.setToolTipText(name);
		}

		public void setText(String name) {
			button.setText(name);
		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		if ((style & SWT.VERTICAL) != 0) {
			parent.setLayout(new GridLayout(2, false));
			buttonTabComposite = new Composite(parent, SWT.NONE);
			buttonTabComposite.setLayoutData(new GridData(SWT.BEGINNING, SWT.FILL, true, true));
			FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
			fillLayout.marginWidth = 10;
			fillLayout.marginHeight = 5;
			fillLayout.spacing = 1;
			buttonTabComposite.setLayout(fillLayout);
		} else {
			buttonTabComposite = new Composite(parent, SWT.NONE);
			buttonTabComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
			FillLayout fillLayout = new FillLayout(SWT.HORIZONTAL);
			fillLayout.marginWidth = 10;
			fillLayout.marginHeight = 5;
			fillLayout.spacing = 1;
			buttonTabComposite.setLayout(fillLayout);
		}
		populateButtonTab();
		return super.createDialogArea(parent);
	}

	@Override
	protected void finishPressed() {
		// if (showTotal()) {
		super.finishPressed();
		// }
	}
}
