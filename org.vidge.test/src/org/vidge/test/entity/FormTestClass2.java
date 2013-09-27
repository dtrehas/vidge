package org.vidge.test.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class FormTestClass2 {

	private String name = "None";
	private Integer intValue = 234234;
	private Date timeValue = new Date();
	private String description = "ggggggggg";
	private FormTestClass2[] array;
	private List<FormTestClass2> testList = new ArrayList<FormTestClass2>();

	public FormTestClass2() {
	}

	public FormTestClass2(String name, Integer intValue, Date timeValue) {
		super();
		this.name = name;
		this.intValue = intValue;
		this.timeValue = timeValue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getIntValue() {
		return intValue;
	}

	public void setIntValue(Integer intValue) {
		this.intValue = intValue;
	}

	public Date getTimeValue() {
		return timeValue;
	}

	public void setTimeValue(Date timeValue) {
		this.timeValue = timeValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public FormTestClass2[] getArray() {
		return array;
	}

	public void setArray(FormTestClass2[] array) {
		this.array = array;
	}

	public List<FormTestClass2> getTestList() {
		return testList;
	}

	public void setTestList(List<FormTestClass2> testList) {
		this.testList = testList;
	}

	public boolean add(FormTestClass2 e) {
		return testList.add(e);
	}

	@Override
	public String toString() {
		return "FormTestClass2 [name=" + name + ", intValue=" + intValue + ", timeValue=" + timeValue + ", description=" + description + ", array=" + Arrays.toString(array) + ", testList=" + testList
			+ "]";
	}
}
