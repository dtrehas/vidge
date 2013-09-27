package org.vidge.test.form;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.vidge.form.IForm;
import org.vidge.test.entity.FormTestClass;
import org.vidge.util.VisualProperty;

public class TestForm4 implements IForm<FormTestClass> {

	private FormTestClass input;

	public TestForm4() {
	}

	public TestForm4(FormTestClass input) {
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

	@VisualProperty(order = 8, embedded = true)
	public List<FormTestClass> getTestList() {
		return input.getTestList2();
	}

	public void setTestList(List<FormTestClass> testList) {
		input.setTestList2(testList);
	}

	public List getTestListValidValues() {
		List<FormTestClass> objectList = new ArrayList<FormTestClass>();
		objectList.add(new FormTestClass("xxx", "ffffffffffffgggggggggg", 456, new Long(34L), new Date(), null));
		objectList.add(new FormTestClass("53xxx", "ffffffffffffgggggggfhfhggg", 456, new Long(34L), new Date(), null));
		objectList.add(new FormTestClass("678xxx", "ffffffffffffggggggfhggggg", 456, new Long(34L), new Date(), null));
		objectList.add(new FormTestClass("78888xxx", "ffffffffffffgggggggggg", 456, new Long(34L), new Date(), null));
		return objectList;
	}
}
