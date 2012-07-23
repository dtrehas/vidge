package org.vidge.test.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.vidge.inface.IForm;
import org.vidge.test.entity.FormTestClass;
import org.vidge.test.entity.FormTestClass2;
import org.vidge.test.entity.TestEnum;
import org.vidge.util.VisualControlType;
import org.vidge.util.VisualProperty;

public class TestForm3 implements IForm<FormTestClass> {

	private FormTestClass input;

	public TestForm3() {
	}

	public TestForm3(FormTestClass input) {
		this.input = input;
	}

	@Override
	public void setInput(FormTestClass input) {
		this.input = input;
	}

	@Override
	public FormTestClass getInput() {
		return input;
	}

	@VisualProperty(order = 6, control = VisualControlType.OBJECT_EDITOR)
	public FormTestClass2 getReference() {
		return input.getReference();
	}

	public void setReference(FormTestClass2 reference) {
		input.setReference(reference);
	}

	@VisualProperty(order = 8, embedded = true)
	public List<FormTestClass2> getTestList() {
		return input.getTestList();
	}

	public void setTestList(List<FormTestClass2> testList) {
		input.setTestList(testList);
	}

	@VisualProperty(order = 7)
	public TestEnum getEnumValue() {
		return input.getEnumValue();
	}

	public void setEnumValue(TestEnum enumValue) {
		input.setEnumValue(enumValue);
	}

	public List getChildValidValues() {
		List<FormTestClass> objectList = new ArrayList<FormTestClass>();
		objectList.add(new FormTestClass("xxx", "ffffffffffffgggggggggg", 456, new Long(34L), new Date(), null));
		objectList.add(new FormTestClass("53xxx", "ffffffffffffgggggggfhfhggg", 456, new Long(34L), new Date(), null));
		objectList.add(new FormTestClass("678xxx", "ffffffffffffggggggfhggggg", 456, new Long(34L), new Date(), null));
		objectList.add(new FormTestClass("78888xxx", "ffffffffffffgggggggggg", 456, new Long(34L), new Date(), null));
		return objectList;
	}

	@VisualProperty(order = 11, embedded = true)
	public FormTestClass getChild() {
		return input.getChild();
	}

	public void setChild(FormTestClass child) {
		input.setChild(child);
	}
}
