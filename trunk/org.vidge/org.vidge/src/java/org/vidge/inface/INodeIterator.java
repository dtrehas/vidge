package org.vidge.inface;

import java.util.List;

import org.vidge.form.INodeForm;


public interface INodeIterator {

	void find(String searchString);
	int count();
	void clear();
	int current();
	INodeForm first();
	INodeForm previous();
	INodeForm last();
	INodeForm next();
	List<INodeForm> getFound();
}
