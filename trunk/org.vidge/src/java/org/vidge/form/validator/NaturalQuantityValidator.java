package org.vidge.form.validator;

import java.math.BigDecimal;

import org.vidge.util.StringUtil;

public class NaturalQuantityValidator implements IValidator<BigDecimal> {

	private static final String PLEASE_ENTER_A_NUMBER = Messages.NaturalQuantityValidator_0;
	String message = PLEASE_ENTER_A_NUMBER;
	private final BigDecimal mimimumQuantity;
	private final BigDecimal maximumQuantity;
	private final boolean canEmpty;
	private BigDecimal newValue;

	public NaturalQuantityValidator(BigDecimal maximumQuantity, BigDecimal mimimumQuantity, boolean canEmpty) {
		this.maximumQuantity = maximumQuantity;
		this.mimimumQuantity = mimimumQuantity;
		this.canEmpty = canEmpty;
	}

	public String getHelp() {
		return message;
	}

	public boolean validateComplete(Object value) {
		if ((value == null) || (value.equals(StringUtil.NN))) {//$NON-NLS-1$
			if (!canEmpty) {
				message = Messages.NaturalQuantityValidator_3 + StringUtil.SP2 + PLEASE_ENTER_A_NUMBER;
				return false;
			}
			return true;
		}
		if (mimimumQuantity != null) {
			try {
				newValue = new BigDecimal(value.toString());
				if (newValue.subtract(mimimumQuantity).floatValue() < 0) {
					message = Messages.NaturalQuantityValidator_1 + StringUtil.SP2 + mimimumQuantity.toString();
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	public boolean validatePartial(Object value) {
		try {
			newValue = new BigDecimal(value.toString());
			if (maximumQuantity != null) {
				BigDecimal newAvailableValue = maximumQuantity.subtract(newValue);
				if (newAvailableValue.floatValue() < 0) {
					return false;
				} else {
					message = Messages.NaturalQuantityValidator_2 + StringUtil.SP2 + newAvailableValue.toString();
				}
			}
		} catch (Exception e) {
			if ((value == null) || StringUtil.isEmpty(value.toString())) {
				message = PLEASE_ENTER_A_NUMBER;
				newValue = null;
				return true;
			}
			return false;
		}
		return true;
	}

	public BigDecimal getMarshalledValue() {
		return newValue;
	}
}
