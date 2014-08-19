package org.vidge.explorer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.langcom.locale.LocalizedString;
import org.vidge.inface.IEntityExplorer;

public class ObjectExplorer<T> extends EntityExplorer {

	public ObjectExplorer() {
	}

	public ObjectExplorer(Object obj) {
		if (obj.getClass().equals(Class.class)) {
			throw new RuntimeException("***Class prohibited");
		}
		explore(obj);
	}

	private void exploreFields(List<Field> fieldList, Class<?> clazz) {
		fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
		if (clazz.getSuperclass() != null) {
			exploreFields(fieldList, clazz.getSuperclass());
		}
	}

	public void explore(Object objIn) {
		super.explore(objIn);
		List<Field> fieldList = new ArrayList<Field>();
		exploreFields(fieldList, input.getClass());
		for (Field field : fieldList) {
			addProperty(new FieldExplorer(input, field, true));
		}
	}

	@Override
	public String getLabel() {
		if (LocalizedString.class.isAssignableFrom(input.getClass())) {
			return ((LocalizedString) input).getDefaultLocalString();
		}
		return super.getLabel();
	}

	@Override
	public IEntityExplorer copy() {
		try {
			return new ObjectExplorer<T>(input.getClass().newInstance());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
