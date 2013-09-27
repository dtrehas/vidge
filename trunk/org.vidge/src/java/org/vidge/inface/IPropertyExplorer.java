package org.vidge.inface;

import java.lang.reflect.Type;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.vidge.controls.tree.hierarchy.IHierarchyProvider;
import org.vidge.form.IFormObjectWizard;
import org.vidge.form.validator.IValidator;
import org.vidge.util.VisualControlType;

public interface IPropertyExplorer extends Comparable<IPropertyExplorer> {

	public String getPropertyName();

	public Class<?> getPropertyClass();

	public Type getPropertyType();

	public Object getValue();

	public Object getValue(Object patient);

	public void setValue(Object value);

	public String getLabel();

	public String getDescription();

	public boolean isReadOnly();

	public boolean hasValidValues();

	public boolean hasHierarchyProvider();

	public IValidator<?> getValidator();

	public List<?> getValidValues();

	public IHierarchyProvider<?> getHierarchyProvider();

	public int getVisualAreaWidth();

	public int getVisualAreaHeight();

	public VisualControlType getVisualControlType();

	public int getOrder();

	public Image getImage(Object element);

	public Color getBackground();

	public IFormObjectWizard getWizard();

	public void setChecked(Boolean selection);

	public boolean getChecked();

	public boolean isAllowChild();

	public boolean isEmbedded();

	public void flush();

	public boolean isFlushable();

	public void setFlushable(boolean flushable);

	public int getImageWidth();

	public int getImageHeight();

	public void setEntityExplorer(IEntityExplorer entityExplorer);

	public IEntityExplorer getEntityExplorer();
}
