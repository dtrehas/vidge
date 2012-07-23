package org.vidge.controls.adapters;

import java.lang.reflect.Type;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.vidge.PropertyController;
import org.vidge.dialog.ListSelectPanel;
import org.vidge.inface.IChangeListener;
import org.vidge.inface.IEntityExplorer;
import org.vidge.util.TypeUtil;

/**
 * @author nemo
 */
@SuppressWarnings("rawtypes")
public class FieldAdapterSelectList extends AbstractFieldAdapter {

	private ListSelectPanel listPanel;

	public FieldAdapterSelectList(PropertyController controller, Composite parent) {
		super(controller, parent);
		listPanel.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	@Override
	protected void createControl(Composite parent) {
		try {
			Class<?> propertyClass = controller.getExplorer().getPropertyClass();
			Type propertyType = TypeUtil.getGenericClass(controller.getExplorer().getPropertyClass(), controller.getExplorer().getPropertyType());
			if (!propertyClass.equals(propertyType)) {
				IEntityExplorer explorer = TypeUtil.getExplorer(TypeUtil.getType(propertyType, propertyClass), controller.getExplorer());
				listPanel = new ListSelectPanel(parent, explorer, controller.getExplorer(), false);
			} else {
				IEntityExplorer explorer = TypeUtil.getExplorer(propertyClass, controller.getExplorer());
				listPanel = new ListSelectPanel(parent, explorer, controller.getExplorer(), true);
			}
			listPanel.addChangeListener(new IChangeListener() {

				@Override
				public void changed() {
					explorer.setValue(validator.getMarshalledValue());
					controller.inValidate();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public Control getControl() {
		return listPanel.getControl();
	}

	@Override
	public Object getVisualValue() {
		return null;
	}

	@Override
	public void setVisualValue(Object newValue) {
	}
}
