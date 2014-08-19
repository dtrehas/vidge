package org.vidge.explorer.def;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.vidge.explorer.EntityExplorer;
import org.vidge.inface.IEntityExplorer;
import org.vidge.inface.IPropertyExplorer;

public class FileExplorer extends EntityExplorer {

	private FileFieldExplorer expStr;
	private File file;

	public FileExplorer() {
	}

	public FileExplorer(File obj) {
		this.file = obj;
		explore(obj);
	}

	public void explore(Object objIn) {
		super.explore(objIn);
		expStr = new FileFieldExplorer((File) objIn, "File");
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
		return new File("/");
	}

	@Override
	public Object getInput() {
		return expStr.getValue();
	}

	@Override
	public IEntityExplorer copy() {
		return new FileExplorer(file);
	}
}
