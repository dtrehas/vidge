package org.vidge.controls;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.EditorPart;

public class SimpleEditor extends EditorPart {

	private CTabFolder container;
	private FormToolkit toolkit;

	protected void addPages() {
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
	}

	protected Composite getContainer() {
		return container;
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	public Shell getShell() {
		return this.getSite().getShell();
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite pageContainer = createPageContainer(parent);
		this.container = createContainer(pageContainer);
		addPages();
		if (getActivePage() == -1) {
			setActivePage(0);
		}
	}

	protected int getActivePage() {
		CTabFolder tabFolder = getTabFolder();
		if (tabFolder != null && !tabFolder.isDisposed()) {
			return tabFolder.getSelectionIndex();
		}
		return -1;
	}

	protected void setActivePage(int pageIndex) {
		Assert.isTrue(pageIndex >= 0 && pageIndex < getPageCount());
		getTabFolder().setSelection(pageIndex);
	}

	protected Composite createPageContainer(Composite parent) {
		toolkit = createToolkit(parent.getDisplay());
		return parent;
	}

	protected FormToolkit createToolkit(Display display) {
		return new FormToolkit(display);
	}

	public int addPage(Control control) {
		int index = getPageCount();
		addPage(index, control);
		return index;
	}

	public CTabItem addPageInTab(Control control) {
		int index = getPageCount();
		return createItem(index, control);
	}

	public void addPage(int index, Control control) {
		createItem(index, control);
	}

	private CTabItem createItem(int index, Control control) {
		CTabItem item = new CTabItem(getTabFolder(), SWT.NONE, index);
		item.setControl(control);
		return item;
	}

	protected int getPageCount() {
		CTabFolder folder = getTabFolder();
		if (folder != null && !folder.isDisposed()) {
			return folder.getItemCount();
		}
		return 0;
	}

	private CTabFolder getTabFolder() {
		return container;
	}

	private CTabFolder createContainer(Composite parent) {
		parent.setLayout(new FillLayout());
		final CTabFolder newContainer = new CTabFolder(parent, SWT.BOTTOM | SWT.FLAT);
		newContainer.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				// int newPageIndex = newContainer.indexOf((CTabItem) e.item);
			}
		});
		return newContainer;
	}

	public void close(final boolean save) {
		Display display = getSite().getShell().getDisplay();
		display.asyncExec(new Runnable() {

			public void run() {
				if (toolkit != null) {
					getSite().getPage().closeEditor(SimpleEditor.this, save);
				}
			}
		});
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	@Override
	public void doSaveAs() {
	}

	public void refresh() {
	}
}
