package org.vidge.inface;

import java.util.List;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

@SuppressWarnings("rawtypes")
public interface IEntityExplorer extends IFormInputChangeListener, IFormDataProvider {

	public void explore(Object input);

	public IEntityExplorer copy();

	public void clear();

	public List<IPropertyExplorer> getPropertyList();

	public Image getImage(Object element, int index);

	public Color getBackground(Object element);

	public Color getForeground(Object element);

	public void setInput(Object input);

	public Object getInput();

	public Class<?> getInputClass();

	public void createInput();

	public String getLabel();

	public boolean isGroup();

	public List<?> getValidValues();

	public void addPropertyChangeListener(IPropertyChangeListener listener);

	public boolean addProperty(IPropertyExplorer e);

	public IPropertyExplorer getProperty(String key);

	public IPropertyExplorer getProperty(int index);

	boolean create();

	boolean edit();

	boolean remove();

	boolean refresh();
}
