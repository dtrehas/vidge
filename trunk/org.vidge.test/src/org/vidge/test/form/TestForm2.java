package org.vidge.test.form;

import java.util.Date;
import java.util.List;

import org.vidge.inface.IForm;
import org.vidge.test.entity.FormTestClass2;
import org.vidge.util.VisualControlType;
import org.vidge.util.VisualProperty;

public class TestForm2 implements IForm<FormTestClass2> {

	private FormTestClass2 input;

	public TestForm2() {
	}

	public TestForm2(FormTestClass2 input) {
		this.input = input;
	}

	@Override
	public void setInput(FormTestClass2 input) {
		this.input = input;
	}

	@Override
	public FormTestClass2 getInput() {
		return input;
	}

	@VisualProperty(order = 1)
	public String getName() {
		return input.getName();
	}

	public void setName(String name) {
		input.setName(name);
	}

	@VisualProperty(order = 2)
	public Integer getIntValue() {
		return input.getIntValue();
	}

	public void setIntValue(Integer intValue) {
		input.setIntValue(intValue);
	}

	@VisualProperty(order = 3)
	public Date getTimeValue() {
		return input.getTimeValue();
	}

	public void setTimeValue(Date timeValue) {
		input.setTimeValue(timeValue);
	}

	@VisualProperty(order = 6, control = VisualControlType.TEXTAREA)
	public String getDescription() {
		return input.getDescription();
	}

	public void setDescription(String description) {
		input.setDescription(description);
	}

	@VisualProperty(order = 4)
	public FormTestClass2[] getArray() {
		return input.getArray();
	}

	public void setArray(FormTestClass2[] array) {
		input.setArray(array);
	}

	@VisualProperty(order = 5)
	public List<FormTestClass2> getTestList() {
		return input.getTestList();
	}

	public void setTestList(List<FormTestClass2> testList) {
		input.setTestList(testList);
	}
}
