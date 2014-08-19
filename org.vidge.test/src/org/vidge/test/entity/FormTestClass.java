package org.vidge.test.entity;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.langcom.locale.LocalizedString;

public class FormTestClass {

	private String name = "TestClass", description;
	private LocalizedString localizedString;
	private Boolean booleanValue = true;
	private Integer invalue = 8790;
	private Long longValue = 346L;
	private Double doubleValue = 45645.7;
	private Date datevalue = new Date();
	private File filevalue;
	private Color color;
	private TestEnum enumValue = TestEnum.ONE;
	private FormTestClass2 reference;
	private FormTestClass child;
	private List<FormTestClass2> testList = new ArrayList<FormTestClass2>();
	private List<FormTestClass> testList2 = new ArrayList<FormTestClass>();

	public FormTestClass() {
	}

	public FormTestClass(String name, String description, Integer invalue, Long longValue, Date datevalue, File filevalue) {
		this.name = name;
		this.description = description;
		this.invalue = invalue;
		this.longValue = longValue;
		this.datevalue = datevalue;
		this.filevalue = filevalue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getInvalue() {
		return invalue;
	}

	public void setInvalue(Integer invalue) {
		this.invalue = invalue;
	}

	public Long getLongValue() {
		return longValue;
	}

	public void setLongValue(Long longValue) {
		this.longValue = longValue;
	}

	public Date getDatevalue() {
		return datevalue;
	}

	public void setDatevalue(Date datevalue) {
		this.datevalue = datevalue;
	}

	public File getFilevalue() {
		return filevalue;
	}

	public void setFilevalue(File filevalue) {
		this.filevalue = filevalue;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
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

	public FormTestClass2 getReference() {
		return reference;
	}

	public void setReference(FormTestClass2 reference) {
		this.reference = reference;
	}

	@Override
	public String toString() {
		return name;
	}

	public Boolean getBooleanValue() {
		return booleanValue;
	}

	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	public TestEnum getEnumValue() {
		return enumValue;
	}

	public void setEnumValue(TestEnum enumValue) {
		this.enumValue = enumValue;
	}

	public FormTestClass getChild() {
		return child;
	}

	public void setChild(FormTestClass child) {
		this.child = child;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FormTestClass other = (FormTestClass) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public List<FormTestClass> getTestList2() {
		return testList2;
	}

	public void setTestList2(List<FormTestClass> testList2) {
		this.testList2 = testList2;
	}

	public Double getDoubleValue() {
		return doubleValue;
	}

	public void setDoubleValue(Double doubleValue) {
		this.doubleValue = doubleValue;
	}

	public LocalizedString getLocalizedString() {
		return localizedString;
	}

	public void setLocalizedString(LocalizedString localizedString) {
		this.localizedString = localizedString;
	}
}
