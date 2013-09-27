package org.vidge.explorer.def;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.vidge.explorer.EntityExplorer;
import org.vidge.inface.IEntityExplorer;
import org.vidge.inface.IPropertyExplorer;

public class FontExplorer extends EntityExplorer {

	private FontFieldExplorer expStr;
	private Font color;

	public FontExplorer() {
	}

	public FontExplorer(Font obj) {
		this.color = obj;
		explore(obj);
	}

	public void explore(Object objIn) {
		super.explore(objIn);
		expStr = new FontFieldExplorer((Font) objIn, "Font");
		addProperty(expStr);
	}

	@Override
	public List<IPropertyExplorer> getPropertyList() {
		ArrayList<IPropertyExplorer> arrayList = new ArrayList<IPropertyExplorer>();
		arrayList.add(expStr);
		return arrayList;
	}

	@Override
	public Object createInput() {
		return Display.getCurrent().getSystemFont();
	}

	@Override
	public Object getInput() {
		return expStr.getValue();
	}

	@Override
	public IEntityExplorer copy() {
		return new FontExplorer(color);
	}
}
