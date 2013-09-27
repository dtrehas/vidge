package org.vidge.explorer.def;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.vidge.explorer.EntityExplorer;
import org.vidge.inface.IEntityExplorer;
import org.vidge.inface.IPropertyExplorer;

public class DateExplorer extends EntityExplorer {

	private DateFieldExplorer expStr;
	private Date date;

	public DateExplorer() {
	}

	public DateExplorer(Date obj) {
		this.date = obj;
		explore(obj);
	}

	public void explore(Object objIn) {
		super.explore(objIn);
		expStr = new DateFieldExplorer((Date) objIn, "Date");
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
		return new Date();
	}

	@Override
	public Object getInput() {
		return expStr.getValue();
	}

	@Override
	public IEntityExplorer copy() {
		return new DateExplorer(date);
	}
}
