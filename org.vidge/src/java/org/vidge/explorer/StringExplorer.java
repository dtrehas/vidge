package org.vidge.explorer;

import org.langcom.locale.LocalizedString;
import org.vidge.inface.IEntityExplorer;

public class StringExplorer extends EntityExplorer {

	private StringFieldExplorer expStr;

	public StringExplorer() {
	}

	public StringExplorer(String obj) {
		explore(obj);
	}

	public void explore(Object objIn) {
		super.explore(objIn);
		expStr = new StringFieldExplorer(objIn.toString());
		addProperty(expStr);
	}

	@Override
	public Object createInput() {
		return "...";
	}

	@Override
	public Object getInput() {
		return expStr.getValue();
	}

	@Override
	public String getLabel() {
		if (LocalizedString.class.isAssignableFrom(input.getClass())) {
			return ((LocalizedString) input).getDefaultLocalString();
		}
		return super.getLabel();
	}

	@Override
	public IEntityExplorer copy() {
		return new StringExplorer("");
	}
}
