package org.vidge.controls.chooser;

import org.langcom.locale.EnumLocalizer;
import org.vidge.SharedImages;
import org.vidge.controls.editor.ExplorerContentTYpe;
import org.vidge.inface.IPropertyExplorer;
import org.vidge.util.StringUtil;
import org.vidge.util.TypeUtil;

public class VColumn<T> {

	private static final String NO = "0";
	private static final String YES = "1";
	public static String defaultFilterMask = "*";
	IPropertyExplorer explorer;
	SharedImages currentImage = SharedImages.TABLE_HEADER_DEFAULT;
	final VTable<T> table;
	private boolean filtered = false;
	private String filterMask = StringUtil.NN;
	private boolean lazy = false;

	public VColumn(VTable<T> table, IPropertyExplorer explorer) {
		this.table = table;
		this.explorer = explorer;
		if (explorer != null && explorer.getContentType() != null) {
			lazy = (explorer.getContentType().equals(ExplorerContentTYpe.ARRAY) || explorer.getContentType().equals(ExplorerContentTYpe.LIST) || explorer.getContentType().equals(ExplorerContentTYpe.MAP));
		}
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
		if (lazy) {
			return StringUtil.DDD;
		}
		try {
			if (explorer != null) {
				Object propertyValue = explorer.getValue(obj);
				if (propertyValue instanceof Enum) {
					objString = EnumLocalizer.getLocalized((Enum<?>) propertyValue);
				} else if (propertyValue instanceof Boolean) {
					objString = ((Boolean) propertyValue) ? YES : NO;
				} else {
					objString = propertyValue == null ? StringUtil.NN : propertyValue.toString();
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

	public IPropertyExplorer getExplorer() {
		return explorer;
	}
}