package org.vidge.controls.tree;

import org.vidge.inface.IForm;
import org.vidge.util.VisualProperty;

public class ObjectTreeCustomizeForm implements IForm<ObjectTree> {

	private ObjectTree input;

	public ObjectTreeCustomizeForm() {
	}

	public ObjectTreeCustomizeForm(ObjectTree input) {
		this.input = input;
	}

	@VisualProperty
	public boolean getExpandAll() {
		return input.isExpandAll();
	}

	@VisualProperty
	public boolean getInputAsRoot() {
		return input.isInputAsRoot();
	}

	public void setExpandAll(boolean expandAll) {
		input.setExpandAll(expandAll);
	}

	public void setInputAsRoot(boolean inputAsRoot) {
		input.setInputAsRoot(inputAsRoot);
	}

	public void setInput(ObjectTree input) {
		this.input = input;
	}

	public ObjectTree getInput() {
		return input;
	}

}
