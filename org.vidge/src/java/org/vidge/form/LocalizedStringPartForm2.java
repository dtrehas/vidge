package org.vidge.form;

import org.langcom.locale.LocalizedStringPart;
import org.vidge.inface.IForm;
import org.vidge.util.VisualProperty;

public class LocalizedStringPartForm2 implements IForm<LocalizedStringPart> {

	private LocalizedStringPart input;

	public LocalizedStringPartForm2() {
	}

	public LocalizedStringPartForm2(LocalizedStringPart input) {
		this();
		setInput(input);
	}

	@VisualProperty(order = 1, width = 30)
	public String getLocale() {
		return input.locale;
	}

	public void setLocale(String locale) {
		input.locale = locale;
	}

	@VisualProperty(order = 2)
	public String getLocalizedString() {
		return input.localizedString;
	}

	public void setLocalizedString(String string) {
		input.localizedString = string;
	}

	public LocalizedStringPart getInput() {
		return input;
	}

	public void setInput(LocalizedStringPart input) {
		this.input = input;
	}
}
