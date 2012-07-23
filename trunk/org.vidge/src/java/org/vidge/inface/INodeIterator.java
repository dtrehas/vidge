package org.vidge.inface;

import java.util.List;


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
