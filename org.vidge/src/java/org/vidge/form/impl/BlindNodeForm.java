package org.vidge.form.impl;

import java.lang.reflect.Method;
import java.util.List;

public class BlindNodeForm extends AbstractNodeForm<Object> {

	private List<Object> objectList;

	public BlindNodeForm(Object input, List<Object> objectList) {
		this.input = input;
		this.objectList = objectList;
		populateChild();
	}
	
	public BlindNodeForm(List<Object> value) {
		this.objectList = value;
		for (Object obj : objectList) {
			Method method = null;
			try {
				method = obj.getClass().getMethod("getParent", new Class[0]);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			if (method != null) {
				try {
					Object invoke = method.invoke(obj, new Object[0]);
					if (invoke == null) {
						input = obj;
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		populateChild();
	}

	@Override
	public String toString() {
		String string = super.toString();
		if(string.length()>50) {
			string = string.substring(0, 50)+"...";
		}
		return string;
	}
	
	protected void populateChild() {
		int a = 0;
		while (a < objectList.size()) {
			Object obj = objectList.get(a);
			Method method = null;
			try {
				method = obj.getClass().getMethod("getParent", new Class[0]);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			if (method != null) {
				try {
					Object invoke = method.invoke(obj, new Object[0]);
					if (invoke != null) {
						if (invoke.equals(input)) {
							objectList.remove(a);
							addBlindChild(obj);
							a--;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			a++;
		}
	}

	private BlindNodeForm addBlindChild(Object obj) {
		BlindNodeForm blindNodeForm = new BlindNodeForm(obj,objectList);
		addChild(blindNodeForm);
		blindNodeForm.setParent(this);
		return blindNodeForm;
	}

	@Override
	public int getChildCount() {
		if (children.size() == 0) {
			return 1;
		}
		return super.getChildCount();
	}
}
