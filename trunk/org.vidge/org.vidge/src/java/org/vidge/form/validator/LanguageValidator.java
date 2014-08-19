package org.vidge.form.validator;


public class LanguageValidator extends LengthValidator {

	EngCharRealm charRealm = new EngCharRealm();

	public LanguageValidator(IValidator validator, Long maxLength, Long minLength) {
		super(validator, maxLength, minLength);
	}

	@Override
	public boolean validatePartial(Object value) {
		String project;
		try {
			if (value == null) {
				newValue = null;
				return true;
			}
			project = value.toString();
			if (project.length() == 0) {
				newValue = null;
			} else {
				for (Character character : newValue.toCharArray()) {
					if (!charRealm.isCharInTheRealm(character)) {
						return false;
					}
				}
				newValue = project.trim();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return super.validatePartial(value);
	}

	public Object getMarshalledValue() {
		return newValue;
	}
	private static class EngCharRealm {

		private int min = 65;
		private int max = 122;
		private boolean oneWord = false;
		private boolean englishWord = false;

		public boolean isCharInTheRealm(Character character) {
			if (oneWord && Character.isWhitespace(character)) {
				oneWord = false;
				englishWord = false;
				return true;
			}
			if (oneWord) {
				if (Character.isLetter(character)) {
					if (englishWord) {
						if (character.charValue() < min || character.charValue() > max) {
							return false;
						}
					} else {
						if (character.charValue() >= min && character.charValue() <= max) {
							return false;
						}
					}
				}
			} else {
				oneWord = Character.isLetter(character);
				englishWord = (character.charValue() >= min && character.charValue() <= max);
			}
			return true;
		}
	}
}
