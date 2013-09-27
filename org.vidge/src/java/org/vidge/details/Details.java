package org.vidge.details;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.graphics.Font;
import org.langcom.locale.EnumLocalizer;
import org.vidge.VidgeSettings;
import org.vidge.inface.IEntityExplorer;

public class Details {

	private String title;
	private List<DetailProperty> properties = new ArrayList<DetailProperty>();
	private IEntityExplorer tableExplorer;
	private List<Details> childList = new ArrayList<Details>();
	private List dataList;
	private Action[] headerActions;

	public Details(String title) {
		this.title = title;
	}

	public Details(String title, Action... actions) {
		this(title);
		headerActions = actions;
	}

	public Details(String title, IEntityExplorer tableExplorer, List dataList) {
		this(title);
		this.tableExplorer = tableExplorer;
		this.dataList = dataList;
	}

	public void putLine() {
		properties.add(new DetailProperty(DetailPropertyType.LINE));
	}

	public void putList(String name, Collection values) {
		properties.add(new DetailProperty(name, ""));
		for (Object obj : values) {
			properties.add(new DetailProperty("", checkValue(obj)));
		}
	}

	public void putProperty(String name, Object value) {
		if (value instanceof Collection) {
			if (((Collection) value).size() > 0) {
				Object first = ((Collection) value).toArray()[0];
				try {
					if (first.getClass().getMethod("getParent") != null) {
						properties.add(new DetailProperty(name, value, DetailPropertyType.TREE));
						return;
					}
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
			}
		}
		properties.add(new DetailProperty(name, checkValue(value)));
	}

	public void putProperty(String name, Object value, Font font) {
		properties.add(new DetailProperty(name, checkValue(value), font));
	}

	public void putImage(String name, Object value) {
		properties.add(new DetailProperty(name, value, DetailPropertyType.IMAGE));
	}

	@SuppressWarnings("rawtypes")
	private Object checkValue(Object value) {
		if (value == null) {
			value = ""; //$NON-NLS-1$
		} else if (value instanceof Date) {
			value = VidgeSettings.formatDateTime((Date) value);
		} else if (value instanceof Enum) {
			value = EnumLocalizer.getLocalized((Enum) value);
		} else if (value instanceof Boolean) {
			value = Messages.toLocalizedString((Boolean) value);
		}
		return value;
	}

	public void putProperty(String name, Object value, IAction action) {
		if (value instanceof Collection) {
			properties.add(new DetailProperty(name, ""));
			for (Object obj : (Collection) value) {
				properties.add(new DetailProperty("", checkValue(obj), action));
			}
		} else {
			properties.add(new DetailProperty(name, checkValue(value), action));
		}
	}

	public String getTitle() {
		return title;
	}

	List<DetailProperty> getProperties() {
		return properties;
	}

	public void show() {
		DetailsView.setDetails(this);
	}

	public static void clear() {
		DetailsView.clear();
	}

	public void putProperty(String line) {
		properties.add(new DetailProperty(line, line));
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List getDataList() {
		return dataList;
	}

	public IEntityExplorer getTableExplorer() {
		return tableExplorer;
	}

	public void addChild(Details details) {
		childList.add(details);
	}

	public List<Details> getChildList() {
		return childList;
	}

	public Action[] getHeaderActions() {
		return headerActions;
	}

	public void setHeaderActions(Action[] headerActions) {
		this.headerActions = headerActions;
	}
}
