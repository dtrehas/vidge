package org.vidge.test.form;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.langcom.locale.LocalizedString;
import org.vidge.form.IForm;
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

	@VisualProperty(order = 0)
	public String getName() {
		return input.getName();
	}

	@VisualProperty(order = 1)
	public LocalizedString getLocalizedString() {
		return input.getLocalizedString();
	}

	public void setLocalizedString(LocalizedString localizedString) {
		input.setLocalizedString(localizedString);
	}

	@VisualProperty(order = 6, control = VisualControlType.OBJECT_EDITOR)
	public FormTestClass2 getReference() {
		return input.getReference();
	}

	public void setReference(FormTestClass2 reference) {
		input.setReference(reference);
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

	@VisualProperty(order = 11)
	public FormTestClass getChild() {
		return input.getChild();
	}

	public void setChild(FormTestClass child) {
		input.setChild(child);
	}

	@VisualProperty(order = 8)
	public List<FormTestClass2> getTestList() {
		return input.getTestList();
	}

	@VisualProperty(order = 9)
	public List<FormTestClass> getTestList2333() {
		return input.getTestList2();
	}

	public void setName(String name) {
		input.setName(name);
	}

	@VisualProperty(order = 12)
	public Integer getInvalue() {
		return input.getInvalue();
	}

	public void setInvalue(Integer invalue) {
		input.setInvalue(invalue);
	}

	@VisualProperty(order = 13)
	public Long getLongValue() {
		return input.getLongValue();
	}

	public void setLongValue(Long longValue) {
		input.setLongValue(longValue);
	}

	@VisualProperty(order = 14)
	public Date getDatevalue() {
		return input.getDatevalue();
	}

	public void setDatevalue(Date datevalue) {
		input.setDatevalue(datevalue);
	}

	@VisualProperty(order = 15)
	public File getFilevalue() {
		return input.getFilevalue();
	}

	public void setFilevalue(File filevalue) {
		input.setFilevalue(filevalue);
	}

	@VisualProperty(order = 16)
	public Color getColor() {
		return input.getColor();
	}

	public void setColor(Color color) {
		input.setColor(color);
	}

	public void setTestList(List<FormTestClass2> testList) {
		input.setTestList(testList);
	}

	@VisualProperty(order = 17)
	public Boolean getBooleanValue() {
		return input.getBooleanValue();
	}

	public void setBooleanValue(Boolean booleanValue) {
		input.setBooleanValue(booleanValue);
	}

	@VisualProperty(order = 18)
	public List<FormTestClass> getTestList2() {
		return input.getTestList2();
	}

	public void setTestList2(List<FormTestClass> testList2) {
		input.setTestList2(testList2);
	}

	@VisualProperty(order = 19)
	public Double getDoubleValue() {
		return input.getDoubleValue();
	}

	public void setDoubleValue(Double doubleValue) {
		input.setDoubleValue(doubleValue);
	}

	@VisualProperty(order = 22, control = VisualControlType.TEXTAREA)
	public String getDescription() {
		return input.getDescription();
	}

	public void setDescription(String description) {
		input.setDescription(description);
	}
}
