package org.vidge.controls.tree;

import org.vidge.inface.IForm;
import org.vidge.inface.INodeForm;
import org.vidge.util.VisualProperty;


public class NodeTableForm implements IForm<INodeForm>{

	private INodeForm input;

	@VisualProperty(order = 1)
	public String getName() {
		return input.toString();
	}
	
	@VisualProperty(order = 2,width=30)
	public int getLevel() {
		return input.getLevel();
	}
	
	public INodeForm getInput() {
		return input;
	}

	public void setInput(INodeForm input) {
		this.input = input;
	}

}


