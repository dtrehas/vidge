package org.vidge.util;

public class ChangeEventObject implements java.io.Serializable {

	private static final long serialVersionUID = 5516075349620653480L;
	/**
	 * The object on which the Event initially occurred.
	 */
	protected Object source;

	/**
	 * Constructs a prototypical Event.
	 * 
	 * @param source
	 *            The object on which the Event initially occurred.
	 * @exception IllegalArgumentException
	 *                if source is null.
	 */
	public ChangeEventObject(Object source) {
		// if (source == null)
		// throw new IllegalArgumentException("null source");
		this.source = source;
	}

	/**
	 * The object on which the Event initially occurred.
	 * 
	 * @return The object on which the Event initially occurred.
	 */
	public Object getSource() {
		return source;
	}

	/**
	 * Returns a String representation of this EventObject.
	 * 
	 * @return A a String representation of this EventObject.
	 */
	public String toString() {
		return getClass().getName() + "[source=" + source + "]";
	}
}
