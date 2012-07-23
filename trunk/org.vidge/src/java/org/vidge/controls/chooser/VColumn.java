package org.vidge.controls.chooser;

import org.langcom.locale.EnumLocalizer;
import org.vidge.SharedImages;
import org.vidge.inface.IPropertyExplorer;
import org.vidge.util.StringUtil;
import org.vidge.util.TypeUtil;

public class VColumn<T> {

	public static String defaultFilterMask = "*";
	IPropertyExplorer explorer;
	SharedImages currentImage = SharedImages.TABLE_HEADER_DEFAULT;
	final VTable<T> table;
	private boolean filtered = false;
	private String filterMask = "";

	public VColumn(VTable<T> table, IPropertyExplorer explorer) {
		this.table = table;
		this.explorer = explorer;
	}

	public boolean isNumeric() {
		if (explorer == null) {
			return true;
		}
		return TypeUtil.isNumeric(explorer.getPropertyClass());
	}

	public void setDefault() {
		currentImage = SharedImages.TABLE_HEADER_DEFAULT;
	}

	public void toggle() {
		switch (currentImage) {
			case TABLE_HEADER_DEFAULT:
				currentImage = SharedImages.TABLE_HEADER_DOWN;
				table.sortList(false, explorer);
				break;
			case TABLE_HEADER_DOWN:
				currentImage = SharedImages.TABLE_HEADER_UP;
				table.sortList(true, explorer);
				break;
			default:
				currentImage = SharedImages.TABLE_HEADER_DEFAULT;
				table.defaultSort();
				break;
		}
	}

	public String getCellText(Object obj) {
		String objString = StringUtil.NN;
		try {
			if (explorer != null) {
				Object propertyValue = explorer.getValue(obj);
				if (propertyValue instanceof Enum) {
					objString = EnumLocalizer.getLocalized((Enum<?>) propertyValue);
				} else if (propertyValue instanceof Boolean) {
					objString = Messages.toLocalizedString((Boolean) propertyValue);
				} else {
					objString = propertyValue == null ? "" : propertyValue.toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return objString;
	}

	public boolean isFiltered() {
		return filtered;
	}

	public void setFiltered(boolean filtered) {
		this.filtered = filtered;
		table.refresh();
	}

	public void setFilterMask(String filterMask) {
		this.filterMask = filterMask;
		table.refresh();
	}

	public String getFilterMask() {
		return filterMask;
	}
}