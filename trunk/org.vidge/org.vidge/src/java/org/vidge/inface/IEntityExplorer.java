package org.vidge.inface;

import java.util.List;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.graphics.Image;
import org.vidge.form.IDialogForm;
import org.vidge.form.IFormColor;
import org.vidge.form.IFormComparator;
import org.vidge.form.IFormDataProvider;
import org.vidge.form.IFormFactory;
import org.vidge.form.IFormInputChangeListener;

@SuppressWarnings("rawtypes")
public interface IEntityExplorer extends IFormInputChangeListener, IFormDataProvider, IFormFactory, IFormColor, IDialogForm {

	public void explore(Object input);

	public void clear();

	public IEntityExplorer copy();

	public List<IPropertyExplorer> getPropertyList();

	public Image getImage(Object element, int index);

	public void setInput(Object input);

	public Object getInput();

	public Class<?> getInputClass();

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

	public void setContext(Object input);

	public Object createInput();

	public boolean removeInput();

	public void checkInput();

	public String getString(Object value);

	public Image getImage(Object value);

	public IFormComparator getComparator();
}
