package org.vidge.controls.adapters;

import java.lang.reflect.Type;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.vidge.PropertyController;
import org.vidge.controls.tree.TreeHPanel;
import org.vidge.controls.tree.hierarchy.IHierarchyProvider;
import org.vidge.inface.IEntityExplorer;
import org.vidge.util.FormContext;
import org.vidge.util.TypeUtil;

/**
 * @author nemo
 */
@SuppressWarnings("rawtypes")
public class FieldAdapterTreeList extends AbstractFieldAdapter {

	private TreeHPanel panel;
	@SuppressWarnings("unused")
	private IEntityExplorer explorer;

	public FieldAdapterTreeList(PropertyController controller, Composite parent) {
		super(controller, parent);
	}

	@Override
	protected void createControl(Composite parent) {
		try {
			Class<?> propertyClass = controller.getExplorer().getPropertyClass();
			Type propertyType = TypeUtil.getGenericClass(controller.getExplorer().getPropertyClass(), controller.getExplorer().getPropertyType());
			IHierarchyProvider<?> hierarchyProvider = controller.getExplorer().getHierarchyProvider();
			if (!propertyClass.equals(propertyType)) {
				explorer = TypeUtil.getExplorer(TypeUtil.getType(propertyType, propertyClass), FormContext.TABLE.name(), controller.getExplorer());
			} else {
				explorer = TypeUtil.getExplorer(propertyClass, FormContext.TABLE.name(), controller.getExplorer());
			}
			panel = new TreeHPanel(parent, hierarchyProvider);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		panel.setEnabled(enabled);
	}

	@Override
	public Control getControl() {
		return panel;
	}

	@Override
	public Object getVisualValue() {
		return null;
	}

	@Override
	public void setVisualValue(Object newValue) {
	}
}
