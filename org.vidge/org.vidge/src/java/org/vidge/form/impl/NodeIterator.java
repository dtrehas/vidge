package org.vidge.form.impl;

import java.util.ArrayList;
import java.util.List;

import org.vidge.form.INodeForm;
import org.vidge.inface.INodeIterator;


public class NodeIterator  implements INodeIterator {

	private List<INodeForm> foundNodes = new ArrayList<INodeForm>();
	private int currentFound = 0;
	private final INodeForm rootNode;

	public NodeIterator(INodeForm root) {
		this.rootNode = root;
	}
	
	public void find(String filterString) {
		find(rootNode,filterString);
	}
	
	//it better than recursive creation of NodeIterators
	private void find(INodeForm parent, String filterString) {
		for (INodeForm child : parent.getChildren()) {
			if(child.toString().contains(filterString)) {
				foundNodes.add(child);
			}
			find(child,filterString);
		}
	}

	public int current() {
		return currentFound;
	}

	public INodeForm first() {
		currentFound = 0;
		return foundNodes.get(currentFound);
	}

	public INodeForm last() {
		currentFound = foundNodes.size() - 1;
		if (currentFound < 0) {
			currentFound = 0;
		}
		return foundNodes.get(currentFound);
	}

	public INodeForm next() {
		currentFound++;
		if (currentFound >= foundNodes.size()) {
			currentFound = foundNodes.size() - 1;
		}
		return foundNodes.get(currentFound);
	}

	public INodeForm previous() {
		currentFound--;
		if (currentFound < 0) {
			currentFound = 0;
		}
		return foundNodes.get(currentFound);
	}

	public void clear() {
		currentFound = 0;
		foundNodes.clear();
	}

	public int count() {
		return foundNodes.size();
	}

	public List<INodeForm> getFound() {
		return foundNodes;
	}

}
