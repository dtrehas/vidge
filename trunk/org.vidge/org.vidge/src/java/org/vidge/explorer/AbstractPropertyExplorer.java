package org.vidge.explorer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.vidge.controls.editor.ExplorerContentTYpe;
import org.vidge.controls.tree.hierarchy.IHierarchyProvider;
import org.vidge.form.IFormObjectWizard;
import org.vidge.form.validator.IValidator;
import org.vidge.form.validator.ValidatorRegistry;
import org.vidge.inface.IEntityExplorer;
import org.vidge.inface.IPropertyExplorer;
import org.vidge.util.StringUtil;
import org.vidge.util.TypeUtil;
import org.vidge.util.VisualControlType;

@SuppressWarnings({
		"rawtypes", "unchecked"
})
public abstract class AbstractPropertyExplorer implements IPropertyExplorer {

	protected static final String DOT = ".";
	protected static final String REQUIRED = " *";
	protected static final String MESSAGES = ".messages";
	protected static final String PROPERTY_DESCRIPTION_POSTFIX = "description";
	protected static final String PROPERTY_LABEL_POSTFIX = "label";
	protected String description = StringUtil.NN; //$NON-NLS-1$
	protected String label;
	protected Object source;
	private boolean allowChild = true;
	private Object mockValue, oldValue;
	private boolean wrapped = false;
	private boolean flushable = false;
	private IEntityExplorer entityExplorer;;
	protected ExplorerContentTYpe contentType;

	protected abstract Object getValueInt();

	protected abstract void setValueInt(Object value);

	public AbstractPropertyExplorer(boolean allowChild) {
		this.allowChild = allowChild;
	}

	@Override
	public void setEntityExplorer(IEntityExplorer entityExplorer) {
		this.entityExplorer = entityExplorer;
	}

	@Override
	public Object getValue() {
		if (!wrapped) {
			mockValue = getValueInt();
			oldValue = mockValue;
			wrapped = true;
		} else if (!flushable) {
			mockValue = getValueInt();
		}
		return mockValue;
	}

	@Override
	public void setValue(Object value) {
		mockValue = value;
		if (!flushable) {
			setValueInt(mockValue);
		}
	}

	@Override
	public ExplorerContentTYpe getContentType() {
		return contentType;
	}

	protected ExplorerContentTYpe getType(Class<?> propertyClass) {
		if (List.class.isAssignableFrom(propertyClass)) {
			return ExplorerContentTYpe.LIST;
		} else if (Map.class.isAssignableFrom(propertyClass)) {
			return ExplorerContentTYpe.MAP;
		} else if (propertyClass.isArray()) {
			return ExplorerContentTYpe.ARRAY;
		} else if (TypeUtil.isPrimitive(propertyClass)) {
			return ExplorerContentTYpe.PRIMITIVE;
		} else {
			return ExplorerContentTYpe.OBJECT;
		}
	}

	@Override
	public void flush() {
		if (flushable) {
			if (oldValue == null) {
				if (mockValue == null) {
				} else {
					setValueInt(mockValue);
				}
			} else {
				if (mockValue == null) {
					setValueInt(mockValue);
				} else {
					if (!mockValue.equals(oldValue)) {
						setValueInt(mockValue);
					}
				}
			}
		}
	}

	@Override
	public void setFlushable(boolean flushable) {
		this.flushable = flushable;
	}

	@SuppressWarnings("nls")
	public String getLabel() {
		if (StringUtil.isEmpty(label)) {
			label = StringUtil.capitalize(getPropertyName());
		}
		return label;
	}

	public String getDescription() {
		return description;
	}

	public int compareTo(IPropertyExplorer o) {
		int compareTo = Double.compare(getOrder(), o.getOrder());
		if (compareTo == 0) {
			compareTo = getLabel().compareTo(o.getLabel());
		}
		return compareTo;
	}

	public double getOrder() {
		return 0;
	}

	public int getVisualAreaWidth() {
		return -1;
	}

	public int getVisualAreaHeight() {
		return -1;
	}

	public int getImageWidth() {
		return -1;
	}

	public int getImageHeight() {
		return -1;
	}

	public VisualControlType getVisualControlType() {
		return VisualControlType.DEFAULT;
	}

	public Object getSource() {
		return source;
	}

	public boolean isReadOnly() {
		return false;
	}

	public IValidator<?> getValidator() {
		return ValidatorRegistry.get(getPropertyClass().getName());
	}

	public List<?> getValidValues() {
		if (getPropertyClass().isEnum()) {
			List list = new ArrayList<Object>();
			Object[] enumConstants = getPropertyClass().getEnumConstants();
			for (int a = 0; a < enumConstants.length; a++) {
				list.add(enumConstants[a]);
			}
			return list;
		}
		return null;
	}

	@Override
	public boolean isFlushable() {
		return flushable;
	}

	public IHierarchyProvider<?> getHierarchyProvider() {
		return null;
	}

	public Image getImage(Object element) {
		return null;
	}

	public Color getBackground() {
		return null;
	}

	public IFormObjectWizard getWizard() {
		return null;
	}

	@Override
	public boolean hasValidValues() {
		return false;
	}

	@Override
	public boolean hasHierarchyProvider() {
		return false;
	}

	@Override
	public void setChecked(Boolean selection) {
	}

	public boolean getChecked() {
		return false;
	}

	@Override
	public boolean isNeedTableEditor() {
		return false;
	}

	@Override
	public Type getPropertyType() {
		return null;
	}

	/**
	 * if source have yet saved into persistent storage
	 */
	@Override
	public boolean isAllowChild() {
		return allowChild;
	}

	public void setAllowChild(boolean allowChild) {
		this.allowChild = allowChild;
	}

	@Override
	public boolean isEmbedded() {
		return false;
	}

	@Override
	public IEntityExplorer getEntityExplorer() {
		return entityExplorer;
	}

	@Override
	public String toString() {
		return label;
	}

	@Override
	public Control getEditor(Control parent) {
		return null;
	}
}
