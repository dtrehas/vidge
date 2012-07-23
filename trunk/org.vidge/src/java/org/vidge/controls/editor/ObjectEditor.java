package org.vidge.controls.editor;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.vidge.PropertyController;
import org.vidge.controls.ObjectField;
import org.vidge.controls.tree.ObjectTreeEditorDialog;
import org.vidge.dialog.ObjectListDialog;
import org.vidge.dialog.ObjectListSelectDialog;
import org.vidge.dialog.SingleObjectDialog;
import org.vidge.inface.IEntityExplorer;
import org.vidge.inface.IObjectDialog;
import org.vidge.util.TypeUtil;

public class ObjectEditor<T> extends ObjectField<T> {

	public ObjectEditor(Composite parent, int style, PropertyController controller) {
		super(parent, style, controller);
	}

	@SuppressWarnings({
			"unchecked", "rawtypes"
	})
	@Override
	protected void showDialog() {
		IObjectDialog<T> dialog = null;
		Class<?> propertyClass = controller.getExplorer().getPropertyClass();
		Type propertyType = TypeUtil.getGenericClass(controller.getExplorer().getPropertyClass(), controller.getExplorer().getPropertyType());
		if (!propertyClass.equals(propertyType)) {
			IEntityExplorer explorer = TypeUtil.getExplorer(TypeUtil.getType(propertyType, propertyClass), controller.getExplorer());
			if (List.class.isAssignableFrom(propertyClass) || Map.class.isAssignableFrom(propertyClass) || propertyClass.isArray()) {
				if (controller.getExplorer().hasHierarchyProvider()) {
					dialog = new ObjectTreeEditorDialog(controller.getExplorer().getLabel(), controller.getExplorer().getHierarchyProvider());
				} else if (controller.getExplorer().hasValidValues()) {
					dialog = new ObjectListSelectDialog(controller.getExplorer().getLabel(), explorer, controller.getExplorer(), false);
				} else {
					dialog = new ObjectListDialog(controller.getExplorer().getLabel(), explorer, controller.getExplorer());
				}
			} else {
				dialog = new SingleObjectDialog<T>(explorer, controller.getExplorer().getLabel(), null);
			}
		} else {
			IEntityExplorer explorer = TypeUtil.getExplorer(propertyClass, controller.getExplorer());
			if (controller.getExplorer().hasHierarchyProvider()) {
				dialog = new ObjectTreeEditorDialog(controller.getExplorer().getLabel(), controller.getExplorer().getHierarchyProvider());
			} else if (controller.getExplorer().hasValidValues()) {
				dialog = new ObjectListSelectDialog(controller.getExplorer().getLabel(), explorer, controller.getExplorer(), true);
			} else {
				Object value = controller.getExplorer().getValue();
				if (value != null) {
					explorer.explore(value);
				}
				dialog = new SingleObjectDialog<T>(explorer, controller.getExplorer().getLabel(), null);
			}
		}
		if (dialog.open() == Window.OK) {
			T selection2 = dialog.getSelection();
			if (selection2 != null) {
				if (List.class.isAssignableFrom(propertyClass)) {
					setSelection(selection2);
				} else if (Set.class.isAssignableFrom(propertyClass)) {
					setSelection((T) new HashSet((Collection) selection2));
				} else if (Map.class.isAssignableFrom(propertyClass)) {
					HashMap hashMap = new HashMap();
					for (int a = 0; a < ((List) selection2).size(); a++) {
						hashMap.put(a, ((List) selection2).get(a));
					}
					setSelection((T) hashMap);
				} else if (propertyClass.isArray()) {
					T[] array = (T[]) Array.newInstance(TypeUtil.getType(propertyType, propertyClass), ((List) selection2).size());
					for (int a = 0; a < ((List) selection2).size(); a++) {
						array[a] = (T) ((List) selection2).get(a);
					}
					setSelection((T) array);
				} else {
					T obj = selection2;
					if (List.class.isAssignableFrom(obj.getClass())) {
						if (((List) obj).size() > 0) {
							setSelection((T) ((List) obj).get(0));
						} else {
							setSelection(null);
						}
					} else {
						setSelection(obj);
					}
				}
			}
			controller.inValidate();
		}
	}

	public void refresh(T object) {
		Class<?> propertyClass = controller.getExplorer().getPropertyClass();
		if (List.class.isAssignableFrom(propertyClass)) {
			text.setText(Messages.ObjectEditor_0);
		} else if (Map.class.isAssignableFrom(propertyClass)) {
			text.setText(Messages.ObjectEditor_1);
		} else if (propertyClass.isArray()) {
			text.setText(Messages.ObjectEditor_2);
		} else {
			super.refresh(object);
		}
	};

	@Override
	protected IObjectDialog<T> getDialog() {
		return null;
	}
}