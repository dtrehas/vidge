package org.vidge.explorer.def;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.vidge.explorer.EntityExplorer;
import org.vidge.inface.IEntityExplorer;
import org.vidge.inface.IPropertyExplorer;

public class ImageExplorer extends EntityExplorer {

	private ImageFieldExplorer expStr;
	private Image color;

	public ImageExplorer() {
	}

	public ImageExplorer(Image obj) {
		this.color = obj;
		explore(obj);
	}

	public void explore(Object objIn) {
		super.explore(objIn);
		expStr = new ImageFieldExplorer((Image) objIn, "Image");
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
		return new Image(Display.getCurrent(), new Rectangle(0, 0, 88, 88));
	}

	@Override
	public Object getInput() {
		return expStr.getValue();
	}

	@Override
	public IEntityExplorer copy() {
		return new ImageExplorer(color);
	}
}
