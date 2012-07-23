package org.vidge.form.validator;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


public final class ValidatorRegistry {

	static {
		dataParsers = new HashMap<String, Class>();
		registerValidator(Double.TYPE.getName(), DoubleValidator.class);
		registerValidator(Float.TYPE.getName(), FloatValidator.class);
		registerValidator(Integer.TYPE.getName(), IntegerValidator.class);
		registerValidator(Long.TYPE.getName(), LongValidator.class);
		registerValidator(BigDecimal.class.getName(), BigDecimalValidator.class);
		registerValidator(Double.class.getName(), DoubleValidator.class);
		registerValidator(Float.class.getName(), FloatValidator.class);
		registerValidator(Integer.class.getName(), IntegerValidator.class);
		registerValidator(Long.class.getName(), LongValidator.class);
	}

	private static Map<String, Class> dataParsers;

	public static void registerValidator(String klassName, Class validator) {
		dataParsers.put(klassName, validator);
	}

	public static IValidator get(String klassName) {
		if (dataParsers.containsKey(klassName)) {
			try {
				return (IValidator) dataParsers.get(klassName).newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return new NullValidator();
	}

}
