package org.vidge.inface;

public interface IObjectDialog<F> {

	public abstract F getSelection();

	public abstract int open();
}