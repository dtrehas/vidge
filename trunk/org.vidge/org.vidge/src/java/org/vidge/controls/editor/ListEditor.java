package org.vidge.controls.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.vidge.controls.TreePanel;
import org.vidge.dialog.ListPanel;
import org.vidge.inface.IChangeListener;
import org.vidge.inface.IEntityExplorer;
import org.vidge.inface.IPropertyExplorer;
import org.vidge.util.StringUtil;

public class ListEditor extends EditorPart {

	public static final String ID = "org.vidge.controls.editor.ListEditor";

	public ListEditor() {
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(input.getName());
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		IEntityExplorer explorer = ((ListEditorInput) getEditorInput()).getEntityExplorer();
		IPropertyExplorer propertyExplorer = ((ListEditorInput<IPropertyExplorer>) getEditorInput()).getInput();
		final TreePanel panel = ((ListEditorInput) getEditorInput()).getTreePanel();
		final ListPanel listPanel = new ListPanel(parent, explorer, propertyExplorer);
		listPanel.getSection().setText(StringUtil.NN);
		listPanel.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		listPanel.addChangeListener(new IChangeListener() {

			@Override
			public void changed() {
				panel.refresh();
			}
		});
	}

	@Override
	public void setFocus() {
	}
}
