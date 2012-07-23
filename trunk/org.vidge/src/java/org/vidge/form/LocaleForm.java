package org.vidge.form;

import java.util.Locale;
import java.util.MissingResourceException;

import org.vidge.inface.IForm;
import org.vidge.util.VisualProperty;


public class LocaleForm implements IForm<Locale>{

	private Locale input;

	public LocaleForm() {
	}

	public LocaleForm(Locale input) {
		this.input = input;
	}

	@VisualProperty(order = 1)
	public String getCountry() {
		return input.getCountry();
	}

	@VisualProperty(order = 2)
	public String getISO3Country() throws MissingResourceException {
		return input.getISO3Country();
	}

	@VisualProperty(order = 3)
	public String getISO3Language() throws MissingResourceException {
		return input.getISO3Language();
	}

	@VisualProperty(order = 4)
	public String getLanguage() {
		return input.getLanguage();
	}

	@VisualProperty(order = 5)
	public String getVariant() {
		return input.getVariant();
	}
	
	public Locale getInput() {
		return input;
	}

	public void setInput(Locale input) {
		this.input = input;
	}
}
