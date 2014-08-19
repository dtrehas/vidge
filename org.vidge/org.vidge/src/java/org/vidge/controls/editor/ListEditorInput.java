package org.vidge.controls.editor;

import org.vidge.controls.TreePanel;
import org.vidge.inface.IEntityExplorer;
import org.vidge.util.CommonEditorInput;

public class ListEditorInput<IPropertyExplorer> extends CommonEditorInput<IPropertyExplorer> {

	private final IEntityExplorer entityExplorer;
	private final String title;
	private final TreePanel treePanel;

	public ListEditorInput(String title, IEntityExplorer entityExplorer, IPropertyExplorer propertyExplorer, TreePanel treePanel) {
		super(propertyExplorer);
		this.title = title;
		this.entityExplorer = entityExplorer;
		this.treePanel = treePanel;
	}

	public IEntityExplorer getEntityExplorer() {
		return entityExplorer;
	}

	@Override
	public String getName() {
		return title;
	}

	public TreePanel getTreePanel() {
		return treePanel;
	}
}
