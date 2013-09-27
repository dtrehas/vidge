package org.vidge.explorer.def;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.vidge.explorer.EntityExplorer;
import org.vidge.inface.IEntityExplorer;
import org.vidge.inface.IPropertyExplorer;

public class ColorExplorer extends EntityExplorer {

	private ColorFieldExplorer expStr;
	private Color color;

	public ColorExplorer() {
		expStr = new ColorFieldExplorer(null, "Color");
	}

	public ColorExplorer(Color obj) {
		this.color = obj;
		explore(obj);
	}

	public void explore(Object objIn) {
		super.explore(objIn);
		expStr = new ColorFieldExplorer((Color) objIn, "Color");
		addProperty(expStr);
	}

	@Override
	public Object createInput() {
		return Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
	}

	@Override
	public List<IPropertyExplorer> getPropertyList() {
		ArrayList<IPropertyExplorer> arrayList = new ArrayList<IPropertyExplorer>();
		arrayList.add(expStr);
		return arrayList;
	}

	@Override
	public Object getInput() {
		return expStr.getValue();
	}

	@Override
	public IEntityExplorer copy() {
		return new ColorExplorer(color);
	}
}
